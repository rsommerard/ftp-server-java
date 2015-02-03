package ftp.rsommerard;

import com.sun.tools.internal.jxc.apt.Const;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static ftp.rsommerard.RequestType.USER;

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
        System.out.println("[FtpRequest::run] Thread FtpRequest created");

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

        System.out.println("[FtpRequest::processRequest] Request: " + requestString);

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
            case FEAT:
                this.processFeat(request);
                break;
            case PWD:
                this.processPwd(request);
                break;
            default:
                throw new RuntimeException("Request Unknown: " + requestString);
        }
    }

    private void processUser(Request request) throws Exception {
        System.out.println("[FtpRequest::processUser] Username: " + request.getArgument());

        if(Constants.ANONYMOUS_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
        }
        if(Constants.DEMO_USER.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_331);
        }

        this.processRequest();
    }

    private void processPass(Request request) throws Exception {
        System.out.println("[FtpRequest::processPass] Password: " + request.getArgument());

        if(Constants.DEMO_PASS.equals(request.getArgument())) {
            this.sendMessage(Constants.MSG_230);
        }

        this.processRequest();
    }

    private void processFeat(Request request) throws Exception {
        System.out.println("[FtpRequest::processFeat] Feat: Not implemented");
        this.sendMessage(Constants.MSG_202);
        this.processRequest();
    }

    private void processSyst(Request request) throws Exception {
        System.out.println("[FtpRequest::processSyst] System: Unix");
        this.sendMessage(Constants.MSG_215.replace("NAME", "Unix"));
        this.processRequest();
    }

    private void processPwd(Request request) throws Exception {
        System.out.println("[FtpRequest::processPwd]");
        this.sendMessage(Constants.MSG_257.replace("DIRECTORY", System.getProperty("user.dir")));
        this.processRequest();
    }

    private void sendMessage(String message) throws Exception {
        System.out.println("[FtpRequest::sendMessage] Message: " + message);

        this.dataOutputStream.writeBytes(message);
        this.dataOutputStream.flush();
    }
}
