package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Database.DataBase;
import java.sql.*;
import static Controller.AlertHelper.*;

public class LoginControl {

    @FXML
    private Button login_Btn;

    @FXML
    private TextField login_username;

    @FXML
    private PasswordField login_password;

    @FXML
    private TextField login_showPassword;

    @FXML
    private CheckBox login_selectShowPassword;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Button exitBtn;

    private double x = 0;
    private double y = 0;

    private Connection connect;
    private PreparedStatement pst;
    private Statement statement;
    private ResultSet resultSet;

    @FXML
    public void login() {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password= ?";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setString(1, login_username.getText());
            pst.setString(2, login_password.getText());
            resultSet = pst.executeQuery();

            if (login_username.getText().isEmpty() || login_password.getText().isEmpty()) {

                if(login_selectShowPassword.isSelected()){
                    login_password.setText(login_showPassword.getText());
                }else{
                    login_showPassword.setText(login_password.getText());
                }
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter all the fields");
            } else {
                if (resultSet.next()) {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashBoard.fxml"));
                    Parent root = loader.load();

                    Stage currentStage = (Stage) login_Btn.getScene().getWindow();
                    currentStage.close();

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    root.setOnMousePressed((javafx.scene.input.MouseEvent e) -> {
                        x = e.getSceneX();
                        y = e.getSceneY();
                    });
                    root.setOnMouseDragged((javafx.scene.input.MouseEvent e) -> {
                        stage.setX(e.getScreenX() - x);
                        stage.setY(e.getScreenY() - y);
                    });
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();

                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Username or Password is Incorrect");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                ;
            }
        }
    }

    public void showPassword() {

        if (login_selectShowPassword.isSelected()) {
            login_showPassword.setText(login_password.getText());
            login_showPassword.setVisible(true);
            login_password.setVisible(false);
        } else {
            login_password.setText(login_showPassword.getText());
            login_showPassword.setVisible(false);
            login_password.setVisible(true);
        }

    }

    @FXML
    public void minimize() {
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void exit() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
}