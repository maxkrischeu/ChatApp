import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.*;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private ServerSocket serverSocket;
    private int port;
    volatile boolean running;
    private DataBase database;
    private ArrayList<ClientThread> clients;
    private Consumer<String> logListener = msg -> {};
    private Consumer<String> userAdded = id -> {};
    private Consumer<String> userRemoved = id -> {};
    private Consumer<String> roomAdded = name -> {};
    private Consumer<String> roomRemoved = name -> {};
    private Map<String, Room> rooms = new HashMap<>();
    private Path logFile = Paths.get("server-log.txt");
    private Set<String> bannedUsers = new HashSet<>();

    public Server(int port) {
        this.port = port;
        this.database = new DataBase();
        this.running = false;
    }

    public void start() {
        this.running = true;
        this.clients  = new ArrayList<>();

        this.rooms.put("Lobby", new Room("Lobby"));
        this.roomAdded.accept("Lobby");

        this.createRoom("Java");
        this.createRoom("Offtopic");

        try {
            this.serverSocket = new ServerSocket(this.port);
            this.log("Server erfolgreich gestartet.");
        } catch (IOException e) {
            this.log("Fehler beim Erstellen des ServerSockets: " + e.getMessage());
        }

        try {
            while(this.running) {
                ClientThread client = new ClientThread(this, serverSocket.accept());
                client.start();
            }
        } catch (IOException e) {
            if (!this.running && this.serverSocket.isClosed()) {
                this.log("Server erfolgreich heruntergefahren.");
            } else {
                this.log("Beim Akzeptieren einer Verbindung ist etwas schiefgelaufen: " + e.getMessage());
            }
        }
    }

    public boolean checkLogIn(String id, String pw) {
        return this.database.checkLogIn(id, pw);
    }

    public boolean checkName(String id) {
        return this.database.checkName(id);
    }

    public void registrieren(String id, String pw) {
        this.database.registrieren(id, pw);
    }

    public void stop() {
        this.running = false;

        try {
            if(this.serverSocket != null) {
                this.serverSocket.close();
                this.database.clear(); 
            }
        } catch (IOException e) {
            this.log("Beim stoppen des Servers ist etwas schiefgelaufen: " + e.getMessage());
        }

        for (ClientThread client: this.clients) {
            // hier eventuell noch Name anpassen?
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
                if (client.getID() != self.getID()) {
                    client.write(self.getID() + " hat den Chatraum betreten.");
                    allClients += client.getID();
                }
            }
            return (allClients.replace(self.getID(), "")+".");
        } else {
            String allClients = "Online: ";
            int i = 1;
            for (ClientThread client: this.clients) {
                if (client.getID() != self.getID()) {
                    client.write(self.getID() + " hat den Chatraum betreten.");
                    allClients += client.getID();
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
        this.log("Anmeldung erfolgreich: " + client.getID());
        this.userAdded.accept(client.getID());

        client.setCurrentRoom("Lobby");
        this.rooms.get("Lobby").addMember(client);
    }

    public void removeClientThread(ClientThread client) {
        this.clients.remove(client);
        this.log("Abmeldung erfolgreich: " + client.getID());
        this.userRemoved.accept(client.getID());
    }

    public void sendMessageToRoom(String roomName, ClientThread self, String msg) {
        Room room = this.rooms.get(roomName);
        for(ClientThread client: room.getMembers()) {
            if (client != self) {
                client.write(msg);
            }
        }
        this.log("[" + roomName + "]" + msg);
    }

    public boolean createRoom(String name) {
        if (name == null) return false;
        name = name.trim();
        if (name.isEmpty()) return false;
        if (this.rooms.containsKey(name)) return false;

        this.rooms.put(name, new Room(name));
        this.log("Raum erstellt: " + name);
        this.roomAdded.accept(name);
        return true;
    }

    public void setLogListener(Consumer<String> logListener) {
        this.logListener = (logListener != null) ? logListener : (msg -> {});
    }

    public void setUserAddedListener(Consumer<String> userAdded) {
        this.userAdded = (userAdded != null) ? userAdded : (id -> {});
    }

    public void setUserRemovedListener(Consumer<String> userRemoved) {
        this.userRemoved = (userRemoved != null) ? userRemoved : (id -> {});
    }

    public void setRoomAddedListener(Consumer<String> roomAdded) {
        this.roomAdded = (roomAdded != null) ? roomAdded : (name -> {});
    }

    public void setRoomRemovedListener(Consumer<String> roomRemoved) {
        this.roomRemoved = (roomRemoved != null) ? roomRemoved : (name -> {});
    }

    private void log(String msg) {
        System.out.println(msg);
        logListener.accept(msg);

        try {
            Files.writeString(this.logFile, msg + "\n", StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Konnte nicht loggen:" + e.getMessage());
        }
    }

    public void joinRoom(ClientThread client, String newRoom) {
        if (!this.rooms.containsKey(newRoom)) {
            this.createRoom(newRoom);
        }

        Room oldRoom = this.rooms.get(client.getCurrentRoom());
        oldRoom.removeMember(client);

        rooms.get(newRoom).addMember(client);
        client.setCurrentRoom(newRoom);

        this.log(client.getID() + "wechselt von " + oldRoom + " nach " + newRoom);
        this.sendMessageToRoom(oldRoom.getName(), client, "[INFO] " + client.getID() + " hat den Raum verlassen.");
        sendMessageToRoom(newRoom, client, "[INFO] " + client.getID() + " ist beigetreten.");
    }

    public void kickUser(String id) {
        ClientThread target = null;
        for (ClientThread client: this.clients) {
            if (id.equals(client.getID())) {
                target = client;
                break;
            }
        }
        if (target == null) {
            this.log("Kick fehlgeschlagen: Nutzer nicht online: " + id);
            return;
        }

        String room = target.getCurrentRoom();
        Room r = this.rooms.get(room);
        r.removeMember(target);

        target.write("Du wurdest vom Server entfernt");

        this.removeClientThread(target);
        target.stopp();
        this.log("Nutzer entfernt: " + id);
    }

    public boolean isBanned(String id) {
        if (id == null) return false;
        return bannedUsers.contains(id.trim());
    }

    public boolean banUser(String id){
        if (id == null) return false;
        id = id.trim();
        if (id.isEmpty()) return false;

        boolean added = bannedUsers.add(id);
        if (added) {
            this.log("Nutzer gebannt: " + id);
            this.kickUser(id);
        }

        return added;
    }

    public boolean deleteRoom(String roomName) {
        if (roomName == null) return false;
        roomName = roomName.trim();
        if (roomName.isEmpty()) return false;

        if (roomName.equals("Lobby")) {
            return false;
        }

        Room room = this.rooms.get(roomName);

        Room lobby = this.rooms.get("Lobby");

        List<ClientThread> copy = new ArrayList<>(room.getMembers());
        for (ClientThread client: copy) {
            room.removeMember(client);
            lobby.addMember(client);
            client.setCurrentRoom("Lobby");

            client.write("Raum" + roomName + " wurde gelöscht. Du bist jetzt in der Lobby.");
        }

        this.rooms.remove(roomName);

        this.log("Raum gelöscht: " + roomName);
        roomRemoved.accept(roomName);

        return true;
    }

    public boolean sendAdminMessageToUser(String id, String message) {
        if (id == null || message == null) return false;
    
        id = id.trim();
        message = message.trim();
    
        if (id.isEmpty() || message.isEmpty()) return false;
    
        ClientThread target = null;
        for (ClientThread client : this.clients) {
            if (id.equals(client.getID())) {
                target = client;
                break;
            }
        }
    
        if (target == null) {
            this.log("[ADMIN] Nachricht nicht gesendet: Nutzer \"" + id + "\" ist nicht online.");
            return false;
        }
        target.write("[ADMIN] " + message);

        return true;
    }
}

