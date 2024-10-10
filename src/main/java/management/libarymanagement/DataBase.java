package management.libarymanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_management";
    private static final String USER = "library_management";
    private static final String PASS = "1111";

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

