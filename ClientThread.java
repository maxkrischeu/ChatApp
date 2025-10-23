import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {
    String id;
    Socket conn;
    WriteThread writerThread;
    ReadThread readerThread;
    DataBase database;
    Server server;
    Boolean running;


    public ClientThread(Server server, Socket conn, DataBase database) throws IOException {
        this.server = server;
        this.conn = conn;
        this.writerThread = new WriteThread(conn.getOutputStream(), this);
        this.readerThread = new ReadThread(conn.getInputStream(), this);
        this.database = database;
        this.running = false;
    }

    public void run(){
        this.running = true;
        while (!this.logIn()) {
            this.logIn();
        }
        this.server.addClientThread(this);
        this.writerThread.write(this.server.getIdOfAvailableClients(this));
        this.readerThread.start();
    }

    public boolean logIn() {
        try {
            writerThread.write("Bitte gib deine Anmeldedaten ein: ");
            String id = readerThread.nextInput();
            writerThread.write("Passwort: ");
            String pw = readerThread.nextInput();

            Boolean result = database.checkLogIn(id, pw);

            if (result) {
                this.id = id;
                writerThread.write("Anmeldung erfolgreich");
                return result;
            } else {
                writerThread.write("Anmeldename oder Passwort falsch.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}
