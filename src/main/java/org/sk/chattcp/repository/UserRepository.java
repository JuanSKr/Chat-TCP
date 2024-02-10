package org.sk.chattcp.repository;

import org.sk.chattcp.entity.User;
import org.sk.chattcp.functionality.connection.Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
                    "    id               INT AUTO_INCREMENT," +
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
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(int id) {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("SELECT * FROM user WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("SELECT * FROM user")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public void update(User user) {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("UPDATE user SET username = ?, password = ? WHERE id = ?")) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}