package DataAccessObject;

import Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Database.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Get all users from the database
    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM user";

        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("username"));
                user.setId(rs.getInt("id"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setRegistrationDate(rs.getString("registrationDate"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ideally, log the exception
        }
        return users;
    }

    // Find a user by their username
    public User findUser(String username) {
        User user = null;
        String sql = "SELECT * FROM user WHERE username = ?";

        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return user;
    }

    // Find a user by their ID
    public User findUserById(int id) {
        User user = null;
        String sql = "SELECT * FROM user WHERE id = ?";

        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return user;
    }

    // Add a new user
    public boolean addUser(User user) {
        String sql = "INSERT INTO user(username, phoneNumber, registrationDate) VALUES(?,?,?)";
        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPhoneNumber());
            pst.setString(3, user.getRegistrationDate());

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false;
    }

    // Delete a user by ID
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setInt(1, id);

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false;
    }

    // Update a user's details
    public boolean updateUser(User user) {
        String sql = "UPDATE user SET username = ?, phoneNumber = ? WHERE id = ?";
        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPhoneNumber());
            pst.setInt(3, user.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false;
    }

    // Helper method to map ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserName(rs.getString("username"));
        user.setId(rs.getInt("id"));
        user.setPhoneNumber(rs.getString("phoneNumber"));
        user.setRegistrationDate(rs.getString("registrationDate"));
        return user;
    }
}
