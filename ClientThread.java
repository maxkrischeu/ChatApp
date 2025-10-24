import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    String id;
    Socket conn;
    PrintWriter writer;
    BufferedReader reader;
    DataBase database;
    Server server;
    Boolean running;


    public ClientThread(Server server, Socket conn, DataBase database) throws IOException {
        this.server = server;
        this.conn = conn;
        this.writer = new PrintWriter(new OutputStreamWriter(this.conn.getOutputStream()), true);
        this.reader = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        this.database = database;
        this.running = true;
    }

    public void run(){
        while(!this.logIn() && this.running) {}
        
        this.server.addClientThread(this);
        this.write(this.server.getIdOfAvailableClients(this));

    }

    public boolean logIn() {
        try {
            this.write("Bitte gib deine Anmeldedaten ein: ");
            String id = reader.readLine();
            this.write("Passwort: ");
            String pw = reader.readLine();

            Boolean result = database.checkLogIn(id, pw);

            if (result) {
                this.id = id;
                this.write("Anmeldung erfolgreich");
                return result;
            } else {
                this.write("Anmeldename oder Passwort falsch.");
                return false;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void write(String msg) {
        this.writer.println(msg);
    }

    public void stopp() {
        this.running = false;
    }
}
