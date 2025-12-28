import java.awt.*;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerGui {
    private Server server;
    private Frame frame;
    private Button startServerButton;
    private Button stopServerButton;
    private Button removeUserButton;
    private TextArea serverLogArea;
    private java.awt.List userList;
    private java.awt.List roomList;

    public ServerGui(Server server) {
        this.server = server;
        this.frame = new Frame("Chat-Server");
        this.frame.setSize(700, 420);
        this.frame.setLayout(new BorderLayout(10, 10));

        Panel leftPanel = new Panel(new BorderLayout(0, 8));

        this.serverLogArea = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        this.serverLogArea.setEditable(false);
        leftPanel.add(serverLogArea, BorderLayout.CENTER);

        this.server.setLogListener(msg -> EventQueue.invokeLater(() -> writeServerLog(msg)));
        this.server.setUserAddedListener(id -> EventQueue.invokeLater(() -> addUserToList(id)));
        this.server.setUserRemovedListener(id -> EventQueue.invokeLater(() -> removeUserFromList(id)));
        this.server.setRoomAddedListener(roomName -> EventQueue.invokeLater(() -> addRoomToList(roomName)));

        Panel bottomButtons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        this.startServerButton = new Button("Server starten");
        this.stopServerButton = new Button("Server beenden");

        this.stopServerButton.setEnabled(false);

        this.startServerButton.addActionListener(e -> 
            {
                new Thread(() -> this.server.start()).start();
                this.startServerButton.setEnabled(false);
                this.stopServerButton.setEnabled(true);
            }
        );
        this.stopServerButton.addActionListener(e -> 
            {
                this.server.stop();
                this.stopServerButton.setEnabled(false);
                this.startServerButton.setEnabled(true);
            }
        );
        
        bottomButtons.add(startServerButton);
        bottomButtons.add(stopServerButton);

        leftPanel.add(bottomButtons, BorderLayout.SOUTH);

        Panel right = new Panel(new GridBagLayout());
        right.setPreferredSize(new Dimension(220, 0)); // macht rechts schmaler

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.BOTH;

        // --- Angemeldete Nutzer ---
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        right.add(new Label("Angemeldete Nutzer"), c);

        this.userList = new java.awt.List(8, false); // 8 sichtbare Zeilen, single-select
        this.userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = userList.getSelectedItem();
                    if (selected != null) {
                        openMessageDialog(selected);
                    }
                }
            }
        });
        c.gridy = 1;
        c.weighty = 0.55; // nimmt etwas mehr Platz als Rooms
        right.add(this.userList, c);

        // --- Räume ---
        c.gridy = 2;
        c.weighty = 0;
        right.add(new Label("Räume"), c);

        this.roomList = new java.awt.List(6, false);
        c.gridy = 3;
        c.weighty = 0.45;
        right.add(this.roomList, c);

        // --- Button unten ---
        this.removeUserButton = new Button("Nutzer entfernen");
        this.removeUserButton.addActionListener(e -> onRemoveSelectedUser());
        c.gridy = 4;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        right.add(this.removeUserButton, c);

        this.frame.add(leftPanel, BorderLayout.CENTER);
        this.frame.add(right, BorderLayout.EAST);

        this.frame.setVisible(true);
    }

    private void writeServerLog(String msg) {
        this.serverLogArea.append(msg + "\n");
    }

    private void addUserToList(String id) {
        this.userList.add(id);
    }

    private void removeUserFromList(String id) {
        this.userList.remove(id);
    }

    private void addRoomToList(String roomName) {
        roomList.add(roomName);
    }

    private void onRemoveSelectedUser() {
        String selected = this.userList.getSelectedItem();
        if (selected == null) {
            return;
        }

        Object[] options = {"Kicken", "Kicken + Bannen", "Abbrechen"};

        int choice = JOptionPane.showOptionDialog(
                null,
                "Was möchtest du mit \"" + selected + "\" machen?",
                "Nutzer entfernen",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {          
            this.server.kickUser(selected);
        } else if (choice == 1) {
            this.server.banUser(selected);
        }
    }

    private void openMessageDialog(String userId) {
        JTextArea textArea = new JTextArea(8, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        int result = JOptionPane.showConfirmDialog(
                frame,
                scrollPane,
                "Nachricht an \"" + userId + "\"",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String msg = textArea.getText();
            if (msg != null) msg = msg.trim();

            if (msg == null || msg.isEmpty()) {
                return;
            }
            this.server.sendAdminMessageToUser(userId, msg);

            writeServerLog("[ADMIN -> " + userId + "]: " + msg);
        }
    }

    public static void main(String[] args) {
        new ServerGui(new Server(5001));
    }
}
