package test.ftp.rsommerard;

import ftp.rsommerard.Constants;
import ftp.rsommerard.Request;
import ftp.rsommerard.RequestTypeEnum;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Romain on 10/02/15.
 */
public class RequestTest {

    @Test
    public void testRequest() {
        String stringUSERRequest = "USER username";
        String stringUNKNOWNRequest = "OEZA username";
        String stringWithoutArgRequest = "LIST";

        Request request = new Request(stringUSERRequest);

        assertEquals("Request type error.", RequestTypeEnum.USER, request.getType());
        assertEquals("Request type string error.", Constants.USER_TYPE_REQUEST, request.getTypeString());
        assertEquals("Request argument error.", "username", request.getArgument());

        request = new Request(stringUNKNOWNRequest);
        assertEquals("Request type error.", RequestTypeEnum.UNKNOWN, request.getType());
        assertEquals("Request type string error.", Constants.UNKNOWN_TYPE_REQUEST, request.getTypeString());
        assertEquals("Request argument error.", "username", request.getArgument());

        request = new Request(stringWithoutArgRequest);
        assertEquals("Request type error.", RequestTypeEnum.LIST, request.getType());
        assertEquals("Request type string error.", Constants.LIST_TYPE_REQUEST, request.getTypeString());
        assertEquals("Request argument error.", Constants.NONE, request.getArgument());
    }

}
