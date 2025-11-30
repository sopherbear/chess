package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    String errorMessage;

    public LoadGameMessage(ServerMessageType type, String errorMessage) {
        super(type);
        errorMessage = errorMessage;
    }


}
