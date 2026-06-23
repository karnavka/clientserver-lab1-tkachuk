package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserService {
    private final String dbUrl;

    public UserService(String dbUrl) {
        this.dbUrl = dbUrl;
        createTable();
    }

    private void createTable() {
        try (Connection con = connect();
             Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "login TEXT PRIMARY KEY, " +
                    "password TEXT NOT NULL)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean authenticate(String login, String password) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT password FROM users WHERE login = ?")) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
            return rs.getString("password").equals(password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean createUser(String login, String password) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT OR IGNORE INTO users(login, password) VALUES (?, ?)")) {
            ps.setString(1, login);
            ps.setString(2, password);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void clearAll() {
        try (Connection con = connect();
             Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM users");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }
}
