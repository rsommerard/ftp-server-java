package ftp.rsommerard;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 27/01/15.
 */
public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(3636);
            System.out.println("[Server] Server started on: " + serverSocket.getInetAddress().getHostAddress() +
                    ":" + serverSocket.getLocalPort());

            while(true) {
                Socket socket = serverSocket.accept();
                new FtpRequest(socket);
            }
        } catch(Exception exception) {
            System.out.println("[Server] Error: " + exception.getMessage());
        }
    }

}
