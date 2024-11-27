package Controller;

import DataAccessObject.UserDAO;
import Entity.User;
import Singleton.Session;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static Tools.AlertHelper.showAlert;

/**
 * Controller for handling the login and registration functionality.
 * Provides methods to authenticate users, switch between login and registration forms,
 * and manage user roles (Admin/User).
 */
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

    private final String[] questionList = {"Admin", "User"};

    private Connection connect;
    private PreparedStatement pst;
    private ResultSet resultSet;

    /**
     * Initializes the controller.
     * Populates the security questions in the registration form.
     */
    public void initialize() {
        questions();
    }

    /**
     * Handles login functionality.
     * Verifies the username and password, and redirects users to the appropriate dashboard based on their role.
     */
    @FXML
    private void login() {
        String username = login_username.getText();
        String password = login_password.getText();

        if (!isValidUsername(username)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username cannot be empty and must be valid.");
            return;
        }

        String sqlAccounts = "SELECT role FROM accounts WHERE username = ? AND password = ?";
        String sqlUsers = "SELECT id, phoneNumber, registrationDate FROM users WHERE username = ? AND password = ?";

        try {
            connect = DataBase.getConnection();

            // Check accounts table
            pst = connect.prepareStatement(sqlAccounts);
            pst.setString(1, username);
            pst.setString(2, password);
            resultSet = pst.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                if ("Admin".equalsIgnoreCase(role)) {
                    openDashboard("/fxml/Admin/DashBoardView.fxml");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unknown role");
                }
            } else {
                // Check users table
                pst = connect.prepareStatement(sqlUsers);
                pst.setString(1, username);
                pst.setString(2, password);
                resultSet = pst.executeQuery();

                if (resultSet.next()) {
                    String id = resultSet.getString("id");
                    Session.getInstance().setUserID(Integer.parseInt(id));
                    showAlert(Alert.AlertType.INFORMATION, "Welcome", "Login successful!\nID: " + id);
                    openDashboard("/fxml/Users/DashBoardUser.fxml");
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

    /**
     * Handles user registration.
     * Validates inputs and inserts user data into the appropriate table based on their role.
     */
    @FXML
    private void register() {
        if (signup_username.getText().isEmpty()
                || signup_password.getText().isEmpty() || signup_cPassword.getText().isEmpty()
                || signup_selectQuestion.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter all the fields");
        } else if (!signup_password.getText().equals(signup_cPassword.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
        } else if (!isValidPassword(signup_password.getText())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password must meet the required complexity.");
        } else {
            String role = String.valueOf(signup_selectQuestion.getSelectionModel().getSelectedItem());
            if (role.equals("Admin")) {
                registerAdmin();
            } else if (role.equals("User")) {
                registerUser();
            }
        }
    }

    private void registerAdmin() {
        String checkUsername = "SELECT * FROM accounts WHERE username = ?";
        connect = DataBase.getConnection();
        try {
            pst = connect.prepareStatement(checkUsername);
            pst.setString(1, signup_username.getText());
            resultSet = pst.executeQuery();

            if (resultSet.next()) {
                showAlert(Alert.AlertType.ERROR, "Error", signup_username.getText() + " is already taken");
            } else {
                String insertData = "INSERT INTO accounts (username, password, role) VALUES(?,?,?)";
                pst = connect.prepareStatement(insertData);
                pst.setString(1, signup_username.getText());
                pst.setString(2, signup_password.getText());
                pst.setString(3, "Admin");

                if (pst.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Info", "Account registered successfully");
                }

                registerClearFields();
                switchToLoginForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerUser() {
        User user = new User(signup_username.getText(), signup_password.getText(), "Updating!!!", LocalDate.now().toString());
        UserDAO userDAO = new UserDAO();
        userDAO.addUser(user);
        showAlert(Alert.AlertType.INFORMATION, "Info", "Account registered successfully");
        registerClearFields();
        switchToLoginForm();
    }

    private void switchToLoginForm() {
        signup_form.setVisible(false);
        login_form.setVisible(true);
    }

    /**
     * Opens a new dashboard based on the specified FXML path.
     *
     * @param fxmlPath the FXML file path of the dashboard to load
     */
    private void openDashboard(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            ((Stage) login_Btn.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the username.
     *
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    /**
     * Validates the password.
     *
     * @param password the password to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password != null && password.matches(passwordRegex);
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

    /**
     * Clears the fields in the registration form.
     */
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
        } else if (event.getSource() == login_createAccount) {
            signup_form.setVisible(true);
            login_form.setVisible(false);
        }
    }

    /**
     * Populates the ComboBox with predefined questions/roles.
     */
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

    /**
     * Exits the application with a fade and scale transition effect.
     */
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
