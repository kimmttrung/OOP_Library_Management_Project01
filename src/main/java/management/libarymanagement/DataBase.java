package management.libarymanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/classicmodels";
    private static final String USER = "root";
    private static final String PASS = "1104205tP";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;

        try {

            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            String sql = "SELECT * FROM products ORDER BY quantityInStock DESC LIMIT 10;";

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String customerName = resultSet.getString("quantityInStock");
                String productId = resultSet.getString("productCode");
                System.out.println(productId + " " + customerName);
            }
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Bước 6: Đóng các tài nguyên
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}

