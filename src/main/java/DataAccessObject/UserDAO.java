package DataAccessObject;

import Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Database.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for performing database operations related to the User entity.
 * Provides methods to fetch, add, update, and delete user records from the database.
 */
public class UserDAO {

    /**
     * Retrieves all users from the database.
     *
     * @return an ObservableList containing all User objects from the database.
     */
    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";

        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("username"));
                user.setId(rs.getInt("id"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setRegistrationDate(rs.getString("registrationDate"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ideally, log the exception
        }
        return users;
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for.
     * @return the User object if found; otherwise, null.
     */
    public User findUser(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";

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

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to search for.
     * @return the User object if found; otherwise, null.
     */
    public User findUserById(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";

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

    /**
     * Adds a new user to the database.
     *
     * @param user the User object to add.
     * @return true if the user was successfully added; false otherwise.
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, phoneNumber, registrationDate) VALUES(?,?,?,?)";
        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getPhoneNumber());
            pst.setString(4, user.getRegistrationDate());

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false;
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id the ID of the user to delete.
     * @return true if the user was successfully deleted; false otherwise.
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
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

    /**
     * Updates an existing user's details in the database.
     *
     * @param user the User object containing updated details.
     * @return true if the user was successfully updated; false otherwise.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, phoneNumber = ? WHERE id = ?";
        try (Connection connect = DataBase.getConnection();
             PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getPhoneNumber());
            pst.setInt(4, user.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false;
    }

    /**
     * Helper method to map a ResultSet to a User object.
     *
     * @param rs the ResultSet containing user data.
     * @return a User object populated with data from the ResultSet.
     * @throws SQLException if an SQL error occurs during mapping.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserName(rs.getString("username"));
        user.setId(rs.getInt("id"));
        user.setPassword(rs.getString("password"));
        user.setPhoneNumber(rs.getString("phoneNumber"));
        user.setRegistrationDate(rs.getString("registrationDate"));
        return user;
    }
}
