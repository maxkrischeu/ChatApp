import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;


public class ClientTest {
    private Socket conn; 
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private StartFrame startframe;
    private Rueckmeldung meldung;
    private Chatfenster chat;
    private Set<String> knownUsers;

    public ClientTest(){
        try{
            this.conn = new Socket("localhost", 5001);
            this.dataOut = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
            this.dataIn = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            this.startframe = new StartFrame(this);
            this.meldung = new Rueckmeldung(this);
            this.chat = new Chatfenster(this);
            this.knownUsers = new HashSet<>();
        }            
        catch(Exception e) {
            System.out.println("Das hat nicht geklappt:" + e.getMessage());
        }
    }

    public void start(){
        this.startframe.frameStart();
        while(true) {
            // Alle Servernachrichten werden hier zugeordnet
            String msg = read();
            if(msg == null || msg.isEmpty()) continue;
            if(msg.startsWith("DOWNLOAD_BEGIN:")){
                String filename = msg.substring("DOWNLOAD_BEGIN:".length()).trim();
                receiveDownloadPayload(filename);
                continue;
            }
            if (msg.equals("FILES_LIST_BEGIN")) {
                receiveFileList();
                continue;
            }
            if(msg.equals("Registrierung erfolgreich.")){
                this.meldung.meldungErfolgRegistrieren();
            }
            else if(msg.equals("Dieser Benutzername existiert bereits. Gib bitte einen neuen Benutzernamen ein.")){
                this.meldung.meldungErrorRegistrieren();
            } 
            else if(msg.equals("Kein Passwort eingegeben.")){
                this.meldung.meldungErrorRegistrierenP();
            }
            else if(msg.equals("Anmeldung erfolgreich.")){
                this.meldung.meldungErfolgAnmelden();
            } 
            else if(msg.equals("Der Benutzername oder das Passwort sind falsch.")){
                this.meldung.meldungErrorAnmelden();
            }
            else if(msg.equals("Du bist gebannt und kannst dich nicht anmelden.")){
                this.meldung.meldungBannedAnmelden();
            }
            else if(msg.equals("Du wurdest vom Server entfernt")){
                this.meldung.meldungBanned();
                this.chat.nichtAnzeigen();
            }
            else if(msg.equals( "Raum Erstellen erfolgreich")){
                this.chat.addRoomName(this.chat.getRaumErstellen().getRoomName().trim());
            }  
            else if(msg.equals("Es wurde kein Raum ausgewählt.")){
                this.meldung.meldungErrorBeitreten();
                this.chat.setRoomName("Lobby");
            }
            
            else if(msg.startsWith("Raumnamen:")){
                String roomNames = msg.substring("Raumnamen:".length());
                String[] rooms = roomNames.split(",");
                for(int i=0; i<rooms.length; i++){
                    if(!(rooms[i].equals("Lobby"))){
                        this.chat.addRoomName(rooms[i].trim());
                    }
                }
            }

            else if(msg.startsWith("Mitglieder:")){
                // Bei jeder vollständigen Mitgliederliste wird es aktualisiert
                this.chat.getUser().removeAll();
                this.knownUsers.clear();
                String roomMembers = msg.substring("Mitglieder:".length());
                String[] members = roomMembers.split(",");
                for(int i=0; i<members.length; i++){
                    if(!(members[i].trim().equals(this.startframe.getUsername()))){
                        if(this.knownUsers.add(members[i].trim())){
                            this.chat.addUser(members[i].trim());
                        }
                    }
                }
            }

            else if(msg.startsWith("Neuer Raum wurde erstellt:")){
                String newRoom = msg.substring("Neuer Raum wurde erstellt:".length());
                this.chat.addRoomName(newRoom.trim()); 
            }

            else if(msg.startsWith("[INFO]") && msg.contains("beigetreten")){
                int ende = msg.indexOf(" ist beigetreten");
                String newMember = msg.substring("[INFO] ".length(), ende);
                if(this.knownUsers.add(newMember.trim())){
                    this.chat.addUser(newMember.trim());
                }
                this.chat.getChatanzeige().add(msg);
            }

            else if(msg.startsWith("[INFO]") && msg.contains("verlassen")){
                int ende = msg.indexOf(" hat den Raum verlassen");
                String oldMember = msg.substring("[INFO] ".length(), ende);
                this.chat.getChatanzeige().add(msg);
            }
            else if(msg.contains("wurde gelöscht. Du bist jetzt in der Lobby.")){
                int start = msg.indexOf("Raum".length());
                int end = msg.indexOf(" wurde gelöscht. Du bist jetzt in der Lobby");
                String oldRoom = msg.substring(start, end);
                this.chat.getRoomLabel().setText("Aktueller Raum: Lobby");
                this.chat.removeRoomName(oldRoom.trim());
            }
            else if(msg.equals("[INFO] Du bist in der Lobby")){
                this.chat.getRoomLabel().setText("Aktueller Raum: Lobby");
                this.chat.getChatanzeige().removeAll();
            }
            else if(msg.startsWith("Soll dieser Raum gelöscht werden:")){
                new RaumVerlassen(this);
            }
            else if(msg.startsWith("[INFO] Raum ") && msg.endsWith(" gelöscht")){
                String prefix = "[INFO] Raum ";
                String suffix = " gelöscht";
                String oldRoom = msg.substring(prefix.length(), msg.length() - suffix.length());
                oldRoom = oldRoom.trim();
                this.chat.removeRoomName(oldRoom.trim());
            }
            else{
                this.chat.getChatanzeige().add(msg);
            }
        }
    }

