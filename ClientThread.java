import java.io.*;
import java.net.Socket;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;


public class ClientThread extends Thread {
    private Server server;
    private Socket conn;
    private PrintWriter writer;
    private BufferedReader reader;
    private String id;
    private String currentRoom = "Lobby";
    private DataInputStream dataIn;

    public ClientThread(Server server, Socket conn) throws IOException {
        this.server = server;
        this.conn = conn;
        this.writer = new PrintWriter(new OutputStreamWriter(this.conn.getOutputStream()), true);
        this.reader = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        this.dataIn = new DataInputStream(new BufferedInputStream(this.conn.getInputStream()));
    }

    @Override
    public void run() {
        while (!startseite()) {}

        //this.write(this.server.getIdOfAvailableClients(this));

        this.server.getCurrentRooms(this);
        this.server.getCurrentRoomMembers(this, "Lobby");
        this.server.UpdateCurrentRoomMembers(this, "Lobby");

        while(true) {
            try {
                String msg = reader.readLine();
                System.out.println(msg);
                if(msg.equals("Button gedrückt")){
                    RoomClient();
                }
                else if(msg.equals("Files")){
                    FilesClient();
                }
                else if(msg != null && msg.length()>0) {
                    this.server.sendMessageToRoom(this.getCurrentRoom(), this, "[" + this.id + "]"  + ": " + msg);
                    if(msg.endsWith("ist abgemeldet")){
                        this.stopp();
                    }
                }
            } catch (Exception e) {
                System.err.println("Der Client hat unerwartet die Verbindung abgebrochen:" + e.getMessage());
                this.server.removeClientThread(this);
                this.server.sendMessageToRoom(this.getCurrentRoom(), this, this.id + " hat den Chatraum verlassen.");
                this.server.UpdateCurrentRoomMembers(this, this.getCurrentRoom());
                break;
            }
        }
    }

    public boolean startseite() {
        try {
            //this.write("Möchtest du dich anmelden oder registrieren?: ");
            String antwort = reader.readLine();
            if (antwort == null) return false;
            switch(antwort) {
                case "registrieren":
                    if(!registrieren()) { return false; }
                    else{
                        return false;
                    }
                case "anmelden":
                    if(!anmelden()) { return false; }
                    else{
                        return true;
                    }
                default:
                    return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean registrieren() {
        try {
            //this.write("Bitte gib deinen Benutzernamen ein: ");
            String id = reader.readLine();
            if (id == null) return false;

            boolean nameExists = this.server.checkName(id);
            if (!nameExists) {
                this.id = id;
                //this.write("Passwort: ");
                String pw = reader.readLine();
                if (pw.length() == 0) {
                    this.write("Kein Passwort eingegeben.");
                    return false;}

                this.server.registrieren(id, pw);
                this.write("Registrierung erfolgreich.");
                //this.server.addClientThread(this);
                return true;
            } else {
                this.write("Dieser Benutzername existiert bereits. Gib bitte einen neuen Benutzernamen ein.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean anmelden() {
        try {
            //this.write("Bitte gib deinen Benutzernamen ein: ");
            String id = reader.readLine();
            if (id == null) return false;

            if (server.isBanned(id)) {
                write("Du bist gebannt und kannst dich nicht anmelden.");
                stopp();
                return false;
            }

            //this.write("Passwort: ");
            String pw = reader.readLine();
            if (pw == null) return false;

            boolean ok = this.server.checkLogIn(id, pw);
            if (ok) {
                this.id = id;
                this.write("Anmeldung erfolgreich.");
                this.server.addClientThread(this);
                return true;
            } else {
                this.write("Der Benutzername oder das Passwort sind falsch.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void write(String msg) {
        this.writer.println(msg);
    }

    public void stopp() {
        try {
            this.conn.close();
            this.writer.close();
            this.reader.close();
        }
        catch(IOException e){System.out.println("Socket konnte nicht geschlossen werden.");}
    }

    public String getID() {
        return this.id;
    }

    public String getCurrentRoom() {
        return this.currentRoom;
    }

    public void setCurrentRoom(String newName) {
        this.currentRoom = newName;
    }

    public void RoomClient(){
        try{
            String msg = reader.readLine(); 
            switch(msg) {
                case "Raum Erstellen":
                    String newName = reader.readLine();
                    boolean ok = this.server.createRoom(newName);
                    if(ok) { 
                        this.write("Raum Erstellen erfolgreich");
                        this.server.sendMessageToAll(this, "Neuer Raum wurde erstellt:" + newName);
                        break;
                    }
                case "Raum Beitreten":
                    String joinName = reader.readLine();
                    this.server.joinRoom(this, joinName);
                    break;
                case "Raum Verlassen":
                    String quitName = reader.readLine();
                    this.server.quitRoom(this ,quitName);
                    break;
                case "Lösche den Raum":
                    String oldRoom = reader.readLine();
                    boolean delete = this.server.deleteRoom(this, oldRoom);
                    if(delete){
                        this.server.sendMessageToAll(this, "[INFO] Raum " + oldRoom + " gelöscht");
                    }
                    break;
                default:
                    this.write("Etwas ist schief gelaufen");
            }
        } catch (IOException e) {
            this.write("Es konnte keine Raumaktion durchgeführt werden");
        }
    }

    public void FilesClient(){
        try{
            String msg = reader.readLine(); 
            switch(msg) {
                case "Datei hochladen":
                    String filename = reader.readLine(); // Client sendet Dateiname als Textzeile und danach die Dateigröße
                    boolean ok = receiveFileToCurrentRoom(filename);
                    if (ok) this.write("[INFO] Upload erfolgreich: " + filename);
                    else this.write("[INFO] Upload fehlgeschlagen: " + filename);
                    break;
                case "Dateien anzeigen":
                    
                    break;
                case "Dateien herunterladen":
                    
                    break;
                default:
                    this.write("Etwas ist schief gelaufen");
            }
        } catch (IOException e) {
            this.write("Es konnte keine Dateienaktionen durchgeführt werden");
        }
    }

    public boolean receiveFileToCurrentRoom(String filename) {
        try {
            String room = this.getCurrentRoom();
            Path dir = this.server.roomDir(room);

            Path target = dir.resolve(filename);

            // Client sendet zuerst die Dateigröße
            long size = dataIn.readLong();
            if (size < 0) return false;

            try (OutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
            )) {
                byte[] buffer = new byte[8192];
                long remaining = size;

                while (remaining > 0) {
                    int read = dataIn.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                    if (read == -1) throw new EOFException("Stream ended early");
                    out.write(buffer, 0, read);
                    remaining -= read;
                }
                out.flush();
            }

            return true;
        } catch (IOException e) {
            System.err.println("receiveFileToCurrentRoom failed: " + e.getMessage());
            return false;
        }
    }
}