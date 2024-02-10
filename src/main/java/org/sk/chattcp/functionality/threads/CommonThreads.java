package org.sk.chattcp.functionality.threads;

import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import java.net.Socket;
import java.util.ArrayList;

public class CommonThreads {
    ArrayList<Socket> conexiones = new ArrayList<>();
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public CommonThreads() {
        this.userRepository = new UserRepository();
        this.messageRepository = new MessageRepository();
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

    public void sendMessage(User sender, User receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        messageRepository.save(message);
    }
}