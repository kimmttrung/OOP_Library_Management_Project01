package Controller;

import DataAccessObject.BookDAO;
import DataAccessObject.BorrowerDAO;
import DataAccessObject.UserDAO;
import Entity.Book;
import Entity.Borrower;
import Entity.User;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;

public class BorrowerControl {

    @FXML
    private TextField borrowerIDField, bookIDField, findBorrowerField;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TableView<Borrower> borrowerTable;
    @FXML
    private TableColumn<Borrower, Integer> idColumn, bookNameColumn;
    @FXML
    private TableColumn<Borrower, String> usernameColumn, statusColumn, fromColumn, toColumn;
    @FXML
    private TableColumn<Borrower, Integer> bookIdColumn;
    @FXML
    private ImageView bookImageView;
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
    private Button close_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button userAll_btn;
    @FXML
    private Circle circleProfile;

    private double x = 0;
    private double y = 0;

    private ObservableList<Borrower> borrowerList = FXCollections.observableArrayList();
    private BorrowerDAO borrowerDAO = new BorrowerDAO();

    @FXML
    public void initialize() {
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        setUpTableColumn();
        loadBorrowers();
        setUpBookSelectionListener();
    }

    private void loadBorrowers() {
        borrowerList = FXCollections.observableArrayList(borrowerDAO.getBorrowerByStatus("processing"));
        borrowerTable.setItems(borrowerList);
    }

    private void setUpTableColumn() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookid"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_to"));

        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));
    }

    @FXML
    private void borrowBook() {
        String borrowerId = borrowerIDField.getText();
        String bookId = bookIDField.getText();
        String borrow_to = convertDatePickerToString(toDatePicker);

        if (borrowerId.isEmpty() || bookId.isEmpty() || borrow_to == null) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both user's ID, Book's ID and return Day.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Borrow", "Are you sure you want to borrow this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            BookDAO bookDAO = new BookDAO();
            Book borrowBook = bookDAO.getBookByID(Integer.parseInt(bookId));

            if (borrowBook == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book not found.");
                return;
            }

            UserDAO userBorrow = new UserDAO();
            User borrower = userBorrow.findUserbyID(Integer.parseInt(borrowerId));
            if (userBorrow == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "User not found.");
                return;
            }
            String username = borrower.getUserName();
            String bookName = borrowBook.getName();;

            borrowerDAO.insertBorrower(username, bookId, bookName, getCurrentDate(), borrow_to);
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

        Optional<ButtonType> result = showConfirmationAlert("Confirm Return", "Are you sure you want to return this book?");
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
        String borrowerId = findBorrowerField.getText();
        int additionalDays = 7;

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Renew Book", "Please enter Borrower ID.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Renewal", "Are you sure you want to renew this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Borrower borrower = borrowerDAO.getBorrowerById(borrowerId);
            if (borrower != null && "processing".equals(borrower.getStatus())) {
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

    @FXML
    private void searchBorrowerById() {
        String borrowerId = findBorrowerField.getText();

        if (borrowerId.isEmpty()) {
            loadBorrowers();
            return;
        }

        Borrower borrower = borrowerDAO.getBorrowerById(borrowerId);

        if (borrower != null) {
            ObservableList<Borrower> foundBorrowers = FXCollections.observableArrayList(borrower);
            borrowerTable.setItems(foundBorrowers);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Search Borrower", "No borrower found with the provided ID.");
        }
    }

    private void setUpBookSelectionListener() {
        borrowerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            int bookId = newSelection.getBookid();
            BookDAO bookDAO = new BookDAO();
            Book borrowBook = bookDAO.getBookByID(bookId);

            if (borrowBook == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book not found.");
                return;
            }
            String imageLink = borrowBook.getImage();
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });
    }

    private String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String convertDatePickerToString(DatePicker datePicker) {
        LocalDate date = datePicker.getValue();
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return null;
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
            } else if (event.getSource() == bookAll_btn) {
                applySceneTransition(bookAll_btn, "/fxml/BookView.fxml");
            } else if (event.getSource() == userAll_btn) {
                applySceneTransition(userAll_btn, "/fxml/UserView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applySceneTransition(Button sourceButton, String fxmlPath) {
        Stage currentStage = (Stage) sourceButton.getScene().getWindow();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentStage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
                Scene newScene = new Scene(root);
                Stage newStage = new Stage();

                root.setOnMousePressed((javafx.scene.input.MouseEvent e) -> {
                    x = e.getSceneX();
                    y = e.getSceneY();
                });
                root.setOnMouseDragged((javafx.scene.input.MouseEvent e) -> {
                    newStage.setX(e.getScreenX() - x);
                    newStage.setY(e.getScreenY() - y);
                });

                newStage.initStyle(StageStyle.TRANSPARENT);
                newStage.setScene(newScene);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                newStage.show();
                currentStage.hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
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
}