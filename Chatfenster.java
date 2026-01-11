import java.awt.*;
import java.awt.event.*;

public class Chatfenster {
    Frame frame;
    ClientTest client;
    StartFrame startframe;
    RaumErstellenFrame raumerstellen;
    Label roomLabel;
    Panel chatFensterPanel;
    Panel chatverwaltung;
    GridBagConstraints c1;
    Button raumErstellen; 
    Button raumBeitreten; 
    Button raumVerlassen;
    Button dateiHochladen;
    Button dateienAnzeigen;
    Button dateiHerunterladen;
    List rooms;
    List user;
    List chatanzeige;

    public Chatfenster(ClientTest client) {
        this.client = client;
        this.startframe = client.startframe;
        this.frame = new Frame("Chatfenster-Client");
        this.frame.setSize(800, 600);
        this.frame.setLayout(new GridBagLayout());
        this.chatverwaltung = new Panel(new GridBagLayout());
        this.chatFensterPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        this.roomLabel = new Label("", Label.CENTER);
        this.c1 = new GridBagConstraints();
        this.raumErstellen = new Button("Raum erstellen"); 
        this.raumBeitreten = new Button("Raum beitreten"); 
        this.raumVerlassen = new Button("Raum verlassen");
        this.dateiHochladen = new Button("Datei hochladen");
        this.dateienAnzeigen = new Button("Dateien anzeigen");
        this.dateiHerunterladen = new Button("Datei herunterladen");
        this.rooms = new List();
        this.user = new List();
        this.chatanzeige = new List();

        /*
         * -------------------------------------------------------
         * 1. LINKE SPALTE – Chatbereich
         * -------------------------------------------------------
         */

        GridBagConstraints gbcChat = new GridBagConstraints();
        gbcChat.gridx = 0;
        gbcChat.gridy = 0;
        gbcChat.weightx = 0.5;
        gbcChat.weighty = 1.0;
        gbcChat.fill = GridBagConstraints.BOTH;

        // --- Zeile 0: Überschrift ---
        this.c1.gridx = 0;
        this.c1.gridy = 0;
        this.c1.weightx = 1.0;
        this.c1.weighty = 0.0;
        this.c1.fill = GridBagConstraints.HORIZONTAL;
        this.roomLabel.setText("Aktueller Raum: Lobby");
        this.roomLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        this.chatFensterPanel.add(this.roomLabel);
        this.chatverwaltung.add(this.chatFensterPanel, this.c1);

        // --- Zeile 1: Chat-Anzeige ---
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.weightx = 1.0;
        c2.weighty = 1.0;
        c2.fill = GridBagConstraints.BOTH;

        this.chatverwaltung.add(this.chatanzeige, c2);

        // --- Zeile 2: Eingabefeld + Button ---
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 2;
        c3.weightx = 1.0;
        c3.weighty = 0.0;
        c3.fill = GridBagConstraints.HORIZONTAL;

        Panel chatPanel = new Panel(new FlowLayout());
        TextField chateingabe = new TextField(20);
        Button senden = new Button("Senden");
        senden.addActionListener(e -> {
            String msg = "[" + client.startframe.getUsername() + "]" + ": " + chateingabe.getText();
            this.chatanzeige.add(msg +"\n");
            this.client.write(chateingabe.getText());
            chateingabe.setText("");
        });
        chatPanel.add(chateingabe);
        chatPanel.add(senden);
        this.chatverwaltung.add(chatPanel, c3);

        frame.add(this.chatverwaltung, gbcChat);


        /*
         * -------------------------------------------------------
         * 2. MITTLERE SPALTE – Räume + Nutzer
         * -------------------------------------------------------
         */

        GridBagConstraints gbcRaum = new GridBagConstraints();
        gbcRaum.gridx = 1;
        gbcRaum.gridy = 0;
        gbcRaum.weightx = 1.5;
        gbcRaum.weighty = 1.0;
        gbcRaum.fill = GridBagConstraints.BOTH;

        Panel raumverwaltung = new Panel(new GridBagLayout());

        GridBagConstraints gbcInner = new GridBagConstraints();
        gbcInner.gridx = 0;
        gbcInner.weightx = 1.0;
        gbcInner.fill = GridBagConstraints.BOTH;

        // --- Räume-Label ---
        gbcInner.gridy = 0;
        gbcInner.weighty = 0.0;
        Panel labelRaume = new Panel(new FlowLayout(FlowLayout.CENTER));
        labelRaume.add(new Label("Räume:"));
        raumverwaltung.add(labelRaume, gbcInner);

        // --- Räume-Anzeige ---
        gbcInner.gridy = 1;
        gbcInner.weighty = 1.0;
        raumverwaltung.add(this.rooms, gbcInner);

        // --- Nutzer-Label ---
        gbcInner.gridy = 2;
        gbcInner.weighty = 0.0;
        Panel labelNutzer = new Panel(new FlowLayout(FlowLayout.CENTER));
        labelNutzer.add(new Label("Nutzer im Raum:"));
        raumverwaltung.add(labelNutzer, gbcInner);

        // --- Nutzer-Anzeige ---
        gbcInner.gridy = 3;
        gbcInner.weighty = 1.0;
        raumverwaltung.add(this.user, gbcInner);

        frame.add(raumverwaltung, gbcRaum);


        /*
         * -------------------------------------------------------
         * 3. RECHTE SPALTE – Buttons
         * -------------------------------------------------------
         */

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 2;
        gbcButtons.gridy = 0;
        gbcButtons.weightx = 0.2;
        gbcButtons.weighty = 1.0;
        gbcButtons.fill = GridBagConstraints.BOTH;

        Panel buttons = new Panel(new GridLayout(0, 1));

        this.raumErstellen.addActionListener(e -> {
            this.raumerstellen = new RaumErstellenFrame(this.client, this);
            this.raumerstellen.visible();
        });

        this.raumBeitreten.addActionListener(e -> {
            this.client.write("Button gedrückt");
            this.client.write("Raum Beitreten");
            String name = this.rooms.getSelectedItem();
            this.client.write(name);
            setRoomName(name);
        });

        this.raumVerlassen.addActionListener(e -> {
            this.client.write("Button gedrückt");
            this.client.write("Raum Verlassen");
            String name = this.rooms.getSelectedItem();
            this.client.write(name);
        });

        buttons.add(this.raumErstellen);

        buttons.add(this.raumBeitreten);

        buttons.add(this.raumVerlassen);

        buttons.add(this.dateiHochladen);

        buttons.add(this.dateienAnzeigen);

        buttons.add(this.dateiHerunterladen); 

        frame.add(buttons, gbcButtons);
    }

    public void anzeigen() {
        this.frame.setVisible(true);
    }

    public void setRoomName(String roomName){
        this.roomLabel.setText("Aktueller Raum: " + roomName);
    }

    public void addRoomName(String roomName) {
        this.rooms.add(roomName.trim());
    }
}