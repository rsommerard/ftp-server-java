package ftp.rsommerard;

/**
 * Created by Romain on 03/02/15.
 */
public class Request {

    private RequestType type;
    private String typeString;
    private String argument;

    public Request(String request) {
        String[] _request = request.split(" ");
        this.setType(_request[0]);
        if(_request.length > 1) {
            this.argument = _request[1];
        }
        else {
            this.argument = Constants.NONE;
        }
    }

    public RequestType getType() {
        return this.type;
    }

    public String getTypeString() {
        return this.typeString;
    }

    public String getArgument() {
        return this.argument;
    }

    private void setType(String type) {
        if(type.contains(Constants.USER_TYPE_USER)) {
            this.type = RequestType.USER;
            this.typeString = Constants.USER_TYPE_USER;
        }
        else if(type.contains(Constants.PASS_TYPE_REQUEST)) {
            this.type = RequestType.PASS;
            this.typeString = Constants.PASS_TYPE_REQUEST;
        }
        else if(type.contains(Constants.SYST_TYPE_REQUEST)) {
            this.type = RequestType.SYST;
            this.typeString = Constants.SYST_TYPE_REQUEST;
        }
        else if(type.contains(Constants.FEAT_TYPE_REQUEST)) {
            this.type = RequestType.FEAT;
            this.typeString = Constants.FEAT_TYPE_REQUEST;
        }
        else if(type.contains(Constants.PWD_TYPE_REQUEST)) {
            this.type = RequestType.PWD;
            this.typeString = Constants.PWD_TYPE_REQUEST;
        }
        else {
            this.type = RequestType.UNKNOWN;
            this.typeString = Constants.UNKNOWN_TYPE_REQUEST;
        }
    }

}
