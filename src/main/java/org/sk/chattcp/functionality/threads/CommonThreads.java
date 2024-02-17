package org.sk.chattcp.functionality.threads;

import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import java.net.Socket;
import java.util.ArrayList;

public class CommonThreads {
    ArrayList<Socket> conexiones = new ArrayList<>();
    private UserRepository userRepository;
    private MessageRepository messageRepository;

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




}