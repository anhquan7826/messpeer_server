package database;

import utils.*;

import java.sql.*;

public class DbObject {
    Statement statement;

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
            if (!addUserToGroup(groupID, username, 'T')) {
                System.out.println("Error: Failed to add host to group");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true; //true if query success, else false
    }

    public boolean addUserToGroup(String group_id, String added_username, char is_host) throws SQLException {
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

    // Test
    public static void main(String[] args) throws SQLException {
        DbObject db = new DbObject();
        db.createGroup("Nhom cua tao la nhat", "ligma");
        String members[] = {"cisco", "shitco", "nu", "sugma"};
        db.addMembersToGroup("Nhom cua tao la nhat", members);
    }

    // Add members to group
    void addMembersToGroup(String groupname, String[] members) throws SQLException {
        String groupID = getGroupID(groupname);
        for (String member : members) {
            addUserToGroup(groupID, member, 'F');
        }
    }

    private String getGroupID(String groupname) {
        String query = "SELECT group_id FROM group_name WHERE group_name = '" + groupname + "'";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getString("group_id");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
