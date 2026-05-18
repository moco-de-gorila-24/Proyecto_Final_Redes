/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor;
import Cliente.Usuario;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer {
    private static final int PORT = 7777; // Puerto TCP
    private ServerSocket serverSocket;
    public static ArrayList<Usuario> usuarios = new ArrayList<>();
    public static ArrayList<Usuario> listaEspera = new ArrayList<>();
            
    private static ArrayList<PrintWriter> mensajes = new ArrayList<>();

    public TCPServer() throws IOException {
        // ServerSocket espera conexiones entrantes
        serverSocket = new ServerSocket(PORT);
        System.out.println("=== SERVIDOR TCP INICIADO ===");
        System.out.println("Escuchando en puerto: " + PORT);
        System.out.println("Esperando clientes...\n");
    }

    public void start() {
        try {
            // Bucle infinito para aceptar conexiones
            while (true) {
                //Crea un hilo para cada cliente
                // accept() es bloqueante: espera a que un cliente se conecte
                Socket clientSocket = serverSocket.accept();
               
                System.out.println("[INFO] Cliente conectado: " + 
                                   clientSocket.getInetAddress().getHostAddress());

                // IMPORTANTE: Crear un hilo para atender a este cliente
                // Si no usamos hilos, el servidor se congela atendiendo solo a uno.
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        try {               
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar servidor: " + e.getMessage());
        }
    }

    // Clase interna para manejar la comunicación con CADA cliente
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * Este metodo es el encargado de inicializar el servidor y de la comunicacion entre los usuarios y tiene la validacion 
         * por si el servidor se llena y tambien cuenta con la logica para enviar mensaje privado 
         * 
         */
        @Override
        public void run() {
            try {
                // Flujo de entrada (Leer los mensajes del cliente)
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
                // Flujo de salida (Escribir al cliente) - autoflush true para enviar inmediato
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String nombreUsuario = in.readLine();
                InetAddress direccion = InetAddress.getByName("127.0.0.1");
                Usuario usuario = new Usuario(nombreUsuario.split(":")[0], direccion, PORT, out);
                usuarios.add(usuario);
                mensajes.add(out);

                if(usuarios.size() >= 5){
                    
                    out.println("[SERVIDOR]: Servidor lleno, estás en lista de espera...");
                    listaEspera.add(usuario);
                    mensajes.add(out);

                    // Bloquear al cliente hasta que haya espacio
                    while(usuarios.size() >= 5){
                        Thread.sleep(3000); // revisar cada 3 segundos
                    }
                    out.println("[SERVIDOR]: Hay espacio, entrando al chat...");
                    usuarios.add(listaEspera.getLast());
                    listaEspera.removeLast();
                }

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                        String[] msg = inputLine.split(" ");
                        System.out.println(msg[1]);
                        String[] nombre = inputLine.split(":");
                        
                        inputLine += " ";
                        inputLine += obtenerFechaActual();
                        System.out.println("[RECIBIDO] " + inputLine);
                        
                        String[] partes = inputLine.split(":", 2);
                        String remitente = partes[0].trim();
                        String cuerpo = partes[1].trim(); // "@Bob hola"

                        if (cuerpo.startsWith("@")) {
                            String[] palabras = cuerpo.split(" ", 2);
                            String destinatario = palabras[0].substring(1); 
                            String texto = palabras.length > 1 ? palabras[1] : "";
                            mensajePrivado(destinatario, "[Privado de " + remitente + "]: " + texto);
                            mensajePrivado(usuario.getNombre(), texto);
                        }
                        else{
                             mensajesGlobales(inputLine);
                        }

                }
            } catch (IOException e) {
                System.err.println("Error de conexión con cliente: " + e.getMessage());
            } catch (InterruptedException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (clientSocket != null) clientSocket.close();
                    System.out.println("[INFO] Cliente desconectado.");
                } catch (IOException e) {
                    // Ignorar
                }
            }
        }
        
        /**
        * Obtiene la fecha y hora actual del sistema para asignarlo a una variable formato que esta es la que regresa
        * con el formato dd/MM/yyyy HH:mm:ss.
        *
        * @return Fecha y hora actual en formato String.
        */
        public String obtenerFechaActual(){
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaHoraActual = LocalDateTime.now().format(formato);
            return fechaHoraActual;
        }   

 /**
 * Envía un mensaje a todos los usuarios conectados.
 *
 * @param mensaje Mensaje que será enviado globalmente.
 */
    public static void mensajesGlobales(String mensaje) {
        for (PrintWriter pw : mensajes) {
            pw.println(mensaje);
        }
    }
    
    /**
    * Envía un mensaje privado a un usuario específico.
    * lo que hace es validar en la lista de usuarios que este exista en primer lugar 
    * para despues enviar el mensaje al usuario con un printWriter
    *
    * @param nombre Nombre del usuario destinatario.
    * @param mensaje Mensaje que se enviará al usuario.
    */
    public void mensajePrivado(String nombre, String mensaje) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (nombre.trim().equalsIgnoreCase(usuarios.get(i).getNombre().trim())) { 
                PrintWriter pw = usuarios.get(i).getOut();
                    if (pw != null) {
                        pw.println("[SERVIDOR]: " + mensaje); 
                    }
                    return; 
                }
            }
        System.out.println("El usuario '" + nombre + "' no existe.");
    }
    
    /**
    * Verifica si un usuario existe en la lista de usuarios.
    *
    * @param nombre Nombre del usuario a buscar.
    * @return true si el usuario existe, false en caso contrario.
    */
    public boolean existeUsuario(String nombre){
            int i = 0;
            while(i < usuarios.size()){
                if(usuarios.get(i).getNombre().trim().equals(nombre.trim())){
                     return true;
                }
                i ++;
            }
            return false;
    }
}    
    public static void main(String[] args) {
        try {
            TCPServer server = new TCPServer();
            
            // Hook para cerrar limpiamente con Ctrl+C
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nCerrando servidor TCP...");
                server.stop();
            }));

            server.start();
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}