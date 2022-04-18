package database;

import utils.*;

import java.sql.*;

public class DbConnect {
    Statement statement;

    public DbConnect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/messpear";
        String user = "root";
        String password = "";
        Connection connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
    }

    public boolean createGroup(String groupname, String username) throws SQLException {
        String groupID = IDGenerator.generate(20);

        // Write to database
        String query = "INSERT INTO chat_group (group_id, groupname, username, is_host) VALUES ('" + groupID + "', '" + groupname + "', '" + username + "', 'T')";

        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true; //true if query success, else false
    }

    public boolean addUserToGroup(String group_id, String added_username) throws SQLException {
        // Write to database
        String query = "INSERT INTO chat_group (group_id, username, is_host) VALUES ('" + group_id + "', '" + added_username + "', 'F')";

        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true; //true if query success, else false
    }

    // Test
    public static void main(String[] args) {
        try {
            DbConnect db = new DbConnect();
            db.createGroup("test", "abc");
            db.addUserToGroup("test", "cisco");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
