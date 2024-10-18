package Controller;

import Entity.Book;
import Entity.BookDAO;
import Entity.User;
import Entity.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.util.Optional;

public class UserController {

    @FXML
    private TextField userID;

    @FXML
    private TextField userNameField;

    @FXML
    private TextField phoneNumberField;


    public void addUser() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Update Book");
        alert.setHeaderText(null);

        if (userNameField.getText().isEmpty()) {
            alert.setContentText("Please enter User's Name");
            alert.showAndWait();
            return;
        }
        if (phoneNumberField.getText().isEmpty()) {
            alert.setContentText("Please enter phone Number");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Update");
        confirmAlert.setHeaderText("Are you sure you want to add this User?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String userName = userNameField.getText();
            String phoneNumber = phoneNumberField.getText();

            User newUser = new User(userName, phoneNumber);

            UserDAO userDAO = new UserDAO();

            if (userDAO.addUser(newUser)) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Add user successfully!");
                successAlert.showAndWait();
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

            UserDAO userDAO = new UserDAO();

            if (userDAO.deleteUser(userId)) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Delete user successfully!");
                successAlert.showAndWait();
            } else {
                alert.setContentText("Error Deleting User. Please try again later!.");
                alert.showAndWait();
            }
        }
    }
}
