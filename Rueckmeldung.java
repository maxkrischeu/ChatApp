import java.awt.*;
import java.awt.event.*;

public class Rueckmeldung{
    Frame frame;
    Button button;
    ClientTest client;
    GridBagConstraints gbc;
    Chatfenster chat;
    
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
        this.chat = new Chatfenster(this.client);
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
            this.chat.anzeigen();
        });
    }

    //Wenn Registrierung nicht erfolgreich, dann muss ich noch Dinge ändern
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
}