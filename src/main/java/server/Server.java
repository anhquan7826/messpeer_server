package server;

import client_connection.Client;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int commandPort = 43896;
    private ServerSocket commandSocket;
    private boolean serverRunning;

    public Server() throws IOException {
        commandSocket = new ServerSocket(commandPort);
        System.out.println("Server is listening on port " + commandPort);
        serverRunning = true;
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
                            new Client(socket);
                            System.out.println("Client at " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " connected!");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
