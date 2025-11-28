import java.awt.*;

public class FrameApplikationDemo{
    static Frame myFrame;
    static List myList;
    static public void main(String argv[]){
        myFrame = new Frame("Beispielframe ");
        myFrame.setLayout(new BorderLayout());
        myFrame.setSize(300,200);
        myFrame.setVisible(true);
        myFrame.add("North", new Button("I like Java"));

        Button neu = new Button("zurück");
        myFrame.add("South", neu);

        myList = new List(3, false);
        myList.add("Java");
        myList.add("Kaffee");
        myList.add("Kuchen");

        myFrame.add("Center", myList);
        // System.out.print(myList.getItemCount());
        // System.out.print(myList.getItem(2));

        //TextField hello = new TextField("Hello", 20);
        TextArea chat = new TextArea("Hier kannst reinschreiben", 3,2);
        myFrame.add(chat);
        //myFrame.add(hello);
    }
}