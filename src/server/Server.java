package server;

import client_connection.Authenticator;
import client_connection.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private final int commandPort = 43896;
    private final int messagePort = commandPort + 1;

    private ServerSocket commandSocket;
    private ServerSocket messageSocket;
    public HashMap<String, Client> userList;
    private boolean serverRunning;

    public Server() throws IOException {
        commandSocket = new ServerSocket(commandPort);
        serverRunning = true;
        userList = new HashMap<>();

        acceptConnection();
    }

    private void acceptConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (serverRunning) {
                    try {
                        Socket socket = commandSocket.accept();
                        if (socket != null) {
                            new Authenticator(socket).start();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {

    }
}
