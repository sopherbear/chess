package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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

    public void drawBoardVisual() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        if (player == ChessGame.TeamColor.BLACK) {
            drawBlackBoardVisual(out);
        } else {
            drawWhiteBoardVisual(out);
        }

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

    private void drawWhiteBoardVisual(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            if (boardRow%2 == 0){
                drawBlackFirstLine(out,7 - boardRow);
            } else {
                drawWhiteFirstLine(out, 7 - boardRow);
            }

        }

    }

    private void drawBlackBoardVisual(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            if (boardRow%2 == 0){
                drawWhiteFirstLine(out,boardRow);
            } else {
                drawBlackFirstLine(out,boardRow);
            }

        }

    }

    private void drawBlackFirstLine(PrintStream out, int num) {
        for (int rowSquare = 0; rowSquare < SQUARE_SIZE_IN_PADDED_CHARS; ++rowSquare) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if(col%2 == 0){
                    setPink(out);
                    if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2){
                        out.print(RESET_TEXT_COLOR);
                        out.print(determinePiece(num, col, out));
                    } else {
                        out.print(EMPTY);
                    }
                } else {
                    setTurquoise(out);
                    if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2){
                        out.print(RESET_TEXT_COLOR);
                        out.print(determinePiece(num, col, out));
                    } else {
                        out.print(EMPTY);
                    }
                }
                setBlack(out);
            }
            setBlack(out);
            if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2) {
                out.print(String.format(" %d", num+1));
            }
            out.println();
        }

    }

    private void drawWhiteFirstLine(PrintStream out, int num) {
        for (int rowSquare = 0; rowSquare < SQUARE_SIZE_IN_PADDED_CHARS; ++rowSquare) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if(col%2 == 0){
                    setTurquoise(out);
                    if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2){
                        out.print(RESET_TEXT_COLOR);
                        out.print(determinePiece(num, col, out));
                    } else {
                        out.print(EMPTY);
                    }
                } else {
                    setPink(out);
                    if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2){
                        out.print(RESET_TEXT_COLOR);
                        out.print(determinePiece(num, col, out));
                    } else {
                        out.print(EMPTY);
                    }
                }
                setBlack(out);
            }
            setBlack(out);
            if (rowSquare == SQUARE_SIZE_IN_PADDED_CHARS/2) {
                out.print(String.format(" %d", num+1));
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
            piece = game.getPiece(new ChessPosition(8-row+1, 8-col+1));
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

}
