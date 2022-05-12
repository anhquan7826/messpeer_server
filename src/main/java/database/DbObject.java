package database;

import utils.IDGenerator;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DbObject {
    private static DbObject instance;

    public static DbObject getInstance() {
        if (instance == null) {
            try {
                instance = new DbObject();
            } catch (SQLException e) {
                System.out.println("Cannot instantiate DbObject!");
            }
        }
        return instance;
    }

    public Statement statement;

    /**
     * Constructor.
     */
    private DbObject() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/messpear";
        String user = "root";
        String password = "";
        Connection connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
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
            statement.executeUpdate(createGroupQuery);
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
            statement.executeUpdate(query);
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
                statement.executeUpdate(query);
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
            statement.executeUpdate(query);
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
            statement.executeUpdate(oldHostQuery);
            statement.executeUpdate(newHostQuery);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Functional method: Send message to group.
     */
    public boolean sendMessageToGroup(String group_id, String user_id, String message_content) throws SQLException {
        String currentTime = TimeObject.getTime();
        String query = "INSERT INTO message (group_id, user_id, message_content, created_date) "
                + "VALUES ('" + group_id + "', '" + user_id + "', '" + message_content + "', '" + currentTime + "')";
        try {
            statement.executeUpdate(query);
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
            ResultSet result = statement.executeQuery(query);

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
            ResultSet result = statement.executeQuery(query);

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
            ResultSet result = statement.executeQuery(query);

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

    /**
     * Utility method: Remove X marked users from a group.
     */
    public void removeXMarkedUsersFromGroup(String group_id) {
        String query = "DELETE FROM group_user "
                + "WHERE group_id = '" + group_id + "'"
                + "AND is_host = 'X'";

        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // TODO: Utility method to remove userless groups
    // TODO: Utility method to get group_id
    public HashMap<String, String> getGroupList(String username) {
        if (username == null) {
            return null;
        }
        ArrayList<String> temp = new ArrayList<>();
        HashMap<String, String> groupList = new HashMap<>();
        try {
            ResultSet query1 = statement.executeQuery("SELECT group_id FROM group_user " + "WHERE username = '" + username + "'");
            while (query1.next()) {
                temp.add(query1.getString("group_id"));
            }
            query1.close();

            for (String id : temp) {
                ResultSet query2 = statement.executeQuery("SELECT * FROM group_name WHERE group_id = '" + id + "'");
                query2.next();
                groupList.put(query2.getString("group_id"), query2.getString("group_name"));
                query2.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupList;
    }

    /**
     * Utility method: Get messages from a group.
     */
    public ArrayList<String> getMessages(String group_id) {
        String query = "SELECT message_content FROM message " +
                "WHERE group_id = " + group_id +
                " ORDER BY created_date DESC LIMIT 10";
        ArrayList<String> messages = new ArrayList<>();

        try {
            String resultString;
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                resultString = result.getString("message_content");
                messages.add(resultString);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        return messages;
    }

    public boolean authenticate(HashMap<String, String> credential) {
        // TODO: authenticate a client
        String checkUsername = "SELECT * FROM `user` WHERE `username` = '" + credential.get("username") + "'";
        //System.out.println("user: " + username);
        try {
            ResultSet resultSet = statement.executeQuery(checkUsername);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(getInstance().getGroupList("anhquan7826"));
    }
}