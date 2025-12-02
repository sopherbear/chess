package websocket.messages;

public class NotificationMessage extends ServerMessage{
    //ServerMessageType serverMessageType;
    String message;

    public NotificationMessage(ServerMessageType type, String serverMessage){
        super(type);
        this.message = serverMessage;
    }

    public String getMessage(){return message;}
}
