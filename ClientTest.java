import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
        try {
            Socket conn = new Socket("localhost", 5001);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            PrintWriter out = new PrintWriter(conn.getOutputStream(), true);

            new Thread() {
                public void run() {
                    Scanner scanner = new Scanner(System.in);
                    Boolean running = true; 
                    while(running) {
                        String msg = scanner.nextLine();
                        out.println(msg);
                        if(msg.equals("quit")){
                            try{
                                conn.close(); 
                                scanner.close();
                                running = false; 
                                System.out.println("Chat wurde beendet."); 
                            }
                            catch(Exception e){}
                        }
                    }
                }
            }.start();



            String line;
            while((line = in.readLine()) != null) {
                System.out.println(line);
            }


            // System.out.println("Server wurde heruntergefahren!");
            // in.close();
            // out.close();
            // System.out.println("Möchtest du die Verbindung wieder herstellen?"); 

            // Scanner scanner = new Scanner(System.in);
            // String msg = scanner.nextLine(); 
            // if(msg.equals("ja")){
            //     System.out.println("Verbindung wird beendet.");
            //     if(in.readLine()==null){
            //         System.out.println("Verbindung kann nicht wieder hergestellt werden."); 
            //     }
            //     else{
            //         System.out.println("Verbindung konnte wieder hergestellt werden.");
            //     }
            // }
            // System.out.println("moin");


        } catch(Exception e) {
            System.out.println("Das hat nicht geklappt:" + e.getMessage());
        }
    }
}