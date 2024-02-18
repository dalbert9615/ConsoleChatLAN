package Zdosc_jose;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Conexion {
    private int puerto; // Puerto para la conexión
    private String host; // Host para la conexión
    protected String mensajeServidor; // Mensajes entrantes (recibidos) en el servidor
    protected ServerSocket ss; // Socket del servidor
    protected Socket cs; // Socket del cliente
    protected DataOutputStream salidaServidor, salidaCliente; // Flujo de datos de salida

    public Conexion(String tipo) throws IOException { // Constructor
        loadConf();
        if (tipo.equalsIgnoreCase("servidor")) {
            ss = new ServerSocket(this.puerto);// Se crea el socket para el servidor en puerto 1234
            cs = new Socket(); // Socket para el cliente
        } else {
            cs = new Socket(this.host, this.puerto); // Socket para el cliente en localhost en puerto 1234
        }
    }

    /*Cambio para usar variables sensibles en una carpeta config/db.properties que no se suba a github */
    private void loadConf() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("config/db.properties"));
        this.puerto = Integer.parseInt(props.getProperty("puerto"));
        this.host = props.getProperty("host");
    }
}