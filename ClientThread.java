import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    String id;
    Socket conn;
    PrintWriter writer;
    BufferedReader reader;
    DataBase database;
    Server server;
    volatile boolean running;

    public ClientThread(Server server, Socket conn, DataBase database) throws IOException {
        this.server = server;
        this.conn = conn;
        this.writer = new PrintWriter(new OutputStreamWriter(this.conn.getOutputStream()), true);
        this.reader = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        this.database = database;
        this.running = true;
    }

    @Override
    public void run() {
        while (running && !startseite()) { /* retry until success */ }
        if (!running) return;
        this.server.addClientThread(this);
        this.write(this.server.getIdOfAvailableClients(this));


        while(running) {
            try {
                String msg;
                if ((msg = reader.readLine()) != null) {
                    this.server.sendMessageToAll(this, this.id  + ": " + msg);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

    }

    public void write(String msg) {
        this.writer.println(msg);
    }

    public void stopp() {
        this.running = false;
        try{
            this.conn.close();
            this.writer.close();
            this.reader.close();
        }
        catch(IOException e){System.out.println("Socket konnte nicht geschlossen werden");}
    }

    public boolean registrieren() {
        try {
            this.write("Bitte gib deinen Benutzernamen ein: ");
            String id = reader.readLine();
            if (id == null) return false;

            boolean nameExists = database.checkName(id);
            if (!nameExists) {
                this.id = id;
                this.write("Passwort: ");
                String pw = reader.readLine();
                if (pw == null) return false;

                database.registrieren(id, pw);
                this.write("Registrierung erfolgreich.");
                return true;
            } else {
                this.write("Dieser Benutzername existiert bereits. Gib bitte einen neuen Benutzernamen ein.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean anmelden() {
        try {
            this.write("Bitte gib deinen Benutzernamen ein: ");
            String id = reader.readLine();
            if (id == null) return false;

            this.write("Passwort: ");
            String pw = reader.readLine();
            if (pw == null) return false;

            boolean ok = database.checkLogIn(id, pw);
            if (ok) {
                this.id = id;
                this.write("Anmeldung erfolgreich.");
                return true;
            } else {
                this.write("Der Benutzername oder das Passwort sind falsch.");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public boolean startseite() {
        try {
            this.write("Möchtest du dich anmelden oder registrieren?: ");
            String antwort = reader.readLine();
            if (antwort == null) return false;

            if (antwort.equals("registrieren")) {
                while (!registrieren()) { /* repeat */ }
                return true;
            } else if (antwort.equals("anmelden")) {
                while (!anmelden()) { /* repeat */ }
                return true;
            } else {
                this.write("Das ist keine zulässige Antwort");
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}