package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        errorMessage = errorMessage;
    }
}
