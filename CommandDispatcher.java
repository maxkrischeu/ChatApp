import java.util.Scanner;

public class CommandDispatcher extends Thread {
    volatile Boolean running;
    Server server;
    Scanner scanner;

    public CommandDispatcher(Server server) {
        this.running = true;
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while(running) {
            String cmd = scanner.nextLine();

            switch(cmd) {
                case "stop":
                    if (this.server.running) {
                        this.server.stop();
                        System.out.println("Server wurde heruntergefahren.");
                        break;
                    } else {
                        System.out.println("Server ist schon heruntergefahren");
                        break;
                    }
                case "start":
                    if (!this.server.running) {
                        new Thread() {
                            public void run() {
                                CommandDispatcher.this.server.start();
                            }
                        }.start();
                        System.out.println("Server wurde wieder gestartet.");
                        break;
                    } else {
                        System.out.println("Server läuft schon.");
                        break;
                    }
                case "beenden":
                    this.running = false;
                    break;
                default: 
                    System.out.println("Jo, das kenne ich nicht");
                    break;
            }
        }
        System.out.println("Programm und server wird beendet");
    }
}
