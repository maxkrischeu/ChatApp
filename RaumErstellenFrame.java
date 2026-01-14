import java.awt.*;
import java.awt.event.*;

public class RaumErstellenFrame{
    private Frame frame;
    private Button ok;
    private Button abbrechen;
    private ClientTest client;
    private GridBagConstraints gbc;
    private Chatfenster chat;
    private TextField raumname;
    private Panel buttonPanel;

    public RaumErstellenFrame(ClientTest client, Chatfenster chat){
        this.client = client;
        this.chat = chat;

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
        enterRoomName();

        this.gbc.gridy = 2;
        this.buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        setAbbruch();
        setOk();
        this.frame.add(buttonPanel, gbc);

    }

    public void visible(){
        this.frame.setVisible(true); 
    }


    private void setOk(){
        this.ok = new Button("OK");
        this.ok.setSize(10,30);
        gbc.gridx = 0;
        this.ok.addActionListener(e -> {
            this.client.write("Button gedrückt");
            this.frame.setVisible(false);
            this.client.write("Raum Erstellen");
            this.client.write(this.getRoomName());
        });
        this.buttonPanel.add(this.ok);
    }

    private void setAbbruch(){
        this.abbrechen = new Button("Abbrechen");
        this.abbrechen.setSize(10,30);
         gbc.gridx = 1;
        this.abbrechen.addActionListener(e -> {
            this.frame.setVisible(false);
        });
        this.buttonPanel.add(this.abbrechen);
    }

    private void enterRoomName(){
        this.raumname = new TextField(20); 
        this.frame.add(raumname, gbc);
    }

    public String getRoomName(){
        return this.raumname.getText();
    }
}