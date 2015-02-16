package ftp.rsommerard;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 27/01/15.
 * Traite les commandes entrantes.
 */
public class FtpRequest implements Runnable {

    /**
     * Socket de commande.
     */
    private Socket piSocket;

    /**
     * Socket de transfert.
     */
    private Socket dtpSocket;

    /**
     * Buffer permettant la recuperation de commande.
     */
    private BufferedReader piBufferReader;

    /**
     * Stream d'envoie de reponses sur le canal de commande.
     */
    private DataOutputStream piDataOutputStream;

    /**
     * ServerSocket de creation du canal de donnees.
     */
    private ServerSocket dtpServerSocket;

    /**
     * Chemin du repertoire du serveur ftp.
     */
    private String directory;

    /**
     * Cette variable indique si l'utilisateur est un utilisateur anonyme.
     */
    private boolean anonymousUser;

    /**
     * Cette variable indique si l'utilisateur est connecte.
     */
    private boolean loggedUser;

    /**
     * Nom d'utilisateur du thread en cours.
     */
    private String username;

    /**
     * Indique a la classe processRequest si elle doit s'arreter ou non.
     */
    private boolean process;

    /**
     * Constructeur FtpRequest
     *
     * @param piSocket
     * @param dtpServerSocket
     * @throws Exception
     */
    public FtpRequest(Socket piSocket, ServerSocket dtpServerSocket) throws Exception {
        this.piSocket = piSocket;
        this.piBufferReader = new BufferedReader(new InputStreamReader(this.piSocket.getInputStream()));
        this.piDataOutputStream = new DataOutputStream(this.piSocket.getOutputStream());
        this.dtpServerSocket = dtpServerSocket;
        this.directory = "/tmp/server/public";
        this.anonymousUser = true;
        this.loggedUser = false;
        this.process = true;
        this.username = Constants.ANONYMOUS_USER;
    }

    @Override
    public void run() {
        System.out.println("[FtpRequest::run]");

        try {
            this.sendMessage(Constants.MSG_220);
            this.processRequest();
        } catch (Exception exception) {
            System.out.println("[FtpRequest::run] Error: " + exception.getMessage());
        }
    }

    /**
     * Lit et dispatche les commandes aux methodes adaptees.
     *
     * @throws Exception
     */
    private void processRequest() throws Exception {
        String requestString = this.piBufferReader.readLine();
        Request request = new Request(requestString);

        System.out.println("[FtpRequest::processRequest] " + requestString);

        switch(request.getType()) {
            case USER:
                this.processUser(request);
                break;
            case PASS:
                this.processPass(request);
                break;
            case SYST:
                this.processSyst();
                break;
            case PWD:
                this.processPwd();
                break;
            case CDUP:
                this.processCdup();
                break;
            case LIST:
                this.processList(request);
                break;
            case STOR:
                this.processStor(request);
                break;
            case RETR:
                this.processRetr(request);
                break;
            case QUIT:
                this.processQuit();
                break;
            case PASV:
                this.processPasv();
                break;
            case CWD:
                this.processCwd(request);
                break;
            default:
                this.sendMessage(Constants.MSG_502);
                break;
        }

        if(this.process) {
            this.processRequest();
        }
        else {
            this.piSocket.close();
        }
    }

