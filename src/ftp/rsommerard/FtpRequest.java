package ftp.rsommerard;

import com.sun.tools.internal.jxc.apt.Const;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Romain on 27/01/15.
 */
public class FtpRequest implements Runnable {

    private Socket piSocket;
    private Socket dtpSocket;
    private BufferedReader piBufferReader;
    private DataOutputStream piDataOutputStream;
    private ServerSocket dtpServerSocket;

    private String directory;

    private boolean anonymousUser;
    private boolean loggedUser;

    private boolean process;

    public FtpRequest(Socket piSocket) throws Exception {
        this.piSocket = piSocket;
        this.piBufferReader = new BufferedReader(new InputStreamReader(this.piSocket.getInputStream()));
        this.piDataOutputStream = new DataOutputStream(this.piSocket.getOutputStream());
        this.dtpServerSocket = new ServerSocket(3637);
        this.directory = System.getProperty("user.dir") + "/server/public/";
        this.anonymousUser = true;
        this.loggedUser = false;
        this.process = true;
        new Thread(this).start();
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
                this.processSyst(request);
                break;
            case PWD:
                this.processPwd(request);
                break;
            case CDUP:
                this.processCdup(request);
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
                this.processQuit(request);
                break;
            case PASV:
                this.processPasv(request);
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

    private void processUser(Request request) throws Exception {
        System.out.println("[FtpRequest::processUser]");
        if(Constants.ANONYMOUS_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
            this.loggedUser = true;
            this.anonymousUser = true;
        }
        else if(Constants.DEMO_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_331);
        }
        else {
            this.sendMessage(Constants.MSG_332);
        }
    }

    private void processPass(Request request) throws Exception {
        System.out.println("[FtpRequest::processPass]");

        if(Constants.DEMO_PASS.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
            this.anonymousUser = false;
            this.loggedUser = true;
        }
        else {
            this.sendMessage(Constants.MSG_332);
        }

    }

    private void processRetr(Request request) throws Exception {
        System.out.println("[FtpRequest::processRetr]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendFile(request.getArgument());
    }

    private void processStor(Request request) throws Exception {
        System.out.println("[FtpRequest::processStor]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.receiveFile(request.getArgument());
    }

    private void processCwd(Request request) throws Exception {
        System.out.println("[FtpRequest::processCwd]");

        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }

        if(this.anonymousUser) {
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
                this.directory = request.getArgument();
            }
            else {
                if(Constants.RACINE_DIRECTORY.equals(this.directory)) {
                    this.directory += request.getArgument();
                }
                else {
                    this.directory += "/" + request.getArgument();
                }
            }
        }
        this.sendMessage(Constants.MSG_200.replace("DIRECTORY", this.directory));
    }

    private void processCdup(Request request) throws Exception {
        System.out.println("[FtpRequest::processCdup]");

        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }

        if(this.anonymousUser) {
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

    private void processSyst(Request request) throws Exception {
        System.out.println("[FtpRequest::processSyst]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendMessage(Constants.MSG_215);
    }

    private void processPwd(Request request) throws Exception {
        System.out.println("[FtpRequest::processPwd]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendMessage(Constants.MSG_257.replace("DIRECTORY", this.directory));
    }

    private void processPasv(Request request) throws Exception {
        System.out.println("[FtpRequest::processPasv]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendMessage(Constants.MSG_227);
        this.dtpSocket = dtpServerSocket.accept();
    }

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

    private void processQuit(Request request) throws Exception {
        System.out.println("[FtpRequest::processQuit]");
        if(!loggedUser) {
            this.sendMessage(Constants.MSG_530);
            return;
        }
        this.sendMessage(Constants.MSG_221);
        this.process = false;
    }

    private void sendMessage(String message) throws Exception {
        System.out.println("[FtpRequest::sendMessage] " + message);
        this.piDataOutputStream.writeBytes(message);
        this.piDataOutputStream.flush();
    }

    private void sendList(String list) throws Exception {
        System.out.println("[FtpRequest::sendList]");
        this.sendMessage(Constants.MSG_125);

        DataOutputStream dtpDataOutputStream = new DataOutputStream(this.dtpSocket.getOutputStream());

        dtpDataOutputStream.writeBytes(list);
        dtpDataOutputStream.flush();

        this.sendMessage(Constants.MSG_226);
        this.dtpSocket.close();
        this.dtpSocket = null;
    }

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
        this.dtpSocket = null;
    }

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
        this.dtpSocket = null;
    }
}
