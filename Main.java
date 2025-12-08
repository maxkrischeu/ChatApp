public class Main {
    public static void main(String[] args) {
        
        Server server = new Server(5001);
        CommandDispatcher dispatcher= new CommandDispatcher(server);

        dispatcher.start();
    }
}

//javac -d out -cp "mysql-connector-j-9.5.0.jar" $(find . -name "*.java")
//java -cp "out:mysql-connector-j-9.5.0.jar" ClientTest                  
//java -cp "out:mysql-connector-j-9.5.0.jar" Main                        