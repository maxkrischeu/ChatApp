import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
    int port;
    ServerSocket serverSocket;
    volatile boolean running;
    ArrayList<ClientThread> clients;
    DataBase database;

    public Server(int port) {
        this.port = port;
        this.running = false;
        this.database = new DataBase();
    }

    public void start() {
        this.running = true;
        this.clients  = new ArrayList<>();

        try {
            this.serverSocket = new ServerSocket(this.port);
            System.out.println("Server erfolgreich gestartet.");
        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen des ServerSockets: " + e.getMessage());
        }

        try {
            while(this.running) {
                ClientThread client = new ClientThread(this, serverSocket.accept(), this.database);
                client.start();
            }
        } catch (IOException e) {
            if (!this.running && this.serverSocket.isClosed()) {
                System.out.println("Server erfolgreich heruntergefahren.");
            } else {
                System.err.println("Beim Akzeptieren einer Verbindung ist etwas schiefgelaufen: " + e.getMessage());
            }
        }
    }

    public void stop() {
        this.running = false;
        try {
            if(this.serverSocket != null) {
                this.serverSocket.close();
                this.database.clear(); 
            }
        } catch (IOException e) {
            System.err.println("Beim stoppen des Servers ist etwas schiefgelaufen: " + e.getMessage());
        }

        for (ClientThread client: this.clients) {
            client.stopp();
        }
    }

    public String getIdOfAvailableClients(ClientThread self) {
        if (this.clients.size() == 1) {
            return "Derzeit sind keine weiteren Personen im Chat.";
        } 
        else if (this.clients.size() == 2) {
            String allClients = "Online: ";
            for (ClientThread client: this.clients) {
                if (client.id != self.id) {
                    client.writer.println(self.id + " hat den Chatraum betreten.");
                    allClients += client.id;
                }
            }
            //ohne replace ging nicht, weil null in der Hand
            return (allClients.replace(self.id, "")+".");
        } else {
            String allClients = "Online: ";
            int i = 1;
            for (ClientThread client: this.clients) {
                if (client.id != self.id) {
                    client.writer.println(self.id + " hat den Chatraum betreten.");
                    allClients += client.id;
                    if (i + 1 < this.clients.size()) {
                        allClients += ", ";
                    }
                }
                i++;
            }
            return (allClients + ".");
        }
    }
    

    public void addClientThread(ClientThread client) {
        this.clients.add(client);
    }


    public void removeClientThread(ClientThread client) {
        this.clients.remove(client);
    }

    public void sendMessageToAll(ClientThread self, String msg) {
        for(ClientThread client: this.clients) {
            if (client != self) {
                client.write(msg);
            }
        }
    }
}

