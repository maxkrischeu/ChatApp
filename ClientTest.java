import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientTest {
    volatile boolean running;
    Socket conn; 
    BufferedReader in;
    PrintWriter out;
    Scanner scanner;

    public ClientTest(){
        try{
            this.conn = new Socket("localhost", 5001);
            this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            this.out = new PrintWriter(conn.getOutputStream(), true);
            this.scanner = new Scanner(System.in);
            this.running = false;
            }
        catch(Exception e) {
            System.out.println("Das hat nicht geklappt:" + e.getMessage());
        }
    }

    public void start(){
        this.running = true;
        try{
            new Thread() {
                public void run() {
                    ClientTest.this.write();
                    if(ClientTest.this.running == false ){
                        System.out.println("running ist hier auch false");
                        return; 
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    ClientTest.this.read();
                    if(ClientTest.this.running == false){
                        System.out.println("running ist false");
                        return;
                    }
                }
            }.start();
        }
        catch(Exception e) {
            System.out.println("Das hat nicht geklappt:" + e.getMessage());
        }
    }

    public void read(){
        try{
            String line;
            while((line = this.in.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch(Exception e){}
    }

    public void write() {
            String msg;
            while(this.running && (msg = this.scanner.nextLine()) != null) {
                this.out.println(msg);
                if(msg.equals("quit")){
                    this.stop();
                }
            }
    }

    public void stop(){
            
        this.running = false;

        try { if (this.out != null) this.out.flush(); } catch (Exception ignore) {}

        // Halbseitig schließen -> Reader auf der Gegenseite bekommt EOF,
        // und unser eigenes readLine() wacht i.d.R. auf
        try { if (this.conn != null && !this.conn.isClosed()) this.conn.shutdownOutput(); } catch (Exception ignore) {}
        try { if (this.conn != null && !this.conn.isClosed()) this.conn.shutdownInput();  } catch (Exception ignore) {}

        // Streams/Sockel schließen
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

//     public static void main(String[] args) {
//         // ClientTest client = new ClientTest();
//         // client.start(); 

//         try {
//             Socket conn = new Socket("localhost", 5001);
//             BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//             PrintWriter out = new PrintWriter(conn.getOutputStream(), true);

//             new Thread() {
//                 // class ThreadKlasse extends Thread{...}
//                 // public void ThreadKlasse(CLientTest client) ...
//                 public void run() {
//                     Scanner scanner = new Scanner(System.in);
//                     Boolean running = true; 
//                     while(running) {
//                         String msg = scanner.nextLine();
//                         out.println(msg);
//                         //quit soll nicht an alle gesendet werden
//                         if(msg.equals("quit")){
//                             try{
//                                 conn.close(); 
//                                 scanner.close();
//                                 running = false; 
//                                 System.out.println("Chat wurde beendet."); 
//                             }
//                             catch(Exception e){}
//                         }
//                     }
//                 }
//             }.start();



//             String line;
//             while((line = in.readLine()) != null) {
//                 System.out.println(line);
//             }


//             System.out.println("Server wurde heruntergefahren!");
//             in.close();
//             out.close();
//             System.out.println("Möchtest du die Verbindung wieder herstellen?"); 

//             Scanner scanner = new Scanner(System.in);
//             String msg = scanner.nextLine(); 
//             if(msg.equals("ja")){
//                 System.out.println("Verbindung wird beendet.");
//                 if(in.readLine()==null){
//                     System.out.println("Verbindung kann nicht wieder hergestellt werden."); 
//                 }
//                 else{
//                     System.out.println("Verbindung konnte wieder hergestellt werden.");
//                 }
//             }
//             System.out.println("moin");


//         } catch(Exception e) {
//             System.out.println("Das hat nicht geklappt:" + e.getMessage());
//         }
//     }
// }