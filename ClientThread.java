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
<<<<<<< HEAD
        while(!this.logIn() && this.running) {}
        
=======
        this.running = true;
        while (!this.startseite()) {
            this.startseite();
        }
>>>>>>> origin/jenny
        this.server.addClientThread(this);
        this.write(this.server.getIdOfAvailableClients(this));

    }

    public boolean registrieren() {
        try {
<<<<<<< HEAD
            this.write("Bitte gib deine Anmeldedaten ein: ");
            String id = reader.readLine();
            this.write("Passwort: ");
            String pw = reader.readLine();
=======
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
>>>>>>> origin/jenny

            Boolean result = database.checkLogIn(id, pw);

            if (result) {
                this.id = id;
<<<<<<< HEAD
                this.write("Anmeldung erfolgreich");
                return result;
            } else {
                this.write("Anmeldename oder Passwort falsch.");
=======
                writerThread.write("Anmeldung erfolgreich.");
                return true;
            } else {
                writerThread.write("Der Benutzername oder das Passwort sind falsch.");
>>>>>>> origin/jenny
                return false;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

<<<<<<< HEAD
    public void write(String msg) {
        this.writer.println(msg);
    }

    public void stopp() {
        this.running = false;
=======
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
>>>>>>> origin/jenny
    }
}
