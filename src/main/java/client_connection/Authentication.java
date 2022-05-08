package client_connection;

import database.DbObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Authentication {

    public static boolean authenticate(String initMessage) {
        // TODO: authenticate a client
        String username = initMessage.split(":")[0].split(" ")[0];
        String passwordHash = initMessage.split(":")[1].split(" ")[1];
        String checkUsername = "SELECT * FROM `user` WHERE `username` = '" + username + "'";
        //System.out.println("user: " + username);
        try {
            ResultSet resultSet = DbObject.statement.executeQuery(checkUsername);
            String username_result = "";
            String passwordHash_result = "";
            while (resultSet.next()) {
                username_result = resultSet.getString("username");
                System.out.println("user: " + username_result);
                passwordHash_result = resultSet.getString("password_hash");
                System.out.println("pass: " + passwordHash_result);
            }
            if (username_result.equals("")) {
                return false;
            }
            if (!passwordHash_result.equals(passwordHash)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
