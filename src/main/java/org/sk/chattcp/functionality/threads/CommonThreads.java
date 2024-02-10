package org.sk.chattcp.functionality.threads;

import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CommonThreads {
    ArrayList<Socket> conexiones = new ArrayList<>();
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public CommonThreads(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public void addConexion(Socket socket) {
        conexiones.add(socket);
    }

    public void delConexion(Socket socket) {
        conexiones.remove(socket);
    }

    public void createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
    }

    public void sendMessage(User sender, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setDate(LocalDateTime.now());
        messageRepository.save(message);
    }

    public void sendToAllExcept(String message, Socket except) {
        for (Socket s : conexiones) {
            if (!s.equals(except)) {
                try {
                    DataOutputStream fsalida = new DataOutputStream(s.getOutputStream());
                    fsalida.writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}