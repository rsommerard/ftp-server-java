package ftp.rsommerard;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 27/01/15.
 */
public class Server implements Runnable {

    private ServerSocket piServerSocket;
    private ServerSocket dtpServerSocket;
    private boolean running;

    public Server() throws Exception {
        this.piServerSocket = new ServerSocket(3636);
        this.dtpServerSocket = new ServerSocket(3637);
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public InetAddress getAddress() {
        return this.piServerSocket.getInetAddress();
    }

    public int getPiPort() {
        return this.piServerSocket.getLocalPort();
    }

    public int getDtpPort() {
        return this.dtpServerSocket.getLocalPort();
    }

    @Override
    public void run() {
        try {
            this.running = true;
            System.out.println("[Server] Server started on: " + piServerSocket.getInetAddress().getHostAddress() +
                    ":" + piServerSocket.getLocalPort());

            while(this.running) {
                Socket socket = this.piServerSocket.accept();
                new Thread(new FtpRequest(socket, this.dtpServerSocket)).start();
            }
        } catch(Exception exception) {
            System.out.println("[Server] Error: " + exception.getMessage());
            this.running = false;
        }
    }
}
