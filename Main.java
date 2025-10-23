public class Main {
    public static void main(String[] args) {
        Server server = new Server(5001);
        CommandDispatcher dispatcher= new CommandDispatcher(server);

        dispatcher.start();
    }
}