package Controller;

import Entity.Book;
import DataAccessObject.BookDAO;
import Entity.User;
import DataAccessObject.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import management.libarymanagement.DataBase;

import java.io.IOException;
import java.sql.*;

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
    private PreparedStatement pst;
    private Statement statement;
    private ResultSet resultSet;

    @FXML
    public void login() {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password= ?";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setString(1, userName.getText());
            pst.setString(2, passWord.getText());
            resultSet = pst.executeQuery();

            Alert alert;

            if (userName.getText().isEmpty() || passWord.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter all the fields");
                alert.showAndWait();
            } else {
                if (resultSet.next()) {
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("admin Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login");
                    alert.showAndWait();

                    login_Btn.getScene().getWindow().hide();

//                  FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/test.fxml"));
//                  FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashbord.fxml"));


//                    Parent root = FXMLLoader.load(getClass().getResource("dashbord.fxml"));

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/test1.fxml"));
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/test2.fxml"));
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

    public void addUserTest() {
        User user = new User();
        user.setUserName("thephap");
        user.setPhoneNumber("09009");

        UserDAO userDAO = new UserDAO();
        userDAO.addUser(user);
        System.out.println("addSuccess");
    }
}