package org.sk.chattcp.functionality;

import org.sk.chattcp.functionality.threads.CommonThreads;
import org.sk.chattcp.functionality.threads.ThreadServerChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args){
        int puerto = 44444;
        ServerSocket servidor=null;
        CommonThreads comun=new CommonThreads();
        try {
            servidor = new ServerSocket(puerto);
            System.out.println("Servidor en marcha...");

            while (true) {
                Socket socket = new Socket();
                socket = servidor.accept();// esperando cliente

                comun.addConexion(socket);

                ThreadServerChat hilo = new ThreadServerChat(socket, comun);
                hilo.start();
            }
            //servidor.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Servidor iniciado...");
    }
}
