package client_connection;

public class Authentication {
    public static boolean authenticate(String initMessage) {
        // TODO: authenticate a client
        String username = initMessage.split(":")[1].split(" ")[0];
        String passwordHash = initMessage.split(":")[1].split(" ")[1];

        return true;
    }
}
