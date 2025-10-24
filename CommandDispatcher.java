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
                        System.out.println("Der Server wurde heruntergefahren.");
                        break;
                    } else {
                        System.out.println("DerServer ist schon heruntergefahren.");
                        break;
                    }
                case "start":
                    if (!this.server.running) {
                        new Thread() {
                            public void run() {
                                CommandDispatcher.this.server.start();
                            }
                        }.start();
                        System.out.println("Der Server wurde wieder gestartet.");
                        break;
                    } else {
                        System.out.println("Der Server läuft schon.");
                        break;
                    }
                case "beenden":
                    this.running = false;
                    CommandDispatcher.this.server.stop();
                    this.scanner.close();
                    break;
                case "hilfe":
                    System.out.println("Mögliche Eingabebefehle: 'start', 'stop', 'beenden'.");
                    break;
                default: 
                    System.out.println("Dies ist keine gültige Eingabe. Siehe 'hilfe' für mögliche eingaben.");
                    break;
            }
        }
        System.out.println("Das Programm und der Server wurden beendet.");
    }
}
