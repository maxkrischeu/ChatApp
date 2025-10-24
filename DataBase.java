import java.sql.*;

public class DataBase{
    public static void main(String[] args){
        try{
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clients", "root", "");
            Statement stmt = conn.createStatement(); 
            String name = "anton";
            String passwort = "1234";
            String sql = "INSERT INTO chatnutzer (name, passwort) VALUES ('"+name+"', '" + passwort+"')";
            //String sql = "INSERT INTO chatnutzer (name, passwort) VALUES ('jenny', '123')";

            stmt.executeUpdate(sql); 
            stmt.executeQuery("SELECT * FROM chatnutzer");
        }
        catch (SQLException ex) {
        // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
