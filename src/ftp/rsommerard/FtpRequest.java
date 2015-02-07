package ftp.rsommerard;

import com.sun.tools.internal.jxc.apt.Const;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;


/**
 * Created by Romain on 27/01/15.
 */
public class FtpRequest implements Runnable {

    private Socket socket;
    private BufferedReader bufferReader;
    private DataOutputStream dataOutputStream;

    public FtpRequest(Socket socket) throws Exception {
        this.socket = socket;
        this.bufferReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
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
        String requestString = this.bufferReader.readLine();
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
            case LIST:
                this.processList(request);
                break;
            case QUIT:
                this.processQuit(request);
                break;
            default:
                this.sendMessage(Constants.MSG_502);
                this.processRequest();
                break;
        }
    }

    private void processUser(Request request) throws Exception {
        System.out.println("[FtpRequest::processUser]");

        if(Constants.ANONYMOUS_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
        }
        if(Constants.DEMO_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_331);
        }

        this.processRequest();
    }

    private void processPass(Request request) throws Exception {
        System.out.println("[FtpRequest::processPass]");

        if(Constants.DEMO_PASS.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
        }

        this.processRequest();
    }

    private void processSyst(Request request) throws Exception {
        System.out.println("[FtpRequest::processSyst]");
        this.sendMessage(Constants.MSG_215.replace("NAME", "Unix"));
        this.processRequest();
    }

    private void processPwd(Request request) throws Exception {
        System.out.println("[FtpRequest::processPwd]");
        this.sendMessage(Constants.MSG_257.replace("DIRECTORY", System.getProperty("user.dir") + "/server/public/"));
        this.processRequest();
    }

    private void processList(Request request) throws Exception {
        System.out.println("[FtpRequest::processList]");
        this.sendMessage(Constants.MSG_125);
        File folder = new File(System.getProperty("user.dir") + "/server/public/");
        String files = "";
        for (File fileEntry : folder.listFiles()) {
            this.sendMessage(fileEntry.getName() + "\n");
        }
        this.sendMessage(Constants.MSG_226);
        this.processRequest();
    }

    private void processQuit(Request request) throws Exception {
        System.out.println("[FtpRequest::processQuit]");
        this.sendMessage(Constants.MSG_221);
        this.socket.close();
    }

    private void sendMessage(String message) throws Exception {
        System.out.println("[FtpRequest::sendMessage] " + message);

        this.dataOutputStream.writeBytes(message);
        this.dataOutputStream.flush();
    }
}
