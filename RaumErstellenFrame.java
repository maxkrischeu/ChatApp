import java.awt.*;
import java.awt.event.*;

public class RaumErstellenFrame{
    Frame frame;
    Button ok;
    Button abbrechen;
    ClientTest client;
    GridBagConstraints gbc;
    Chatfenster chat;
    TextField raumname;
    Panel buttonPanel;

    public RaumErstellenFrame(ClientTest client){
        this.client = client;
        this.chat = new Chatfenster(this.client);

        this.frame = new Frame("Neuen Raum erstellen"); 
        this.frame.setSize(400,200);
        this.frame.setLayout(new GridBagLayout()); 
        this.gbc = new GridBagConstraints();
        this.gbc.insets = new Insets(30,0,0,0);
        this.gbc.fill = GridBagConstraints.HORIZONTAL;
        this.gbc.anchor = GridBagConstraints.CENTER;
        this.gbc.gridx = 0;

        this.gbc.gridy = 0;
        this.frame.add(new Label("Raumname eingeben: ", Label.CENTER), this.gbc);

        this.gbc.gridy = 1;
        setRaumname();

        this.gbc.gridy = 2;
        this.buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        setAbbruch();
        setOk();
        this.frame.add(buttonPanel, gbc);

    }

    public void raumErstellen(){
        this.frame.setVisible(true); 
    }

    public void okay(){
        this.frame.setVisible(false);
    }

    public void abbrechen(){
        this.frame.setVisible(false);
    }

    public void setOk(){
        this.ok = new Button("OK");
        this.ok.setSize(10,30);
        gbc.gridx = 0;
        this.ok.addActionListener(e -> {
            okay();
        });
        this.buttonPanel.add(this.ok);
    }

    public void setAbbruch(){
        this.abbrechen = new Button("Abbrechen");
        this.abbrechen.setSize(10,30);
         gbc.gridx = 1;
        this.abbrechen.addActionListener(e -> {
            abbrechen();
        });
        this.buttonPanel.add(this.abbrechen);
    }

    public void setRaumname(){
        this.raumname = new TextField(20); 
        this.frame.add(raumname, gbc);
    }

    public String getRaumname(){
        return this.raumname.getText();
    }
}