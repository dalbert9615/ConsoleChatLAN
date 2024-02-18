package client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Client {
    public static void main(String[] args) {

        //obtencion de credenciales mediante db.properties (no se sube a github)
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config/db.properties"));
        } catch (IOException e) {
            System.err.printf("Error load 'db.properties' %s \n", e.getMessage());
            return;
        }
        final String serverIp = properties.getProperty("serverIp");
        final int port = Integer.parseInt(properties.getProperty("port"));
        //
        
        try {
            //nuevo Socket -> interfaz de comunicación mediante red
            Socket socket = new Socket(serverIp, port);
            System.out.printf("Connection on with server \n");

            //objetos interacción socket servidor-cliente
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));     
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            //objetos interacción consola-cliente
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            System.out.printf("Write your name:");
            String name = userInput.readLine();
            output.println(name);

            //funcion escrita como referenced method
            //lee mensaje recibido del servidor (otros clientes o servidor) y los publica en el chat
            Thread receivedMessageThread = new Thread(
                    () -> {
                        try {
                            String message;
                            while ((message = input.readLine()) != null) {
                                System.out.println(message);
                            }
                        } catch (IOException e) {
                            System.err.printf("Error received message from server: %s \n", e.getMessage());
                        }
                    });
            receivedMessageThread.start();
            
            //publica lo que el usuario escribe en la consola
            String message;
            while ((message = userInput.readLine()) != null) {
                output.println(message);
            }

            //cierra socket (comunicacion)
            socket.close();

        } catch (IOException e) {
            System.err.printf("Error connecting with server: %s \n", e.getMessage());
        }
    }
}
