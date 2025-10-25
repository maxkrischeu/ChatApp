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
        while (!this.startseite()) {
            this.startseite();
        }
        this.server.addClientThread(this);
        this.writerThread.write(this.server.getIdOfAvailableClients(this));
        this.readerThread.start();
    }

    public boolean registrieren() {
        try {
            writerThread.write("Bitte gib deinen Benutzernamen ein: "); 
            String id = readerThread.nextInput(); //liest Eingabe ein, Funktion aus ReadThread

            Boolean result = database.checkName(id);

            if (!result) {
                this.id = id;
                writerThread.write("Passwort: ");
                String pw = readerThread.nextInput();
                database.registrieren(id, pw); 
                writerThread.write("Registrierung erfolgreich.");
                return true;
            } else {
                writerThread.write("Dieser Benutzername existiert bereits. Gib bitte einen neuen Benutzernamen ein.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean anmelden(){
        try {
            writerThread.write("Bitte gib deinen Benutzernamen ein: "); 
            String id = readerThread.nextInput(); //liest Eingabe ein, Funktion aus ReadThread

            writerThread.write("Passwort: ");
            String pw = readerThread.nextInput();

            Boolean result = database.checkLogIn(id, pw);

            if (result) {
                this.id = id;
                writerThread.write("Anmeldung erfolgreich.");
                return true;
            } else {
                writerThread.write("Der Benutzername oder das Passwort sind falsch.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean startseite(){
        try{
            writerThread.write("Möchtest du dich anmelden oder registrieren?: ");
            String antwort = readerThread.nextInput(); //Antwort besteht nur aus einem Wort 

            if(antwort.equals("registrieren")){
                while (!this.registrieren()) {
                    this.registrieren();
                }
                return true;
            }
            else if(antwort.equals("anmelden")){
                while (!this.anmelden()) {
                    this.anmelden();
                }
                return true;
            }
            else{
                writerThread.write("Das ist keine zulässige Antwort");
                return false;
            }
        }
        catch (IOException e) {
            return false;
        }
    }
}
