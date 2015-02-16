Serveur FTP en Java
Romain SOMMERARD
17/02/15


# Introduction

Ce projet est une implémentation de serveur FTP en Java.
L'implémentation se base sur la norme RFC 959.
Les commandes implémentées sont: USER, PASS, SYST, LIST, QUIT, STOR, RETR, CDUP,
PASV, CWD et PWD.


# Architecture

L'application est regroupée dans deux packages. Le premier contient le projet.
L'autre regroupe les tests unitaires.

Une classe Contants regroupe toutes les constantes du projet (message de retour,
commandes...).

La classe Main est la classe principale qui lance un nouveau serveur.

La classe Server se lance dans un thread. Elle permet de recevoir les requêtes
entrantes. Lorsqu'une nouvelle requête arrive, la classe Server crée une
instance de FtpRequest dans un nouveau thread. Cela permet de traiter plusieurs
requêtes en même temps.

FtpRequest dispatche les requêtes vers les bonnes méthodes de traitements.

La classe Request fait le mappage des requêtes. Elle permet un traitement des
requêtes plus facile.


# Code Samples

- Classe FtpRequest, méthode run. Toutes les exceptions levées par les méthodes de
traitements sont récupérées à un seul endroit.

```java
  @Override
  public void run() {
    try {
      this.sendMessage(Constants.MSG_220);
      this.processRequest();
    } catch (Exception exception) {
      System.out.println("[FtpRequest::run] Error: " + exception.getMessage());
    }
  }
```

- Classe FtpRequest, méthode processRequest qui dispatche les requêtes reçues.

```java
  private void processRequest() throws Exception {
    String requestString = this.piBufferReader.readLine();
    Request request = new Request(requestString);

    switch(request.getType()) {
      case USER:
        this.processUser(request);
        break;
      case PASS:
        this.processPass(request);
        break;

            ...

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
```

- Classe FtpRequest, méthode processCwd qui permet de changer le dossier courant.

```java
  private void processCwd(Request request) throws Exception {
    if(!loggedUser) {
      this.sendMessage(Constants.MSG_530);
      return;
    }
    if(this.anonymousUser) {
      this.sendMessage(Constants.MSG_200.replace("DIRECTORY", this.directory));
      return;
    }
    if(Constants.PARENT_DIRECTORY.equals(request.getArgument())) {

      ...

    }
    else if(!Constants.CURRENT_DIRECTORY.equals(request.getArgument())) {
      if(request.getArgument().startsWith("/")) {
        if(!Constants.NONE.equals(request.getArgument())) {
          this.directory = request.getArgument();
        }
      }
      else {
        if(Constants.RACINE_DIRECTORY.equals(this.directory)) {
          if(!Constants.NONE.equals(request.getArgument())) {
            this.directory += request.getArgument();
          }
        }
        else {
          if(!Constants.NONE.equals(request.getArgument())) {
            this.directory += "/" + request.getArgument();
          }
        }
      }
    }
    this.sendMessage(Constants.MSG_200.replace("DIRECTORY", this.directory));
  }
```

- Classe FtpRequest, méthode sendFile qui envoie le fichier demandé sur le canal
de données.

```java
  private void sendFile(String filename) throws Exception {
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
  }
```

- Classe FtpRequest, méthode sendMessage qui envoie un message sur le canal de
commandes.
```java
  private void sendMessage(String message) throws Exception {
    this.piDataOutputStream.writeBytes(message);
    this.piDataOutputStream.flush();
  }
```
