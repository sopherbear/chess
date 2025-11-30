package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    ServerMessageType serverMessageType;
    String errorMessage;

    public LoadGameMessage(ServerMessageType type, String errorMessage) {
        super(type);
        errorMessage = errorMessage;
    }


}
