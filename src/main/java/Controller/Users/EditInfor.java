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
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        userIdLabel.setText(String.valueOf(Session.getInstance().getUserID()));
        int userId = Integer.parseInt(userIdLabel.getText());
        User existingUser = userDAO.findUserById(userId);
        RegDateLabel.setText(existingUser.getRegistrationDate().toString());
        userNameField.setText(existingUser.getUserName());
        passField.setText(existingUser.getPassword());
        numberField.setText(existingUser.getPhoneNumber());
    }

    @FXML
    public void updateUser() {
        int userId = Session.getInstance().getUserID();
        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to change infor?");
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        String updatedName = userNameField.getText();
        String updatePassword = passField.getText();
        String updatePhone = numberField.getText();

        User updatedUser = new User(userId, updatedName, updatePassword, updatePhone);
        // Attempt to update the user in the database
        if (userDAO.updateUser(updatedUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Update User", "User updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Update User", "Failed to update user. Please try again.");
        }
    }

    @Override
    protected void applySceneTransition(Button sourceButton, String fxmlPath) {
        Stage currentStage = (Stage) sourceButton.getScene().getWindow();
        currentStage.hide();
    }

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