    /**
     * Traite la commande USER.
     *
     * @param request
     * @throws Exception
     */
    private void processUser(Request request) throws Exception {
        System.out.println("[FtpRequest::processUser]");
        if(Constants.ANONYMOUS_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
            this.loggedUser = true;
            this.anonymousUser = true;
            this.username = Constants.ANONYMOUS_USER;
        }
        else if(Constants.DEMO_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_331);
            this.username = Constants.DEMO_USER;
        }
        else {
            this.sendMessage(Constants.MSG_332);
        }
    }

    /**
     * Traite la commande PASS.
     *
     * @param request
     * @throws Exception
     */
    private void processPass(Request request) throws Exception {
        System.out.println("[FtpRequest::processPass]");

        if(Constants.DEMO_USER.equals(this.username) && Constants.DEMO_PASS.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
            this.anonymousUser = false;
            this.loggedUser = true;
        }
        else {
            this.sendMessage(Constants.MSG_332);
        }
    }

    /**
     * Traite la commande RETR.
     * L'utilisateur doit etre connecte.
     *
     * @param request
     * @throws Exception
     */
    private void processRetr(Request request) throws Exception {
        System.out.println("[FtpRequest::processRetr]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendFile(request.getArgument());
    }

    /**
     * Traite la commande STOR.
     * L'utilisateur doit etre connecte.
     *
     * @param request
     * @throws Exception
     */
    private void processStor(Request request) throws Exception {
        System.out.println("[FtpRequest::processStor]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.receiveFile(request.getArgument());
    }

    /**
     * Traite la commande CWD.
     * L'utilisateur doit etre connecte.
     * L'utilisateur anonyme ne peut pas changer de dossier.
     *
     * @param request
     * @throws Exception
     */
    private void processCwd(Request request) throws Exception {
        System.out.println("[FtpRequest::processCwd]");

        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }

        if(this.anonymousUser) {
            this.sendMessage(Constants.MSG_200.replace("DIRECTORY", this.directory));
            return;
        }

        if(Constants.PARENT_DIRECTORY.equals(request.getArgument())) {
            if(!this.directory.equals("/")) {
                String[] dir = this.directory.split("/");
                this.directory = "/";
                for(int i = 1; i < dir.length - 1; i++) {
                    this.directory += dir[i] + "/";
                }
            }
        }
        else if(!Constants.CURRENT_DIRECTORY.equals(request.getArgument())) {
            if(request.getArgument().startsWith("/")) {
                if(!Constants.NONE.equals(request.getArgument())) {
                    this.directory = request.getArgument();
                }
            }
            else {
                if(Constants.RACINE_DIRECTORY.equals(this.directory)) {
                    if(!Constants.NONE.equals(request.getArgument())) {
                        this.directory += request.getArgument();
                    }
                }
                else {
                    if(!Constants.NONE.equals(request.getArgument())) {
                        this.directory += "/" + request.getArgument();
                    }
                }
            }
        }
        this.sendMessage(Constants.MSG_200.replace("DIRECTORY", this.directory));
    }

    /**
     * Traite la commande CDUP.
     * L'utilisateur doit etre connecte.
     * L'utilisateur anonyme ne peut pas changer de dossier.
     *
     * @throws Exception
     */
    private void processCdup() throws Exception {
        System.out.println("[FtpRequest::processCdup]");

        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }

        if(this.anonymousUser) {
            this.sendMessage(Constants.MSG_200.replace("DIRECTORY", this.directory));
            return;
        }

        if(!this.directory.equals("/")) {
            String[] dir = this.directory.split("/");
            this.directory = "/";
            for(int i = 1; i < dir.length - 1; i++) {
                this.directory += dir[i] + "/";
            }
        }
        this.sendMessage(Constants.MSG_200.replace("DIRECTORY", this.directory));
    }

    /**
     * Traite la commande SYST.
     * L'utilisateur doit etre connecte.
     *
     * @throws Exception
     */
    private void processSyst() throws Exception {
        System.out.println("[FtpRequest::processSyst]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendMessage(Constants.MSG_215);
    }

    /**
     * Traite la commande PWD.
     * L'utilisateur doit etre connecte.
     *
     * @throws Exception
     */
    private void processPwd() throws Exception {
        System.out.println("[FtpRequest::processPwd]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendMessage(Constants.MSG_257.replace("DIRECTORY", this.directory));
    }

    /**
     * Traite la commande PASV.
     * L'utilisateur doit etre connecte.
     *
     * @throws Exception
     */
    private void processPasv() throws Exception {
        System.out.println("[FtpRequest::processPasv]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendMessage(Constants.MSG_227);
        this.dtpSocket = dtpServerSocket.accept();
    }

    /**
     * Traite la commande LIST.
     * L'utilisateur doit etre connecte.
     * L'utilisateur anonyme ne peut lister que le dossier public.
     *
     * @param request
     * @throws Exception
     */
    private void processList(Request request) throws Exception {
        System.out.println("[FtpRequest::processList]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }

        File folder;
        if(this.anonymousUser) {
            folder = new File(this.directory);
        }
        else {
            if (Constants.NONE.equals(request.getArgument())) {
                folder = new File(this.directory);
            }
            else {
                folder = new File(request.getArgument());
            }
        }

        String list = "";
        for (File fileEntry : folder.listFiles()) {
            list += fileEntry.getName() + "\r\n";
        }
        this.sendList(list);
    }

    /**
     * Traite la commande QUIT.
     *
     * @throws Exception
     */
    private void processQuit() throws Exception {
        System.out.println("[FtpRequest::processQuit]");
        this.sendMessage(Constants.MSG_221);
        this.process = false;
    }

    /**
     * Envoie un message sur la socket de commande.
     *
     * @param message
     * @throws Exception
     */
    private void sendMessage(String message) throws Exception {
        System.out.println("[FtpRequest::sendMessage] " + message);
        this.piDataOutputStream.writeBytes(message);
        this.piDataOutputStream.flush();
    }

    /**
     * Envoie la liste des fichiers du dossier sur la socket de donnees.
     * Envoie des messages sur la socket de commande.
     *
     * @param list
     * @throws Exception
     */
    private void sendList(String list) throws Exception {
        System.out.println("[FtpRequest::sendList]");
        this.sendMessage(Constants.MSG_125);

        DataOutputStream dtpDataOutputStream = new DataOutputStream(this.dtpSocket.getOutputStream());

        dtpDataOutputStream.writeBytes(list);
        dtpDataOutputStream.flush();

        this.sendMessage(Constants.MSG_226);
        this.dtpSocket.close();
    }

    /**
     * Envoie le fichier demande sur le canal de donnees.
     * Envoie des messages sur la socket de commande.
     *
     * @param filename
     * @throws Exception
     */
    private void sendFile(String filename) throws Exception {
        System.out.println("[FtpRequest::sendFile]");
        this.sendMessage(Constants.MSG_125);

        DataOutputStream dtpDataOutputStream = new DataOutputStream(this.dtpSocket.getOutputStream());

        File file = new File(this.directory + filename);

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[this.dtpSocket.getSendBufferSize()];
        int bytesRead = 0;

        while((bytesRead = fileInputStream.read(buffer))>0)
        {
            dtpDataOutputStream.write(buffer,0,bytesRead);
        }

        fileInputStream.close();

        dtpDataOutputStream.flush();

        this.sendMessage(Constants.MSG_226);
        this.dtpSocket.close();
    }

    /**
     * Recoit le fichier sur le canal de donnees et l'enregistre sur le serveur.
     * Envoie des messages sur la socket de commande.
     *
     * @param filename
     * @throws Exception
     */
    private void receiveFile(String filename) throws Exception {
        System.out.println("[FtpRequest::receiveFile]");
        this.sendMessage(Constants.MSG_125);

        InputStream dtpInputStream = this.dtpSocket.getInputStream();

        File file = new File(this.directory + filename);

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        byte[] buffer = new byte[this.dtpSocket.getReceiveBufferSize()];
        int bytesRead = 0;

        while ((bytesRead = dtpInputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        fileOutputStream.close();
        fileOutputStream.flush();

        this.sendMessage(Constants.MSG_226);
        this.dtpSocket.close();
    }
}
