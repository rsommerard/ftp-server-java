package test.ftp.rsommerard;

import ftp.rsommerard.Server;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Romain on 10/02/15.
 */
public class ServerTest {

    @Test
    public void testStart() {
        Exception exception = null;
        Server server1 = null;

        try {
            server1 = new Server();
        } catch (Exception e) {
            exception = e;
        }
        assertEquals("Server can't start.", null, exception);
        assertEquals("Server running.", false, server1.isRunning());

        Server server2 = null;

        try {
            server2 = new Server();
        } catch (Exception e) {
            exception = e;
        }
        assertEquals("Server can't start.", "Address already in use", exception.getMessage());
    }

}
