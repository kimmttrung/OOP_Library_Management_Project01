package Controller;

import Entity.User;
import DataAccessObject.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class UserControl {

    @FXML
    private TextField userID;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        loadUsers();
    }

    public void addUser() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Add User");
        alert.setHeaderText(null);

        if (userNameField.getText().isEmpty()) {
            alert.setContentText("Please enter User's Name");
            alert.showAndWait();
            return;
        }
        if (phoneNumberField.getText().isEmpty()) {
            alert.setContentText("Please enter Phone Number");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Addition");
        confirmAlert.setHeaderText("Are you sure you want to add this User?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String userName = userNameField.getText();
            String phoneNumber = phoneNumberField.getText();
            String registrationDate = getCurrentDate();

            User newUser = new User(userName, phoneNumber, registrationDate);

            if (userDAO.addUser(newUser)) {
                loadUsers(); // Reload users to refresh the table
                showSuccessAlert("Add User", "User added successfully!");
            } else {
                alert.setContentText("Error adding user. Please try again.");
                alert.showAndWait();
            }
        }
    }

    public void deleteUser() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Delete User");
        alert.setHeaderText(null);

        if (userID.getText().isEmpty()) {
            alert.setContentText("Please enter UserID you want to delete");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this User?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int userId = Integer.parseInt(userID.getText());

            if (userDAO.deleteUser(userId)) {
                loadUsers();
                showSuccessAlert("Delete User", "User deleted successfully!");
            } else {
                alert.setContentText("Error deleting user. Please try again later!");
                alert.showAndWait();
            }
        }
    }

    public void updateUser() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Update User");
        alert.setHeaderText(null);

        if (userID.getText().isEmpty()) {
            alert.setContentText("Please enter UserID of the user to update");
            alert.showAndWait();
            return;
        }
        if (userNameField.getText().isEmpty()) {
            alert.setContentText("Please enter User's Name");
            alert.showAndWait();
            return;
        }
        if (phoneNumberField.getText().isEmpty()) {
            alert.setContentText("Please enter Phone Number");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Update");
        confirmAlert.setHeaderText("Are you sure you want to update this User?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //int userId = Integer.parseInt(userID.getText());
            String userName = userNameField.getText();
            String phoneNumber = phoneNumberField.getText();

            User updatedUser = new User(userName, phoneNumber);

            userDAO.updateUser(updatedUser);
            loadUsers(); // Reload users to refresh the table
            showSuccessAlert("Update User", "User updated successfully!");
        }
    }

    private void loadUsers() {
        userList.clear();
        userList.addAll(userDAO.getAllUsers());
        userTable.setItems(userList);
    }

    private void showSuccessAlert(String title, String content) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(title);
        successAlert.setHeaderText(null);
        successAlert.setContentText(content);
        successAlert.showAndWait();
    }

    private String getCurrentDate() {
        // Implement this method to return the current date as a string
        return "";
    }
}
