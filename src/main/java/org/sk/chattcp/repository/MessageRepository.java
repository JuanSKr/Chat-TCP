package org.sk.chattcp.repository;

import org.sk.chattcp.entity.Message;
import org.sk.chattcp.functionality.connection.Conexion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class MessageRepository {

    private Conexion conexion;

    public MessageRepository(Conexion conexion) {
        this.conexion = conexion;
        createTable();
    }

    public void createTable() {
        try (Statement stmt = conexion.getConnection().createStatement()) {

            String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS message (" +
                    "    id               BIGINT AUTO_INCREMENT," +
                    "    sender_id BIGINT," +
                    "    content TEXT," +
                    "    date TIMESTAMP," +
                    " PRIMARY KEY (id)," +
                    " FOREIGN KEY (sender_id) REFERENCES user(id)" +
                    ");";
            stmt.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void save(Message message) {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("INSERT INTO message (sender_id, content, date) VALUES (?, ?, ?)")) {
            ps.setLong(1, message.getSender().getId());
            ps.setString(2, message.getContent());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> findAll() {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("SELECT * FROM message")) {
            return ps.executeQuery().next() ? List.of(new Message()) : List.of();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

public List<Message> findBySender(Long senderId) {
        try (PreparedStatement ps = conexion.getConnection().prepareStatement("SELECT * FROM message WHERE sender_id = ?")) {
            ps.setLong(1, senderId);
            return ps.executeQuery().next() ? List.of(new Message()) : List.of();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}