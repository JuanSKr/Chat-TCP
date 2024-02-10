package org.sk.chattcp.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame implements ActionListener, Runnable {
    private static final long serialVersionUID = 1L;
    Socket socket = null;
    DataInputStream fentrada;
    DataOutputStream fsalida;
    String nombre;

    static JTextField txtMensaje = new JTextField();
    private JScrollPane scrollpane1;
    static JTextArea textarea1;
    JButton botonEnviar = new JButton("Enviar");
    JButton botonSalir = new JButton("Salir");
    boolean repetir = true;

    // constructor
    public Client(Socket s, String nombre) {
        super(" Conexión del cliente del chat: " + nombre);
        //Creo los elementos gráficos de Swing
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
        this.nombre = nombre;
        try {
            fentrada = new DataInputStream(socket.getInputStream());
            fsalida = new DataOutputStream(socket.getOutputStream());
            String texto = " > Entra en el Chat ... " + nombre;
            fsalida.writeUTF(texto);
        } catch (IOException e) {
            System.out.println("ERROR DE E/S");
            e.printStackTrace();
            System.exit(0);
        }
    }// fin constructor

    // accion cuando pulsamos botones
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonEnviar) { // SE PULSA EL ENVIAR

            if (txtMensaje.getText().trim().length() == 0)
                return;
            String texto = nombre + "> " + txtMensaje.getText();

            try {
                txtMensaje.setText("");
                fsalida.writeUTF(texto);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (e.getSource() == botonSalir) { // SE PULSA BOTON SALIR
            String texto = " > Abandona el Chat ... " + nombre;
            try {
                fsalida.writeUTF(texto);
                fsalida.writeUTF("*");
                repetir = false;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }// accion botones

    public void run() {
        String texto = "";
        while (repetir) {
            try {
                texto = fentrada.readUTF();
                textarea1.setText(texto);

            } catch (IOException e) {
                // este error sale cuando el servidor se cierra
                JOptionPane.showMessageDialog(null, "Fallo servidor\n" + e.getMessage(),
                        "<<MENSAJE DE ERROR:2>>", JOptionPane.ERROR_MESSAGE);
                repetir = false;
            }
        } // while

        try {
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// run

    public static void main(String args[]) {
        int puerto = 44444;
        Socket s = null;

        String nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");

        if (nombre.trim().length() == 0) {
            System.out.println("El nombre está vacío....");
            return;
        }

        try {
            //Se conecta al servidor
            s = new Socket("localhost", 44444);
            //Usa el socket y el nombre para crear un nuevo Cliente
            Client cliente = new Client(s, nombre);
            //Le da un tamaño a la ventana y la hace visible
            cliente.setBounds(0, 0, 540, 400);
            cliente.setVisible(true);
            new Thread(cliente).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Fallo servidor\n" + e.getMessage(),
                    "<<MENSAJE DE ERROR:1>>", JOptionPane.ERROR_MESSAGE);
        }

    }// main
}// ..ClienteChat

