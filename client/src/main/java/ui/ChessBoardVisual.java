package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.awt.Point;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;


public class ChessBoardVisual {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private ChessBoard game;
    private ChessGame.TeamColor player;

    private static final String EMPTY = "         ";
    private static final String KINGVISUAL = "    K    ";
    private static final String QUEENVISUAL = "    Q    ";
    private static final String ROOKVISUAL = "    R    ";
    private static final String KNIGHTVISUAL = "    N    ";
    private static final String BISHOPVISUAL = "    B    ";
    private static final String PAWNVISUAL = "    P    ";

    public ChessBoardVisual(ChessBoard game, ChessGame.TeamColor player) {
        this.game = game;
        this.player = player;
    }

    public void getBoardVisual(ChessGame game, ChessPosition pos) {

        Collection<Point> hiliteCoords = null;
        if (game != null && pos != null) {
            Collection<ChessMove> moves = game.validMoves(pos);
             hiliteCoords = convertMovesToCoords(moves);
        }
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawBoardVisual(out, hiliteCoords);

        drawColumnHeaders(out);

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    private void drawColumnHeaders(PrintStream out) {
        setBlack(out);
        String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h"};

        for (int col = 0; col < 8; ++col) {
            if (player == ChessGame.TeamColor.BLACK) {
                drawHeader(out, headers[7-col]);
            } else {
                drawHeader(out, headers[col]);
            }
        }

        out.println();
    }

    private void drawHeader(PrintStream out, String header) {
        int prefixLength = 4;
        int suffixLength = 4;

        out.print(" ".repeat(prefixLength));
        printHeaderText(out, header);
        out.print(" ".repeat(suffixLength));
    }

    private void printHeaderText(PrintStream out, String header) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(RESET_TEXT_COLOR);

        out.print(header);

        setBlack(out);
    }

    private void drawBoardVisual(PrintStream out, Collection<Point> hiliteCoords) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            if (boardRow%2 == 0){
                drawBlackFirstLine(out,7 - boardRow, hiliteCoords);
            } else {
                drawWhiteFirstLine(out, 7 - boardRow, hiliteCoords);
            }

        }

    }


    private void handleCellPrinting(PrintStream out, int rowSquare, int row, int col) {
        if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2){
            out.print(RESET_TEXT_COLOR);
            out.print(determinePiece(row, col, out));
        } else {
            out.print(EMPTY);
        }
    }

    private void drawWhiteFirstLine(PrintStream out, int num, Collection<Point> hiliteCoords) {
        for (int rowSquare = 0; rowSquare < SQUARE_SIZE_IN_PADDED_CHARS; ++rowSquare) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if(col%2 == 0){
                    setPink(out);
                    setBlueIfHilite(out, hiliteCoords, num, col);
                    handleCellPrinting(out, rowSquare, num, col);
                } else {
                    setTurquoise(out);
                    setBlueIfHilite(out, hiliteCoords, num, col);
                    handleCellPrinting(out, rowSquare, num, col);
                }
                setBlack(out);
            }
            setBlack(out);
            if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2) {
                if (player == ChessGame.TeamColor.BLACK) {
                    out.print(String.format(" %d", 8 - num));
                } else {
                    out.print(String.format(" %d", num+1));
                }
            }
            out.println();
        }

    }

    private void drawBlackFirstLine(PrintStream out, int num, Collection<Point> hiliteCoords) {
        for (int rowSquare = 0; rowSquare < SQUARE_SIZE_IN_PADDED_CHARS; ++rowSquare) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if(col%2 == 0){
                    setTurquoise(out);
                    setBlueIfHilite(out, hiliteCoords, num, col);
                    handleCellPrinting(out, rowSquare, num, col);
                } else {
                    setPink(out);
                    setBlueIfHilite(out, hiliteCoords, num, col);
                    handleCellPrinting(out, rowSquare, num, col);
                }
                setBlack(out);
            }
            setBlack(out);
            if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2) {
                if (player == ChessGame.TeamColor.WHITE) {
                    out.print(String.format(" %d", num+1));
                } else {
                    out.print(String.format(" %d", 8 - num));
                }

            }
            out.println();
        }

    }

    private String determinePiece(int row, int col, PrintStream out) {
        ChessPiece piece;
        if (player == ChessGame.TeamColor.WHITE) {
            piece = game.getPiece(new ChessPosition(row+1, col+1));

        }
        else {
            piece = game.getPiece(new ChessPosition(8-row, 8-col));
        }

        if (piece == null) {
            return EMPTY;
        }
        ChessPiece.PieceType pieceType = piece.getPieceType();

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            out.print(RESET_TEXT_COLOR);
        } else {
            out.print(SET_TEXT_COLOR_BLACK);
        }

        return switch (pieceType) {
            case KING -> KINGVISUAL;
            case QUEEN -> QUEENVISUAL;
            case BISHOP -> BISHOPVISUAL;
            case KNIGHT -> KNIGHTVISUAL;
            case ROOK -> ROOKVISUAL;
            case PAWN -> PAWNVISUAL;
        };
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(RESET_TEXT_COLOR);
    }

    private static void setPink(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private static void setTurquoise(PrintStream out) {
        out.print(SET_BG_COLOR_TURQUOISE);
        out.print(SET_TEXT_COLOR_TURQUOISE);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private Collection<Point> convertMovesToCoords(Collection<ChessMove> moves) {
        Collection<Point> moveCoords = new ArrayList<>();
        for (ChessMove move:moves){
            var hilite = move.getEndPosition();
            moveCoords.add(new Point(hilite.getRow()-1, hilite.getColumn()-1));
        }
        return moveCoords;
    }

    private void setBlueIfHilite(PrintStream out, Collection<Point> hilites, int row, int col) {
        if (hilites != null) {
            if (player== ChessGame.TeamColor.BLACK) {
                row = 7 - row;
                col = 7 - col;
            }
            if (hilites.contains(new Point(row, col))) {
                setBlue(out);
            }
        }
    }

}
