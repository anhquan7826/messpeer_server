package database;

import utils.IDGenerator;
import utils.Json;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    private static Database instance;

    public static Database getInstance() {
        if (instance == null) {
            try {
                instance = new Database();
            } catch (SQLException e) {
                System.out.println("Cannot instantiate DbObject!");
            }
        }
        return instance;
    }

    private Connection connection;

    private Database() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/messpear";
        String user = "root";
        String password = "";
        connection = DriverManager.getConnection(url, user, password);
        // TODO: statement should be created every time a method is called.
        //dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSS");
    }

    ///////////////// FUNCTIONAL METHODS /////////////////

    /**
     * Functional method: Create a Group.
     */
    public boolean createGroup(String groupname, String username) throws SQLException {
        String groupID = IDGenerator.generate(20);
        String createGroupQuery = "INSERT INTO group_name (group_id, group_name) " +
                "VALUES ('" + groupID + "', '" + groupname + "')";

        try {
            connection.createStatement().executeUpdate(createGroupQuery);
            if (!addUserToGroup(groupID, username, true)) {
                System.out.println("Error: Failed to add host to group");
                System.out.println("Deleting failed-to-create group...");
                removeGroup(groupID);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Functional method: Add a user to a group.
     */
    public boolean addUserToGroup(String group_id, String added_username, boolean is_hostBool) throws SQLException {
        char is_host = is_hostBool ? 'T' : 'F';

        // Check if user exists
        if (userExistsInGroup(group_id, added_username)) {
            System.out.println("Error: User already exists in the group");
            return false;
        }

        String query = "INSERT INTO group_user (group_id, username, is_host) " +
                "VALUES ('" + group_id + "', '" + added_username + "', '" + is_host + "')";

        try {
            connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Functional method: Remove (kick) user from a group.
     */
    public boolean removeUserFromGroup(String group_id, String removed_username) throws SQLException {
        String query = "DELETE FROM group_user " +
                "WHERE group_id = '" + group_id + "' AND username = '" + removed_username + "'";
        try {
            // Can't delete if user is the host
            if (!removed_username.equals(getGroupHost(group_id))) {
                connection.createStatement().executeUpdate(query);
            } else {
                System.out.println("Error: Can't remove host from group");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Functional method: Remove group.
     */
    public boolean removeGroup(String group_id) throws SQLException {
        String query = "DELETE FROM group_name " +
                "WHERE group_id = '" + group_id + "'";
        try {
            connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Functional method: Change group host.
     */
    public boolean changeGroupHost(String group_id, String new_host) throws SQLException {
        String oldHostQuery = "UPDATE group_user " +
                "SET is_host = 'F' " +
                "WHERE group_id = '" + group_id + "' AND is_host = 'T'";
        String newHostQuery = "UPDATE group_user " +
                "SET is_host = 'T' " +
                "WHERE group_id = '" + group_id + "' AND username = '" + new_host + "'";

        try {
            connection.createStatement().executeUpdate(oldHostQuery);
            connection.createStatement().executeUpdate(newHostQuery);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Functional method: Send message to group.
     */
    public boolean sendMessageToGroup(HashMap<String, String> message) throws SQLException {
        String query = "INSERT INTO message (message_id, group_id, username, timestamp, content) " + "VALUES ('" +
                    message.get("message_id") + "', '" +
                    message.get("group_id") + "', '" +
                    message.get("username") + "', '" +
                    message.get("timestamp") + "', '" +
                    message.get("content") +
                "')";
        try {
            connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    ///////////////// UTILITY METHODS /////////////////

    /**
     * Utility method: Check if a certain user exists in a group.
     */
    public boolean userExistsInGroup(String group_id, String username) throws SQLException {
        String query = "SELECT * FROM group_user " +
                "WHERE group_id = '" + group_id + "' AND username = '" + username + "'";
        try {
            String resultString;
            ResultSet result = connection.createStatement().executeQuery(query);

            if (result.next()) {
                resultString = result.getString("username");
                if (resultString.equals(username)) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }

        return false;
    }

    /**
     * Utility method: Check if a certain group exists.
     */
    public boolean groupExists(String group_id) throws SQLException {
        String query = "SELECT * FROM group_name " +
                "WHERE group_id = '" + group_id + "'";
        try {
            String resultString;
            ResultSet result = connection.createStatement().executeQuery(query);

            if (result.next()) {
                resultString = result.getString("group_id");
                if (resultString.equals(group_id)) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }

        return false;
    }

    /**
     * Utility method: Get the name of a group host.
     */
    public String getGroupHost(String group_id) throws SQLException {
        String query = "SELECT * FROM group_user " +
                "WHERE group_id = '" + group_id + "' AND is_host = 'T'";
        try {
            String resultString;
            ResultSet result = connection.createStatement().executeQuery(query);

            if (result.next()) {
                resultString = result.getString("username");
                return resultString;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public HashMap<String, String> getGroupList(String username) {
        if (username == null) {
            return null;
        }
        HashMap<String, String> groupList = new HashMap<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM group_name WHERE group_id IN (SELECT group_id FROM group_user WHERE group_user.username = '" + username +"')");
            while (resultSet.next()) {
                groupList.put(resultSet.getString(1), resultSet.getString(2));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupList;
    }

    /**
     * Utility method: Get messages from a group.
     */
    public ArrayList<String> getMessages(String group_id, int limit) {
        String query = "SELECT * FROM message " +
                "WHERE group_id = '" + group_id +
                "' ORDER BY timestamp DESC" + (limit == -1 ? "" : " LIMIT " + limit);
        ArrayList<String> messages = new ArrayList<>();

        try {
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                HashMap<String, String> data = new HashMap<>();
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    data.put(result.getMetaData().getColumnName(i), result.getString(i));
                }
                messages.add(Json.toJson(data));
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return messages;
    }

    public boolean authenticate(HashMap<String, String> credential) {
        // TODO: authenticate a client
        String checkUsername = "SELECT * FROM `user` WHERE `username` = '" + credential.get("username") + "'";
        //System.out.println("user: " + username);
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(checkUsername);
            String username_result = "";
            String passwordHash_result = "";
            while (resultSet.next()) {
                username_result = resultSet.getString("username");
                passwordHash_result = resultSet.getString("password_hash");
            }
            if (username_result.equals("")) {
                return false;
            }
            if (!passwordHash_result.equals(credential.get("password"))) {
                return false;
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public ArrayList<String> getUsers(String groupID) {
        ArrayList<String> usernames = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT username from group_user WHERE group_id = '" + groupID + "'");
            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernames;
    }

    public static void main(String[] args) {
        System.out.println(Database.getInstance().getMessages("765GYVyvjjh78Hgjhgu", -1));
    }
}