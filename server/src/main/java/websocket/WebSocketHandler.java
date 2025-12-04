package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import java.io.IOException;
import java.util.Map;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private MySqlAuthDAO authDAO = new MySqlAuthDAO();
    private MySqlGameDAO gameDAO = new MySqlGameDAO();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {

        var gameId = -1;
        Session session = ctx.session;

        try {
            UserGameCommand userCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            gameId = userCommand.getGameID();

            AuthData authData = authDAO.getAuth(userCommand.getAuthToken());
            if (authData == null){
                throw new ResponseException(ResponseException.Code.ClientError, "Error: Invalid login");
            }
            String username = authDAO.getAuth(userCommand.getAuthToken()).username();

            GameData gameData = gameDAO.getGame(gameId);
            ChessGame.TeamColor color = getClientColor(gameData, username);

            switch (userCommand.getCommandType()) {
                case CONNECT -> connect(gameId, session, username, color);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(ctx.message(), MakeMoveCommand.class), gameData, username, color, session);
                case LEAVE -> leave(username, gameId, session);
                case RESIGN -> resign(gameId, session, username, color);
                case GET_BOARD -> getBoard(gameId, session);
            }
        } catch(ResponseException ex){
            sendResponseErrorMessage(ex, session);
        } catch(DataAccessException ex) {
            sendDataAccessErrorMessage(ex, session);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Integer gameId, Session session, String username, ChessGame.TeamColor color) throws IOException{
        connections.add(gameId, session);
        getBoard(gameId, session);

        String message;
        if (color == null) {
            message = String.format("%s joined the game as an observer", username);
        } else {
            message = String.format("%s joined the game as %s", username, color);
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, gameId, notification);
    }

    private void getBoard(Integer gameId, Session session) throws IOException{
        try{
            GameData gameData = gameDAO.getGame(gameId);
            ChessGame game = gameData.game();
            var loadGameMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.notifySession(session, loadGameMsg);
        } catch (ResponseException e) {
            sendResponseErrorMessage(e, session);
        }catch(DataAccessException ex){
            sendDataAccessErrorMessage(ex, session);
        }
    }

    private void makeMove(MakeMoveCommand moveCommand, GameData gameData, String username, ChessGame.TeamColor color, Session session){
        if (rejectIfIsObserver(color, session)){
            return;
        }
        ChessGame game = gameData.game();
        if (rejectIfGameOver(game, session)) {
            return;
        }

        if (game.getTeamTurn() != color) {
            var ex = new ResponseException(ResponseException.Code.ClientError, String.format("Error: It is not %s's turn", color));
            sendResponseErrorMessage(ex, session);
            return;
        }
        try {
            ChessMove move = moveCommand.getMove();
            game.makeMove(move);
            var gameId = gameData.gameID();
            gameDAO.updateGame(gameId, game);

            var updateBoards = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            connections.broadcast(null, gameId, updateBoards);
            var moveMessage = String.format("%s moved %s", username, convertMoveNotation(move));
            var moveNote = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMessage);
            connections.broadcast(session, gameId, moveNote);
            game.otherTeamTurn(color);

            if (game.isInCheckmate(game.otherTeam(color))){
                game.endGame();
                notifyAllOfGameState(gameData, color, "checkmate");
            } else if (game.isInCheck(game.otherTeam(color))){
                notifyAllOfGameState(gameData, color, "check");
            } else if (game.isInStalemate(color)) {
                game.endGame();
                var message = String.format("Game over: stalemate");
                var checkNote = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(null, gameId, checkNote);
            }
            gameDAO.updateGame(gameId, game);

        } catch (InvalidMoveException ex){
            var newEx = new ResponseException(ResponseException.Code.ClientError, ex.getMessage());
            sendResponseErrorMessage(newEx, session);
        } catch(IOException ex) {
            ex.printStackTrace();
        } catch (ResponseException ex) {
            sendResponseErrorMessage(ex, session);
        } catch (DataAccessException ex) {
            sendDataAccessErrorMessage(ex, session);
        }
    }

    private void leave(String username, Integer gameId, Session session){
        try {
            connections.remove(gameId, session);
            var message = String.format("%s has left the game", username);
            var leftNote = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(null, gameId, leftNote);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void resign(Integer gameId, Session session, String username, ChessGame.TeamColor color){
        if (rejectIfIsObserver(color, session)){
            return;
        }
        try {
            GameData gameData = gameDAO.getGame(gameId);
            ChessGame chessGame = gameData.game();
            if (rejectIfGameOver(chessGame, session)) {
                return;
            }
            chessGame.endGame();
            gameDAO.updateGame(gameId, chessGame);

            var message = String.format("%s has resigned and forfeits the game", username);
            var resignNote = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(null, gameId, resignNote);
        }  catch (ResponseException e) {
            sendResponseErrorMessage(e, session);
        }  catch(DataAccessException ex){
            sendDataAccessErrorMessage(ex, session);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendResponseErrorMessage(ResponseException ex, Session session){
        try {
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            connections.notifySession(session, errorMsg);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void sendDataAccessErrorMessage(DataAccessException e, Session session){
        try {
            var errorMsg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.notifySession(session, errorMsg);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private String getOtherPlayer(GameData gameData, ChessGame.TeamColor myColor) {
        String otherPlayerName;
        if (myColor == ChessGame.TeamColor.WHITE) {
            otherPlayerName = gameData.blackUsername();
        } else {
            otherPlayerName = gameData.whiteUsername();
        }
        return otherPlayerName;
    }

    private String convertMoveNotation(ChessMove move) {
        var start = move.getStartPosition();
        var end = move.getEndPosition();

        String startStr = convertPositionNotation(start);
        String endStr = convertPositionNotation(end);

        return String.format("%s:%s", startStr, endStr);
    }

    private String convertPositionNotation(ChessPosition pos){
        Map <Integer, String> colVals = Map.of(
                1, "a",
                2,"b",
                3, "c",
                4, "d",
                5, "e",
                6, "f",
                7, "g",
                8, "h"
        );
        var col = pos.getColumn();
        return String.format("%s%d", colVals.get(col), pos.getRow());
    }

    private ChessGame.TeamColor getClientColor(GameData gameData, String username){
        var blackUser = gameData.blackUsername();
        var whiteUser = gameData.whiteUsername();

        ChessGame.TeamColor color;
        if (blackUser != null && blackUser.equals(username)){
            color = ChessGame.TeamColor.BLACK;
        } else if (whiteUser != null && whiteUser.equals(username)){
            color = ChessGame.TeamColor.WHITE;
        } else {
            color = null;
        }
        return color;
    }

    private Boolean rejectIfIsObserver(ChessGame.TeamColor color, Session session){
        if (color == null) {
            var ex = new ResponseException(ResponseException.Code.ClientError, "Error: Observer can't participate in the game");
            sendResponseErrorMessage(ex, session);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void notifyAllOfGameState(GameData gameData, ChessGame.TeamColor color, String gameState) throws IOException{
        var otherPlayerName = getOtherPlayer(gameData, color);
        var message = String.format("%s is in %s", otherPlayerName, gameState);
        var checkMateNote = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(null, gameData.gameID(), checkMateNote);
    }

    private Boolean rejectIfGameOver(ChessGame game, Session session){
        if (!game.gameIsInSession()) {
            var ex =  new ResponseException(ResponseException.Code.ClientError, String.format("Error: Game is over"));
            sendResponseErrorMessage(ex, session);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
