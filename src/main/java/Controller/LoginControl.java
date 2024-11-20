package Controller;

import animatefx.animation.*;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Database.DataBase;
import javafx.util.Duration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static Controller.AlertHelper.*;

public class LoginControl {

    @FXML
    private Button login_Btn;
    @FXML
    private TextField login_username, signup_username;
    @FXML
    private PasswordField login_password, signup_password, signup_cPassword;
    @FXML
    private TextField login_showPassword;
    @FXML
    private CheckBox login_selectShowPassword;
    @FXML
    private ComboBox<?> signup_selectQuestion;
    @FXML
    private Button login_createAccount, signup_loginAccount;
    @FXML
    private AnchorPane login_form, signup_form;

    @FXML
    private Button minimizeBtn;
    @FXML
    private Button exitBtn;

    private double x = 0;
    private double y = 0;

    private String[] questionList = {"Admin", "User"};

    private Connection connect;
    private PreparedStatement pst;
    private Statement statement;
    private ResultSet resultSet;

    public void initialize() {
        questions();  // Gọi phương thức để nạp câu hỏi vào ComboBox
    }

    @FXML
    private void login() {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password= ?";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setString(1, login_username.getText());
            pst.setString(2, login_password.getText());
            resultSet = pst.executeQuery();

            if (login_username.getText().isEmpty() || login_password.getText().isEmpty()) {

                if (login_selectShowPassword.isSelected()) {
                    login_password.setText(login_showPassword.getText());
                } else {
                    login_showPassword.setText(login_password.getText());
                }
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter all the fields");
            } else {
                if (resultSet.next()) {
                    Parent rootNode = (Parent) login_Btn.getScene().getRoot();
                    ZoomOut zoomOut = new ZoomOut (rootNode);
                    zoomOut.setOnFinished(event -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashBoardView.fxml"));
                            Parent root = loader.load();

                            Stage stage = new Stage();
                            Scene scene = new Scene(root);

                            root.setOnMousePressed(e -> {
                                x = e.getSceneX();
                                y = e.getSceneY();
                            });
                            root.setOnMouseDragged(e -> {
                                stage.setX(e.getScreenX() - x);
                                stage.setY(e.getScreenY() - y);
                            });

                            stage.initStyle(StageStyle.TRANSPARENT);
                            stage.setScene(scene);

                            new ZoomIn (root).play();

                            stage.show();
                            ((Stage) login_Btn.getScene().getWindow()).close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    zoomOut.play();

                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Username or Password is Incorrect");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (pst != null) pst.close();
                if (connect != null) connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void register() {

        if (signup_username.getText().isEmpty()
                || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()
                || signup_selectQuestion.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter all the fields");
        } else if (!signup_password.getText().equals(signup_cPassword.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
        } else if (signup_password.getText().length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords must be at least 4 characters");
        } else {
            String checkUsername = "SELECT * FROM accounts WHERE username = '"
                    + signup_username.getText() + "'";
            connect = DataBase.getConnection();
            try {
                statement = connect.createStatement();
                resultSet = statement.executeQuery(checkUsername);

                if (resultSet.next()) {
                    showAlert(Alert.AlertType.ERROR, "Error", signup_username.getText() + " is already taken");
                } else {

                    String insertData = "INSERT INTO accounts "
                            + "(username, password, role) "
                            + "VALUES(?,?,?)";

                    pst = connect.prepareStatement(insertData);
                    pst.setString(1, signup_username.getText());
                    pst.setString(2, signup_password.getText());
                    pst.setString(3, (String) signup_selectQuestion.getSelectionModel().getSelectedItem());

                    int affectedRows = pst.executeUpdate();
                    if (affectedRows > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Info", "Account registered successfully");
                    }

                    registerClearFields();

                    signup_form.setVisible(false);
                    login_form.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void showPassword() {

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

    private void registerClearFields() {
        signup_username.setText("");
        signup_password.setText("");
        signup_cPassword.setText("");
        signup_selectQuestion.getSelectionModel().clearSelection();
    }

    @FXML
    private void switchForm(ActionEvent event) {

        if (event.getSource() == signup_loginAccount) {
            signup_form.setVisible(false);
            login_form.setVisible(true);
            //forgot_form.setVisible(false);
        } else if (event.getSource() == login_createAccount) { // THE LOGIN FORM WILL BE VISIBLE
            signup_form.setVisible(true);
            login_form.setVisible(false);
            //forgot_form.setVisible(false);
        }
    }

    private void questions() {
        List<String> listQ = new ArrayList<>();

        for (String data : questionList) {
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        signup_selectQuestion.setItems(listData);
    }

    @FXML
    private void minimize() {
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

    public void exit() {
        Stage primaryStage = (Stage) exitBtn.getScene().getWindow();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(500), primaryStage.getScene().getRoot());
        scaleDown.setFromX(1.0);
        scaleDown.setToX(0.5);
        scaleDown.setFromY(1.0);
        scaleDown.setToY(0.5);

        fadeOut.setOnFinished(event -> Platform.exit());

        fadeOut.play();
        scaleDown.play();
    }
}