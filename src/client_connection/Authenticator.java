package client_connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Authenticator extends Thread {
    private Socket clientConnection;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public Authenticator(Socket socket) throws IOException {
        clientConnection = socket;
        bufferedReader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        printWriter = new PrintWriter(clientConnection.getOutputStream());
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                message = bufferedReader.readLine();
                if (message.startsWith("INITIAL_MESSAGE")) {
                    message = message.split(":")[1];
                    // TODO: Query db. if true create new Client.
                    Client client = new Client(clientConnection);
                    client.start();
                } else {
                    printWriter.println("AUTHENTICATE_FAIL");
                }
            } catch (IOException e) {
                printWriter.println("AUTHENTICATE_FAIL");
            }
        }
    }
}
