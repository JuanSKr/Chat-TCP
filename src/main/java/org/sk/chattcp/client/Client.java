package org.sk.chattcp.client;

import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.functionality.connection.Conexion;
import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

public class Client extends JFrame implements ActionListener, Runnable {
    private static final long serialVersionUID = 1L;
    Socket socket = null;
    DataInputStream fentrada;
    DataOutputStream fsalida;
    static User currentUser;

    static JTextField txtMensaje = new JTextField();
    private JScrollPane scrollpane1;
    static JTextArea textarea1;
    JButton botonEnviar = new JButton("Enviar");
    JButton botonSalir = new JButton("Salir");
    boolean repetir = true;

    MessageRepository messageRepository;
    UserRepository userRepository;

    // constructor
    public Client(Socket s, User currentUser) {
        super(" Conexión del cliente del chat: " + Client.currentUser.getUsername());
        setLayout(null);

        txtMensaje.setBounds(10, 10, 400, 30);
        add(txtMensaje);

        textarea1 = new JTextArea();
        scrollpane1 = new JScrollPane(textarea1);
        scrollpane1.setBounds(10, 50, 400, 300);
        add(scrollpane1);

        botonEnviar.setBounds(420, 10, 100, 30);
        add(botonEnviar);
        botonSalir.setBounds(420, 50, 100, 30);
        add(botonSalir);

        textarea1.setEditable(false);
        botonEnviar.addActionListener(this);
        botonSalir.addActionListener(this);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        socket = s;
        this.currentUser = currentUser;
        try {
            fentrada = new DataInputStream(socket.getInputStream());
            fsalida = new DataOutputStream(socket.getOutputStream());
            String texto = " > Entra en el Chat ... " + currentUser.getUsername();
            fsalida.writeUTF(texto);
        } catch (IOException e) {
            System.out.println("ERROR DE E/S");
            e.printStackTrace();
            System.exit(0);
        }

        Conexion conexion = new Conexion();
        this.messageRepository = new MessageRepository(conexion);
        this.userRepository = new UserRepository(conexion);
    }

    // accion cuando pulsamos botones
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonEnviar) { // SE PULSA EL ENVIAR
            String content = txtMensaje.getText();
            if (Client.currentUser == null) {
                throw new RuntimeException("User not found: " + Client.currentUser.getUsername());
            }
            Message message = new Message();
            message.setSender(Client.currentUser);
            System.out.println("Client.currentUser: " + Client.currentUser.getUsername() + " - " + Client.currentUser.getId());
            message.setContent(content);
            message.setDate(LocalDateTime.now());
            messageRepository.save(message);
            txtMensaje.setText("");
        }
        if (e.getSource() == botonSalir) { // SE PULSA BOTON SALIR
            String texto = " > Abandona el Chat ... " + currentUser.getUsername();
            try {
                fsalida.writeUTF(texto);
                fsalida.writeUTF("*");
                repetir = false; // Para salir del bucle
                // Agrega el mensaje a la interfaz de usuario del cliente
                textarea1.append(texto + "\n");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void run() {
        String texto = " > Entra en el Chat ... " + currentUser.getUsername();
        // Agrega el mensaje a la interfaz de usuario del cliente
        textarea1.append(texto + "\n");

        while (repetir) {
            try {
                List<Message> messages = messageRepository.findAll();
                StringBuilder sb = new StringBuilder();
                for (Message message : messages) {
                    sb.append(message.getSender().getUsername()).append(": ").append(message.getContent()).append("\n");
                }
                textarea1.setText(sb.toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Fallo servidor\n" + e.getMessage(),
                        "<<MENSAJE DE ERROR:2>>", JOptionPane.ERROR_MESSAGE);
                System.out.println("Fallo servidor\n" + e.getMessage());
                System.out.println(currentUser.getUsername() + " - " + currentUser.getId());
                repetir = false;
            }
        }

        try {
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Conexion conexion = new Conexion();
        UserRepository userRepository = new UserRepository(conexion);
        String nombre = "";
        String password = "";
        int puerto = 44444;
        Socket s = null;
        User currentUser = null;

        while (true) {
            String option = JOptionPane.showInputDialog("1. Registrarse\n2. Iniciar sesion");

            if ("1".equals(option)) {
                nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
                password = JOptionPane.showInputDialog("Introduce tu contraseña:");
                User user = new User();
                user.setUsername(nombre);
                user.setPassword(password);
                userRepository.save(user);
                Client.currentUser = userRepository.findByUsername(nombre);
                break;
            } else if ("2".equals(option)) {
                nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
                password = JOptionPane.showInputDialog("Introduce tu contraseña:");
                User user = userRepository.findByUsername(nombre);
                if (user != null && password.equals(user.getPassword())) {
                    Client.currentUser = user;
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Nombre de usuario o contraseña incorrectos");
                }
            }
        }

        if (nombre.trim().isEmpty()) {
            System.out.println("El nombre no puede estar vacío...");
            return;
        }

        try {
            s = new Socket("localhost", 44444);
            Client cliente = new Client(s, Client.currentUser);
            cliente.setBounds(0, 0, 540, 400);
            cliente.setVisible(true);
            new Thread(cliente).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Fallo servidor\n" + e.getMessage(),
                    "<<MENSAJE DE ERROR:1>>", JOptionPane.ERROR_MESSAGE);
        }
    }
}