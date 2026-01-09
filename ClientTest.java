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
            switch(msg) {
                case "Registrierung erfolgreich.": 
                    this.meldung.meldungErfolgRegistrieren();
                    break;
                case "Dieser Benutzername existiert bereits. Gib bitte einen neuen Benutzernamen ein.":
                    this.meldung.meldungErrorRegistrieren();
                    break;
                case "Anmeldung erfolgreich.":
                    this.meldung.meldungErfolgAnmelden();
                    break;
                case "Der Benutzername oder das Passwort sind falsch.":
                    this.meldung.meldungErrorAnmelden();
                    break;
                //TODO: "Du bist gebannt und kannst dich nicht anmelden."
                case "Raum Erstellen erfolgreich":
                    this.chat.addRoomName(this.chat.raumerstellen.getRoomName());
                    break;
            }
            if(msg.startsWith("Raumnamen:")){
                String roomNames = msg.substring("Raumnamen:".length());
                String[] rooms = roomNames.split(",");
                for(int i=0; i<rooms.length; i++){
                    if(!(rooms[i].equals("Lobby"))){
                        this.chat.addRoomName(rooms[i]);
                    }
                }
            }

            
            // int index;
            // String substring;
            // String roomName;
            // if(msg.contains(":")){
            //     index = msg.indexOf(":");
            //     substring = msg.substring(0, index);
            //     roomName = msg.substring(index+1);
            //     switch(substring) {
            //         case "Neuer Raum wurde erstellt:":
            //             this.chat.addRoomName(roomName);
            //             break;
            //         default:
            //             System.out.println("Beim Lesen im ClientTest ist etwas schief gelaufen");
            //     }
            // }
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