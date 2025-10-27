package dataaccess;
import model.AuthData;
import exception.ResponseException;

public interface AuthDAO {
    AuthData getAuth(String authToken);
    void createAuth(AuthData authData);
    void deleteInfo();
    void deleteAuth(String authToken);
}
