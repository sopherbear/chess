package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import exception.ResponseException;
import service.*;
import model.*;
import websocket.WebSocketHandler;


public class Server {

    private Javalin javalin;
    private UserService userService;
    private GameService gameService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private WebSocketHandler webSocketHandler;


//    private final

    public Server(){
        try {
        this.userDAO = new MySqlUserDAO();
        this.authDAO = new MySqlAuthDAO();
        this.gameDAO = new MySqlGameDAO();
        this.userService = new UserService(authDAO, userDAO, gameDAO);
        this.gameService = new GameService(authDAO, gameDAO);
        this.webSocketHandler = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::exceptionHandler)
                .exception(DataAccessException.class, this::dataAccessHandler)
                .ws("/ws", ws-> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                })
        ;

        } catch (Throwable ex){
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.json(ex.toJson());
    }

    private void dataAccessHandler(DataAccessException ex, Context ctx) {
        ctx.status(500);
        ctx.json(ex.toJson());
    }

    private void clear(Context ctx) throws DataAccessException{
        userService.clear();
        ctx.status(200);
    }

    private void register(Context ctx) throws ResponseException, DataAccessException{
        RegisterRequest newUser = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: Registration info not complete\n");
        }
        AuthData userAuth = userService.register(newUser);

        ctx.json(new Gson().toJson(userAuth));
    }

    private void login(Context ctx) throws ResponseException, DataAccessException {
        LoginRequest newLogin = new Gson().fromJson(ctx.body(), LoginRequest.class);
        if (newLogin.password() == null || newLogin.username() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: login info not complete\n");
        }
        AuthData sessionAuth = userService.login(newLogin);

        ctx.json(new Gson().toJson(sessionAuth));
    }

    private void logout(Context ctx) throws ResponseException, DataAccessException{
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included\n");
        }
        userService.logout(authToken);
        ctx.status(200);
    }

    private void createGame(Context ctx) throws ResponseException, DataAccessException{
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included\n");
        }
        GameName newGame = new Gson().fromJson(ctx.body(), GameName.class);
        if (newGame.gameName() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: gameName not included\n");
        }

        var newGameID = gameService.createGame(authToken, newGame);

        ctx.json(new Gson().toJson(newGameID));
    }

    private void joinGame(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included\n");
        }
        GameRequest joinInfo = new Gson().fromJson(ctx.body(), GameRequest.class);
        if (joinInfo.playerColor() == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: playerColor not included\n");
        }
        if (!joinInfo.playerColor().equals("BLACK") && !joinInfo.playerColor().equals("WHITE")) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: playerColor is invalid\n");
        }

        gameService.joinGame(authToken, joinInfo);
        ctx.status(200);
    }

    private void listGames(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: authToken not included\n");
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
