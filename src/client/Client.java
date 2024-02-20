package client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Client {
    private static final String EXIT_COMMAND = "exit";
    public static void main(String[] args) {

        Scanner sc=new Scanner(System.in);
        String serverIp="";
        int port=0;
        while (true) {
            try {
                System.out.printf("Enter server IP:");
                serverIp=sc.nextLine();
                Inet4Address.getByName(serverIp);
                break;
            } catch (UnknownHostException e) {
                System.err.printf("Invalid IP address, please enter a valid IP address. %s \n",e.getMessage());
            }
        }
        while (true) {
            try {
                System.out.printf("Enter port:");
                port=Integer.parseInt(sc.nextLine());
                if(port>0 && port<=65535){
                    break;
                }
                else{
                    System.err.printf("Invalid port number. Valid:[1-65535] \n");
                }
            } catch (NumberFormatException e) {
                System.err.printf("Invalid port number, please enter a valid integer number. %s \n",e.getMessage());
            }
        }

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
                if(message.equals(EXIT_COMMAND)){
                    System.out.printf("Closing client... \n");
                    break;
                }
                output.println(message);
            }

            //cierra socket (comunicacion)
            socket.close();

        } catch (IOException e) {
            System.err.printf("Error connecting with server: %s \n", e.getMessage());
        }
    }
}
