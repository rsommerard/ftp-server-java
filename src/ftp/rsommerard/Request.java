package ftp.rsommerard;

/**
 * Created by Romain on 03/02/15.
 * Represente une requete entrante sur le serveur.
 */
public class Request {

    /**
     * Type de la commande.
     */
    private RequestTypeEnum type;

    /**
     * Type de la commande pour affichage et mappage.
     */
    private String typeString;

    /**
     * Argument de la commande.
     */
    private String argument;

    /**
     * Constructeur Request
     *
     * @param request
     */
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

    /**
     * Retourne le type de la commande.
     *
     * @return
     */
    public RequestTypeEnum getType() {
        return this.type;
    }

    /**
     * Retourne le type de la commande.
     *
     * @return
     */
    public String getTypeString() {
        return this.typeString;
    }

    /**
     * Retourne l'argument de la commande.
     *
     * @return
     */
    public String getArgument() {
        return this.argument;
    }

    /**
     * Determine et ajoute le type de la commande.
     *
     * @param type
     */
    private void setType(String type) {
        if(type.contains(Constants.USER_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.USER;
            this.typeString = Constants.USER_TYPE_REQUEST;
        }
        else if(type.contains(Constants.PASS_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.PASS;
            this.typeString = Constants.PASS_TYPE_REQUEST;
        }
        else if(type.contains(Constants.SYST_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.SYST;
            this.typeString = Constants.SYST_TYPE_REQUEST;
        }
        else if(type.contains(Constants.PWD_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.PWD;
            this.typeString = Constants.PWD_TYPE_REQUEST;
        }
        else if(type.contains(Constants.CDUP_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.CDUP;
            this.typeString = Constants.CDUP_TYPE_REQUEST;
        }
        else if(type.contains(Constants.CWD_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.CWD;
            this.typeString = Constants.CWD_TYPE_REQUEST;
        }
        else if(type.contains(Constants.PASV_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.PASV;
            this.typeString = Constants.PASV_TYPE_REQUEST;
        }
        else if(type.contains(Constants.LIST_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.LIST;
            this.typeString = Constants.LIST_TYPE_REQUEST;
        }
        else if(type.contains(Constants.QUIT_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.QUIT;
            this.typeString = Constants.QUIT_TYPE_REQUEST;
        }
        else if(type.contains(Constants.STOR_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.STOR;
            this.typeString = Constants.STOR_TYPE_REQUEST;
        }
        else if(type.contains(Constants.RETR_TYPE_REQUEST)) {
            this.type = RequestTypeEnum.RETR;
            this.typeString = Constants.RETR_TYPE_REQUEST;
        }
        else {
            this.type = RequestTypeEnum.UNKNOWN;
            this.typeString = Constants.UNKNOWN_TYPE_REQUEST;
        }
    }

}
