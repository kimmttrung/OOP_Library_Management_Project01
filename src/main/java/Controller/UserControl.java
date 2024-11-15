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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Optional;

public class UserControl {
    @FXML
    private Button arrow_btn;
    @FXML
    private TableColumn<?, ?> authorColumn;
    @FXML
    private Button bars_btn;
    @FXML
    private Button bookAll_btn;
    @FXML
    private TableView<?> bookTable;
    @FXML
    private Button borrowerBook_btn;
    @FXML
    private Button close_btn;
    @FXML
    private Button dashBoard_btn;
    @FXML
    private TableColumn<?, ?> idColumn;
    @FXML
    private Button minus_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private TableColumn<?, ?> publishedDateColumn;

    @FXML
    private TableColumn<?, ?> publisherColumn;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button search_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private Button userAll_btn;

//    @FXML
//    private TextField userID;
//    @FXML
//    private TextField userNameField;
//    @FXML
//    private TextField phoneNumberField;
//    @FXML
//    private TableView<User> userTable;
//    @FXML
//    private TableColumn<User, Integer> idColumn;
//    @FXML
//    private TableColumn<User, String> nameColumn;
//    @FXML
//    private TableColumn<User, String> phoneColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserDAO userDAO = new UserDAO();

    private double x = 0;
    private double y = 0;

    @FXML
    public void DownloadPages(ActionEvent event){
        try{
            if (event.getSource() == signOut_btn){
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
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

//    @FXML
//    public void initialize() {
//        // Set up the table columns
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
//        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
//
//        loadUsers();
//    }

//    public void addUser() {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Add User");
//        alert.setHeaderText(null);
//
//        if (userNameField.getText().isEmpty()) {
//            alert.setContentText("Please enter User's Name");
//            alert.showAndWait();
//            return;
//        }
//        if (phoneNumberField.getText().isEmpty()) {
//            alert.setContentText("Please enter Phone Number");
//            alert.showAndWait();
//            return;
//        }
//
//        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmAlert.setTitle("Confirm Addition");
//        confirmAlert.setHeaderText("Are you sure you want to add this User?");
//
//        Optional<ButtonType> result = confirmAlert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            String userName = userNameField.getText();
//            String phoneNumber = phoneNumberField.getText();
//            String registrationDate = getCurrentDate();
//
//            User newUser = new User(userName, phoneNumber, registrationDate);
//
//            if (userDAO.addUser(newUser)) {
//                loadUsers(); // Reload users to refresh the table
//                showSuccessAlert("Add User", "User added successfully!");
//            } else {
//                alert.setContentText("Error adding user. Please try again.");
//                alert.showAndWait();
//            }
//        }
//    }

//    public void deleteUser() {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Delete User");
//        alert.setHeaderText(null);
//
//        if (userID.getText().isEmpty()) {
//            alert.setContentText("Please enter UserID you want to delete");
//            alert.showAndWait();
//            return;
//        }
//
//        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmAlert.setTitle("Confirm Delete");
//        confirmAlert.setHeaderText("Are you sure you want to delete this User?");
//
//        Optional<ButtonType> result = confirmAlert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            int userId = Integer.parseInt(userID.getText());
//
//            if (userDAO.deleteUser(userId)) {
//                loadUsers();
//                showSuccessAlert("Delete User", "User deleted successfully!");
//            } else {
//                alert.setContentText("Error deleting user. Please try again later!");
//                alert.showAndWait();
//            }
//        }
//    }

//    public void updateUser() {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Update User");
//        alert.setHeaderText(null);
//
//        if (userID.getText().isEmpty()) {
//            alert.setContentText("Please enter UserID of the user to update");
//            alert.showAndWait();
//            return;
//        }
//        if (userNameField.getText().isEmpty()) {
//            alert.setContentText("Please enter User's Name");
//            alert.showAndWait();
//            return;
//        }
//        if (phoneNumberField.getText().isEmpty()) {
//            alert.setContentText("Please enter Phone Number");
//            alert.showAndWait();
//            return;
//        }
//
//        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmAlert.setTitle("Confirm Update");
//        confirmAlert.setHeaderText("Are you sure you want to update this User?");
//
//        Optional<ButtonType> result = confirmAlert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            //int userId = Integer.parseInt(userID.getText());
//            String userName = userNameField.getText();
//            String phoneNumber = phoneNumberField.getText();
//
//            User updatedUser = new User(userName, phoneNumber);
//
//            userDAO.updateUser(updatedUser);
//            loadUsers(); // Reload users to refresh the table
//            showSuccessAlert("Update User", "User updated successfully!");
//        }
//    }
//
//    private void loadUsers() {
//        userList.clear();
//        userList.addAll(userDAO.getAllUsers());
//        userTable.setItems(userList);
//    }

    private void showSuccessAlert(String title, String content) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(title);
        successAlert.setHeaderText(null);
        successAlert.setContentText(content);
        successAlert.showAndWait();
    }

    private String getCurrentDate() {
        // Implement this method to return the current date as a string
        return "";
    }
}
