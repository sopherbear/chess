package chess;

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


    private static class BishopMovesCalculator {

        private final List<ChessMove> upLeftMoves = new ArrayList<>();
        private final List<ChessMove> upRightMoves = new ArrayList<>();
        private final List<ChessMove> downLeftMoves = new ArrayList<>();
        private final List<ChessMove> downRightMoves = new ArrayList<>();
        private final List<ChessMove> bishopMoves = new ArrayList<>();


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
            while (row < 8 && col > 1) {
                row +=1;
                col -=1;
                addChessMove(upLeftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void upRightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row < 8 && col < 8) {
                row +=1;
                col +=1;
                addChessMove(upRightMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downLeftSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row > 1 && col > 1) {
                row -=1;
                col -=1;
                addChessMove(downLeftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downRightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row > 1 && col < 8) {
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


    private static class RookMovesCalculator {
        private final List<ChessMove> upMoves = new ArrayList<>();
        private final List<ChessMove> downMoves = new ArrayList<>();
        private final List<ChessMove> leftMoves = new ArrayList<>();
        private final List<ChessMove> rightMoves = new ArrayList<>();
        private final List<ChessMove> rookMoves = new ArrayList<>();

        private RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            upSpaces(board, myPosition);
            downSpaces(board, myPosition);
            leftSpaces(board, myPosition);
            rightSpaces(board, myPosition);

            addValidMoves(rookMoves, upMoves, board, myPosition);
            addValidMoves(rookMoves, downMoves, board, myPosition);
            addValidMoves(rookMoves, leftMoves, board, myPosition);
            addValidMoves(rookMoves, rightMoves, board, myPosition);
            return rookMoves;
        }

        private void upSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row < 8 ) {
                row +=1;
                addChessMove(upMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row > 1) {
                row -=1;
                addChessMove(downMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void leftSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (col > 1) {
                col -=1;
                addChessMove(leftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void rightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (col < 8) {
                col +=1;
                addChessMove(rightMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
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

    private static class KnightMovesCalculator {

        private KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of();
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

    private static class KingMovesCalculator {
        private final List<ChessMove> potentialMoves = new ArrayList<>();
        private final List<ChessMove> validMoves = new ArrayList<>();

        private KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            addPotentialMoves(board, myPosition);
            addValidMoves(validMoves, potentialMoves, board, myPosition);
            return validMoves;
        }

        private void addPotentialMoves(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            if (row < 8 ) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row+1, col), null));
            }
            if (row > 1) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row-1, col), null));
            }
            if (col > 1) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row, col-1), null));
            }
            if (col < 8) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row, col+1), null));
            }
            if (row < 8 && col > 1) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row+1, col-1), null));
            }
            if (row > 1 && col > 1) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row-1, col-1), null));
            }
            if (row < 8 && col < 8) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row+1, col+1), null));
            }
            if (row > 1 && col < 8) {
                addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row-1, col+1), null));
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
                } else {
                    addChessMove(validMovesList, move);
                }
            }
        }
    }

    private static class QueenMovesCalculator {
        private final List<ChessMove> upMoves = new ArrayList<>();
        private final List<ChessMove> downMoves = new ArrayList<>();
        private final List<ChessMove> leftMoves = new ArrayList<>();
        private final List<ChessMove> rightMoves = new ArrayList<>();
        private final List<ChessMove> upLeftMoves = new ArrayList<>();
        private final List<ChessMove> upRightMoves = new ArrayList<>();
        private final List<ChessMove> downLeftMoves = new ArrayList<>();
        private final List<ChessMove> downRightMoves = new ArrayList<>();
        private final List<ChessMove> queenMoves = new ArrayList<>();
        private QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

            upLeftSpaces(board, myPosition);
            upRightSpaces(board, myPosition);
            downLeftSpaces(board, myPosition);
            downRightSpaces(board, myPosition);
            upSpaces(board, myPosition);
            downSpaces(board, myPosition);
            leftSpaces(board, myPosition);
            rightSpaces(board, myPosition);

            addValidMoves(queenMoves, upLeftMoves, board, myPosition);
            addValidMoves(queenMoves, upRightMoves, board, myPosition);
            addValidMoves(queenMoves, downLeftMoves, board, myPosition);
            addValidMoves(queenMoves, downRightMoves, board, myPosition);
            addValidMoves(queenMoves, upMoves, board, myPosition);
            addValidMoves(queenMoves, downMoves, board, myPosition);
            addValidMoves(queenMoves, leftMoves, board, myPosition);
            addValidMoves(queenMoves, rightMoves, board, myPosition);
            return queenMoves;
        }

        private void upSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row < 8 ) {
                row +=1;
                addChessMove(upMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row > 1) {
                row -=1;
                addChessMove(downMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void leftSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (col > 1) {
                col -=1;
                addChessMove(leftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void rightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (col < 8) {
                col +=1;
                addChessMove(rightMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void upLeftSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row < 8 && col > 1) {
                row +=1;
                col -=1;
                addChessMove(upLeftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void upRightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row < 8 && col < 8) {
                row +=1;
                col +=1;
                addChessMove(upRightMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downLeftSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row > 1 && col > 1) {
                row -=1;
                col -=1;
                addChessMove(downLeftMoves, new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }

        private void downRightSpaces(ChessBoard board, ChessPosition myPosition) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (row > 1 && col < 8) {
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

    private static class PawnMovesCalculator {

        private PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {}

        private Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            return List.of();
        }
    }
}

