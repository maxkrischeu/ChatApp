import java.io.*;
import java.net.Socket;
import java.nio.file.*;


public class ClientThread extends Thread {
    private Server server;
    private Socket conn;
    private String id;
    private String currentRoom = "Lobby";
    private DataInputStream dataInReader;
    private DataOutputStream dataOutWriter;

    public ClientThread(Server server, Socket conn) throws IOException {
        this.server = server;
        this.conn = conn;
        this.dataInReader = new DataInputStream(new BufferedInputStream(this.conn.getInputStream()));
        this.dataOutWriter = new DataOutputStream(new BufferedOutputStream(this.conn.getOutputStream()));
    }

    @Override
    public void run() {
        // Login/Registrierung so lange abfragen, bis ein erfolgreicher Login erfolgt
        while (!startseite()) {}

        // Nach Login initiale Daten für das GUI
        this.server.getCurrentRooms(this);
        this.server.getCurrentRoomMembers(this, "Lobby");
        this.server.UpdateCurrentRoomMembers(this, "Lobby");

        while(true) {
            try {
                String msg = dataInReader.readUTF();
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
                // Alles danach aufräumen
                this.server.removeClientThread(this);
                this.server.sendMessageToRoom(this.getCurrentRoom(), this, this.id + " hat den Chatraum verlassen.");
                this.server.UpdateCurrentRoomMembers(this, this.getCurrentRoom());
                break;
            }
        }
    }

    public boolean startseite() {
        try {
            String antwort = dataInReader.readUTF();
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
            String id = dataInReader.readUTF();
            if (id == null) return false;

            boolean nameExists = this.server.checkName(id);
            if (!nameExists) {
                this.id = id;
                String pw = dataInReader.readUTF();
                if (pw.length() == 0) {
                    this.write("Kein Passwort eingegeben.");
                    return false;}

                this.server.registrieren(id, pw);
                this.write("Registrierung erfolgreich.");
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
            String id = dataInReader.readUTF();
            if (id == null) return false;

            if (server.isBanned(id)) {
                write("Du bist gebannt und kannst dich nicht anmelden.");
                stopp();
                return false;
            }

            String pw = dataInReader.readUTF();
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
        try {
            this.dataOutWriter.writeUTF(msg);
            this.dataOutWriter.flush();
        } catch (IOException e) {
            System.err.println("Write fehlgeschlagen: " + e.getMessage());
            stopp(); 
        }
    }

    public void stopp() {
        try {
            this.conn.close();
            this.dataInReader.close();
            this.dataOutWriter.close();
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
            String msg = dataInReader.readUTF(); 
            switch(msg) {
                case "Raum Erstellen":
                    String newName = dataInReader.readUTF();
                    boolean ok = this.server.createRoom(newName);
                    if(ok) { 
                        this.write("Raum Erstellen erfolgreich");
                        this.server.sendMessageToAll(this, "Neuer Raum wurde erstellt:" + newName);
                    } else {
                        this.write("Raum Erstellen fehlgeschlagen");
                    }
                    break;
                case "Raum Beitreten":
                    String joinName = dataInReader.readUTF();
                    this.server.joinRoom(this, joinName);
                    break;
                case "Raum Verlassen":
                    String quitName = dataInReader.readUTF();
                    this.server.quitRoom(this ,quitName);
                    break;
                case "Lösche den Raum":
                    String oldRoom = dataInReader.readUTF();
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
            String msg = dataInReader.readUTF(); 
            String filename = null;
            boolean ok = false;
            switch(msg) {
                case "Datei hochladen":
                    filename = dataInReader.readUTF();
                    ok = receiveFileToCurrentRoom(filename);
                    if (ok) {
                        this.write("[INFO] Upload erfolgreich: " + filename);
                        this.server.log("[INFO] Upload erfolgreich: " + filename);
                    }
                    else this.write("[INFO] Upload fehlgeschlagen: " + filename);
                    break;
                case "Dateien anzeigen":
                    sendFileListForCurrentRoom();
                    break;
                case "Datei herunterladen":
                    filename = dataInReader.readUTF();
                    ok = sendFileFromCurrentRoom(filename);
                    if (ok) this.write("[INFO] Download erfolgreich: " + filename);
                    else this.write("[INFO] Download fehlgeschlagen: " + filename);
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
            if (filename == null || filename.trim().isEmpty()) {
                return false;
            }
            String room = this.getCurrentRoom();
            Path dir = this.server.roomDir(room);

            Path target = dir.resolve(filename);

            // Client sendet zuerst die Dateigröße, danach die Bytes
            long size = dataInReader.readLong();
            if (size < 0) return false;

            try (OutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
            )) {
                byte[] buffer = new byte[8192];
                long remaining = size;

                while (remaining > 0) {
                    int read = dataInReader.read(buffer, 0, (int)Math.min(buffer.length, remaining));
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

    public boolean sendFileFromCurrentRoom(String filename) {
    try {
        if (filename == null || filename.trim().isEmpty()) {
            this.write("[INFO] Datei nicht gefunden: " + filename);
            return false;
        }
        String room = this.getCurrentRoom();
        Path dir = this.server.roomDir(room);

        Path source = dir.resolve(filename);

        if (!Files.exists(source) || !Files.isRegularFile(source)) {
            this.write("[INFO] Datei nicht gefunden: " + filename);
            return false;
        }

        long size = Files.size(source);
        if (size < 0) return false;

        // erst Metadaten, dann Byte-Stream
        dataOutWriter.writeUTF("DOWNLOAD_BEGIN:" + filename);
        dataOutWriter.writeLong(size);
        dataOutWriter.flush();

        try (InputStream in = new BufferedInputStream(Files.newInputStream(source))) {
            byte[] buffer = new byte[8192];
            int read;

            while ((read = in.read(buffer)) != -1) {
                dataOutWriter.write(buffer, 0, read);
            }
            dataOutWriter.flush();
        }   
            return true;
        } catch (IOException e) {
            System.err.println("sendFileFromCurrentRoom failed: " + e.getMessage());
            return false;
        }
    }

    public void sendFileListForCurrentRoom() {
    try {
        Path dir = server.roomDir(getCurrentRoom());

        if (!Files.exists(dir)) {
            write("FILES_LIST_BEGIN");
            dataOutWriter.writeInt(0);
            write("FILES_LIST_END");
            return;
        }

        File[] files = dir.toFile().listFiles(File::isFile);

        write("FILES_LIST_BEGIN");
        dataOutWriter.writeInt(files == null ? 0 : files.length);

        if (files != null) {
            for (File f : files) {
                dataOutWriter.writeUTF(f.getName());
            }
        }

        write("FILES_LIST_END");
        dataOutWriter.flush();

        } catch (IOException e) {
            write("[INFO] Fehler beim Laden der Dateiliste");
        }
    }
}
