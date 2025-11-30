package websocket;

import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import exception.ResponseException;
import jakarta.websocket.*;
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
        try {
            var userCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(userCommand));
        } catch(IOException ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

}
