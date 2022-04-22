package database;

import client_connection.Authentication;
import utils.IDGenerator;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DbObject {
    public static Statement statement;

    private DateFormat dateFormat;
    private Calendar calendar;

    /** Constructor. */
    public DbObject() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/messpear";
        String user = "root";
        String password = "";
        Connection connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    }

    ///////////////// FUNCTIONAL METHODS /////////////////

    /** Functional method: Create a Group. */
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

    /** Functional method: Add a user to a group. */
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

    /** Functional method: Remove (kick) user from a group. */
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

    /** Functional method: Remove group. */
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

    /** Functional method: Change group host. */
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

    /** Functional method: Send message to group. */
    public boolean sendMessageToGroup(String group_id, String user_id, String message_content) throws SQLException {
        String currentTime = getCurrentTime();
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

    /** Utility method: Check if a certain user exists in a group. */
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

    /** Utility method: Check if a certain group exists. */
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

    /** Utility method: Get the name of a group host. */
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

    /** Utility method: Get current time. */
    public String getCurrentTime() {
        calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    /** Utility method: Remove X marked users from a group. */
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
    public ArrayList<String> getGroupId(String username){
        // Query to get all messages (order by time)
        String query = "SELECT group_id FROM group_user " +
                "WHERE username = '" + username + "'";
        ArrayList<String> group_id = new ArrayList<>();

        try {
            String resultString;
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                resultString = result.getString("group_id");
                group_id.add(resultString);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        return group_id;
    }

    /** Utility method: Get messages from a group. */
    public ArrayList<String> getMessages(String group_id) {
        String query = "SELECT message_content FROM message " +
                "WHERE group_id = '" + group_id + "'" +
                "ORDER BY created_date DESC LIMIT 20" ;
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

    // Temporary method to get group_id
//    public String getGroupIdx(String group_name) throws SQLException {
//        String query = "SELECT * FROM group_name " +
//                "WHERE group_name = '" + group_name + "'";
//        try {
//            String resultString;
//            ResultSet result = statement.executeQuery(query);
//
//            if (result.next()) {
//                resultString = result.getString("group_id");
//                return resultString;
//            } else {
//                return null;
//            }
//        } catch (SQLException e) {
//            System.out.println("Error: " + e.getMessage());
//            return null;
//        }
//    }

    // Test: querry all
    public void testQueryAll() {
        String query = "SELECT * FROM message ORDER BY created_date DESC LIMIT 20";
        try {
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                System.out.println(result.getString("created_date"));
                System.out.println(result.getString("message_content"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            DbObject dbObject = new DbObject();
             //Create group xxx with host ligma
             dbObject.createGroup("xxx", "ligma");
             //Add to the group: abc, cisco, shitco, sugma
            //String id = dbObject.getGroupId("xxx");
            String id = "2r7GDiZMeaDHOEaNBtVi";
//            dbObject.addUserToGroup(id, "abc", false);
//            dbObject.addUserToGroup(id, "cisco", false);
//            dbObject.addUserToGroup(id, "shitco", false);
//            dbObject.addUserToGroup(id, "sugma", false);

            // Remove from the group: cisco
//            dbObject.removeUserFromGroup(id, "cisco");
//
//            // Change host to sugma
//            dbObject.changeGroupHost(id, "sugma");
//            System.out.println(dbObject.getMessages("sugma"));
//
//            String groupid = "h7a7EgzucVgRDIKBIQYF";
            String userid = "ligma";

            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                dbObject.sendMessageToGroup(id, userid, "Zzzz " + i);

            }
//
//            System.out.println(dbObject.getMessages(groupid));;
//            //System.out.println(dbObject.getCurrentTime());
            dbObject.testQueryAll();
            System.out.println(Authentication.authenticate("cisco : 12345"));
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
