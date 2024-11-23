package Controller;

import DataAccessObject.BorrowerDAO;
import Entity.User;
import DataAccessObject.UserDAO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.Optional;

import static Controller.AlertHelper.*;
import static Animation.ColorTransitionExample.addColorTransition;

public class UserControl extends BaseDashBoardControl {
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
    private TableColumn<User, String> passwordColumn;
    @FXML
    private Button minus_btn;
    @FXML
    private Button close_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private TextField userIdField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Circle circleProfile;
    @FXML
    private TextField usernameSearchField;
    @FXML
    private TextField passWordField;
    @FXML
    private AnchorPane userBook_from;

    private double x = 0;
    private double y = 0;

    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final UserDAO userDAO = new UserDAO();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();

    @FXML
    public void initialize() {
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        setUpTableColumn();
        setUpSelectionUser();
        loadUsers();
        addColorTransition(userBook_from);
    }

    private void loadUsers() {
        // Clear the current list and load all users from the database into the table
        userList.clear();
        userList.addAll(userDAO.getAllUsers());
        userTable.setItems(userList);
    }

    private void setUpTableColumn() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        // Set the default profile image for users
        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));
    }

    private void setUpSelectionUser() {
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // If a book is selected, update the book details in the UI
            if (newSelection != null) {
                userIdField.setText(String.valueOf(newSelection.getId()));
                userNameField.setText(newSelection.getUserName());
                phoneNumberField.setText(newSelection.getPhoneNumber());
                passWordField.setText(newSelection.getPassword());
            } else {
                userIdField.setText("");
                userNameField.setText("");
                phoneNumberField.setText("");
                passWordField.setText("");
            }
        } );
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
        if (passWordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "ADD USER", "Please enter Password");
            return;
        }
        Optional<ButtonType> result = showConfirmationAlert("Confirm Addition", "Are you sure you want to add this User?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String userName = userNameField.getText();
            String passWord = passWordField.getText();
            String phoneNumber = phoneNumberField.getText();
            String registrationDate = LocalDate.now().toString(); // Set registration date to current date

            User newUser = new User(userName, passWord, phoneNumber, registrationDate);
            if (userDAO.addUser(newUser)) {
                loadUsers();
                showSuccessAlert("Add User", "User added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "ERROR", "Something went wrong");
            }
        }
    }

    public void deleteUser() {
        if (userIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "DELETE USER", "Please enter User ID");
            return;
        }
        int userId = Integer.parseInt(userIdField.getText());
        //Check if user are borrowing book
        if (borrowerDAO.hasBorrowerByStatusAndUserId("processing", userId)) {
            showAlert(Alert.AlertType.ERROR, "DELETE USER", "User is borrowing. Please return book first!");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Deletion", "Are you sure want to delete this User?");
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (userDAO.deleteUser(userId)) {
                loadUsers();
                showSuccessAlert("Delete User", "User deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "ERROR", "Something went wrong");
            }
        }
    }

    @FXML
    public void updateUser() {
        if (userIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update User", "Please enter User ID to update.");
            return;
        }
        int userId;
        try {
            // Try to parse the user ID
            userId = Integer.parseInt(userIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Update User", "User ID must be a valid number.");
            return;
        }
        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to update this user?");
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        // Find the existing user with the given ID
        User existingUser = userDAO.findUserById(userId);
        if (existingUser == null) {
            showAlert(Alert.AlertType.ERROR, "Update User", "User not found with ID: " + userId);
            return;
        }
        String updatedName = userNameField.getText().isEmpty() ? existingUser.getUserName() : userNameField.getText();
        String updatedPhone = phoneNumberField.getText().isEmpty() ? existingUser.getPhoneNumber() : phoneNumberField.getText();
        String updatePassword = passWordField.getText().isEmpty() ? existingUser.getPassword() : passWordField.getText();

        User updatedUser = new User(userId, updatedName, updatePassword, updatedPhone);
        // Attempt to update the user in the database
        if (userDAO.updateUser(updatedUser)) {
            loadUsers();
            showAlert(Alert.AlertType.INFORMATION, "Update User", "User updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Update User", "Failed to update user. Please try again.");
        }
    }

    @FXML
    private void searchUserByUsername() {
        String username = usernameSearchField.getText().trim();

        if (username.isEmpty()) {
            loadUsers();
            return;
        }

        User user = userDAO.findUser(username);

        if (user != null) {
            ObservableList<User> userList = FXCollections.observableArrayList();
            userList.add(user);
            userTable.setItems(userList);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Search User", "No user found with username: " + username);
        }
    }

    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == searchAPI_btn) {
                applySceneTransition(searchAPI_btn, "/fxml/SearchView.fxml");
            } else if (event.getSource() == dashBoard_btn) {
                applySceneTransition(dashBoard_btn, "/fxml/DashBoardView.fxml");
            } else if (event.getSource() == borrowerBook_btn) {
                applySceneTransition(borrowerBook_btn, "/fxml/BorrowerView.fxml");
            } else if (event.getSource() == bookAll_btn) {
                applySceneTransition(bookAll_btn, "/fxml/BookView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        Stage primaryStage = (Stage) close_btn.getScene().getWindow();

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
