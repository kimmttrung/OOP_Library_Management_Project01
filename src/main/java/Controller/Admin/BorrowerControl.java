package Controller.Admin;

import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import DataAccessObject.BorrowerDAO;
import DataAccessObject.UserDAO;
import Entity.Book;
import Entity.Borrower;
import Entity.User;

import Singleton.Session;
import Tools.DateStringFormatter;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static Tools.AlertHelper.showAlert;
import static Tools.AlertHelper.showConfirmationAlert;
import static Animation.ColorTransitionExample.addColorTransition;

public class BorrowerControl extends BaseDashBoardControl {

    @FXML
    private TextField borrowerIDField, bookIDField, findBorrowerField, borrowID;
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

    private final ObservableList<Borrower> borrowerList = FXCollections.observableArrayList();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();
    private final DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");
    private FilteredList<Borrower> filteredList;

    @FXML
    public void initialize() {
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        setUpTableColumn();
        setUpFilter();
        markOverdueBorrowers();
        setUpBookSelectionListener();
        addColorTransition(borrower_from_animation);
        loadBorrowers();
    }

    @FXML
    private void loadBorrowers() {
        borrowerList.setAll(borrowerDAO.getAllBorrowers());
        filteredList = new FilteredList<>(borrowerList, p -> true); // Cập nhật danh sách đã lọc
        borrowerTable.setItems(filteredList); // Đặt lại bảng
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

        // Confirm borrowing action
        Optional<ButtonType> result = showConfirmationAlert("Confirm Borrow", "Do you want to borrow this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                BookDAO bookDAO = new BookDAO();
                UserDAO userDAO = new UserDAO();

                Book borrowBook = bookDAO.getBookByID(Integer.parseInt(bookId));
                User borrower = userDAO.findUserById(Integer.parseInt(borrowerId));

                if (borrower == null || borrowBook == null) {
                    showAlert(Alert.AlertType.ERROR, "Borrow Book", borrower == null ? "User not found." : "Book not found.");
                    return;
                }

                if (borrowerDAO.checkBookExists(borrowBook.getBookID())) {
                    showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book is already borrowed.");
                    return;
                }

                if (borrowerDAO.checkLimitStmt(borrower.getUserName())) {
                    showAlert(Alert.AlertType.ERROR, "Borrow Book", "User has reached the borrowing limit.");
                    return;
                }

                borrowerDAO.insertBorrower(Integer.parseInt(borrowerId), Integer.parseInt(bookId), borrowBook.getName(),
                        dateFormatter.formatDate(today), dateFormatter.formatDate(borrowToDate));
                loadBorrowers();
                showAlert(Alert.AlertType.INFORMATION, "Borrow Book", "Book borrowed successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
            }
        }
    }

    @FXML
    private void returnBook() {
        String borrowerId = borrowID.getText();

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Return Book", "Please enter Borrower ID.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Return", "Are you sure you want to return this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Update borrower's status to "returned"
            Borrower borrower = borrowerDAO.getBorrowerById(Integer.parseInt(borrowerId));
            if (borrower == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Borrower not found.");
                return;
            }

            if ("returned".equalsIgnoreCase(borrower.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Return Book", "This book has already been returned.");
                return;
            }

            borrower.setStatus("returned");
            borrowerDAO.updateBorrower(borrower);
            loadBorrowers();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
        }
    }

    @FXML
    private void renewBook() {
        String borrowerId = borrowID.getText();
        int additionalDays = 7;

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Renew Book", "Please enter Borrower ID.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Renewal", "Are you sure you want to renew this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Borrower borrower = borrowerDAO.getBorrowerById(Integer.parseInt(borrowerId));

            if (borrower == null || !"processing".equalsIgnoreCase(borrower.getStatus())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Borrower record not found or book not eligible for renewal.");
                return;
            }

            try {
                LocalDate currentDueDate = dateFormatter.parseDate(borrower.getBorrow_to());
                LocalDate newDueDate = currentDueDate.plusDays(additionalDays);
                borrower.setBorrow_to(dateFormatter.formatDate(newDueDate));
                borrower.setUser_id(Session.getInstance().getUserID());
                borrowerDAO.updateBorrower(borrower);
                loadBorrowers();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book renewed successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to renew book: " + e.getMessage());
            }
        }
    }

    @FXML
    private void searchBorrowerByUserName() {
        String borrowerName = findBorrowerField.getText().trim();

        if (borrowerName.isEmpty()) {
            loadBorrowers();
            return;
        }

        try {

            if (borrowerDAO == null) {
                showAlert(Alert.AlertType.ERROR, "System Error", "The borrower data access object is not initialized.");
                return;
            }

            ArrayList<Borrower> borrower = borrowerDAO.getBorrowerByUsername(borrowerName);

            if (borrower != null && !borrower.isEmpty()) {
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

    @FXML
    private void markOverdueBorrowers() {
        try {
            for (Borrower borrower : borrowerList) {
                LocalDate dueDate = dateFormatter.parseDate(borrower.getBorrow_to());
                if (dueDate != null && !dueDate.isAfter(LocalDate.now()) && borrower.getStatus().equals("processing")) {
                    borrower.setStatus("overdue");
                    borrowerDAO.updateBorrower(borrower);
                }
            }
            loadBorrowers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to mark overdue borrowers: " + e.getMessage());
        }
    }

    private void setUpBookSelectionListener() {
        // Set up a listener to change the book image when a borrower is selected
        borrowerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            borrowID.setText(String.valueOf(newSelection.getId()));
            borrowerIDField.setText(String.valueOf(newSelection.getUser_id()));
            bookIDField.setText(String.valueOf(newSelection.getBookId()));

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
            if ("All".equals(newValue)) {
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
                applySceneTransition(searchAPI_btn, "/fxml/Admin/SearchView.fxml");
            } else if (event.getSource() == dashBoard_btn) {
                applySceneTransition(dashBoard_btn, "/fxml/Admin/DashBoardView.fxml");
            } else if (event.getSource() == bookAll_btn) {
                applySceneTransition(bookAll_btn, "/fxml/Admin/BookView.fxml");
            } else if (event.getSource() == userAll_btn) {
                applySceneTransition(userAll_btn, "/fxml/Admin/UserView.fxml");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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