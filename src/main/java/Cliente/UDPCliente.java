/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author Angel
 * Cliente UDP lo que pasa es que el cliente le envia paquetes al ServidorUDP y el servidorUDP lo interpreta
 */
public class UDPCliente {
    
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 7778;

    private DatagramSocket socket;
    private InetAddress direccion;
    private String usuario;

    public UDPCliente() throws Exception {
        socket = new DatagramSocket(); // puerto automático
        direccion = InetAddress.getByName(SERVER_IP);

        System.out.println("=== CLIENTE UDP INICIADO ===");
        System.out.println("Para mesnsajes privados");
        System.out.println("Escriba PRIVADO:USUARIO:MENSAJE");
        System.out.println("Para mensajes publicos solo escriba en el servidor");
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingresa tu usuario para registrarse: ");
        usuario = sc.nextLine();

        // Registrarse en el servidor mediante el UDPServer
        /*
        En este momento el Usuario o cliente crea un paquete UDP y lo envia al servidorUDP mediante el puerto
        */
        enviar("REGISTRAR:" + usuario);

        //Hilo para recibir mensajes
        Thread recibir = new Thread(() -> {
            try {
                //Arreglo de bytes para guardar los datos
                byte[] buffer = new byte[1024];

                while (true) {
                    //Creamos el paquete
                    DatagramPacket paquete =new DatagramPacket(buffer, buffer.length);
                    //Recibimos el paquete
                    socket.receive(paquete);

                    //Convertimos los bytes a texto
                    String msg = new String(paquete.getData(), 0,paquete.getLength());

                    //Mostramos el mensaje
                    System.out.println("\n" + msg);
                    System.out.print("> ");
                }

            } catch (Exception e) {
                System.out.println("UDP cerrado");
            }
        });

        //Iniciamos el hilo
        recibir.start();

        //Esto es para enviar mensajes
         while (true) {
            System.out.print("> ");
            String mensaje = sc.nextLine();

            if (mensaje.equalsIgnoreCase("exit")) {
                socket.close();
                break;
            }

            // Detecta si es privado y si no lo envia a todos
            if (mensaje.startsWith("PRIVADO:")) {
                enviar(mensaje);
            } else {
                enviar("TODOS:" + mensaje);
            }
        }
    }

    // Método para enviar mensajes al servidor
    private void enviar(String msg) throws Exception {
        //Obtenemos el mensaje en bytes
        byte[] data = msg.getBytes();
        //Creamos el paquete con el mensaje que vamos a enviar
        DatagramPacket paquete = new DatagramPacket(data,data.length,direccion,SERVER_PORT);
        //Lo enviamos mediante el socket
        socket.send(paquete);
    }

    public static void main(String[] args) {
        try {
            new UDPCliente();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    

