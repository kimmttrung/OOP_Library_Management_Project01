package Controller;

import Entity.User;
import DataAccessObject.UserDAO;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Optional;

import static Controller.AlertHelper.*;

public class UserControl {
    @FXML
    private Button arrow_btn;
    @FXML
    private Button bars_btn;
    @FXML
    private Button bookAll_btn;
    @FXML
    private TableView<User> userTable;
    @FXML
    private Button borrowerBook_btn;
    @FXML
    private Button dashBoard_btn;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> registrationDateColumn;
    @FXML
    private Button minus_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private TextField userID;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Circle circleProfile;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserDAO userDAO = new UserDAO();

    private double x = 0;
    private double y = 0;

    @FXML
    public void initialize() {
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        loadUsers();

        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));
    }

    public void addUser() {

        if (userNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "ADD USER", "Please enter User's Name");
            return;
        }
        if (phoneNumberField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "ADD USER", "Please enter Phone Number");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Addition", "Are you sure you want to add this User?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String userName = userNameField.getText();
            String phoneNumber = phoneNumberField.getText();
            String registrationDate = getCurrentDate();

            User newUser = new User(userName, phoneNumber, registrationDate);

            if (userDAO.addUser(newUser)) {
                loadUsers(); // Reload users to refresh the table
                showSuccessAlert("Add User", "User added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "ERROR", "Something went wrong");
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

            if (userDAO.deleteUser(userId)) {
                loadUsers();
                showSuccessAlert("Delete User", "User deleted successfully!");
            } else {
                alert.setContentText("Error deleting user. Please try again later!");
                alert.showAndWait();
            }
        }
    }

    public void updateUser() {

        if (userID.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update User", "Please enter UserID of the user to update");
            return;
        }
        if (userNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update User", "Please enter User's Name");
            return;
        }
        if (phoneNumberField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update User", "Please enter Phone Number");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Update","Are you sure you want to update this User?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //int userId = Integer.parseInt(userID.getText());
            String userName = userNameField.getText();
            String phoneNumber = phoneNumberField.getText();

            User updatedUser = new User(userName, phoneNumber);

            userDAO.updateUser(updatedUser);
            loadUsers(); // Reload users to refresh the table
            showSuccessAlert("Update User", "User updated successfully!");
        }
    }

    private void loadUsers() {
        userList.clear();
        userList.addAll(userDAO.getAllUsers());
        userTable.setItems(userList);
    }

    private String getCurrentDate() {
        // Implement this method to return the current date as a string
        return "";
    }

    @FXML
    public void DownloadPages(ActionEvent event){
        try{
            if (event.getSource() == signOut_btn){
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/loginForm.fxml"));
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
                signOut_btn.getScene().getWindow().hide();
            } else if (event.getSource() == searchAPI_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/serachAPI.fxml"));
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
                searchAPI_btn.getScene().getWindow().hide();

            } else if (event.getSource() == dashBoard_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashBoard.fxml"));
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
                dashBoard_btn.getScene().getWindow().hide();


            } else if (event.getSource() == borrowerBook_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Borrower.fxml"));
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
                borrowerBook_btn.getScene().getWindow().hide();
            } else if(event.getSource() == bookAll_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/availableBook.fxml"));
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
                bookAll_btn.getScene().getWindow().hide();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void exit(){
        System.exit(0);
    }

    public void minimize(){
        Stage stage = (Stage)minus_btn.getScene().getWindow();
        stage.setIconified(true);
    }

    public void sliderArrow() {

        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(nav_from);
        slide.setToX(-320);

        slide.setOnFinished((ActionEvent event) -> {
            bars_btn.setVisible(true);
            arrow_btn.setVisible(false);
        });

        slide.play();
    }

    public void sliderBars() {

        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(nav_from);
        slide.setToX(0);


        slide.setOnFinished((ActionEvent event) -> {
            arrow_btn.setVisible(true);
            bars_btn.setVisible(false);
        });

        slide.play();
    }
}
