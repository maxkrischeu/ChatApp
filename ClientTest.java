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
                    while(true) {
                        String msg = scanner.nextLine();
                        out.println(msg);
                    }
                }
            }.start();

            while(true) {
                System.out.println(in.readLine());
            }

        } catch(Exception e) {
            System.out.println("Das hat nicht geklappt:" + e.getMessage());
        }
    }
}