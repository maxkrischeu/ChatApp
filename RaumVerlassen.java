import java.awt.*;
import java.awt.event.*;

public class RaumVerlassen {
    Frame frame;
    Button jaButton;
    Button neinButton;
    ClientTest client;

    public RaumVerlassen(ClientTest client) {
        this.client = client;

        this.frame = new Frame("Raum Verlassen bestätigen");
        this.frame.setSize(600, 200);
        this.frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // -------- Label (zentriert) --------
        gbc.gridy = 0;
        gbc.weighty = 1.0;

        Label errorL = new Label(
            "Du bist die letzte Person in diesem Raum. Das Verlassen wird den Raum löschen. Fortfahren?",
            Label.CENTER
        );
        this.frame.add(errorL, gbc);

        // -------- Button-Panel --------
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(20, 0, 20, 0);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        Button jaButton = new Button("Ja");
        Button neinButton = new Button("Nein");

        buttonPanel.add(jaButton);
        buttonPanel.add(neinButton);

        this.frame.add(buttonPanel, gbc);

        // -------- Aktionen --------
        neinButton.addActionListener(e -> {
            this.frame.setVisible(false);
            this.frame.remove(errorL);
        });

        jaButton.addActionListener(e -> {
            client.write("Button gedrückt");
            client.write("Lösche den Raum");
            String oldRoom = client.chat.rooms.getSelectedItem();
            client.write(oldRoom);

            this.frame.setVisible(false);
            this.frame.remove(errorL);

            client.chat.roomLabel.setText("Aktueller Raum: Lobby");
            client.chat.rooms.remove(oldRoom);
        });

        this.frame.setVisible(true);
    }
}