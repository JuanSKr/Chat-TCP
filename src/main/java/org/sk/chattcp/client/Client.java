package org.sk.chattcp.client;

import org.sk.chattcp.entity.Message;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.functionality.connection.Conexion;
import org.sk.chattcp.repository.MessageRepository;
import org.sk.chattcp.repository.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Client extends JFrame implements ActionListener, Runnable {
    private static final long serialVersionUID = 1L;
    Socket socket = null;
    DataInputStream fentrada;
    DataOutputStream fsalida;
    static User currentUser;

    static JTextField txtMensaje = new JTextField();
    private JScrollPane scrollpane1;
    static JTextArea msgTextArea;
    JButton botonEnviar = new JButton("Enviar");
    JButton botonSalir = new JButton("Salir");
    boolean repetir = true;

    MessageRepository messageRepository;
    UserRepository userRepository;

    // constructor
    public Client(Socket s, User currentUser) {
        super(" Conexión del cliente del chat: " + Client.currentUser.getUsername());
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(txtMensaje, BorderLayout.CENTER);
        topPanel.add(botonEnviar, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        msgTextArea = new JTextArea();
        msgTextArea.setFont(new Font("Serif", Font.PLAIN, 18));
        msgTextArea.setForeground(Color.BLACK);
        msgTextArea.setLineWrap(true);
        scrollpane1 = new JScrollPane(msgTextArea);
        add(scrollpane1, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(botonSalir, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        msgTextArea.setEditable(false);
        botonEnviar.addActionListener(this);
        botonSalir.addActionListener(this);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        socket = s;
        this.currentUser = currentUser;
        try {
            fentrada = new DataInputStream(socket.getInputStream());
            fsalida = new DataOutputStream(socket.getOutputStream());
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
            String content = txtMensaje.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No puedes enviar un mensaje vacio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
            try {
                fsalida.writeUTF("*");
                repetir = false; // Para salir del bucle
                // Agrega el mensaje a la interfaz de usuario del cliente
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void run() {
        while (repetir) {
            try {
                List<Message> messages = messageRepository.findAll();
                StringBuilder sb = new StringBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

                for (Message message : messages) {
                    sb.append(message.getSender().getUsername()).append(": ")
                            .append(message.getContent()).append(" ")
                            .append(" | [" + message.getDate().format(formatter)).append("]" +"\n");
                }
                msgTextArea.setText(sb.toString());
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
            Object[] options = {"Registrarse", "Iniciar sesion"};
            int option = JOptionPane.showOptionDialog(null, "¡Bienvenido! \nElige una opción", "Chat-TCP",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (option == 0) { // Registrarse
                JPanel panel = new JPanel(new GridLayout(2, 2));
                JTextField usernameField = new JTextField();
                JPasswordField passwordField = new JPasswordField();
                panel.add(new JLabel("Nombre de usuario:"));
                panel.add(usernameField);
                panel.add(new JLabel("Contraseña:"));
                panel.add(passwordField);
                int result = JOptionPane.showConfirmDialog(null, panel, "Registro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    nombre = usernameField.getText();
                    password = new String(passwordField.getPassword());
                    if (nombre.trim().isEmpty() || password.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El nombre y la contraseña no pueden estar vacíos");
                        continue;
                    }
                    User user = new User();
                    user.setUsername(nombre);
                    user.setPassword(password);
                    try {
                        userRepository.save(user);
                        Client.currentUser = userRepository.findByUsername(nombre);
                        break;
                    } catch (RuntimeException e) {
                        JOptionPane.showMessageDialog(null, "El nombre de usuario ya existe");
                    }
                }
            } else if (option == 1) { // Iniciar sesion
                JPanel panel = new JPanel(new GridLayout(2, 2));
                JTextField usernameField = new JTextField();
                JPasswordField passwordField = new JPasswordField();
                panel.add(new JLabel("Nombre de usuario:"));
                panel.add(usernameField);
                panel.add(new JLabel("Contraseña:"));
                panel.add(passwordField);
                int result = JOptionPane.showConfirmDialog(null, panel, "Inicio de sesión", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    nombre = usernameField.getText();
                    password = new String(passwordField.getPassword());
                    if (nombre.trim().isEmpty() || password.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El nombre y la contraseña no pueden estar vacíos");
                        continue;
                    }
                    User user = userRepository.findByUsername(nombre);
                    if (user != null && password.equals(user.getPassword())) {
                        Client.currentUser = user;
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Nombre de usuario o contraseña incorrectos");
                    }
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
            cliente.setBounds(0, 0, 840, 620);
            cliente.setVisible(true);
            new Thread(cliente).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Fallo servidor\n" + e.getMessage(),
                    "<<MENSAJE DE ERROR:1>>", JOptionPane.ERROR_MESSAGE);
        }
    }
}