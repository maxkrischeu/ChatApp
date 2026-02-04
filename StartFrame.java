import java.awt.*;
import java.awt.event.*;

public class StartFrame{
    private Frame frame;
    private ClientTest client;
    private Rueckmeldung meldung;
    private Panel eingabePanel;
    private TextField bEingabe;
    private String benutzername;
    private TextField pEingabe;
    private Button anmeldeButton;
    private Button registrierenButton;

    public StartFrame(ClientTest client){
        // Konstruktor: Nur GUI-Bausteine erzeugen, noch nichts sichtbar machen
        this.frame = new Frame("Chat-Client");
        this.client = client;
        this.meldung = new Rueckmeldung(client);
        this.eingabePanel = new Panel();
        this.bEingabe = new TextField(20);
        this.pEingabe = new TextField(20);
        this.anmeldeButton = new Button("Anmelden"); 
        this.registrierenButton = new Button("Registrieren"); 
    }

    public void frameStart(){
        // Hauptlayout des Fensters: ein zentriertes Start-Panel
        GridBagLayout frame_layout = new GridBagLayout();
        frame.setLayout(frame_layout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Dieses Panel enthält alle Eingaben und Buttons untereinander
        Panel startPanel = new Panel(); 
        GridLayout startPanel_layout = new GridLayout(0, 1); //1 Spalte, mehrere Zeilen
        startPanel.setLayout(startPanel_layout);
        
        //Chat Anmeldung
        Label chatanmeldung = new Label("Chat Anmeldung", 1); //1 steht für zentrieren
        chatanmeldung.setFont(new Font("SansSerif", Font.BOLD, 14));
        startPanel.add(chatanmeldung); 

        //Label Benutzername
        GridLayout eingabe_layout = new GridLayout(2, 2, 0, 10);
        eingabePanel.setLayout(eingabe_layout);
        setEingabepanel();
        startPanel.add(eingabePanel);

        //Anmeldebutton und Registrierenbutton
        Panel buttonPanel = new Panel(new FlowLayout());
        setAnmeldeButton();
        setRegistrierenButton();
        buttonPanel.add(anmeldeButton);
        buttonPanel.add(registrierenButton);
        startPanel.add(buttonPanel);

        frame.add(startPanel, gbc);
        frame.setSize(800,600);
        frame.setVisible(true);
    }

    public void frameEnd(){
        frame.setVisible(false);
    }

    private void setEingabepanel(){
        Label benutzername = new Label("Benutzername: ", 1); 
        eingabePanel.add(benutzername);
        eingabePanel.add(bEingabe);
        
        Label passwort = new Label("Passwort: ", 1); 
        pEingabe.setEchoChar('*');
        eingabePanel.add(passwort);
        eingabePanel.add(pEingabe);
    }

    private void setAnmeldeButton(){
        anmeldeButton.addActionListener(e -> {
            // Protokoll mit dem Server: erst Aktion, dann Benutzername, dann Passwort
            this.client.write("anmelden");
            this.client.write(bEingabe.getText()); 
            this.client.write(pEingabe.getText());
        });
    }

    private void setRegistrierenButton(){
        registrierenButton.addActionListener(e -> {
            // Gleiches Protokoll wie beim Login, aber mit Aktion "registrieren"
            this.client.write("registrieren");
            this.client.write(bEingabe.getText());
            this.client.write(pEingabe.getText());
        });
    }

    public String getUsername(){
        return bEingabe.getText();
    }
}
