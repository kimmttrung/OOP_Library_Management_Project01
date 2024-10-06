package management.libarymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {
    @FXML
    private Button login_Btn;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField passWord;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Button exitBtn;


    private final String username = "admin";
    private final String password = "123";

    @FXML
    public void login() {
        String enteredUsername = userName.getText();
        String enteredPassword = passWord.getText();

        Alert alert;

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Admin Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your username and password.");
            alert.showAndWait();
        } else if (!enteredUsername.equals(username) || !enteredPassword.equals(password)) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Admin Message");
            alert.setHeaderText(null);
            alert.setContentText("Incorrect username or password.");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainLibrary.fxml"));
                Parent root = loader.load();

                Stage currentStage = (Stage) login_Btn.getScene().getWindow();
                currentStage.close();

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Unable to load the main library interface.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    void minimize() {
        Stage stage = (Stage)minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void exit() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
}