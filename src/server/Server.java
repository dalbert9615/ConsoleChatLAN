package server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    private static final String EXIT_COMMAND = "exit";
    // Lista de clientes conectados.
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {

        // obtencion de credenciales mediante db.properties (no se sube a github)
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config/db.properties"));
        } catch (IOException e) {
            System.err.printf("Error load 'db.properties' %s \n", e.getMessage());
            return;
        }
        final int port = Integer.parseInt(properties.getProperty("port"));

        // apertura de socket -> comunicación con posibles clientes
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Server ON, waiting clients...\n");

            Thread consoleInpuThread = new Thread(
                    () -> {
                        Scanner sc = new Scanner(System.in);
                        while (true) {
                            String input = sc.nextLine();
                            if (input.equals(EXIT_COMMAND)) {
                                System.out.printf("Closing server... \n");
                                closeServer();
                                break;
                            }
                        }
                    }
            );
            consoleInpuThread.start();

            // escucha siempre activa
            while (true) {

                // cuando hay una conexion valida da la bienvenida
                // y procede a instanciar los objetos para comunicación servidor-clientes
                Socket socket = serverSocket.accept();
                System.out.printf("New client connect.\n");

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                // obtiene el nombre del cliente y lo guarda para sus futuros mensajes
                // esto se podría guardar posteriormente en una BD
                String nameClient = input.readLine();
                System.out.printf("Client %s connected.\n", nameClient);
                clients.put(nameClient, output);
                output.printf("Welcome, %s!\n", nameClient);

                // hilo que manipula la comunicación con el cliente
                Thread clientHandlerThread = new Thread(new ClientHandler(socket, nameClient));
                clientHandlerThread.start();
            }

        } catch (IOException e) {
            System.err.printf("Error init Server: %s \n", e.getMessage());
        }
    }

    /*
     * funcion que publica el mensaje enviado por alguien a los diferentes clientes
     */
    public static void broadcastMessage(String message, String remitent) {
        for (PrintWriter writer : clients.values()) {
            writer.printf("%s : %s\n", remitent, message);
        }
    }

    private static void closeServer(){
        for(PrintWriter writer:clients.values()){
            writer.println("Server is shutting down.");
            writer.close();
        }
        clients.clear();
        System.exit(0);
    }

    public static void removeClient(String nameClient){
        clients.remove(nameClient);
        System.out.printf("Client %s disconnected. \n",nameClient);
    }
}
