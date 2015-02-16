package ftp.rsommerard;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 27/01/15.
 * Serveur FTP.
 */
public class Server implements Runnable {

    /**
     * Socket de commande.
     */
    private ServerSocket piServerSocket;

    /**
     * Socket de donnees.
     */
    private ServerSocket dtpServerSocket;

    /**
     * Etat du serveur.
     */
    private boolean running;

    /**
     * Constructeur Server.
     *
     * @throws Exception
     */
    public Server() throws Exception {
        this.piServerSocket = new ServerSocket(3636);
        this.dtpServerSocket = new ServerSocket(3637);
        this.running = false;
    }

    /**
     * Renvoie l'etat du serveur.
     *
     * @return
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Renvoie l'adresse du serveur.
     *
     * @return
     */
    public InetAddress getAddress() {
        return this.piServerSocket.getInetAddress();
    }

    /**
     * Renvoie le port d'ecoute de la socket de commande.
     *
     * @return
     */
    public int getPiPort() {
        return this.piServerSocket.getLocalPort();
    }

    /**
     * Renvoie le port d'ecoute de la socket de donnees.
     *
     * @return
     */
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
