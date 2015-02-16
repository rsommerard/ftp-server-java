package ftp.rsommerard;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 10/02/15.
 * Classe principale.
 * Lance le serveur dans un thread.
 */
public class Main {

    public static void main(String[] args) {
        try {
            Server server = new Server();
            new Thread(server).start();
            while(server.isRunning());
        } catch(Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

}
