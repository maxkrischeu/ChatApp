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
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.running = true;
            this.clients  = new ArrayList<>();

            while(running) {
                ClientThread client = new ClientThread(this, serverSocket.accept(), this.database);
                client.start();
            }
            
        } catch (IOException e) {
            System.err.println("Beim starten des Servers ist etwas schiefgelaufen: " + e.getMessage());
        }
    }

    public void stop() {
        this.running = false;

        try {
            if(this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Beim stoppen des Servers ist etwas schiefgelaufen: " + e.getMessage());
        }

        for (ClientThread client: this.clients) {
            client.stopp();
        }
    }

    public String getIdOfAvailableClients(ClientThread self) {
        String allClients = "";
        for (ClientThread client: this.clients) {
            client.writer.println(self.id + " hat den Chatraum betreten.");
            if (client != self) {
                allClients += client.id + ", ";
            }
        }
        if (allClients == "") {
            return "Derzeit sind keine weiteren Personen im Chat.";
        } 
        return allClients; 
    }

    public void addClientThread(ClientThread client) {
        this.clients.add(client);
    }

    public void sendMessageToAll(ClientThread self, String msg) {
        for(ClientThread client: this.clients) {
            if (client != self) {
                client.writer.write(msg);
            }
        }
    }
}

