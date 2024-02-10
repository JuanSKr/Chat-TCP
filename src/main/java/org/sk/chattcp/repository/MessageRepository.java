package org.sk.chattcp.repository;

import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.functionality.connection.Conexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    private Conexion conexion;

    public MessageRepository(Conexion conexion) {
        this.conexion = conexion;
        createTable();
    }

    public void createTable() {
        Statement stmt = null;
        try {
            stmt = conexion.getConnection().createStatement();
            String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS message (" +
                    "    id               BIGINT AUTO_INCREMENT," +
                    "    sender_id INT," +
                    "    content TEXT," +
                    "    date TIMESTAMP," +
                    " PRIMARY KEY (id)," +
                    " FOREIGN KEY (sender_id) REFERENCES user(id)" +
                    ");";
            stmt.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(Message message) {
        PreparedStatement ps = null;
        try {
            ps = conexion.getConnection().prepareStatement("INSERT INTO message (sender_id, content, date) VALUES (?, ?, ?)");
            ps.setInt(1, message.getSender().getId());
            ps.setString(2, message.getContent());
            ps.setTimestamp(3, Timestamp.valueOf(message.getDate()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conexion.getConnection().prepareStatement("SELECT m.id, m.content, m.date, u.id as sender_id, u.username as sender_username FROM message m JOIN user u ON m.sender_id = u.id ORDER BY m.id ASC");
            rs = ps.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setContent(rs.getString("content"));
                message.setDate(rs.getTimestamp("date").toLocalDateTime());
                User sender = new User();
                sender.setId(rs.getInt("sender_id"));
                sender.setUsername(rs.getString("sender_username"));
                message.setSender(sender);
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return messages;
    }

    public void update(Message message) {
        PreparedStatement ps = null;
        try {
            ps = conexion.getConnection().prepareStatement("UPDATE message SET sender_id = ?, content = ?, date = ? WHERE id = ?");
            ps.setInt(1, message.getSender().getId());
            ps.setString(2, message.getContent());
            ps.setTimestamp(3, Timestamp.valueOf(message.getDate()));
            ps.setInt(4, message.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void delete(Message message) {
        PreparedStatement ps = null;
        try {
            ps = conexion.getConnection().prepareStatement("DELETE FROM message WHERE id = ?");
            ps.setInt(1, message.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}