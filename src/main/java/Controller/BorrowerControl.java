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
import javafx.collections.transformation.FilteredList;
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
import java.time.LocalDate;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;
import static Animation.ColorTransitionExample.addColorTransition;

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
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private AnchorPane borrower_from_animation;


    private double x = 0;
    private double y = 0;

    private ObservableList<Borrower> borrowerList = FXCollections.observableArrayList();
    private BorrowerDAO borrowerDAO = new BorrowerDAO();
    private DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");
    private FilteredList<Borrower> filteredList;


    @FXML
    public void initialize() {
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        setUpTableColumn();
        loadBorrowers();
        markOverdueBorrowers();
        setUpBookSelectionListener();
        setUpFilter();
        addColorTransition(borrower_from_animation);
    }

    @FXML
    private void loadBorrowers() {
        borrowerList.setAll(borrowerDAO.getAllBorrowers());
    }

    private void setUpTableColumn() {
        // Set up table columns with PropertyValueFactory to map fields to columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_to"));

        // Set default profile image in the table
        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));

        // Set custom styling for the status column based on the status value
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setStyle("-fx-background-color: " +
                            ("overdue".equals(status) ? "red" :
                                    "processing".equals(status) ? "yellow" : "green"));
                }
            }
        });
    }

    @FXML
    private void borrowBook() {
        String borrowerId = borrowerIDField.getText();
        String bookId = bookIDField.getText();
        LocalDate borrowToDate = toDatePicker.getValue();

        // Validate input fields
        if (borrowerId.isEmpty() || bookId.isEmpty() || borrowToDate == null) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both user's ID, Book's ID and return Day.");
            return;
        }

        // Ensure the return date is after today
        LocalDate today = LocalDate.now();
        if (!borrowToDate.isAfter(today)) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Return date must be after today.");
            return;
        }

        // Format the return date
        String formattedReturnDate = dateFormatter.formatDate(borrowToDate);

        // Confirm borrowing action
        Optional<ButtonType> result = showConfirmationAlert("Confirm Borrow", "Are you sure you want to borrow this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Check if the borrower and book exist, and if the book is available
            BookDAO bookDAO = new BookDAO();
            UserDAO userBorrow = new UserDAO();
            Book borrowBook = bookDAO.getBookByID(Integer.parseInt(bookId));
            User borrower = userBorrow.findUserById(Integer.parseInt(borrowerId));

            // Handle various error scenarios
            if (borrower == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "User not found.");
                return;
            } else if (borrowBook == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book not found.");
                return;
            } else if (borrowerDAO.checkBookExists(borrowBook.getBookID())) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book already being borrowed.");
                return;
            } else if (borrowerDAO.checkLimitStmt(borrower.getUserName())) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book limit reached.");
                return;
            }

            // Add borrower record to the database and reload borrowers
            String bookName = borrowBook.getName();
            borrowerDAO.insertBorrower(Integer.parseInt(borrowerId), Integer.parseInt(bookId), bookName, dateFormatter.formatDate(today), formattedReturnDate);
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
            // Update borrower's status to "returned"
            Borrower borrower = borrowerDAO.getBorrowerById(Integer.parseInt(borrowerId));
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
        int additionalDays = 7;

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Renew Book", "Please enter Borrower ID.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Renewal", "Are you sure you want to renew this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Renew the borrower's book
            Borrower borrower = borrowerDAO.getBorrowerById(Integer.parseInt(borrowerId));
            if (borrower != null && "processing".equals(borrower.getStatus())) {
                try {
                    LocalDate currentDueDate = dateFormatter.parseDate(borrower.getBorrow_to());
                    LocalDate newDueDate = currentDueDate.plusDays(additionalDays);
                    borrower.setBorrow_to(dateFormatter.formatDate(newDueDate));
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
        String borrowerId = findBorrowerField.getText().trim();

        if (borrowerId.isEmpty()) {
            loadBorrowers();
            return;
        }

        try {
            int id = Integer.parseInt(borrowerId);

            if (borrowerDAO == null) {
                showAlert(Alert.AlertType.ERROR, "System Error", "The borrower data access object is not initialized.");
                return;
            }

            Borrower borrower = borrowerDAO.getBorrowerById(id);

            if (borrower != null) {
                ObservableList<Borrower> foundBorrowers = FXCollections.observableArrayList(borrower);
                borrowerTable.setItems(foundBorrowers);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Search Borrower", "No borrower found with the provided ID.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid numeric ID.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }


    private void markOverdueBorrowers() {
        // Iterate through borrowers and mark overdue ones
        for (Borrower borrower : borrowerList) {
            LocalDate dueDate = dateFormatter.parseDate(borrower.getBorrow_to());
            if (dueDate.isBefore(LocalDate.now())) {
                borrower.setStatus("overdue");
                borrowerDAO.updateBorrower(borrower);
            }
        }
        loadBorrowers();
    }

    private void setUpBookSelectionListener() {
        // Set up a listener to change the book image when a borrower is selected
        borrowerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            // Retrieve the selected book and set the image
            int bookId = newSelection.getBookId();
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

    @FXML
    private void setUpFilter() {
        // Set up the filter combo box with statuses
        filterComboBox.getItems().addAll("All", "Processing", "Returned", "Overdue");
        filterComboBox.setValue("All");

        // Initialize FilteredList based on borrowerList
        filteredList = new FilteredList<>(borrowerList, p -> true);

        // Listen for value changes in ComboBox to update filter
        filterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || "All".equals(newValue)) {
                filteredList.setPredicate(p -> true);
            } else {
                filteredList.setPredicate(borrower -> newValue.equalsIgnoreCase(borrower.getStatus()));
            }
        });

        borrowerTable.setItems(filteredList);
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
            throw new RuntimeException(e);
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
                throw new RuntimeException(e);
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