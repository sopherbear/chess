import clientstate.State;
import ui.Client;

import static ui.EscapeSequences.WHITE_BISHOP;

public class Main {

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");
        System.out.println();

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new Client(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}