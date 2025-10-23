import java.util.HashMap;

public class DataBase {
    HashMap<String, String> users;

    public DataBase() {
        this.users = new HashMap<>();
        this.users.put("max", "1234");
        this.users.put("jenny", "1234");
    }

    public Boolean checkLogIn(String id, String pw){
        if (!users.containsKey(id)) return false;

        return users.get(id).equals(pw);
    }
}
