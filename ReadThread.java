import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;

public class ReadThread extends Thread {
    BufferedReader reader;
    ClientThread client;
    
    public ReadThread(InputStream in, ClientThread client) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.client = client;
    }

    public void run() {
        while (true) {
            try {
                this.client.server.sendMessageToAll(this.client, this.reader.readLine());
            } catch (IOException e) {
                
            }
            
        }
    }

    public String nextInput() throws IOException{
        return this.reader.readLine();
    }
}
