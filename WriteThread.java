import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class WriteThread extends Thread {
    PrintWriter writer;

    public WriteThread(OutputStream out) {
        this.writer = new PrintWriter(new OutputStreamWriter(out), true);
    }
    

    public void write(String str) {
        this.writer.println(str);
    }
}
