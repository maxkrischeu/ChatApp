import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name; 
    private List<ClientThread> members = new ArrayList<>();

    public Room(String name) {
        this.name = name;   
    }

    public String getName() {
        return this.name;
    }

    public List<ClientThread> getMembers() {
        return this.members;
    }

    public void addMember(ClientThread client) {
        this.members.add(client);
    }

    public void removeMember(ClientThread client) {
        this.members.remove(client);
    }
}
