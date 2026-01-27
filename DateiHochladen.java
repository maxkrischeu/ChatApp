import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class DateiHochladen{
    private Chatfenster chat;
    private ClientTest client;
    private Frame frame;
    private Button hochladen;
    private Button abbrechen;
    private GridBagConstraints gbc;
    private Panel dropArea;
    private Set<File> selectedFiles = new HashSet<>();
    private Label statusLabel;
    
    public DateiHochladen(ClientTest client, Chatfenster chat){
        this.client = client;
        this.chat = chat;

        this.frame = new Frame("Datei hochladen");
        this.frame.setSize(600,400);
        this.gbc = new GridBagConstraints();
        this.gbc.fill = GridBagConstraints.BOTH;

        //DropArea:
        this.dropArea = new Panel(new GridBagLayout());
        this.statusLabel = new Label("Datei hier reinziehen mit Drag & Drop", Label.CENTER);
        this.statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        GridBagConstraints inner = new GridBagConstraints();
        inner.gridx = 0;
        inner.gridy = 0;
        inner.weightx = 1.0;
        inner.weighty = 1.0;
        inner.fill = GridBagConstraints.BOTH;
        inner.insets = new Insets(20,20,20,20);

        this.dropArea.add(this.statusLabel, inner);

         // Drop-Area in die Mitte des Frames
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 20, 10, 20);
        this.frame.add(this.dropArea, this.gbc);

        // ====== Unten: Buttons links/rechts ======
        this.abbrechen = new Button("Abbrechen");

        this.abbrechen.addActionListener(e -> {
            this.frame.dispose();
        });

        this.hochladen = new Button("Hochladen");

        // links unten: Abbrechen
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 20, 10);
        this.frame.add(this.abbrechen, this.gbc);

        // rechts unten: Hchladen
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 20, 20);
        this.frame.add(this.hochladen,this.gbc);

        //Wohin soll gedropped werden -> dropArea 
        //Was passiert bei einem drop -> DropTargetAdapter
        new DropTarget(dropArea, 
            new DropTargetAdapter(){
            @Override
            //Brauchen wir für den Cast in eine Liste, weil zu Compilezeit nicht klar ist, dass das passt
            @SuppressWarnings("unchecked")
            //Reaktion des Programms auf einen Drop (wie ActionListener für Button)
            public void drop(DropTargetDropEvent dtde) {
                try{
                    //dtde ist das, was gedropped wird
                    //Test, ob der Drop eine Datei ist (PDF, Bild,...)
                    if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                        //Drop Datei wird akzeptiert -> Drop = eine Kopie der Datei
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        //t ist Objekt, mit dem ich Zugriff auf die Daten des Drops habe
                        Transferable t = dtde.getTransferable(); 
                        //die Daten, die gedropped wurden in einer Liste
                        List<File> list_temp = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        setSelectedFiles(list_temp);
                        setStatusLabel(list_temp);
                        dtde.dropComplete(true);
                    }
                    else{
                        dtde.rejectDrop();
                        statusLabel.setText("Bitte eine Datei droppen (nicht Text).");
                    }
                }
                catch (Exception e){
                    DateiHochladen.this.selectedFiles = null;
                    DateiHochladen.this.statusLabel.setText("Fehler beim Drop: " + e.getMessage());
                    dtde.dropComplete(false);
                }
            }
        });
        this.frame.setVisible(true);
    }

    public void setSelectedFiles(List<File> list){
        for(File file : list){
            this.selectedFiles.add(file);
        }
    }

    public void setStatusLabel(List<File> droppedFiles){
        if(droppedFiles!=null && !droppedFiles.isEmpty()){
            this.statusLabel.setText("Es wurden " + droppedFiles.size() + " Dateien zum Hochladen ausgewählt");
        }
        else{
            this.statusLabel.setText("Es wurde keine Datei erkannt.");
            this.selectedFiles = null;
        }
    }  

    public void setHochladenButton(){
        this.hochladen.addActionListener(e -> {
            if (this.selectedFiles == null) {
                this.statusLabel.setText("Bitte zuerst eine Datei droppen.");
            }
            else{
                for(File file : this.selectedFiles){
                    upload(file);
                    this.frame.setVisible(false);
                }
            }
        });
    }

    //TODO:
    public void upload(File file){
        try(FileOutputStream output = new FileOutputStream(file.getName)){
            int b;
            while(b )
        }
    } 
}



        