package database;

import utils.IDGenerator;

import java.sql.*;

public class DbObject {
    private static Statement statement;
    
    public DbObject() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/messpear";
        String user = "root";
        String password = "";
        Connection connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
    }

    public boolean createGroup(String groupname, String username) throws SQLException {
        String groupID = IDGenerator.generate(20);

        // Write to database
        String createGroupQuery = "INSERT INTO group_name (group_id, group_name) VALUES ('" + groupID + "', '" + groupname + "')";

        try {
            statement.executeUpdate(createGroupQuery);
            if (!addUserToGroup(groupID, username, true)) {
                System.out.println("Error: Failed to add host to group");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true; //true if query success, else false
    }

    public boolean addUserToGroup(String group_id, String added_username, boolean is_hostBool) throws SQLException {
        char is_host = is_hostBool ? 'T' : 'F';

        // Check if user exists
        if (userExistsInGroup(group_id, added_username)) {
            System.out.println("Error: User already exists in the group");
            return false;
        }

        // Write to database
        String query = "INSERT INTO group_user (group_id, username, is_host) VALUES ('" + group_id + "', '" + added_username + "', '" + is_host + "')";

        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true; //true if query success, else false
    }

    // Utility function to check if user exists in group
    public boolean userExistsInGroup(String group_id, String username) throws SQLException {
        String query = "SELECT * FROM group_user WHERE group_id = '"
                + group_id + "' AND username = '" + username + "'";
        try {
            String resultString = "";
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

    public static void main(String[] args) {
        try {
            DbObject dbObject = new DbObject();
            System.out.println(dbObject.userExistsInGroup("1lP2AtiPbyYigH9X2fCH", "ligma"));
            dbObject.addUserToGroup("1lP2AtiPbyYigH9X2fCH", "ligma", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
