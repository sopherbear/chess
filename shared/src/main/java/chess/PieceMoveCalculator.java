package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMoveCalculator {

    public PieceMoveCalculator(ChessBoard board, ChessPosition myPosition){
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return new BishopMovesCalculator(board, myPosition).pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return  new RookMovesCalculator(board, myPosition).pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return  new KnightMovesCalculator(board, myPosition).pieceMoves(board, myPosition);

        }  else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return  new KingMovesCalculator(board, myPosition).pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return  new QueenMovesCalculator(board, myPosition).pieceMoves(board, myPosition);

        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return  new PawnMovesCalculator(board, myPosition).pieceMoves(board, myPosition);

        }
        return List.of();
    }


    private class BishopMovesCalculator {

        private List<ChessMove> upLeftMoves = new ArrayList<>();
        private List<ChessMove> upRightMoves = new ArrayList<>();
        private List<ChessMove> downLeftMoves = new ArrayList<>();
        private List<ChessMove> downRightMoves = new ArrayList<>();
        private List<ChessMove> bishopMoves = new ArrayList<>();


        private BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            UpLeftSpaces(board, myPosition);
            return upLeftMoves;
        }

        private void UpLeftSpaces(ChessBoard board, ChessPosition myPosition) {
            addChessMove(upLeftMoves, new ChessMove(new ChessPosition(5,4), new ChessPosition(1, 8), null));
        }

        private void upRightSpaces(ChessBoard board, ChessPosition myPosition) {
        }

        private void downLeftSpaces(ChessBoard board, ChessPosition myPosition) {
        }

        private void downRightSpaces(ChessBoard board, ChessPosition myPosition) {
        }

        private void addChessMove(List<ChessMove> moveList, ChessMove move) {
            moveList.add(move);
        }

    }


    private class RookMovesCalculator {

        private RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of();
        }
    }

    private class KnightMovesCalculator {

        private KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of();
        }
    }

    private class KingMovesCalculator {

        private KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of();
        }
    }

    private class QueenMovesCalculator {

        private QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of();
        }
    }

    private class PawnMovesCalculator {

        private PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of();
        }
    }
}