    public String read(){
        try{
            String line = this.dataIn.readUTF();
            return line;
        }
        catch(Exception e){
            return "";
        }
    }

    public synchronized void write(String msg) {
        try{
            this.dataOut.writeUTF(msg);
            this.dataOut.flush();
        }
        catch (IOException e) {
            System.err.println("Write fehlgeschlagen: " + e.getMessage());
        }
    }

    public synchronized void writeLong(long value) {
        try{
            this.dataOut.writeLong(value);
            this.dataOut.flush();
        }
        catch (IOException e) {
            System.err.println("Write fehlgeschlagen: " + e.getMessage());
        }
    }

    public synchronized void writeBytes(byte[] buffer, int len) {
        try{
            this.dataOut.write(buffer, 0, len);
        }
        catch (IOException e) {
            System.err.println("Write fehlgeschlagen: " + e.getMessage());
        }
    }

    public void flush(){
        try{
            this.dataOut.flush();
        }
        catch (IOException e) {
            System.err.println("Flush fehlgeschlagen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientTest client = new ClientTest();
        client.start();
    }

    public StartFrame getStartFrame(){
        return this.startframe;
    }

    public Chatfenster getChat(){
        return this.chat;
    }

    private void receiveDownloadPayload(String filenameFromServer) {
    try {
        // Downloads landen im Download Ordner
        File dir = new File(System.getProperty("user.home"), "Downloads/");
        dir.mkdirs();
        File target = new File(dir, filenameFromServer);

        long size = this.dataIn.readLong();
        if (size < 0) {
            this.chat.getChatanzeige().add("[INFO] Download fehlgeschlagen: " + filenameFromServer);
            return;
        }

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
            byte[] buffer = new byte[8192];
            long remaining = size;

            while (remaining > 0) {
                int read = this.dataIn.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1) throw new EOFException("Stream ended early");
                out.write(buffer, 0, read);
                remaining -= read;
            }
            out.flush();
        }

        this.chat.getChatanzeige().add("[INFO] Download gespeichert: " + target.getAbsolutePath());

        } catch (IOException e) {
            this.chat.getChatanzeige().add("[INFO] Download-Fehler: " + e.getMessage());
        }
    }

    private void receiveFileList() {
    try {
        int count = dataIn.readInt();

        java.util.List<String> files = new java.util.ArrayList<>();

        for (int i = 0; i < count; i++) {
            files.add(dataIn.readUTF());
        }

        String end = dataIn.readUTF();
        if (!"FILES_LIST_END".equals(end)) {
            System.err.println("Protokollfehler bei FILES_LIST");
            return;
        }

        this.chat.showAvailableFiles(files);

        } catch (IOException e) {
            System.err.println("Fehler beim Empfangen der Dateiliste: " + e.getMessage());
        }
    }
}
