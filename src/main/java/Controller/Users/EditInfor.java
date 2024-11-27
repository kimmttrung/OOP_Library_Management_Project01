package Controller.Users;

import Controller.BaseDashBoardControl;
import DataAccessObject.UserDAO;
import Entity.User;
import Singleton.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

import static Tools.AlertHelper.showAlert;
import static Tools.AlertHelper.showConfirmationAlert;

/**
 * Controller class responsible for managing the user interface and interactions
 * for editing user information in the application.
 * It allows users to update their username, password, and phone number,
 * and displays their registration date and user ID.
 */
public class EditInfor extends BaseDashBoardControl {

    @FXML
    private Label RegDateLabel;
    @FXML
    private Button cancel_btn;
    @FXML
    private TextField numberField;
    @FXML
    private TextField passField;
    @FXML
    private Label userIdLabel;
    @FXML
    private TextField userNameField;

    private final UserDAO userDAO = new UserDAO(); // Data access object for interacting with user-related data.

    /**
     * Initializes the controller by populating the fields with the existing user data.
     * Retrieves user information from the database using the user's session ID.
     */
    @FXML
    public void initialize() {
        // Set the user ID from the session.
        userIdLabel.setText(String.valueOf(Session.getInstance().getUserID()));

        // Fetch the user's existing information.
        int userId = Integer.parseInt(userIdLabel.getText());
        User existingUser = userDAO.findUserById(userId);

        // Populate the fields with the user's current information.
        RegDateLabel.setText(existingUser.getRegistrationDate().toString());
        userNameField.setText(existingUser.getUserName());
        passField.setText(existingUser.getPassword());
        numberField.setText(existingUser.getPhoneNumber());
    }

    /**
     * Handles updating the user information in the database when the "Update" button is clicked.
     * Displays a confirmation dialog before proceeding with the update.
     */
    @FXML
    public void updateUser() {
        // Get the user ID from the session.
        int userId = Session.getInstance().getUserID();

        // Show a confirmation alert before proceeding with the update.
        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to change infor?");
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // Exit if the user cancels the action.
        }

        // Retrieve the updated values from the input fields.
        String updatedName = userNameField.getText();
        String updatePassword = passField.getText();
        String updatePhone = numberField.getText();

        // Create a new user object with the updated values.
        User updatedUser = new User(userId, updatedName, updatePassword, updatePhone);

        // Attempt to update the user in the database and display the appropriate alert.
        if (userDAO.updateUser(updatedUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Update User", "User updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Update User", "Failed to update user. Please try again.");
        }
    }

    /**
     * Applies scene transition logic when navigating to a new view.
     *
     * @param sourceButton the button that triggered the scene transition.
     * @param fxmlPath the path to the FXML file for the new view.
     */
    @Override
    protected void applySceneTransition(Button sourceButton, String fxmlPath) {
        // Close the current stage.
        Stage currentStage = (Stage) sourceButton.getScene().getWindow();
        currentStage.hide();
    }

    /**
     * Handles navigation actions when the "Cancel" button is clicked.
     * Navigates back to the Member View page.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == cancel_btn) {
                applySceneTransition(cancel_btn, "/fxml/Users/MemberView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
