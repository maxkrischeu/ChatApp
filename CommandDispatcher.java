import java.util.Scanner;

public class CommandDispatcher extends Thread {
    private volatile Boolean running;
    private Server server;
    private Scanner scanner;

    public CommandDispatcher(Server server) {
        this.running = true;
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println("Herzlich Willkommen in unserem kleinen Chatsystem. Sie befinden sich im Adminbereich. Für mögliche Befehle bitte 'hilfe' eingeben.");

        while(this.running) {
            String cmd = scanner.nextLine();

            switch(cmd) {
                case "stopp":
                    if (this.server.running) {
                        this.server.stop();
                        break;
                    } else {
                        System.out.println("Der Server ist schon heruntergefahren.");
                        break;
                    }
                case "start":
                    if (!this.server.running) {
                        // Wir brauchen hier einen eigenen Thread, da wir sonst nicht mehr aus this.server.start() herauskommen würden.
                        new Thread() {
                            public void run() {
                                // Dieser Thread ist eine anonyme Klasse innerhalb vom CommandDispatcher. 
                                // Dieser kennt nicht die Attribute vom CommandDispatcher, weshalb ich explizit sagen muss: gehe bitte zur äußeren Klasse und suche nach dem Attribut Server dort.
                                CommandDispatcher.this.server.start();
                            }
                        }.start();
                        break;
                    } else {
                        System.out.println("Der Server läuft schon.");
                        break;
                    }
                case "beenden":
                    this.running = false;
                    this.server.stop();
                    this.scanner.close();
                    break;
                case "hilfe":
                    System.out.println("Mögliche Eingabebefehle: 'start', 'stopp', 'beenden'.");
                    break;
                default: 
                    System.out.println("Dies ist keine gültige Eingabe. Siehe 'hilfe' für mögliche eingaben.");
                    break;
            }
        }
        System.out.println("Das Programm wurde beendet.");
    }
}
