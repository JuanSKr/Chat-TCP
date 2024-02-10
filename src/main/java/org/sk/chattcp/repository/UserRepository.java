package org.sk.chattcp.repository;

import org.sk.chattcp.entity.User;
import org.sk.chattcp.functionality.connection.Conexion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class UserRepository {

    private Conexion conexion;

    public UserRepository(Conexion conexion) {
        this.conexion = conexion;
        createTable();
    }

    public void createTable() {
        try (Statement stmt = conexion.getConnection().createStatement()) {

            String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS user (" +
                    "    id               BIGINT AUTO_INCREMENT," +
                    "    username VARCHAR(255) UNIQUE," +
                    "    password VARCHAR(255)," +
                    " PRIMARY KEY (id)" +
                    ");";
            stmt.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void save(User user) {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)")) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findByUsername(String username) {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("SELECT * FROM user WHERE username = ?")) {
            ps.setString(1, username);
            return ps.executeQuery().next() ? new User() : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(Long id) {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("SELECT * FROM user WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeQuery().next() ? new User() : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll() {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("SELECT * FROM user")) {
            return ps.executeQuery().next() ? List.of(new User()) : List.of();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}