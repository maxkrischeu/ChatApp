import java.awt.*;
import java.awt.event.*;

public class Chatfenster {
    Frame frame;
    ClientTest client;

    public Chatfenster(ClientTest client) {

        this.frame = new Frame("Chatfenster-Client");
        this.frame.setSize(800, 600);
        this.frame.setLayout(new GridBagLayout());

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

        Panel chatverwaltung = new Panel(new GridBagLayout());

        // --- Zeile 0: Überschrift ---
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.weightx = 1.0;
        c1.weighty = 0.0;
        c1.fill = GridBagConstraints.HORIZONTAL;

        Panel chatFensterPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        chatFensterPanel.add(new Label("Aktueller Raum:", Label.CENTER));
        chatverwaltung.add(chatFensterPanel, c1);

        // --- Zeile 1: Chat-Anzeige ---
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.weightx = 1.0;
        c2.weighty = 1.0;
        c2.fill = GridBagConstraints.BOTH;

        TextArea chatanzeige = new TextArea();
        chatanzeige.setEditable(false);
        chatanzeige.setBackground(frame.getBackground());
        chatanzeige.setForeground(Color.BLACK);
        chatverwaltung.add(chatanzeige, c2);

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
        chatPanel.add(chateingabe);
        chatPanel.add(senden);
        chatverwaltung.add(chatPanel, c3);

        frame.add(chatverwaltung, gbcChat);


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
        TextArea raumanzeige = new TextArea();
        raumanzeige.setEditable(false);
        raumanzeige.setBackground(frame.getBackground());
        raumanzeige.setForeground(Color.BLACK);
        raumverwaltung.add(raumanzeige, gbcInner);

        // --- Nutzer-Label ---
        gbcInner.gridy = 2;
        gbcInner.weighty = 0.0;
        Panel labelNutzer = new Panel(new FlowLayout(FlowLayout.CENTER));
        labelNutzer.add(new Label("Nutzer im Raum:"));
        raumverwaltung.add(labelNutzer, gbcInner);

        // --- Nutzer-Anzeige ---
        gbcInner.gridy = 3;
        gbcInner.weighty = 1.0;
        TextArea nutzeranzeige = new TextArea();
        nutzeranzeige.setEditable(false);
        nutzeranzeige.setBackground(frame.getBackground());
        nutzeranzeige.setForeground(Color.BLACK);
        raumverwaltung.add(nutzeranzeige, gbcInner);

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

        frame.add(buttons, gbcButtons);
    }

    public void anzeigen() {
        this.frame.setVisible(true);
    }
}