package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import exception.ResponseException;
import Service.*;
import model.*;

import javax.xml.crypto.Data;


public class Server {

    private Javalin javalin;
    private UserService userService;
    private GameService gameService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;


//    private final

    public Server(){
        try {
        this.userDAO = new MySqlUserDAO();
        this.authDAO = new MySqlAuthDAO();
        this.gameDAO = new MySqlGameDAO();
        this.userService = new UserService(authDAO, userDAO, gameDAO);
        this.gameService = new GameService(authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::exceptionHandler)
//                .exception(DataAccessException.class, this::dataAccessHandler)

        ;

        } catch (Throwable ex){
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

        // Register your endpoints and exception handlers here.


    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.json(ex.toJson());
    }

//    private void dataAccessHandler(DataAccessException ex, Context ctx) {
//        ctx.status(500);
//    }

    private void clear(Context ctx) throws DataAccessException{
        userService.clear();
        ctx.status(200);
    }

    private void register(Context ctx) throws ResponseException, DataAccessException{
        RegisterRequest newUser = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: Registration info not complete");
        }
        AuthData userAuth = userService.register(newUser);

        ctx.json(new Gson().toJson(userAuth));
    }

    private void login(Context ctx) throws ResponseException, DataAccessException {
        LoginRequest newLogin = new Gson().fromJson(ctx.body(), LoginRequest.class);
        if (newLogin.password() == null || newLogin.username() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: login info not complete");
        }
        AuthData sessionAuth = userService.login(newLogin);

        ctx.json(new Gson().toJson(sessionAuth));
    }

    private void logout(Context ctx) throws ResponseException, DataAccessException{
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included");
        }
        userService.logout(authToken);
        ctx.status(200);
    }

    private void createGame(Context ctx) throws ResponseException, DataAccessException{
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included");
        }
        GameName newGame = new Gson().fromJson(ctx.body(), GameName.class);
        if (newGame.gameName() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: gameName not included");
        }

        var newGameID = gameService.createGame(authToken, newGame);

        ctx.json(new Gson().toJson(newGameID));
    }

    private void joinGame(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included");
        }
        GameRequest joinInfo = new Gson().fromJson(ctx.body(), GameRequest.class);
        if (joinInfo.playerColor() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: playerColor not included");
        }
        if (!joinInfo.playerColor().equals("BLACK") && !joinInfo.playerColor().equals("WHITE")) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: playerColor is invalid");
        }

        gameService.joinGame(authToken, joinInfo);
        ctx.status(200);
    }

    private void listGames(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included");
        }

        var gamesList = gameService.listGames(authToken);
        ctx.json(new Gson().toJson(gamesList));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
