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
    public static User currentUser;

    static JTextField txtMensaje = new JTextField();
    private JScrollPane scrollpane1;
    static JTextArea msgTextArea;
    JButton botonEnviar = new JButton("Enviar");
    JButton botonSalir = new JButton("Salir");
    boolean repetir = true;

    JFrame serverSelection = new JFrame();


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

    // Eventos de los botones: Enviar y Salir
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonEnviar) { // SE PULSA EL ENVIAR
            String content = txtMensaje.getText().trim();
            if (content.isEmpty()) { // Comprueba si el mensaje está vacío
                JOptionPane.showMessageDialog(this, "No puedes enviar un mensaje vacio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (Client.currentUser == null) { // Comprueba si el usuario está logueado
                throw new RuntimeException("User not found: " + Client.currentUser.getUsername());
            }
            try {
                fsalida.writeUTF(Client.currentUser.getUsername() + ":" + content);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            txtMensaje.setText("");
        }
        if (e.getSource() == botonSalir) { // SE PULSA BOTON SALIR
            try {
                fsalida.writeUTF("*");
                repetir = false; // Para salir del bucle
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // Ejectuta el hilo del cliente y recibe los mensajes desde la base de datos
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

    // Register - Login - Conexion con el servidor
    public static void main(String[] args) {
        Conexion conexion = new Conexion();
        UserRepository userRepository = new UserRepository(conexion);
        String name = "";
        String password = "";
        String ip = null;
        int port = 44444;
        Socket s = null;
        User currentUser = null;

        // Obtener IP y Puerto que escribe el usuario
        JPanel panelConnection = new JPanel(new GridLayout(2, 2));
        JTextField ipField = new JTextField();
        JTextField portField = new JTextField();
        panelConnection.add(new JLabel("IP:"));
        panelConnection.add(ipField);
        panelConnection.add(new JLabel("Puerto:"));
        panelConnection.add(portField);

        int resultConnection = JOptionPane.showConfirmDialog(null, panelConnection, "Conexión con un servidor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultConnection == JOptionPane.OK_OPTION) {
            ip = ipField.getText();
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El puerto debe ser un número");
                System.exit(0);
            }
        } else {
            System.exit(0);
        }

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
                    name = usernameField.getText();
                    password = new String(passwordField.getPassword());
                    if (name.trim().isEmpty() || password.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El name y la contraseña no pueden estar vacíos");
                        continue;
                    }
                    User user = new User();
                    user.setUsername(name);
                    user.setPassword(password);
                    try {
                        userRepository.save(user);
                        Client.currentUser = userRepository.findByUsername(name);
                        break;
                    } catch (RuntimeException e) {
                        JOptionPane.showMessageDialog(null, "El name de usuario ya existe");
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
                    name = usernameField.getText();
                    password = new String(passwordField.getPassword());
                    if (name.trim().isEmpty() || password.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El name y la contraseña no pueden estar vacíos");
                        continue;
                    }
                    User user = userRepository.findByUsername(name);
                    if (user != null && password.equals(user.getPassword())) {
                        Client.currentUser = user;
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Nombre de usuario o contraseña incorrectos");
                    }
                }
            }
        }


        if (name.trim().isEmpty()) {
            System.out.println("El name no puede estar vacío...");
            return;
        }

        try {
            s = new Socket(ip, port);
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