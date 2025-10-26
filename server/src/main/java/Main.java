import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.Server;
import Service.*;

public class Main {
    public static void main(String[] args) {

        var userDAO = new MemoryUserDAO();
        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();

        var userService = new UserService(authDAO, userDAO);
        var authService = new AuthService(authDAO);
        var gameService = new GameService();

        Server server = new Server(userService, authService, gameService);
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}