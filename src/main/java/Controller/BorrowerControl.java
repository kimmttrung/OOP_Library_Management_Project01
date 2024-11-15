package Controller;

import DataAccessObject.BorrowerDAO;
import Entity.Borrower;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class BorrowerControl {

    @FXML
    private TextField borrowerIDField, borrowerUsernameField, bookIDField, statusField, fromDateField, toDateField;
    @FXML
    private TableView<Borrower> borrowerTable;
    @FXML
    private TableColumn<Borrower, Integer> idColumn;
    @FXML
    private TableColumn<Borrower, String> usernameColumn, statusColumn, fromColumn, toColumn;
    @FXML
    private TableColumn<Borrower, Integer> bookIdColumn;
    @FXML
    private Button arrow_btn;
    @FXML
    private Button bars_btn;
    @FXML
    private Button bookAll_btn;
    @FXML
    private Button dashBoard_btn;
    @FXML
    private Button minus_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button userAll_btn;



    private ObservableList<Borrower> borrowerList = FXCollections.observableArrayList();
    private BorrowerDAO borrowerDAO = new BorrowerDAO();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_to"));
        loadBorrowers();
    }

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


            } else if (event.getSource() == bookAll_btn) {
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
            } else if (event.getSource() == userAll_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/userBook.fxml"));
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
                userAll_btn.getScene().getWindow().hide();
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
        slide.setToX(-340);

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

    private void loadBorrowers() {
        borrowerList = FXCollections.observableArrayList(borrowerDAO.getBorrowerByStatus("processing"));
        borrowerTable.setItems(borrowerList);
    }

    @FXML
    private void borrowBook() {
        String username = borrowerUsernameField.getText();
        String bookId = bookIDField.getText();

        if (username.isEmpty() || bookId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both Username and Book ID.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Borrow");
        confirmAlert.setHeaderText("Are you sure you want to borrow this book?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            borrowerDAO.insertBorrower(username, bookId);
            loadBorrowers();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
        }
    }

    @FXML
    private void returnBook() {
        String borrowerId = borrowerIDField.getText();

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Return Book", "Please enter Borrower ID.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Return");
        confirmAlert.setHeaderText("Are you sure you want to return this book?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Borrower borrower = borrowerDAO.getBorrowerById(borrowerId);
            if (borrower != null) {
                borrower.setStatus("returned");
                borrowerDAO.updateBorrower(borrower);
                loadBorrowers();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Borrower not found.");
            }
        }
    }

    @FXML
    private void renewBook() {
        String borrowerId = borrowerIDField.getText();
        int additionalDays = 7; // Number of days for renewal

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Renew Book", "Please enter Borrower ID.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Renewal");
        confirmAlert.setHeaderText("Are you sure you want to renew this book?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Borrower borrower = borrowerDAO.getBorrowerById(borrowerId);
            if (borrower != null && borrower.getStatus().equals("processing")) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date currentDueDate = dateFormat.parse(borrower.getBorrow_to());
                    Date newDueDate = new Date(currentDueDate.getTime() + (long) additionalDays * 24 * 60 * 60 * 1000);
                    borrower.setBorrow_to(dateFormat.format(newDueDate));
                    borrowerDAO.updateBorrower(borrower);
                    loadBorrowers();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book renewed successfully.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unable to renew book: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Borrower record not found or book not eligible for renewal.");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

