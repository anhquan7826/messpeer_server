package client_connection;

import database.Database;
import utils.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Client {
    private HashSet<Client> clientList;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String username;
    private boolean clientConnected;
    private boolean clientAuthenticated;

    public Client(Socket socket, HashSet<Client> clientList) throws IOException {
        this.socket = socket;
        this.clientList = clientList;
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
                        initMessage = initMessage.replaceFirst("INITIAL_MESSAGE:", "");
                        HashMap<String, String> credential = Json.toHashMap(initMessage);
                        clientAuthenticated = Database.getInstance().authenticate(credential);
                        if (clientAuthenticated) {
                            System.out.println(username + ": Authenticate success!");
                            sendMessage("AUTHENTICATE_SUCCESS");
                            setUsername(credential.get("username"));
                            getAllMessages();
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

                            String messageHeader = message.split(":")[0];
                            message = message.replaceFirst(messageHeader + ":", "");
                            if ("SEND_MESSAGE".equals(messageHeader)) {
                                HashMap<String, String> msgJson = Json.toHashMap(message);
                                for (String user : Database.getInstance().getUsers(msgJson.get("group_id"))) {
                                    for (Client client : clientList) {
                                        if (!user.equals(username) && client.getUsername().equals(user)) {
                                            client.sendMessage("RECEIVE_MESSAGE:" + message);
                                        }
                                    }
                                }
                                Database.getInstance().sendMessageToGroup(msgJson);

                            } else if ("GET_GROUPCHAT_LIST".equals(messageHeader)) {
                                HashMap<String, String> groupList = Database.getInstance().getGroupList(username);
                                if (groupList == null) {
                                    sendMessage("GET_GROUPCHAT_LIST_ERROR");
                                } else {
                                    sendMessage("GET_GROUPCHAT_LIST_OK:" + Json.toJson(groupList));
                                }

                            } else if ("GROUP_CHAT_CREATE".equals(messageHeader)) {

                            } else if ("GROUP_CHAT_ADD".equals(messageHeader)) {
                            } else if ("GROUP_CHAT_KICK".equals(messageHeader)) {
                            } else if ("GROUP_CHAT_CHANGE_HOST".equals(messageHeader)) {
                            } else if ("GROUP_CHAT_DELETE".equals(messageHeader)) {
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else break;
                }
            }
        }).start();
    }

    private void getAllMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> groupList = Database.getInstance().getGroupList("anhquan7826");
                for (String group_id : groupList.keySet()) {
                    ArrayList<String> messages = Database.getInstance().getMessages(group_id, -1);
                    messages.forEach((msg) -> {
                        sendMessage("RECEIVE_MESSAGE:" + msg);
                    });
                }
            }
        }).start();
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}