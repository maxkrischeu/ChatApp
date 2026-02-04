import java.util.HashSet;
import java.util.Set;

public class Room {
    private String name; 
    private Set<ClientThread> members = new HashSet<>();

    public Room(String name) {
        this.name = name;   
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ClientThread> getMembers() {
        return this.members;
    }

    public void addMember(ClientThread client) {
        // Set verhindert automatisch doppelte Einträge
        this.members.add(client);
    }

    public void removeMember(ClientThread client) {
        this.members.remove(client);
    }
}
