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
        String username = login_username.getText();
        String password = login_password.getText();

        // Kiểm tra username và password trước khi truy vấn CSDL
        if (!isValidUsername(username)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username cannot be empty and must be valid.");
            return;
        }
        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 8 characters long, contain 1 uppercase letter, 1 special character, and 1 digit.");
            return;
        }

        String sqlAccounts = "SELECT role FROM accounts WHERE username = ? AND password = ?";
        String sqlUsers = "SELECT id, phoneNumber, registrationDate FROM user WHERE username = ? AND password = ?";

        try {
            connect = DataBase.getConnection();

            // Kiểm tra bảng `accounts`
            pst = connect.prepareStatement(sqlAccounts);
            pst.setString(1, login_username.getText());
            pst.setString(2, login_password.getText());
            resultSet = pst.executeQuery();

            if (resultSet.next()) {
                // Nếu tìm thấy trong `accounts`
                String role = resultSet.getString("role");
                if ("Admin".equalsIgnoreCase(role)) {
                    openDashboard("/fxml/DashBoardView.fxml");
                } else if ("User".equalsIgnoreCase(role)) {
                    openDashboard("/fxml/DashBoardUser.fxml");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unknown role");
                }
            } else {
                // Không tìm thấy trong `accounts`, kiểm tra bảng `user`
                pst = connect.prepareStatement(sqlUsers);
                pst.setString(1, login_username.getText());
                pst.setString(2, login_password.getText());
                resultSet = pst.executeQuery();

                if (resultSet.next()) {
                    // Nếu tìm thấy trong `user`
                    String id = resultSet.getString("id");
                    String phoneNumber = resultSet.getString("phoneNumber");

                    // Chuyển đến giao diện User
                    showAlert(Alert.AlertType.INFORMATION, "Welcome", "Login successful!\nID: " + id + "\nPhone: " + phoneNumber);
                    openDashboard("/fxml/DashBoardUser.fxml");
                } else {
                    // Không tìm thấy trong cả hai bảng
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

    // Hàm mở giao diện
    private void openDashboard(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            // Đóng cửa sổ hiện tại
            ((Stage) login_Btn.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm hiển thị thông báo
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Kiểm tra username hợp lệ
    private boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    // Kiểm tra password hợp lệ
    private boolean isValidPassword(String password) {
        // Regex kiểm tra điều kiện
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    @FXML
    private void register() {

        if (signup_username.getText().isEmpty()
                || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()
                || signup_selectQuestion.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter all the fields");
        } else if (!signup_password.getText().equals(signup_cPassword.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
        } else if (!isValidPassword(signup_password.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 8 characters long, contain 1 uppercase letter, 1 special character, and 1 digit.");
        } else if (signup_password.getText().length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords must be at least 4 characters");
        } else {
            String checkUsername = "SELECT * FROM accounts WHERE username = ?";
            connect = DataBase.getConnection();
            try {
                pst = connect.prepareStatement(checkUsername);
                pst.setString(1, signup_username.getText());
                resultSet = pst.executeQuery();

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
            login_showPassword.setVisible(true);
            login_password.setVisible(false);
        } else {
            login_showPassword.setVisible(false);
            login_password.setVisible(true);
        }
        login_showPassword.textProperty().bindBidirectional(login_password.textProperty());
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