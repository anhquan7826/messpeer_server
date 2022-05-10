package client_connection;

import database.DbObject;
import utils.MessageJoiner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String username;
    private boolean clientConnected;
    private boolean clientAuthenticated;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        setUsername(socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        clientConnected = true;
        clientAuthenticated = false;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (clientConnected && !clientAuthenticated) {
                    try {
                        String initMessage = getMessage();
                        if (initMessage != null) System.out.println(username + ": " + initMessage);
                        else continue;
                        clientAuthenticated = DbObject.getInstance().authenticate(initMessage);
                        if (clientAuthenticated) {
                            System.out.println(username + ": Authenticate success!");
                            sendMessage("AUTHENTICATE_SUCCESS");
                            setUsername(initMessage.split(":")[1].split(" ")[0]);
                            startCommunicate();
                        } else {
                            System.out.println(username + ": Authenticate failed!");
                            sendMessage("AUTHENTICATE_FAILED");
                        };
                    } catch (Exception e) {
                        clientAuthenticated = false;
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                printWriter.println(message);
            }
        }).start();
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
        System.out.println(username + " disconnected!");
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

    public void startCommunicate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (clientConnected) {
                    if (clientAuthenticated) {
                        try {
                            String message = getMessage();
                            if (message != null) System.out.println(username + ": " + message);
                            else continue;
                            switch (message) {
                                case "SEND_MESSAGE":
                                    String username = message.split(":")[1].split(" ")[0];
                                    String groupChatID = message.split(":")[1].split(" ")[1];
                                    String[] temp = message.split(":")[1].split(" ");
                                    String messageJson = MessageJoiner.join(temp, 2, temp.length);


                                    break;
                                case "GET_GROUPCHAT_LIST":
                                    break;
                                case "GROUP_CHAT_CREATE":
                                    break;
                                case "GROUP_CHAT_ADD":
                                    break;
                                case "GROUP_CHAT_KICK":
                                    break;
                                case "GROUP_CHAT_CHANGE_HOST":
                                    break;
                                case "GROUP_CHAT_DELETE":
                                    break;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }).start();
    }

}