package ftp.rsommerard;

/**
 * Created by Romain on 03/02/15.
 */
public enum RequestTypeEnum {
    UNKNOWN,
    USER,
    PASS,
    SYST,
    LIST,
    QUIT,
    STOR,
    RETR,
    CDUP,
    PASV,
    CWD,
    PWD
}
