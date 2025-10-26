package Service;

import dataaccess.AuthDAO;
import model.AuthData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void createAuth(AuthData authData) {
        authDAO.createAuth(authData);
    }
}
