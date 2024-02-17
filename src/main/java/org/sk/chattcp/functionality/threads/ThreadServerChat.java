package org.sk.chattcp.functionality.threads;

import org.sk.chattcp.client.Client;
import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

public class ThreadServerChat extends Thread {
    DataInputStream fentrada;
    Socket socket = null;
    CommonThreads comun;
    UserRepository userRepository;
    MessageRepository messageRepository;

    public ThreadServerChat(Socket s, CommonThreads comun, UserRepository userRepository, MessageRepository messageRepository) {
        this.socket = s;
        this.comun = comun;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        try {
            // CREO FLUJO DE entrada para leer los mensajes
            fentrada = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("ERROR DE E/S");
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // Recibir el mensaje del cliente
            DataInputStream fentrada = new DataInputStream(socket.getInputStream());
            String received = fentrada.readUTF();

            // Dividir el mensaje entrante en partes
            String[] parts = received.split(":");
            String command = parts[0];
            String username = parts[1];
            String password = parts[2];

            if (command.equals("LOGIN")) {
                // Buscar el usuario en la base de datos
                User user = userRepository.findByUsername(username);
                if (user != null && password.equals(user.getPassword())) {
                    // Enviar la lista de usuarios al cliente
                    DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
                    fsalida.writeUTF("USERS:" + userRepository.findAll());
                }
            } else if (command.equals("REGISTER")) {
                // Registrar el nuevo usuario en la base de datos
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                userRepository.save(user);
            } else {
                // Buscar el usuario en la base de datos
                User sender = userRepository.findByUsername(username);
                if (sender == null) {
                    throw new RuntimeException("User not found: " + username);
                }

                // Guardar el mensaje en la base de datos
                Message message = new Message();
                message.setSender(sender);
                message.setContent(received);
                message.setDate(LocalDateTime.now());
                messageRepository.save(message);

                // Enviar el mensaje al cliente
                DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
                fsalida.writeUTF(received);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}