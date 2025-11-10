package ui;

import exception.ResponseException;
import facade.ServerFacade;
import clientstate.State;

import java.util.Scanner;

public class PreLoginClient {
    private final ServerFacade server;
    private State state = State.PRELOGIN;
    // TODO: make sure to add in state

    public PreLoginClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Sophie's Chess Program! Enter \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);

    }
}
