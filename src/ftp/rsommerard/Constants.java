package ftp.rsommerard;

import java.net.InetAddress;

/**
 * Created by Romain on 27/01/15.
 */
public class Constants {
    public static final String MSG_125 = "125 Data connection already open; transfer starting.\r\n";

    public static final String MSG_200 = "200 Directory changed to \"DIRECTORY\"\r\n";
    public static final String MSG_215 = "215 Unix system type.\r\n";
    public static final String MSG_220 = "220 Service ready for new user.\r\n";
    public static final String MSG_221 = "221 Service closing control connection.\r\n";
    public static final String MSG_226 = "226 Closing data connection.\r\n";
    public static final String MSG_227 = "227 Entering Passive Mode (0,0,0,0,14,53).\r\n";
    public static final String MSG_230 = "230 User logged in, proceed.\r\n";
    public static final String MSG_257 = "257 \"DIRECTORY\"\r\n";

    public static final String MSG_331 = "331 User name okay, need password.\r\n";
    public static final String MSG_332 = "332 Need account for login.\r\n";

    public static final String MSG_502 = "502 Command not implemented.\r\n";
    public static final String MSG_530 = "530 Not logged in.\r\n";

    public static final String USER_TYPE_USER = "USER";
    public static final String PASS_TYPE_REQUEST = "PASS";
    public static final String SYST_TYPE_REQUEST = "SYST";
    public static final String PWD_TYPE_REQUEST = "PWD";
    public static final String LIST_TYPE_REQUEST = "LIST";
    public static final String QUIT_TYPE_REQUEST = "QUIT";
    public static final String STOR_TYPE_REQUEST = "STOR";
    public static final String RETR_TYPE_REQUEST = "RETR";
    public static final String CDUP_TYPE_REQUEST = "CDUP";
    public static final String CWD_TYPE_REQUEST = "CWD";
    public static final String PASV_TYPE_REQUEST = "PASV";
    public static final String UNKNOWN_TYPE_REQUEST = "UNKNOWN";

    public static final String PARENT_DIRECTORY = "..";
    public static final String CURRENT_DIRECTORY = ".";
    public static final String RACINE_DIRECTORY = "/";

    public static final String NONE = "NONE";

    public static final String ANONYMOUS_USER = "anonymous";

    public static final String DEMO_USER = "demo";
    public static final String DEMO_PASS = "demo";

}
