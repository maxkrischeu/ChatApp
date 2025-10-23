import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;

public class ReadThread extends Thread {
    BufferedReader reader;
    
    public ReadThread(InputStream in) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    public String nextInput() throws IOException{
        return this.reader.readLine();
    }
}
