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

public class TCPServer {
    private static final int PORT = 7777; // Puerto TCP
    private ServerSocket serverSocket;
    public static ArrayList<Usuario> usuarios = new ArrayList<>();
            
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

        @Override
        public void run() {
            try {
                // Flujo de entrada (Leer los mensajes del cliente)
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // Flujo de salida (Escribir al cliente) - autoflush true para enviar inmediato
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                // Leer línea por línea
                while ((inputLine = in.readLine()) != null) {
                    
                    System.out.println("[RECIBIDO] " + inputLine);
                    
                    // Enviar confirmación al cliente (Echo)
                    out.println("SERVIDOR: Mensaje recibido -> " + inputLine + " [" + obtenerFechaActual() + "]");
                }
            } catch (IOException e) {
                System.err.println("Error de conexión con cliente: " + e.getMessage());
            } finally {
                try {
                    if (clientSocket != null) clientSocket.close();
                    System.out.println("[INFO] Cliente desconectado.");
                } catch (IOException e) {
                    // Ignorar
                }
            }
        }
        public String obtenerFechaActual(){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHoraActual = LocalDateTime.now().format(formato);
        return fechaHoraActual;
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