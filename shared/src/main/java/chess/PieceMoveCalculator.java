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
            upLeftSpaces(board, myPosition);
            upRightSpaces(board, myPosition);
            downLeftSpaces(board, myPosition);
            downRightSpaces(board, myPosition);

            addValidMoves(bishopMoves, upLeftMoves, board, myPosition);
            addValidMoves(bishopMoves, upRightMoves, board, myPosition);
            addValidMoves(bishopMoves, downLeftMoves, board, myPosition);
            addValidMoves(bishopMoves, downRightMoves, board, myPosition);
            return bishopMoves;
        }

        private void upLeftSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while ((row > 1 && row < 8 )&& (col > 1 && col < 8)) {
                row +=1;
                col -=1;
                addChessMove(upLeftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void upRightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while ((row > 1 && row < 8 )&& (col > 1 && col < 8)) {
                row +=1;
                col +=1;
                addChessMove(upRightMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downLeftSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while ((row > 1 && row < 8 )&& (col > 1 && col < 8)) {
                row -=1;
                col -=1;
                addChessMove(downLeftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downRightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while ((row > 1 && row < 8 )&& (col > 1 && col < 8)) {
                row -=1;
                col +=1;
                addChessMove(downRightMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void addChessMove(List<ChessMove> moveList, ChessMove move) {
            moveList.add(move);
        }

        private void addValidMoves(List<ChessMove> validMovesList, List<ChessMove> possibleMovesList, ChessBoard board, ChessPosition myPosition) {
            for (ChessMove move : possibleMovesList) {
                ChessPiece myPiece = board.getPiece(myPosition);
                ChessPiece blockingPiece = board.getPiece(move.getEndPosition());
                if (blockingPiece != null) {
                    ChessGame.TeamColor myColor = myPiece.getTeamColor();
                    ChessGame.TeamColor blockingColor = blockingPiece.getTeamColor();
                    if (!blockingColor.equals(myColor)) {
                        addChessMove(validMovesList, move);
                    }
                    break;
                }
                addChessMove(validMovesList, move);
            }
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

