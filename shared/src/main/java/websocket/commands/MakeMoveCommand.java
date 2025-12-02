package websocket.commands;

import chess.ChessMove;


public class MakeMoveCommand extends UserGameCommand{

    ChessMove chessMove;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.chessMove = move;


    }

    public ChessMove getMove(){return chessMove;}
}
