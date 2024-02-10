package org.sk.chattcp.functionality.threads;

import lombok.Getter;
import lombok.Setter;
import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;

import java.net.Socket;
import java.util.ArrayList;
@Getter
@Setter
public class CommonThreads {
    ArrayList<Socket> conexiones=new ArrayList<>();
    ArrayList<Message> mensajes=new ArrayList<>();
    ArrayList<User> usuarios=new ArrayList<>();

    String stMensajes;

    public CommonThreads(){
        stMensajes ="";
        usuarios.add(new User(1L, "Antonio", "1234"));
        usuarios.add(new User(2L, "Ana", "1234"));
    }

    //Añade el socket a la lista de sockets
    public void addConexion(Socket socket){
        conexiones.add(socket);
    }

    //Busca el socket que le pasan y lo elimina
    public void delConexion(Socket socket){
        if(conexiones.size()>0) {
            for (Socket s : conexiones) {
                if (s.equals(socket)) {
                    conexiones.remove(s);
                }
            }
        }
    }

    public synchronized void setStMensajes(String stMensajes) {
        this.stMensajes = stMensajes;
    }
}
