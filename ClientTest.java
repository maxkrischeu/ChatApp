import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.awt.*;

public class ClientTest {
    volatile boolean running;
    Socket conn; 
    BufferedReader in;
    PrintWriter out;
    Scanner scanner;
    StartFrame startframe;
    Rueckmeldung meldung;
    Chatfenster chat;

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
            else if(msg.equals("Anmeldung erfolgreich.")){
                this.meldung.meldungErfolgAnmelden();
            } 
            else if(msg.equals("Der Benutzername oder das Passwort sind falsch.")){
                this.meldung.meldungErrorAnmelden();
            }     
            //TODO: "Du bist gebannt und kannst dich nicht anmelden."
            else if(msg.equals( "Raum Erstellen erfolgreich")){
                this.chat.addRoomName(this.chat.raumerstellen.getRoomName());
            }  
            
            else if(msg.startsWith("Raumnamen:")){
                String roomNames = msg.substring("Raumnamen:".length());
                String[] rooms = roomNames.split(",");
                for(int i=0; i<rooms.length; i++){
                    if(!(rooms[i].equals("Lobby"))){
                        this.chat.addRoomName(rooms[i]);
                    }
                }
            }

            else if(msg.startsWith("Mitglieder:")){
                this.chat.user.removeAll();
                String roomMembers = msg.substring("Mitglieder:".length());
                String[] members = roomMembers.split(",");
                for(int i=0; i<members.length; i++){
                    if(!(members[i].equals(this.startframe.getUsername()))){
                        this.chat.user.add(members[i]);
                    }
                }
            }

            else if(msg.startsWith("Neuer Raum wurde erstellt:")){
                String newRoom = msg.substring("Neuer Raum wurde erstellt:".length());
                this.chat.addRoomName(newRoom); 
            }

            else if(msg.startsWith("[INFO]") && msg.contains("beigetreten")){
                int ende = msg.indexOf(" ist beigetreten");
                String newMember = msg.substring("[INFO] ".length(), ende);
                this.chat.user.add(newMember);
            }

            else if(msg.startsWith("[INFO]") && msg.contains("verlassen")){
                int ende = msg.indexOf(" hat den Raum verlassen");
                String oldMember = msg.substring("[INFO] ".length(), ende);
                System.out.println(oldMember);
                this.chat.user.remove(oldMember);
            }
            else{
                this.chat.chatanzeige.add(msg);
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
            System.out.println("ich bin im catch");
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
}