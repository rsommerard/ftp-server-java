package test.ftp.rsommerard;

import ftp.rsommerard.Constants;
import ftp.rsommerard.FtpRequest;
import ftp.rsommerard.Server;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Romain on 03/02/15.
 */
public class FtpRequestTest {

    public static Server server;

    @BeforeClass
    public static void initServer() throws Exception {
        server = new Server();
        new Thread(server).start();
    }

    @Test
    public void testConnection() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_220.replace("\r\n", ""), responseServer);
    }

    @Test
    public void testMultipleConnection() throws IOException {
        Socket socket1 = new Socket(server.getAddress(), server.getPiPort());
        Socket socket2 = new Socket(server.getAddress(), server.getPiPort());

        String response1Server = this.readMessage(socket1);
        String response2Server = this.readMessage(socket2);
        assertEquals("Response error.", Constants.MSG_220.replace("\r\n", ""), response1Server);
        assertEquals("Response error.", Constants.MSG_220.replace("\r\n", ""), response2Server);
    }

    @Test
    public void testProcessUser() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        //flush line BufferReader
        this.readMessage(socket);
        String cmd = "USER\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_332.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER username\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_332.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_331.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER anonymous\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_230.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;
    }

    @Test
    public void testProcessPass() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        //flush line BufferReader
        this.readMessage(socket);
        String cmd = "PASS\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_332.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS 12345\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_332.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_230.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;
    }

    @Test
    public void testProcessPasv() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        this.readMessage(socket);
        String cmd = "PASV\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_530.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASV\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_227.replace("\r\n", ""), responseServer);
        Socket dtpSocket = new Socket(server.getAddress(), server.getDtpPort());
        assertEquals("Response error.", true, dtpSocket.isConnected());
        socket.close();
        dtpSocket.close();
        socket = null;
        dtpSocket = null;
    }

    @Test
    public void testProcessSyst() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        this.readMessage(socket);
        String cmd = "SYST\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_530.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "SYST\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_215.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;
    }

    @Test
    public void testProcessQuit() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        String cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "QUIT\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_221.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;
    }

    @Test
    public void testProcessCwd() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        this.readMessage(socket);
        String cmd = "CWD\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_530.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER anonymous\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "CWD\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("200"));
        System.out.println(responseServer);
        assertTrue("Response error.", responseServer.contains("/tmp/server/public"));

        cmd = "CWD /tmp/\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("200"));
        assertTrue("Response error.", responseServer.contains("/tmp/server/public"));
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "CWD\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("200"));
        assertTrue("Response error.", responseServer.contains("/tmp/server/public"));

        cmd = "CWD /tmp/\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("200"));
        assertTrue("Response error.", responseServer.contains("/tmp"));
        socket.close();
        socket = null;
    }

    @Test
    public void testProcessPwd() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        this.readMessage(socket);
        String cmd = "PWD\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_530.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER anonymous\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PWD\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("257"));
        assertTrue("Response error.", responseServer.contains("server/public"));
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PWD\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("257"));
        assertTrue("Response error.", responseServer.contains("server/public"));
        socket.close();
        socket = null;
    }

    @Test
    public void testProcessCdup() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        this.readMessage(socket);
        String cmd = "CDUP\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_530.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER anonymous\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "CDUP\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("200"));
        assertTrue("Response error.", responseServer.contains("public"));
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "CDUP\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertTrue("Response error.", responseServer.contains("200"));
        assertTrue("Response error.", responseServer.contains("server"));
        assertTrue("Response error.", !responseServer.contains("public"));
        socket.close();
        socket = null;
    }

    @Test
    public void testProcessList() throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPiPort());

        this.readMessage(socket);
        String cmd = "LIST\r\n";
        this.sendMessage(socket, cmd);
        String responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_530.replace("\r\n", ""), responseServer);
        socket.close();
        socket = null;

        socket = new Socket(server.getAddress(), server.getPiPort());
        this.readMessage(socket);
        cmd = "USER demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASS demo\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        cmd = "PASV\r\n";
        this.sendMessage(socket, cmd);
        this.readMessage(socket);
        Socket dtpSocket = new Socket(server.getAddress(), server.getDtpPort());
        cmd = "LIST\r\n";
        this.sendMessage(socket, cmd);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_125.replace("\r\n", ""), responseServer);
        String dtpResponseServer = this.readMessage(dtpSocket);
        responseServer = this.readMessage(socket);
        assertEquals("Response error.", Constants.MSG_226.replace("\r\n", ""), responseServer);
        assertTrue("Response error.", dtpResponseServer.length() != 0);
        socket.close();
        dtpSocket.close();
        socket = null;
        dtpSocket = null;
    }

    private void sendMessage(Socket socket, String message) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeBytes(message);
        dataOutputStream.flush();
    }

    private String readMessage(Socket socket) throws IOException {
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return bufferReader.readLine();
    }
}
