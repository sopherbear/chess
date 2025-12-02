package websocket;

import chess.ChessGame;
import chess.ChessMove;
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
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;


import java.io.IOException;

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
            ChessGame.TeamColor color = null;

            GameData game = gameDAO.getGame(gameId);
            var blackUser = game.blackUsername();
            var whiteUser = game.whiteUsername();
            if (blackUser != null && blackUser.equals(username)){
                color = ChessGame.TeamColor.BLACK;
            } else if (whiteUser != null && whiteUser.equals(username)){
                color = ChessGame.TeamColor.WHITE;
            }

            switch (userCommand.getCommandType()) {
                case CONNECT -> connect(gameId, session, username, color);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(ctx.message(), MakeMoveCommand.class), game, username, color, session);
                case LEAVE -> leave();
                case RESIGN -> resign();
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
        if (color == null) {
            var ex = new ResponseException(ResponseException.Code.ClientError, "Error: Observer can't make moves");
            sendResponseErrorMessage(ex, session);
            return;
        }

        ChessGame game = gameData.game();
        if (game.getTeamTurn() != color) {
            var ex = new ResponseException(ResponseException.Code.ClientError, String.format("Error: It is not %s's turn", color));
            sendResponseErrorMessage(ex, session);
            return;
        }
        try {
            ChessMove move = moveCommand.getMove();
            game.makeMove(move);
            game.otherTeamTurn(color);

            var moveMessage = String.format("%s moved %s", username, move.toString());
            var moveNote = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMessage);
            connections.broadcast(session, gameData.gameID(), moveNote);

        }catch (InvalidMoveException ex){
            var newEx = new ResponseException(ResponseException.Code.ClientError, ex.getMessage());
            sendResponseErrorMessage(newEx, session);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void leave(){}

    private void resign(){}

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

}
