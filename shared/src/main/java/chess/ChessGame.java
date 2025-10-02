package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public TeamColor teamTurn = TeamColor.WHITE;
    public ChessBoard gameBoard = new ChessBoard();


    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }


    // Sets the teamTurn to the opposite team
    public void otherTeamTurn(TeamColor team) {
        if (team == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    // Returns the color of the opposite team
    public TeamColor otherTeam(TeamColor team) {
        if (team == TeamColor.WHITE){
            return TeamColor.BLACK;
        } else {
           return TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var playingPiece = gameBoard.getPiece(startPosition);
        if (playingPiece == null) {
            return null;
        }

        Collection<ChessMove> legalMoves = new ArrayList<>();
        var possMoves = playingPiece.pieceMoves(gameBoard, startPosition);

        for (var move : possMoves) {
            var possBoard = gameBoard;
            var piecePos = move.getStartPosition();
            var piece = possBoard.getPiece(piecePos);

            gameBoard.removePiece(piecePos);
            gameBoard.addPiece(move.getEndPosition(), piece);

            if (!isInCheck(teamTurn, possBoard)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var piecePosition = move.getStartPosition();
        var piece = gameBoard.getPiece(piecePosition);
        if (piece == null){
            throw new InvalidMoveException("There is no piece there to move");
        }

        if (teamTurn != piece.getTeamColor()) {
            throw new InvalidMoveException("It is not your turn.");
        }

        var fineMoves = piece.pieceMoves(gameBoard, piecePosition);
        for (var fineMove: fineMoves){
            if (fineMove.equals(move)) {
                gameBoard.removePiece(piecePosition);
                if (move.getPromotionPiece() != null){
                    piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                }
                gameBoard.addPiece(move.getEndPosition(), piece);
                otherTeamTurn(teamTurn);
                return;
            }
        }
        throw new InvalidMoveException("That move is illegal.");
    }

    public Collection<ChessPosition> kingAndThreatsPos(ChessBoard board, TeamColor teamColor) {
        var relevantPos = new ArrayList<ChessPosition>();
        for (int row = 1; row <=8; row++){
            for (int col = 1; col <=8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    continue;
                } else if (piece.getTeamColor() == otherTeam(teamColor)) {
                    relevantPos.add(pos);
                } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    relevantPos.add(pos);
                }
            }
        }
        return relevantPos;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> posToCheck = kingAndThreatsPos(gameBoard, teamColor);
        ChessPosition kingPosition = null;
        Collection<Collection<ChessMove>> enemyMoves = new ArrayList<Collection<ChessMove>>();

        for (var pos: posToCheck) {
            var piece = gameBoard.getPiece(pos);
            if (piece.getTeamColor() == teamColor) {
                kingPosition = pos;
            } else {
                enemyMoves.add(piece.pieceMoves(gameBoard, pos));
            }
        }

        for (var moveSet: enemyMoves) {
            for (var move : moveSet) {
                if( move.getEndPosition() == kingPosition) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        Collection<ChessPosition> posToCheck = kingAndThreatsPos(board, teamColor);
        ChessPosition kingPosition = null;
        Collection<Collection<ChessMove>> enemyMoves = new ArrayList<Collection<ChessMove>>();

        for (var pos: posToCheck) {
            var piece = board.getPiece(pos);
            if (piece.getTeamColor() == teamColor) {
                kingPosition = pos;
            } else {
                enemyMoves.add(piece.pieceMoves(gameBoard, pos));
            }
        }

        for (var moveSet: enemyMoves) {
            for (var move : moveSet) {
                if( move.getEndPosition() == kingPosition) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (this == o){
            return true;
        }

        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }
}
