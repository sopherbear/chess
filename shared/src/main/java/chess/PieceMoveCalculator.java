package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.PieceType.*;

public class PieceMoveCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;

    public PieceMoveCalculator(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        Collection<ChessMove> validMoves = switch(piece.getPieceType()) {
            case BISHOP -> new BishopMovesCalculator(board, myPosition).pieceMoves();
            case ROOK -> new RookMovesCalculator(board, myPosition).pieceMoves();
            case KNIGHT -> new KnightMovesCalculator(board, myPosition).pieceMoves();
            case QUEEN -> new QueenMovesCalculator(board, myPosition).pieceMoves();
            case KING -> new KingMovesCalculator(board, myPosition).pieceMoves();
            case PAWN -> new PawnMovesCalculator(board, myPosition).pieceMoves();
        };
        return validMoves;
    }

    // Functions used by multiple chess pieces
    private void addChessMove(List<ChessMove> moveList, ChessMove move) {
        moveList.add(move);
    }

    private void upLeftSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (row < 8 && col > 1) {
            row +=1;
            col -=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void upRightSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (row < 8 && col < 8) {
            row +=1;
            col +=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void downLeftSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (row > 1 && col > 1) {
            row -=1;
            col -=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void downRightSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (row > 1 && col < 8) {
            row -=1;
            col +=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void upSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (row < 8 ) {
            row +=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void downSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (row > 1) {
            row -=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void leftSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (col > 1) {
            col -=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void rightSpaces(List<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (col < 8) {
            col +=1;
            var moveClear = addValidMoveSequence(moves, new ChessMove(myPosition, new ChessPosition(row, col), null), board);
            if (!moveClear) {
                break;
            }
        }
    }

    private void addValidMove(List<ChessMove> validMovesList, ChessMove possibleMove, ChessBoard board) {
        ChessPiece myPiece = board.getPiece(possibleMove.getStartPosition());
        ChessPiece blockingPiece = board.getPiece(possibleMove.getEndPosition());
        if (blockingPiece != null) {
            ChessGame.TeamColor myColor = myPiece.getTeamColor();
            ChessGame.TeamColor blockingColor = blockingPiece.getTeamColor();
            if (!blockingColor.equals(myColor)) {
                addChessMove(validMovesList, possibleMove);
            }
        } else {
            addChessMove(validMovesList, possibleMove);
        }
    }

    private boolean addValidMoveSequence(List<ChessMove> validMovesList, ChessMove possibleMove, ChessBoard board) {
        ChessPiece myPiece = board.getPiece(possibleMove.getStartPosition());
        ChessPiece blockingPiece = board.getPiece(possibleMove.getEndPosition());
        if (blockingPiece != null) {
            ChessGame.TeamColor myColor = myPiece.getTeamColor();
            ChessGame.TeamColor blockingColor = blockingPiece.getTeamColor();
            if (!blockingColor.equals(myColor)) {
                addChessMove(validMovesList, possibleMove);
                return false;
            }
            return false;
        }
        addChessMove(validMovesList, possibleMove);
        return true;
    }

    //BISHOPMOVESCALCULATOR
    private class BishopMovesCalculator{
        private final List<ChessMove> bishopMoves = new ArrayList<>();


        private BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        }

        private Collection<ChessMove> pieceMoves() {
            upLeftSpaces(bishopMoves);
            upRightSpaces(bishopMoves);
            downLeftSpaces(bishopMoves);
            downRightSpaces(bishopMoves);

            return bishopMoves;
        }

    }


    // ROOKMOVESCALCULATOR
    private class RookMovesCalculator {
        private final List<ChessMove> rookMoves = new ArrayList<>();

        private RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        }

        private Collection<ChessMove> pieceMoves() {
            upSpaces(rookMoves);
            downSpaces(rookMoves);
            leftSpaces(rookMoves);
            rightSpaces(rookMoves);

            return rookMoves;
        }
    }


    // KNIGHTMOVESCALCULATOR
    private class KnightMovesCalculator {
        private final List<ChessMove> validMoves = new ArrayList<>();

        private KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        }

        private Collection<ChessMove> pieceMoves() {
            addKnightMoves();
            return validMoves;
        }

        private void addKnightMoves(){
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            // 2 above current position
            if (row < 7) {
                if (col > 1) {
                    addValidMove(validMoves, new ChessMove(myPosition, new ChessPosition(row+2, col-1), null), board);
                }
                if (col < 8) {
                    addValidMove(validMoves, new ChessMove(myPosition, new ChessPosition(row+2, col+1), null), board);
                }
            }

            // 2 below current position
            if (row > 2) {
                if (col > 1) {
                    addValidMove(validMoves, new ChessMove(myPosition, new ChessPosition(row-2, col-1), null), board);
                }
                if (col < 8) {
                    addValidMove(validMoves, new ChessMove(myPosition, new ChessPosition(row-2, col+1), null), board);
                }
            }

            // 2 right of current position
            if (col < 7) {
                if (row > 1) {
                    addValidMove(validMoves,new ChessMove(myPosition, new ChessPosition(row-1, col+2), null), board);
                }
                if (row < 8) {
                    addValidMove(validMoves,new ChessMove(myPosition, new ChessPosition(row+1, col+2), null), board);
                }
            }

            // 2 left of current position
            if (col > 2) {
                if (row > 1) {
                    addValidMove(validMoves,new ChessMove(myPosition, new ChessPosition(row-1, col-2), null), board);
                }
                if (row < 8) {
                    addValidMove(validMoves,new ChessMove(myPosition, new ChessPosition(row+1, col-2), null), board);
                }
            }
        }
    }


    // KINGMOVESCALCULATOR
    private class KingMovesCalculator {
        private final List<ChessMove> kingMoves = new ArrayList<>();

        private KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        }

        private Collection<ChessMove> pieceMoves() {
            addKingMoves(kingMoves);
            return kingMoves;
        }

        private void addKingMoves(List<ChessMove> moves) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            if (row < 8 ) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row+1, col), null), board);
            }
            if (row > 1) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row-1, col), null), board);
            }
            if (col > 1) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row, col-1), null), board);
            }
            if (col < 8) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row, col+1), null), board);
            }
            if (row < 8 && col > 1) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row+1, col-1), null), board);
            }
            if (row > 1 && col > 1) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row-1, col-1), null), board);
            }
            if (row < 8 && col < 8) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row+1, col+1), null), board);
            }
            if (row > 1 && col < 8) {
                addValidMove(moves, new ChessMove(myPosition, new ChessPosition(row-1, col+1), null), board);
            }
        }
    }


    // QUEENMOVESCALCULATOR
    private class QueenMovesCalculator {
        private final List<ChessMove> queenMoves = new ArrayList<>();

        private QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        }

        private Collection<ChessMove> pieceMoves() {

            upLeftSpaces(queenMoves);
            upRightSpaces(queenMoves);
            downLeftSpaces(queenMoves);
            downRightSpaces(queenMoves);
            upSpaces(queenMoves);
            downSpaces(queenMoves);
            leftSpaces(queenMoves);
            rightSpaces(queenMoves);

            return queenMoves;
        }
    }


    // PAWNMOVESCALCULATOR
    private class PawnMovesCalculator {
        private final List<ChessMove> potentialMoves = new ArrayList<>();
        private final List<ChessMove> validMoves = new ArrayList<>();

        private PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        }

        private Collection<ChessMove> pieceMoves() {
            addPotentialMoves();
            addValidMoves(validMoves, potentialMoves);
            return validMoves;
        }

        private void addPotentialMoves() {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            ChessPiece myPiece = board.getPiece(myPosition);
            ChessGame.TeamColor myColor = myPiece.getTeamColor();

            // potential moves for white pawns
            if (myColor == ChessGame.TeamColor.WHITE) {
                if (row == 2) {
                    addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row+2, col), null));
                }
                if (row < 7) {
                        addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row+1, col), null));
                    if (col > 1) {
                        addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row+1, col-1), null));
                    }
                    if (col < 8) {
                        addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row+1, col+1), null));
                    }
                } else if (row == 7) {
                    addPromotionMove(potentialMoves,new ChessPosition(row+1, col));

                    if (col > 1) {
                        addPromotionMove(potentialMoves,new ChessPosition(row+1, col-1));
                    }
                    if (col < 8) {
                        addPromotionMove(potentialMoves,new ChessPosition(row+1, col+1));
                    }
                }
            } else if (myColor == ChessGame.TeamColor.BLACK) { // potential moves for black pawns
                if (row == 7) {
                    addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row-2, col), null));
                }
                if (row > 2) {
                    addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row-1, col), null));
                    if (col > 1) {
                        addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row-1, col-1), null));
                    }
                    if (col < 8) {
                        addChessMove(potentialMoves, new ChessMove(myPosition, new ChessPosition(row-1, col+1), null));
                    }
                } else if (row == 2) {
                    addPromotionMove(potentialMoves,new ChessPosition(row-1, col));
                    if (col > 1) {
                        addPromotionMove(potentialMoves,new ChessPosition(row-1, col-1));
                    }
                    if (col < 8) {
                        addPromotionMove(potentialMoves,new ChessPosition(row-1, col+1));
                    }
                }
            }
        }

        private void addValidMoves(List<ChessMove> validMovesList, List<ChessMove> possibleMovesList) {
            for (ChessMove move : possibleMovesList) {
                ChessPiece myPiece = board.getPiece(myPosition);
                int myCol = myPosition.getColumn();
                ChessPiece blockingPiece = board.getPiece(move.getEndPosition());
                // Check for pieces blocking initial 2 square move
                if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) {
                    if (move.getEndPosition().getRow() == 4) {
                        ChessPosition middlePosition = new ChessPosition(3, myCol);
                        ChessPiece middleBlockingPiece = board.getPiece(middlePosition);
                        if (middleBlockingPiece != null) {
                            continue;
                        }
                    }
                } else if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) {
                    if (move.getEndPosition().getRow() == 5) {
                        ChessPosition middlePosition = new ChessPosition(6, myCol);
                        ChessPiece middleBlockingPiece = board.getPiece(middlePosition);
                        if (middleBlockingPiece != null) {
                            continue;
                        }
                    }
                }
                if (blockingPiece == null) {
                    if (move.getEndPosition().getColumn() == myCol) {
                        addChessMove(validMovesList, move);
                    }
                } else {
                    if (move.getEndPosition().getColumn() != myCol){
                        ChessGame.TeamColor myColor = myPiece.getTeamColor();
                        ChessGame.TeamColor blockingColor = blockingPiece.getTeamColor();
                        if (!blockingColor.equals(myColor)) {
                            addChessMove(validMovesList, move);
                        }
                    }
                }
            }
        }

        private void addPromotionMove(List<ChessMove> moves, ChessPosition endPosition){
            addChessMove(moves, new ChessMove(myPosition, endPosition, ROOK));
            addChessMove(moves, new ChessMove(myPosition, endPosition, BISHOP));
            addChessMove(moves, new ChessMove(myPosition, endPosition, KNIGHT));
            addChessMove(moves, new ChessMove(myPosition, endPosition, QUEEN));
        }
    }
}

