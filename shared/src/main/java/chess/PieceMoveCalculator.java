package chess;

import java.util.Collection;
import java.util.List;

public class PieceMoveCalculator {

    public PieceMoveCalculator(ChessBoard board, ChessPosition myPosition){
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            Collection<ChessMove> bishopMoves =  new BishopMovesCalculator(board, myPosition).pieceMoves(board, myPosition);
            return bishopMoves;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return List.of();
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return List.of();
        }  else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return List.of();
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return List.of();
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return List.of();
        }
        return List.of();
    }


    private class BishopMovesCalculator {

        private BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of(new ChessMove(new ChessPosition(5, 4), new ChessPosition(1, 8), null));
        }
    }
}

