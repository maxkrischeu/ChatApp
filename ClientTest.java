import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class ClientTest {
    private volatile boolean running;
    private Socket conn; 
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private StartFrame startframe;
    private Rueckmeldung meldung;
    private Chatfenster chat;
    private RaumVerlassen raumVerlassenBestätigen;
    private DateiHochladen upload;
    private Set<String> knownUsers;
    private Set<String> knownRooms;

    public ClientTest(){
        try{
            this.conn = new Socket("localhost", 5001);
            this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            this.out = new PrintWriter(conn.getOutputStream(), true);
            this.scanner = new Scanner(System.in);
            this.running = false;
            this.startframe = new StartFrame(this);
            this.meldung = new Rueckmeldung(this);
            this.chat = new Chatfenster(this);
            this.knownUsers = new HashSet<>();
            this.knownRooms = new HashSet<>();
        }            
        catch(Exception e) {
            System.out.println("Das hat nicht geklappt:" + e.getMessage());
        }
    }

    public void start(){
        this.running = true;
        this.startframe.frameStart();
        while(true) {
            String msg = read();
            System.out.println(msg);
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
                int start = msg.indexOf("Soll dieser Raum gelöscht werden:".length());
                this.raumVerlassenBestätigen = new RaumVerlassen(this);
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


        //     System.out.println("Wir lesen");

        //     new Thread() {
        //         public void run() {
        //             ClientTest.this.read();
        //         }
        //     }.start();
        // }
        // catch(Exception e) {
        //     System.out.println("Das hat nicht geklappt:" + e.getMessage());
        // }
    }

    public String read(){
        try{
            String line = this.in.readLine();
            //while((line = this.in.readLine()) != null && this.startframe.lesen == true) {
                return line;
            //}
            //return "";
        }
        catch(Exception e){
            //TODO: Kommt hier rein wenn Nutzer gebannt
            //System.out.println("ich bin im catch");
            return "";
        }
    }

    public void write(String msg) {
            // while(this.running) {
                this.out.println(msg);
                // if(msg.equals("quit")){
                //     this.stop();
                // }
            
    }

    public void stop(){
        this.running = false;

        try { if (this.out != null) this.out.flush(); } catch (Exception ignore) {}

        // Halbseitig schließen -> Reader auf der Gegenseite bekommt EOF,
        // und unser eigenes readLine() wacht i.d.R. auf
        try { if (this.conn != null && !this.conn.isClosed()) this.conn.shutdownOutput(); } catch (Exception ignore) {}
        try { if (this.conn != null && !this.conn.isClosed()) this.conn.shutdownInput();  } catch (Exception ignore) {}

        // Streams/Socket schließen
        try { if (this.in  != null) this.in.close(); }  catch (Exception ignore) {}
        try { if (this.out != null) this.out.close(); } catch (Exception ignore) {}
        try { if (this.conn!= null && !this.conn.isClosed()) this.conn.close(); } catch (Exception ignore) {}

        System.out.println("Chat wurde beendet.");
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
}