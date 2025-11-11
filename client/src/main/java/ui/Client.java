package ui;

import exception.ResponseException;
import facade.ServerFacade;
import clientstate.State;
import model.*;

import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Scanner;


public class Client {
    private final ServerFacade server;
    private State state = State.PRELOGIN;
    private String authToken = null;
    private String game =  null;

    public Client(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_CYAN + RESET_TEXT_ITALIC + "Welcome to Sophie's Chess Program!\n");
        System.out.print(RESET_TEXT_COLOR + help());

        Scanner scanner = new Scanner(System.in);

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_NEON_PURPLE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.PRELOGIN) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            }
            // TODO: turn this into else if state = POSTLOGIN once gameplay is introduced
            else
            {
                return switch (cmd) {
                    case "create" -> create(params);
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "observe" -> observeGame();
                    case "logout" -> logout();
                    case "quit" -> "quit";
                    default -> help();
                };
            }

        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException{
        if (params.length == 2) {
            var loginRequest = new LoginRequest(params[0], params[1]);
            var authData = server.login(loginRequest);
            authToken = authData.authToken();
            state = State.POSTLOGIN;
            return String.format("Welcome back, %s!\n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException{
        if (params.length == 3) {
            var registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            var authData = server.register(registerRequest);
            authToken = authData.authToken();
            state = State.POSTLOGIN;
            return String.format("Welcome, %s. Your account has been created. \n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String logout() throws ResponseException{
        server.logout(authToken);
        authToken = null;
        state = State.PRELOGIN;
        return String.format("You have been logged out.");
    }

    public String create(String... params) throws ResponseException{
        if (params.length == 1) {
            var gameName = new GameName(params[0]);
            server.createGame(authToken, gameName);
            //TODO: Store gameID somewhere
            return String.format("Your game %s has been created!", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMENAME>");
    }

    public String listGames() throws ResponseException {
        GamesList games = server.listGames(authToken);
        String gameText = "";
        // TODO: convert to be readable, not just json
        return gameText;
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            // TODO: use game number to retrieve ID for game.
            server.joinGame(authToken, new GameRequest(new GameID(params[0]), params[1]));
            // TODO: print a gameboard
            return String.format("You have successfully joined the game");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMEID>, <COLOR> (either WHITE or BLACK)");
    }

    public String observeGame(String... params) throws ResponseException{
        if (params.length == 1) {
            // TODO: retrieve correct game, display chessboard
            return String.format("REMEMBER TO DISPLAY CHESSBOARD INSTEAD.");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMEID>");
    }

    public String help() {
        String helpMenu;
        if (state == State.PRELOGIN) {
            helpMenu =
                    RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - register a new user\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - log in to existing account\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "quit" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - exit chess\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "help" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - list possible actions\n";

        }
        else if (state == State.POSTLOGIN) {
            helpMenu =
                    RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "create <GAMENAME>" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - create a new game and give it a name\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "list" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - get a list of current chess games\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "join <ID> [WHITE|BLACK]" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - join an existing game as a particular color\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "observe" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - watch a particular game\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "logout" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - log out of chess\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "quit" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - exit chess console\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "help" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - list possible actions\n";
        }
        else {return "GAME NOT IMPLEMENTED YET";}

        return helpMenu;
    }
}
