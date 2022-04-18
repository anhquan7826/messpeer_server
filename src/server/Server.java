package server;

import client_connection.Authentication;
import client_connection.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private ServerSocket listeningSocket;
    public HashMap<String, Client> userList;
    private boolean serverRunning;

    public Server(int port) throws IOException {
        listeningSocket = new ServerSocket(port);
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
                        Client client = new Client(listeningSocket.accept());
                        if (Authentication.authenticate(client.getMessage())) {
                            userList.put(client.getUsername(), client);
                            client.sendMessage("AUTHENTICATE_SUCCESS");
                        } else {
                            client.sendMessage("AUTHENTICATE_FAIL");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }


}
