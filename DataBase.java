import java.sql.*;

public class DataBase{
    public boolean checkName(String name){
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/clients", "root", "")){
            try(PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM chatnutzer WHERE name = ? LIMIT 1")){
                ps.setString(1, name);
                try(ResultSet rs = ps.executeQuery()){
                        if(rs.next()){
                            System.out.println("Benutzername existiert");
                            return true; 
                        }
                }
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return false;
    } 

    public boolean checkLogIn(String name, String passwort){
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/clients", "root", "")){
            try(PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM chatnutzer WHERE name = ? AND passwort = ?")){
                ps.setString(1, name);
                ps.setString(2, passwort);
                try(ResultSet rs = ps.executeQuery()){
                        if(rs.next()){
                            System.out.println("Passwort stimmt");
                            return true; 
                        }
                }
            }
        }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return false;
    } 

    public void registrieren(String name, String passwort){
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/clients", "root", "")){
            // Ein neuer Benutzer wird in der Tabelle chatnutzer gespeichert
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO chatnutzer (name, passwort) VALUES (?, ?)")){
                ps.setString(1, name);
                ps.setString(2, passwort); 
                int rows = ps.executeUpdate();
                System.out.println(rows + "Zeile wurde eingefügt");
                }
            }

        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void clear(){
        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/clients", "root", "")){
            // Für Tests/Neustarts: alle gespeicherten Chatnutzer löschen
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM chatnutzer")){
                ps.executeUpdate();
                System.out.println("Datenbank wurde geleert");
                }
            }
        catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
