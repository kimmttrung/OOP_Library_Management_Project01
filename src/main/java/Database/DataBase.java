package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * The {@code DataBase} class provides utility methods for managing database connections
 * and executing common database operations in an SQLite database. It follows the Singleton
 * pattern to ensure only one instance of the database connection is used throughout the application.
 */
public class DataBase {

    /**
     * The JDBC URL for the SQLite database file. The path is dynamically constructed
     * based on the user's working directory and points to the "library.db" file.
     */
    private static final String jdbcURL = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\src\\main\\resources\\sqlite\\library.db";

    /**
     * The single instance of the {@code DataBase} class.
     */
    private static DataBase instance;

    /**
     * The connection object for the SQLite database.
     */
    private Connection connection;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private DataBase() {
        try {
            this.connection = DriverManager.getConnection(jdbcURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of the {@code DataBase} class.
     *
     * @return the singleton instance of the {@code DataBase} class
     */
    public static DataBase getInstance() {
        if (instance == null) {
            synchronized (DataBase.class) {
                if (instance == null) {
                    instance = new DataBase();
                }
            }
        }
        return instance;
    }

    /**
     * Returns the connection object for the SQLite database.
     *
     * @return the connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Retrieves the total number of rows in the specified table.
     *
     * @param tableName the name of the table whose row count is to be retrieved
     * @return the total number of rows in the table, or {@code 0} if an error occurs
     */
    public int getCount(String tableName) {
        int count = 0;
        String query = "SELECT COUNT(*) AS count FROM " + tableName;
        try (Statement stmt = connection.createStatement();
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
