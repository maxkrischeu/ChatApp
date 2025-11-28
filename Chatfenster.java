import java.awt.*;
import java.awt.event.*;

public class Chatfenster{
    Frame frame;
    ClientTest client;

    public Chatfenster(ClientTest client){
        this.frame = new Frame("Chatfenster-Client");
        this.frame.setSize(800,600); 
        GridBagLayout chat_layout = new GridBagLayout();
        frame.setLayout(chat_layout);

        GridBagConstraints gbc = new GridBagConstraints();
        
        //Eigentliches Chatfenster
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        Panel chatverwaltung = new Panel();
        GridLayout chat = new GridLayout(0, 1);
        chatverwaltung.setLayout(chat);

        //Aktueller Raum plus Chat
        Panel chatFensterPanel = new Panel(new GridLayout(2, 1));
        chatFensterPanel.add(new Label("Aktueller Raum: ", 1));
        TextArea chatanzeige = new TextArea();
        chatFensterPanel.add(chatanzeige);
        chatverwaltung.add(chatFensterPanel);

        //Nachrichteneingabe plus Sendenbutton
        Panel chatPanel = new Panel(new FlowLayout()); 
        TextField chateingabe = new TextField(20);
        chatPanel.add(chateingabe);
        Button senden = new Button("Senden");
        chatPanel.add(senden); 
        chatverwaltung.add(chatPanel);

        frame.add(chatverwaltung, gbc);

        //Spalte mit Räume und Nutzer im Raum
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        Panel raumverwaltung = new Panel();
        GridLayout raume = new GridLayout(0, 1);
        raumverwaltung.setLayout(raume);

        //verfügbare Räume
        Panel raumPanel = new Panel(new GridLayout(2, 1));
        raumPanel.add(new Label("Räume:", 1));
        TextArea raumanzeige = new TextArea();
        raumPanel.add(raumanzeige);
        raumverwaltung.add(raumPanel);

        //Nutzer im Raum
        Panel nutzerPanel = new Panel(new GridLayout(2, 1));
        nutzerPanel.add(new Label("Nutzer im Raum:", 1));
        TextArea nutzeranzeige = new TextArea();
        nutzerPanel.add(nutzeranzeige);
        raumverwaltung.add(nutzerPanel);

        frame.add(raumverwaltung, gbc);

        //Spalte mit den Buttons
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;

        Panel buttons = new Panel();
        GridLayout buttonPanel = new GridLayout(0, 1);
        buttons.setLayout(buttonPanel);

        Button raumErstellen = new Button("Raum erstellen"); 
        buttons.add(raumErstellen);

        Button raumBeitreten = new Button("Raum beitreten"); 
        buttons.add(raumBeitreten);

        Button raumVerlassen = new Button("Raum verlassen");
        buttons.add(raumVerlassen);

        Button dateiHochladen = new Button("Datei hochladen");
        buttons.add(dateiHochladen);

        Button dateienAnzeigen = new Button("Dateien anzeigen");
        buttons.add(dateienAnzeigen);

        Button dateiHertunerladen = new Button("Datei herunterladen");
        buttons.add(dateiHertunerladen); 

        frame.add(buttons, gbc);

    }

    public void anzeigen(){
        this.frame.setVisible(true); 
    }
}