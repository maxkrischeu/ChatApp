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
                }
            }.start();

            new Thread() {
                public void run() {
                    ClientTest.this.read();
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