package websocket;

import com.google.gson.Gson;
import model.GameID;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public ConcurrentHashMap<Integer, Vector<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameId, Session session){
        if (! connections.containsKey(gameId)){
            connections.put(gameId, new Vector<>());
        }
        connections.get(gameId).add(session);}

    public void remove(Integer gameId, Session session) {
        if (connections.containsKey(gameId)) {
            connections.get(gameId).remove(session);
        }
    }

    public void broadcast(Session excludeSession, Integer gameId, ServerMessage serverMessage) throws IOException {
        var sessions = connections.get(gameId);
        for (Session c : sessions) {
            if (!c.equals(excludeSession)) {
                notifySession(c, serverMessage);
            }
        }
    }

    public void notifySession(Session session, ServerMessage serverMessage) throws IOException{
        if (session.isOpen()) {
            String msg = new Gson().toJson(serverMessage);
            session.getRemote().sendString(msg);
        }
    }

//    @NotNull
//    private Collection<Vector<Session>> getValues() {
//        return connections.values();
//    }


}
