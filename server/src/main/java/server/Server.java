package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
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
    private final GameService gameService;
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;


//    private final

    public Server() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.userService = new UserService(authDAO, userDAO, gameDAO);
        this.gameService = new GameService(authDAO, userDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
//                .get("/game", this::listGames)
                .post("/game", this::createGame)
//                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::exceptionHandler)

        ;

        // Register your endpoints and exception handlers here.


    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.json(ex.toJson());
    }

    private void clear(Context ctx) throws ResponseException{
        userService.clear();
        ctx.status(200);
    }

    private void register(Context ctx) throws ResponseException{
        RegisterRequest newUser = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: Registration info not complete");
        }
        AuthData userAuth = userService.register(newUser);

        ctx.json(new Gson().toJson(userAuth));
    }

    private void login(Context ctx) throws ResponseException{
        LoginRequest newLogin = new Gson().fromJson(ctx.body(), LoginRequest.class);
        if (newLogin.password() == null || newLogin.username() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: login info not complete");
        }
        AuthData sessionAuth = userService.login(newLogin);

        ctx.json(new Gson().toJson(sessionAuth));
    }

    private void logout(Context ctx) throws ResponseException{
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included");
        }
        userService.logout(authToken);
        ctx.status(200);
    }

    private void createGame(Context ctx) throws ResponseException{
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included");
        }
        GameRequest newGame = new Gson().fromJson(ctx.body(), GameRequest.class);
        if (newGame.gameName() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: gameName not included");
        }

        var newGameID = gameService.createGame(authToken, newGame);

        ctx.json(new Gson().toJson(newGameID));
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
