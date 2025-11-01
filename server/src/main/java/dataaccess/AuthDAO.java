package dataaccess;
import model.AuthData;
import exception.ResponseException;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws ResponseException, DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException;
    void deleteInfo() throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
