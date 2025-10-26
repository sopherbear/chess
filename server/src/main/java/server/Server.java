package server;

import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import exception.ResponseException;
import Service.*;
import model.*;
import java.util.UUID;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;
//    private final

    public Server(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
//                .delete("/db", this::clear)
                .post("/user", this::register)
//                .post("/session", this::login)
//                .delete("/session", this::logout)
//                .get("/game", this::listGames)
//                .post("/game", this::createGame)
//                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::exceptionHandler)

        ;

        // Register your endpoints and exception handlers here.


    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.json(ex.toJson());
    }

//    private void clear(Context ctx) throws ResponseException{
//        ctx.result("Filler");
//    }

    private void register(Context ctx) throws ResponseException{
        RegisterRequest newUser = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        var userName = newUser.username();
        var user = userService.getUser(userName);

        if (user != null) {
            throw new ResponseException(ResponseException.Code.AlreadyTakenError, "Error: username does not exist");
        }

        UserData user2add = new Gson().fromJson(ctx.body(), UserData.class);
        userService.createUser(user2add);

        var newToken = generateToken();
        AuthData authData = new AuthData(newToken, userName);
        authService.createAuth(authData);
        ctx.json(new Gson().toJson(authData));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
