package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static ui.EscapeSequences.*;


public class ChessBoardVisual {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;

    private static final String EMPTY = "         ";


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawChessBoardVisual(out);
        drawColumnHeaders(out);

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawColumnHeaders(PrintStream out) {
        setBlack(out);
        String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h"};

        for (int col = 0; col < 8; ++col) {
            drawHeader(out, headers[col]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String header) {
        int prefixLength = 4;
        int suffixLength = 4;

        out.print(" ".repeat(prefixLength));
        printHeaderText(out, header);
        out.print(" ".repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String header) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(RESET_TEXT_COLOR);

        out.print(header);

        setBlack(out);
    }

    private static void drawChessBoardVisual(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            if (boardRow%2 == 0){
                drawBlackFirstLine(out,8 - boardRow);
            } else {
                drawWhiteFirstLine(out, 8 - boardRow);
            }

        }

    }

    private static void drawBlackFirstLine(PrintStream out, int num) {
        var rowNumSet = false;
        for (int rowSquare = 0; rowSquare < SQUARE_SIZE_IN_PADDED_CHARS; ++rowSquare) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if(col%2 == 0){
                    setPink(out);
                } else {
                    setTurquoise(out);
                }
                out.print(EMPTY);
                setBlack(out);
            }
            setBlack(out);
            if (!rowNumSet) {
                out.print(String.format(" %d", num));
                rowNumSet = true;
            }
            out.println();
        }

    }

    private static void drawWhiteFirstLine(PrintStream out, int num) {
        var rowNumSet = false;
        for (int rowSquare = 0; rowSquare < SQUARE_SIZE_IN_PADDED_CHARS; ++rowSquare) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if(col%2 == 0){
                    setTurquoise(out);
                } else {
                    setPink(out);
                }
                out.print(EMPTY);
                setBlack(out);
            }
            setBlack(out);
            if (!rowNumSet) {
                out.print(String.format(" %d", num));
                rowNumSet = true;
            }
            out.println();
        }

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
