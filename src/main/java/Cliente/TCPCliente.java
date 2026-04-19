package Cliente;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class TCPCliente {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 7777;
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread receiverThread;
    private boolean connected = false;
    
    public static String usernameIn;

    public TCPCliente() throws IOException {
        // El cliente inicia el proceso de conexión (Three-way handshake)
        socket = new Socket(SERVER_IP, SERVER_PORT);
        connected = true;

        // Configurar flujos de datos (Streams)
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("=== CLIENTE TCP CONECTADO ===");
        System.out.println("Conectado a: " + SERVER_IP + ":" + SERVER_PORT);
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingresa tu nombre de usuario: ");
        usernameIn = scanner.nextLine().trim();
        System.out.println("Escribe 'exit' para desconectarte.\n");
    }

    // Hilo para recibir mensajes del servidor asincrónamente
    public void startReceiving() {
        receiverThread = new Thread(() -> {
            try {
                String response;
                // Mientras haya conexión, leer líneas del servidor
                while (connected && (response = in.readLine()) != null) {
                    System.out.println(response);
                    System.out.print("> "); // Mostrar prompt de nuevo
                }
            } catch (IOException e) {
                if (connected) {
                    System.err.println("\nConexión perdida con el servidor.");
                }
            }
        });
        receiverThread.start();
    }

    // Método para enviar mensaje
    public void sendMessage(String message) {
        if (out != null) {
            // Formato: Usuario: Mensaje
            String fullMessage = usernameIn + ": " + message;
            out.println(fullMessage); 
        }
    }

    public void stop() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Cierra el stream y el socket
            }
            if (receiverThread != null) {
                receiverThread.join(1000);
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar cliente: " + e.getMessage());
        }
        System.out.println("Cliente desconectado.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TCPCliente client = null;

        try {
            client = new TCPCliente();
            client.startReceiving();

            System.out.println("--- Comandos ---");
            System.out.println("   <mensaje>    - Enviar al servidor");
            System.out.println("   exit         - Salir");
            System.out.println();

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    client.sendMessage("SE HA DESCONECTADO"); // Aviso al server
                    client.stop();
                    break;
                } else if (!input.isEmpty()) {
                    client.sendMessage(input);
                }
            }

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        } finally {
            if (client != null) client.stop();
            scanner.close();
        }
    }
}
