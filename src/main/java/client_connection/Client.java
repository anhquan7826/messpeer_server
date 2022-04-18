package client_connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private final String username;
    private boolean clientConnected;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        clientConnected = true;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        username = bufferedReader.readLine().trim();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (socket.getInputStream().available() < 0) {
                            clientConnected = false;
                            break;
                        }
                    } catch (IOException e) {
                        clientConnected = false;
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String message) throws Exception {
        if (clientConnected) {
            printWriter.println(message);
        } else {
            throw new Exception("Client is disconnected!");
        }
    }

    public String getMessage() throws Exception {
        if (clientConnected) {
            return bufferedReader.readLine().trim();
        } else {
            throw new Exception("Client is disconnected!");
        }
    }

    public String getUsername() {
        return username;
    }

    public void closeConnection() throws IOException {
        socket.close();
        clientConnected = false;
    }

    public void setSocket(Socket socket) throws Exception {
        try {
            this.socket = socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            clientConnected = true;
        } catch (Exception e) {
            clientConnected = false;
            throw new Exception("Cannot get " + username + " socket!");
        }
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public boolean isClientConnected() {
        return clientConnected;
    }

    @Override
    public void run() {
        while (clientConnected) {
            try {
                String message = getMessage();
                if (message.startsWith("GROUP_CHAT_CREATE")) {
                    // TODO: Create group chat
                } else if (message.startsWith("GROUP_CHAT_ADD")) {
                    // TODO: query database
                } else if (message.startsWith("GROUP_CHAT_KICK")) {
                    // TODO: query database
                } else if (message.startsWith("GROUP_CHAT_CHANGE_HOST")) {
                    // TODO: query database
                } else if (message.startsWith("GROUP_CHAT_DELETE")) {
                    // TODO: query database
                } else if (message.startsWith("SEND_MESSAGE")) {
                    String username = message.split(":")[1].split(" ")[0];
                    String groupChatID = message.split(":")[1].split(" ")[1];
                    String messageJson = message.split(":")[1].split(" ")[2];
                    // TODO: send message to client in ${groupChatID} group
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}