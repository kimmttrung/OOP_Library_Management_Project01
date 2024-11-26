package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * The {@code DataBase} class provides utility methods for managing database connections
 * and executing common database operations in an SQLite database. It handles the connection
 * setup and simplifies tasks such as retrieving row counts from tables.
 */
public class DataBase {

    /**
     * The JDBC URL for the SQLite database file. The path is dynamically constructed
     * based on the user's working directory and points to the "library.db" file.
     */
    private static final String jdbcURL = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\src\\main\\resources\\sqlite\\library.db";

    /**
     * Establishes and returns a connection to the SQLite database.
     *
     * @return a {@link Connection} object to interact with the SQLite database,
     *         or {@code null} if the connection fails
     */
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(jdbcURL);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the total number of rows in the specified table.
     *
     * @param tableName the name of the table whose row count is to be retrieved
     * @return the total number of rows in the table, or {@code 0} if an error occurs
     */
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
