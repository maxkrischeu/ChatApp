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
    }

    public String showAvailableClients() {
        String allClients = "";
        for (ClientThread client: this.clients) {
            allClients += client.id + ", ";
        }
        return allClients;
    }

    public void addClientThread(ClientThread client) {
        this.clients.add(client);
    }
}
