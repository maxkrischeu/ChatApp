import java.awt.*;
import java.awt.event.*;

public class Rueckmeldung{
    private Frame frame;
    private Button button;
    private ClientTest client;
    private GridBagConstraints gbc;
    
    public Rueckmeldung(ClientTest client){
        this.frame = new Frame("Meldung"); 
        this.frame.setSize(600,200);
        this.frame.setLayout(new GridBagLayout()); 
        this.gbc = new GridBagConstraints();
        this.gbc.gridx = 2;
        this.gbc.gridy = 1;
        this.gbc.insets = new Insets(30,0,0,0);
        this.gbc.anchor = GridBagConstraints.CENTER;
        this.button = new Button("OK");
        this.button.setSize(10,10);
        this.frame.add(this.button, this.gbc);
        this.client = client;
    }

    public void meldungErfolgRegistrieren(){
        Label erfolgsmeldungR = new Label("Registrierung erfolgreich. Bitte Anmelden");
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        this.frame.add(erfolgsmeldungR, this.gbc); 
        this.frame.setVisible(true);
        this.button.addActionListener(e -> {
            this.frame.setVisible(false);
            this.frame.remove(erfolgsmeldungR);
        });
    }

    public void meldungErfolgAnmelden(){
        Label erfolgsmeldungA = new Label("Anmeldung erfolgreich.");
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        this.frame.add(erfolgsmeldungA, this.gbc); 
        this.frame.setVisible(true);
        this.button.addActionListener(e -> {
            this.frame.setVisible(false);
            this.frame.remove(erfolgsmeldungA);
            this.client.getStartFrame().frameEnd();
            this.client.getChat().anzeigen();
        });
    }

    public void meldungErrorRegistrieren(){
        Label errorR = new Label("Dieser Benutzername existiert bereits. Gib bitte einen neuen Benutzernamen ein.");
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        this.frame.add(errorR, this.gbc); 
        this.frame.setVisible(true);
        this.button.addActionListener(e -> {
            this.frame.setVisible(false);
            this.frame.remove(errorR);
        });
    }

    public void meldungErrorRegistrierenP(){
    Label errorP = new Label("Du hast kein Passwort eingegeben.");
    this.gbc.gridx = 0;
    this.gbc.gridy = 0;
    this.frame.add(errorP, this.gbc); 
    this.frame.setVisible(true);
    this.button.addActionListener(e -> {
        this.frame.setVisible(false);
        this.frame.remove(errorP);
    });
    }

    public void meldungErrorAnmelden(){
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        Label errorA = new Label("Der Benutzername oder das Passwort sind falsch.");
        this.frame.add(errorA, this.gbc); 
        this.frame.setVisible(true);
        this.button.addActionListener(e -> {
            this.frame.setVisible(false);
            this.frame.remove(errorA);
        });
    }

    public void meldungErrorBeitreten(){
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        Label errorRaum = new Label("Es wurde kein Raum ausgewählt. Bitte wähle einen Raum aus.");
        this.frame.add(errorRaum, this.gbc); 
        this.frame.setVisible(true);
        this.button.addActionListener(e -> {
            this.frame.setVisible(false);
            this.frame.remove(errorRaum);
        });
    }

    public void meldungBannedAnmelden(){
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        Label errorBannedA = new Label("Du bist gebannt und kannst dich nicht anmelden.");
        this.frame.add(errorBannedA, this.gbc); 
        this.frame.setVisible(true);
        this.button.addActionListener(e -> {
            this.frame.setVisible(false);
            this.frame.remove(errorBannedA);
        });
    }

    public void meldungBanned(){
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        Label errorBanned = new Label("Du wurdest vom Server entfernt.");
        this.frame.add(errorBanned, this.gbc); 
        this.frame.setVisible(true);
        this.frame.remove(this.button);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                frame.dispose();
                System.exit(0);
            }
        });
    }
}