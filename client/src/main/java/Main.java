import chess.*;
import clientstate.State;
import ui.PreLoginClient;

import static ui.EscapeSequences.WHITE_BISHOP;

public class Main {
    private State state = State.PRELOGIN;

    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + WHITE_BISHOP);
        System.out.println();

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new PreLoginClient(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}