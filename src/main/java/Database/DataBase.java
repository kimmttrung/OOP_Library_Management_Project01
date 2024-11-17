package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBase {

    private static final String jdbcURL = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\src\\main\\resources\\sqlite\\library.db";

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(jdbcURL);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getCount(String tableName) {
        int count = 0;
        String query = "SELECT COUNT(*) AS count FROM " + tableName;
        try (Connection conn = DriverManager.getConnection(jdbcURL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
