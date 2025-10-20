package server;

import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clearDB)
//                .post("/user", this::registerUser)
//                .post("/session", this::login)
//                .delete("/session", this::logout)
//                .get("/game", this::listGames)
//                .post("/game", this::createGame)
//                .put("/game", this::joinGame)
//                .exception(ResponseException.class, this::exceptionHandler)

        ;

        // Register your endpoints and exception handlers here.


    }

    private void clearDB(Context ctx){
        ctx.result("Filler");
    }



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
