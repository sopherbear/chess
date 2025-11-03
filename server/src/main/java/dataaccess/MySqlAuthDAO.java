package dataaccess;

import exception.ResponseException;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO{

    public AuthData getAuth(String authToken) throws ResponseException, DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth_data WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs);
                    }
                }
            }
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void createAuth(AuthData authData) throws DataAccessException{
        var statement = "INSERT INTO auth_data (authToken, username) VALUES (?, ?)";

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(statement, authData.authToken(), authData.username());
    }

    public void deleteInfo() throws DataAccessException{
        var statement = "TRUNCATE auth_data";

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(statement);
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        var statement = "DELETE FROM auth_data WHERE authToken=?";

        var updateExecutor = new ExecuteDatabaseUpdates();
        updateExecutor.executeUpdate(statement, authToken);
    }

    public AuthData readAuthData(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    public int getTableCount() throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM auth_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return getCount(rs);
                    }
                }
            }
        } catch (Throwable e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return -1;
    }

    private int getCount(ResultSet rs) throws SQLException{
        return rs.getInt(1);
    }
}
