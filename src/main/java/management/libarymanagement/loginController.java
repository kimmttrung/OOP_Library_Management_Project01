package management.libarymanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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

    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet resultSet;

    @FXML
    public void login() {
        String sql = "SELECT * FROM account WHERE username = ? AND password= ?";
        connect = DataBase.getConnection();

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, userName.getText());
            prepare.setString(2, passWord.getText());
            resultSet = prepare.executeQuery();

            Alert alert;

            if (userName.getText().isEmpty() || passWord.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter all the fields");
                alert.showAndWait();
            } else {
                if (resultSet.next()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainLibrary.fxml"));
                    Parent root = loader.load();

                    Stage currentStage = (Stage) login_Btn.getScene().getWindow();
                    currentStage.close();

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Username or Password is Incorrect");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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