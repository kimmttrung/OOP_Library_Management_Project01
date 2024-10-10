package management.libarymanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}

