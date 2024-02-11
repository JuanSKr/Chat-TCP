package org.sk.chattcp.server;

import org.sk.chattcp.functionality.connection.Conexion;
import org.sk.chattcp.functionality.threads.CommonThreads;
import org.sk.chattcp.functionality.threads.ThreadServerChat;
import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args){
        int puerto = 44444;
        ServerSocket servidor=null;
        Conexion conexion = new Conexion();
        // Inicializo los repositorios
        UserRepository userRepository = new UserRepository(conexion);
        MessageRepository messageRepository = new MessageRepository(conexion);
        // Creo las tablas
        userRepository.createTable();
        messageRepository.createTable();
        // Creo el objeto CommonThreads con los repositorios
        CommonThreads comun=new CommonThreads(userRepository, messageRepository);
        try {
            servidor = new ServerSocket(puerto);
            System.out.println("Servidor en marcha...");

            while (true) {
                Socket socket = new Socket();
                socket = servidor.accept();// esperando cliente

                comun.addConexion(socket);

                ThreadServerChat hilo = new ThreadServerChat(socket, comun, userRepository, messageRepository);
                hilo.start();
            }
            //servidor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Servidor iniciado...");
    }
}