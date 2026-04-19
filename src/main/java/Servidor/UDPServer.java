/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * @author Angel
 * Como en UDP no existe la conexión todo se basa en el paquete recibido
 */
public class UDPServer {
    
    private static final int PORT = 7777;
    //Aqui se usa DatagramSocket y no serverSocket como TCP por que en UDP no hay conexion con el cliente, solo se envían los mensajes o paquetes
    private DatagramSocket datagramSocket;

    //Lista de clientes para poder usar validaciones como maximo 5 clientes
    private static ArrayList<Usuario> usuarios = new ArrayList<>();
    
    //Clase cliente para identificarlos a cada uno
    public class Usuario{
    String nombre;
    //Clase que representa una dirección ip y la guardamos 
    InetAddress direccion;
    int puerto;

    public Usuario(String nombre, InetAddress direccion, int puerto) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.puerto = puerto;
    }
}
    
    //Constructor que recibe el puerto 
    public UDPServer() throws SocketException {
        System.out.println("=== SERVIDOR UDP INICIADO ===");
        this.datagramSocket = new DatagramSocket(PORT);
    }
    
    //Metodo para iniciar el servidor
    public void start() throws IOException {
        //Arreglo de bytes que guarda los datos que le llegan
        byte[] buffer = new byte[1024];
        //Bucle infinito ya que el servidor nunca se detiene
         while (true) {
            //Creamos el paquete para recibir vacio aun
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
            /*
            En esta parte el servidor se detiene hasta que se llene el paquete
            Cuando llega guarda el mensaje que viene siendo el cliente, el puerto y el mensaje
            */
            datagramSocket.receive(paquete);
            //Convertimos los bytes a un string del buffer, el 0 es por que empieza en la posición 0
            String mensaje = new String(paquete.getData(), 0, paquete.getLength());
            //Mostramos el mensaje
            System.out.println("[UDP] Recibido: " + mensaje);
            
            //Registrar el usuario
            if (mensaje.startsWith("Registrar:")) {
                //Dividimos el nombre en un arreglo con split y que tome la posición 1 osea el nombre de usuario en vez de Registrar
                String nombreUsuario = mensaje.split(":")[1];

                if (usuarios.size() >= 5) {
                    enviar("Servidor lleno", paquete);
                    continue;
                }
                if (existeUsuario(nombreUsuario)) {
                    enviar("No pueden existir 2 usuarios iguales", paquete);
                    continue;
                }
                //Si pasa las validaciones registramos el usuario
                Usuario nuevoUsuario = new Usuario(nombreUsuario,paquete.getAddress(),paquete.getPort());
                usuarios.add(nuevoUsuario);
                enviar("OK: usuario registrado", paquete);
            }
                /*
               En esta parte lo que hace es
               1. Llega el mensaje
               2. Se identifica el usuario que lo envio
               3. Se extrae el mensaje
               4. Se lo envia a todos
               */
                // Si ya esta registrado el usuario ahora puede mandar mensaje a todos
                // Verificamos que el mensaje empieze con todos
                else if (mensaje.startsWith("TODOS:")) {
                //Buscamos quien mando el mensaje mediante el metodo que hice    
                Usuario remitente = buscarPorDireccion(paquete);
                //Validamos que esta registrado el usuario que mando el mensaje
                if (remitente == null) {
                    enviar("Usuario no registrado", paquete);
                    //Continue es para ignorar el mensaje
                    continue;
                }
                //Obtenemos el texto del mensaje quitando el Todos el mensaje y 6 por que todos: tiene 6 caracteres y empieza en el 7
                String texto = mensaje.substring(6);
                //Esta parte es la importante
                /*
                En esta parte a cada usuario mediante el metodo de enviarDirecto que hice va a hacer que se el envie el mensaje a todos los de la sesion
                Recorremos la lista de usuarios y usamos el metodo para enviarles el mensaje a todos
                */
                for (Usuario c : usuarios) {
                    enviarDirecto(c, remitente.nombre + ": " + texto);
                }
            }
        }

    }

    //Metodo para validar que no existan 2 usuarios con el mismo nombre
    private boolean existeUsuario(String usuario) {
        for (Usuario c : usuarios) {
            if (c.nombre.equals(usuario)) {
                return true;
            }
        }
        return false;
    }

    //Metodo identificar que usuario envío el mensaje mediante el IP y el puerto
    private Usuario buscarPorDireccion(DatagramPacket paquete) {
        //Recorre la lista de clientes
        for (Usuario c : usuarios) {
            //Compara el puerto y el ip del paquete con el usuario
            if (c.direccion.equals(paquete.getAddress()) &&
                c.puerto == paquete.getPort()) {
                //Si son iguales devuelve el cliente
                return c;
            }
        }
        return null;
    }

    //Metodo para enviar un mensaje al mismo usuario
    private void enviar(String msg, DatagramPacket paquete) throws IOException {
        DatagramPacket respuesta = new DatagramPacket(
                //Le respondemos
                msg.getBytes(),
                msg.length(),
                //Obtenemos el usuario mediante el paquete
                paquete.getAddress(),
                paquete.getPort()
        );
        //Enviamos la respuesta
        datagramSocket.send(respuesta);
    }

    //Metodo para enviar un mensaje a cualquier usuario especifico
    private void enviarDirecto(Usuario usuario, String msg) throws IOException {
        DatagramPacket paquete = new DatagramPacket(
                //Le enviamos el mensaje
                msg.getBytes(),
                msg.length(),
                //A este usuario
                usuario.direccion,
                usuario.puerto
        );
        //Lo mandamos
        datagramSocket.send(paquete);
    }

    public static void main(String[] args) {
        try {
            UDPServer server = new UDPServer();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    



