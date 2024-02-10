package org.sk.chattcp.functionality.threads;

import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

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
        while (true) {
            try {
                String cadena = fentrada.readUTF(); //Lee el mensaje enviado por el cliente
                if (cadena.trim().equals("*")) {// El cliente se desconecta
                    comun.delConexion(socket);
                    break;
                }
                String[] parts = cadena.split(":");
                User sender = userRepository.findById(Integer.parseInt(parts[0]));
                String content = parts[1];
                Message message = new Message();
                message.setSender(sender);
                message.setContent(content);
                message.setDate(LocalDateTime.now());
                messageRepository.save(message);
                EnviarMensajesaTodos();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        // se cierra el socket del cliente
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Envía los mensajes a todos los clientes
    private void EnviarMensajesaTodos() {
        List<Message> messages = messageRepository.findAll();
        for (Socket s : comun.conexiones) {
            if (!s.isClosed()) {
                try {
                    DataOutputStream fsalida = new DataOutputStream(s.getOutputStream());
                    for (Message message : messages) {
                        fsalida.writeUTF(message.getSender().getUsername() + ":" + message.getContent());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}