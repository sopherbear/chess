package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebsocketCommunicator(String url, ServerMessageObserver observer) throws ResponseException {
        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message){
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage msg = switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> new Gson().fromJson(message, NotificationMessage.class);
                        case ERROR -> new Gson().fromJson(message, ErrorMessage.class);
                        case LOAD_GAME -> new Gson().fromJson(message, LoadGameMessage.class);
                    };
                    //TODO: figure out the kind of server message and handle them
                    serverMessageObserver.notify(msg);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // TODO: add websocket commands connect, make_move, leave, resign

    public void connect(String authToken, Integer gameId) throws ResponseException{
        sendCommand(authToken, gameId, UserGameCommand.CommandType.CONNECT);
    }

    public void getBoard(String authToken, Integer gameId) throws ResponseException{
        sendCommand(authToken, gameId, UserGameCommand.CommandType.GET_BOARD);
    }

    public void makeMove(String authToken, Integer gameId, ChessMove move) throws ResponseException{
        try {
            var moveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameId, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (IOException ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameId) throws ResponseException{
        sendCommand(authToken, gameId, UserGameCommand.CommandType.RESIGN);
    }


    private void sendCommand(String authToken, Integer gameId, UserGameCommand.CommandType type) throws ResponseException{
        try {
            var userCommand = new UserGameCommand(type, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(userCommand));
        } catch(IOException ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leave(String authToken, Integer gameId) throws ResponseException{
        sendCommand(authToken, gameId, UserGameCommand.CommandType.LEAVE);
    }

}
