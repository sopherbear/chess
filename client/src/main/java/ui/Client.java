package ui;

import chess.*;
import exception.ResponseException;
import facade.ServerFacade;
import clientstate.State;
import model.*;

import static java.lang.Character.isLetter;
import static ui.EscapeSequences.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import websocket.ServerMessageObserver;
import websocket.WebsocketCommunicator;
import websocket.messages.*;


public class Client implements ServerMessageObserver {
    private final ServerFacade server;
    private final WebsocketCommunicator websocketCommunicator;
    private State state = State.PRELOGIN;
    private String authToken = null;
    private Map<Integer, Integer> currGames = new HashMap<>();
    private ChessGame.TeamColor playerColor = null;
    private Integer myGameId = null;
    private ChessGame myGame = null;

    public Client(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        websocketCommunicator = new WebsocketCommunicator(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_CYAN + RESET_TEXT_ITALIC + "Welcome to Sophie's Chess Program!\n");
        System.out.print(RESET_TEXT_COLOR + help());

        Scanner scanner = new Scanner(System.in);

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_NEON_PURPLE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        if (state != State.PRELOGIN) {
            try{server.logout(authToken);}
            catch(ResponseException e){}
        }
        System.out.println();

    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.PRELOGIN) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            }
            // TODO: turn this into else if state = POSTLOGIN once gameplay is introduced
            if (state == State.POSTLOGIN)
            {
                return switch (cmd) {
                    case "create" -> create(params);
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    case "logout" -> logout();
                    case "quit" -> "quit";
                    default -> help();
                };
            }
            else {
                return switch (cmd) {
                    case "redraw" -> redrawBoard();
                    case "leave" -> leaveGame();
                    case "move" -> makeMove(params);
                    case "resign" -> resignGame();
                    case "highlight" -> highlightValidMoves(params);
                    default -> help();
                };
            }

        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException{
        if (params.length == 2) {
            var loginRequest = new LoginRequest(params[0], params[1]);
            var authData = server.login(loginRequest);
            authToken = authData.authToken();
            state = State.POSTLOGIN;
            return String.format("Welcome back, %s!\n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <USERNAME> <PASSWORD>\n");
    }

    public String register(String... params) throws ResponseException{
        if (params.length == 3) {
            var registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            var authData = server.register(registerRequest);
            authToken = authData.authToken();
            state = State.POSTLOGIN;
            return String.format("Welcome, %s. Your account has been created. \n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <USERNAME> <PASSWORD> <EMAIL>\n");
    }

    public String logout() throws ResponseException{
        server.logout(authToken);
        authToken = null;
        state = State.PRELOGIN;
        return String.format("You have been logged out.\n");
    }

    public String create(String... params) throws ResponseException{
        if (params.length == 1) {
            var gameName = new GameName(params[0]);
            server.createGame(authToken, gameName);
            //TODO: Store gameID somewhere
            return String.format("Your game %s has been created!\n", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMENAME>\n");
    }

    public String listGames() throws ResponseException {
        GamesList gameDatas = server.listGames(authToken);
        var games = gameDatas.getGames();

        if (games.size() == 0) {
            return String.format("No chess games have been created yet. Run 'create <GAMENAME>' to make one!\n");
        }

        String gameText = "";
        int gameNum = 0;
        Map<Integer, Integer> updatedGames = new HashMap<>();

        for (GameData game : games) {
            gameNum += 1;

            String white = (game.whiteUsername() == null) ? "available" : game.whiteUsername();
            String black = (game.blackUsername() == null) ? "available" : game.blackUsername();
            var gameName = game.gameName();
            String gameString = String.format("GAMENUMBER: %d\t GAMENAME: %s\t WHITE: %s\t BLACK: %s\n", gameNum, gameName, white, black);
            gameText += gameString;
            updatedGames.put(gameNum, game.gameID());
        }

        currGames = updatedGames;
        return gameText;
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            int gameNum;
            int gameId;

            try {
                gameNum = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMENUMBER> to be a number\n");
            }
            try {
                gameId = currGames.get(gameNum);
                myGameId =  gameId;
            } catch(NullPointerException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game not found. Run 'list' command to see available games.\n");
            }

            server.joinGame(authToken, new GameRequest(gameId, params[1].toUpperCase()));
            playerColor = params[1].equals("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            websocketCommunicator.connect(authToken, gameId);

            state = State.PLAYBALL;
            return String.format("Joining game\n");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMENUMBER> <PLAYERCOLOR> (WHITE or BLACK)\n");
    }

    public String observeGame(String... params) throws ResponseException{
        if (params.length == 1) {
            int gameNum;
            int gameId;
            try {
                gameNum = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMENUMBER> to be a number\n");
            }

            try {
                gameId = currGames.get(gameNum);
                myGameId =  gameId;
            } catch(NullPointerException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Game not found. Run 'list' command to see available games.\n");
            }
            state = State.PLAYBALL;
            websocketCommunicator.connect(authToken, gameId);
            return String.format("Successfully observing game.");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <GAMENUMBER>\n");
    }

    public String redrawBoard() throws ResponseException{
        websocketCommunicator.getBoard(authToken, myGameId);
        return String.format("Redrawing board...\n");
    }

    public String leaveGame() throws ResponseException{
        websocketCommunicator.leave(authToken, myGameId);
        myGameId = null;
        playerColor = null;
        currGames = new HashMap<>();
        myGame = null;
        state = State.POSTLOGIN;

        return String.format("Left game.\n");
    }

    public String makeMove(String... params) throws ResponseException{
        if (params.length  == 3 && params[0].length() == 2 && params[1].length() == 2) {
            String start = params[0].toLowerCase();
            String end = params[1].toLowerCase();
            String promoPiece = params[2].toUpperCase();

            var startPosition = convertToChessPosition(start);
            var endPosition = convertToChessPosition(end);

            ChessPiece.PieceType promoType = verifyPieceType(promoPiece);
            var chessMove = new ChessMove(startPosition, endPosition, promoType);
            websocketCommunicator.makeMove(authToken, myGameId, chessMove);
            return String.format("Assessing move...\n");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <Start Position> <End Position> <Promotion Piece>\n");
    }

    public String resignGame() throws ResponseException{
        websocketCommunicator.resign(authToken, myGameId);
        return String.format("Processing resignation...\n");
    }

    public String highlightValidMoves(String... params) throws ResponseException{
        if (playerColor == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: Observer cannot make moves\n");
        }
        if (params.length == 1) {
            ChessPosition pos = convertToChessPosition(params[0]);
            ChessPiece piece = myGame.getBoard().getPiece(pos);
            if (piece.getTeamColor() != playerColor) {
                throw new ResponseException(ResponseException.Code.ClientError, "Error: you don't have a piece there");
            }
            drawHighlights(myGame, pos);
            return String.format("Valid moves highlighted in orange.\n");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected <Piece Coordinates>\n");
    }


    public String help() {
        String helpMenu;
        if (state == State.PRELOGIN) {
            helpMenu =
                    RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "register <USERNAME> <PASSWORD> <EMAIL>"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - register a new user\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "login <USERNAME> <PASSWORD>"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - log in to existing account\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "quit"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - exit chess\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "help"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - list possible actions\n";

        }
        else if (state == State.POSTLOGIN) {
            helpMenu =
                    RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "create <GAMENAME>"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - create a new game and give it a name\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "list"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - list current chess games and their numbers\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "join <GAMENUMBER> [WHITE|BLACK]"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - join an existing game as a particular color\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "observe <GAMENUMBER>"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - watch a particular game\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "logout"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - log out of chess\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "quit"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - exit chess console\n"
                            + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "help"
                            + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - list possible actions\n";
        }
        else {
            helpMenu = RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "redraw"
                    + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - redraws the chessboard\n"
                    + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "leave"
                    + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - leave the game at any point\n"
                    + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "move <Start Position> <End Position> <Promotion Piece>"
                    + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC
                    + " - make a chess move (e.g. b3 c4).\n - If promoting a pawn, put the piece name (e.g. rook). If not, type none\n"
                    + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "resign"
                    + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - forfeit the game\n"
                    + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "highlight <piece coordinates>"
                    + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - enter coordinates of a piece (e.g. b3) to highlight moves in orange\n"
                    + RESET_TEXT_ITALIC + RESET_TEXT_COLOR + "help"
                    + SET_TEXT_COLOR_NEON_PURPLE + SET_TEXT_ITALIC + " - list possible actions\n";
        }

        return helpMenu;
    }

    @Override
    public void notify(ServerMessage message){
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    public void displayNotification(String message){
        System.out.println(SET_TEXT_COLOR_LIGHT_PINK + message);
        printPrompt();
    }

    public void displayError(String errorMessage){
        System.out.println(SET_TEXT_COLOR_LIGHT_PINK + errorMessage);
        printPrompt();
    }

    public void loadGame(ChessGame game){
        myGame = game;
        drawBoard(game.getBoard());
        printPrompt();
    }

    public void drawBoard(ChessBoard board) {
        System.out.println("\n");
        ChessBoardVisual boardVisual;
        if (playerColor == ChessGame.TeamColor.BLACK) {
            boardVisual = new ChessBoardVisual(board, playerColor);
        } else {
            boardVisual = new ChessBoardVisual(board, ChessGame.TeamColor.WHITE);
        }
        boardVisual.getBoardVisual(null, null);
    }

    public void drawHighlights(ChessGame game, ChessPosition pos) {
        System.out.println("\n");
        ChessBoardVisual boardVisual;
        if (playerColor == ChessGame.TeamColor.BLACK) {
            boardVisual = new ChessBoardVisual(game.getBoard(), playerColor);
        } else  {
            boardVisual = new ChessBoardVisual(game.getBoard(), ChessGame.TeamColor.WHITE);
        }
        boardVisual.getBoardVisual(game, pos);
    }

    private ChessPosition convertToChessPosition(String positionCoords) throws ResponseException{
        var colCoord = positionCoords.charAt(0);
        var rowCoord = positionCoords.charAt(1);

        var possibleCol = "abcdefgh";
        int col = possibleCol.indexOf(colCoord);

        if (!Character.isDigit(rowCoord)) {
            throw new ResponseException(ResponseException.Code.ClientError, "Chess positions must be entered in this format: b3\n");
        }
        int row = Character.getNumericValue(rowCoord);
        if (isLetter(colCoord) && col != -1 && row >= 1 && row <= 8){

            return new ChessPosition(row, col+1);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Chess positions must be entered in this format: b3\n");
    }

    private ChessPiece.PieceType verifyPieceType(String piece) {
        try{
            ChessPiece.PieceType type = ChessPiece.PieceType.valueOf(piece);
            return type;
        }catch(IllegalArgumentException e){
            return null;
        }
    }
}
