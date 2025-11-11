package ui;

import exception.ResponseException;
import facade.ServerFacade;
import clientstate.State;
import model.LoginRequest;
import model.RegisterRequest;

import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Scanner;


public class PreLoginClient {
    private final ServerFacade server;
    private State state = State.PRELOGIN;
    // TODO: make sure to add in state

    public PreLoginClient(String serverUrl) throws ResponseException {
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
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException{
        if (params.length == 2) {
            var loginRequest = new LoginRequest(params[0], params[1]);
            server.login(loginRequest);
            state = State.POSTLOGIN;
            return String.format("Welcome back, %s!\n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException{
        if (params.length == 3) {
            var registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            server.register(registerRequest);
            state = State.POSTLOGIN;
            return String.format("Welcome, %s. Your account has been created. \n", params[0]);

            //TODO: handle the authToken
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String help() {
        String helpMenu =
                RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - register a new user\n"
                + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - log in to existing account\n"
                + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "quit" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - exit chess\n"
                + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "help" + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - list possible actions\n";

                return helpMenu;
    }

    public State getState() {
        return state;
    }
}
