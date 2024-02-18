package server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/*Implementa runnable para ejecutarse como un hilo independiente 
 * y los diferentes clientes puedan enviar mensajes a la vez
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private String nameClient;

    public ClientHandler(Socket socket, String nameClient) {
        this.socket = socket;
        this.nameClient = nameClient;
    }

    /*Hilo que recibe el mensaje de un cliente
     * se crea un flujo de entrada de datos y los pasa a la función
     * de transmision para publicar el mensaje a todos los clientes
     * mediante la funcion "broadcastMessage" de "Server"
     */
    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String message;
            while ((message = input.readLine()) != null) {
                Server.broadcastMessage(message, nameClient);
            }
        } catch (IOException e) {
            // destaca un mensaje de error en la consola
            System.err.printf("Error receiving message from client: %s \n", e.getMessage());
        }
    }
}
