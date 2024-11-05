package Controller;

import Entity.User;
import DataAccessObject.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UserControl {

    @FXML
    private TextField userID, userNameField, phoneNumberField;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn, phoneColumn, registrationDate;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        registrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        loadUsers();
    }

    public void addUser() {
        if (validateInput()) {
            if (confirmAction("Confirm Addition", "Are you sure you want to add this User?")) {
                User newUser = new User(userNameField.getText(), phoneNumberField.getText(), getCurrentDate());

                if (userDAO.addUser(newUser)) {
                    loadUsers();
                    showAlert(Alert.AlertType.INFORMATION, "Add User", "User added successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Add User", "Error adding user. Please try again.");
                }
            }
        }
    }

    public void deleteUser() {
        if (!userID.getText().isEmpty() && confirmAction("Confirm Delete", "Are you sure you want to delete this User?")) {
            int userId = Integer.parseInt(userID.getText());

            if (userDAO.deleteUser(userId)) {
                loadUsers();
                showAlert(Alert.AlertType.INFORMATION, "Delete User", "User deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete User", "Error deleting user. Please try again later!");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Delete User", "Please enter UserID you want to delete");
        }
    }

    public void updateUser() {
        if (validateUpdateInput()) {
            if (confirmAction("Confirm Update", "Are you sure you want to update this User?")) {
                User updatedUser = new User();
                updatedUser.setId(Integer.parseInt(userID.getText()));
                updatedUser.setPhoneNumber(phoneNumberField.getText());

                if (userDAO.updateUser(updatedUser)) {
                    loadUsers();
                    showAlert(Alert.AlertType.INFORMATION, "Update User", "User updated successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update User", "Error updating user. Please try again.");
                }
            }
        }
    }

    public void findUserByUsername() {
        if (!userNameField.getText().isEmpty()) {
            User user = userDAO.findUser(userNameField.getText());
            if (user != null) {
                showUserInfo(user);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Find User", "User not found");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Find User", "Please enter the username to search");
        }
    }

    public void findUserByID() {
        if (!userID.getText().isEmpty()) {
            User user = userDAO.findUserbyID(Integer.parseInt(userID.getText()));
            if (user != null) {
                showUserInfo(user);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Find User", "User not found");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Find User", "Please enter the UserID to search");
        }
    }

    private void loadUsers() {
        userList.setAll(userDAO.getAllUsers());
        userTable.setItems(userList);
    }

    private boolean validateInput() {
        if (userNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Add User", "Please enter User's Name");
            return false;
        }
        if (phoneNumberField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Add User", "Please enter Phone Number");
            return false;
        }
        return true;
    }

    private boolean validateUpdateInput() {
        if (userID.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update User", "Please enter UserID of the user to update");
            return false;
        }
        if (phoneNumberField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update User", "Please enter Phone Number");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showUserInfo(User user) {
        userID.setText(String.valueOf(user.getId()));
        userNameField.setText(user.getUserName());
        phoneNumberField.setText(user.getPhoneNumber());
        registrationDate.setText(user.getRegistrationDate());
    }

    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
