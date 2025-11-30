package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
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
            String username = authDAO.getAuth(userCommand.getAuthToken()).username();
            String color = null;

            GameData game = gameDAO.getGame(gameId);
            var blackUser = game.blackUsername();
            var whiteUser = game.whiteUsername();
            if (blackUser != null && blackUser.equals(username)){
                color ="black";
            } else if (whiteUser != null && whiteUser.equals(username)){
                color = "white";
            }
            connections.add(gameId, session);

            switch (userCommand.getCommandType()) {
                case CONNECT -> connect(gameId, session, username, color);
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave();
                case RESIGN -> resign();
            }
        } catch(ResponseException ex){

        } catch(DataAccessException ex) {

        }
        catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Integer gameId, Session session, String username, String color) throws IOException{
        String message;
        if (color == null) {
            message = String.format("%s joined the game as an observer", username);
        } else {
            message = String.format("%s joined the game as %s", username, color);
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(null, gameId, notification);
    }

    private void makeMove(){}

    private void leave(){};

    private void resign(){};

}
