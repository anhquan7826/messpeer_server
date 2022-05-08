package client_connection;

import database.DbObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String username;
    private boolean clientConnected;
    private boolean clientAuthenticated;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        clientConnected = true;
        clientAuthenticated = false;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                if (socket.getInputStream().available() < 0) {
                                    clientConnected = false;
                                    System.out.println(getUsername() + " disconnected!");
                                    break;
                                }
                            } catch (IOException e) {
                                clientConnected = false;
                                System.out.println(getUsername() + " disconnected!");
                                break;
                            }
                        }
                    }
                }).start();*/
                while (clientConnected && !clientAuthenticated) {
                    try {
                        String initMessage = getMessage();
                        System.out.println(initMessage);
                        clientAuthenticated = DbObject.getInstance().authenticate(initMessage);
                        if (clientAuthenticated) {
                            System.out.println("Authenticate success!");
                            sendMessage("AUTHENTICATE_SUCCESS");
                            setUsername(initMessage.split(":")[1].split(" ")[0]);
                        } else {
                            System.out.println("Authenticate failed!");
                            sendMessage("AUTHENTICATE_FAILED");
                            setUsername(null);
                        };
                    } catch (Exception e) {
                        clientAuthenticated = false;
                        setUsername(null);
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    public String getMessage() throws Exception {
        String message = bufferedReader.readLine();
        if (message == null) {
            closeConnection();
        }
        return message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void closeConnection() {
        System.out.println("Client disconnected!");
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
            if (clientAuthenticated) {
                try {
                    String message = getMessage();
                    if (message.startsWith("GROUP_CHAT_CREATE")) {
                        // TODO: Create group chat
                    } else if (message.startsWith("GROUP_CHAT_ADD")) {
                        // TODO: query database
                    } else if (message.startsWith("GROUP_CHAT_KICK")) {
                        // TODO: query database
                    } else if (message.startsWith("GROUP_CHAT_CHANGE_HOST")) {
                        // TODO: Change group chat host (done)
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
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}