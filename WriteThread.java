import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class WriteThread extends Thread {
    PrintWriter writer;
    ClientThread client;

    public WriteThread(OutputStream out, ClientThread client) {
        this.writer = new PrintWriter(new OutputStreamWriter(out), true);
        this.client = client;
    }
    

    public void write(String str) {
        this.writer.println(str);
    }
}
