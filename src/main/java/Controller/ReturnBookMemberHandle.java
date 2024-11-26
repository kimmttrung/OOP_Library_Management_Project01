package Controller;
import DataAccessObject.BookDAO;
import DataAccessObject.BorrowerDAO;
import Entity.Book;
import Entity.Borrower;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;
public class ReturnBookMemberHandle extends BaseDashBoardControl{
    @FXML
    private Button Back_btn;
    @FXML
    private TableColumn<?, ?> BorrowerColumn;
    @FXML
    private TableView<Borrower> borrowerTable;
    @FXML
    private Button close_btn;
    @FXML
    private TableColumn<?, ?> idColumn;
    @FXML
    private Button minus_btn;
    @FXML
    private Label borrowerLabel;
    @FXML
    private TableColumn<?, ?> fromColumn;
    @FXML
    private TableColumn<?, ?> toColumn;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private TableColumn<?, ?> statusColumn;
    @FXML
    private Button signOut_btn;
    @FXML
    private ImageView bookImageView;
    @FXML
    private ComboBox<String> filterComboBox;
    private FilteredList<Borrower> filteredList;

    private ObservableList<Borrower> borrowerList = FXCollections.observableArrayList();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();
    private final DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");

    @FXML
    public void initialize() {
        setUpTableColumns();
        setUpBookSelectionListener();
        setUpFilter();
        loadTable();
    }

    private void loadTable() {
        Platform.runLater(() -> {
            Integer userID = Session.getInstance().getUserID(); // Lấy userID từ Session
            if (userID != null) {
                loadBooksByUserID(userID);
            } else {
                System.out.println("UserID is null or empty");
            }
        });
    }

    @FXML
    private void loadBooksByUserID(int userID) {
        Task<ObservableList<Borrower>> loadBooksTask = new Task<>() {
            @Override
            protected ObservableList<Borrower> call() {
                return FXCollections.observableArrayList(borrowerDAO.getBorrowerListByUserId(userID));
            }
        };

        loadBooksTask.setOnSucceeded(event -> {
            borrowerList = loadBooksTask.getValue();
            filteredList = new FilteredList<>(borrowerList, p -> true);
            borrowerTable.setItems(filteredList); // Đặt danh sách đã lọc vào bảng
        });

        loadBooksTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Load Books", "Failed to load borrowed books.");
        });

        Thread loadBooksThread = new Thread(loadBooksTask);
        loadBooksThread.setDaemon(true);
        loadBooksThread.start();
    }

    private void setUpTableColumns() {
        // Define how each column will fetch data from the Book object
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        BorrowerColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_to"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setUpBookSelectionListener() {
        // Set up a listener to change the book image when a borrower is selected
        borrowerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            };

            int borrowerID = newSelection.getId();
            borrowerLabel.setText(String.valueOf(borrowerID));

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
    private void returnBook() {
        String borrowerId = borrowerLabel.getText().trim();

        if (borrowerId.isEmpty() || "...".equals(borrowerId)) {
            showAlert(Alert.AlertType.ERROR, "Return Book", "Please select a valid borrower ID.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Return", "Are you sure you want to return this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
            borrower.setUser_id(Session.getInstance().getUserID());
            borrowerDAO.updateBorrower(borrower);
            loadTable();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
        }
    }

    @FXML
    private void renewBook() {
        String borrowerId = borrowerLabel.getText().trim();
        int additionalDays = 7;

        if (borrowerId.isEmpty() || "Select Borrow ID".equals(borrowerId)) {
            showAlert(Alert.AlertType.ERROR, "Renew Book", "Please select a valid borrower ID.");
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
                loadTable();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book renewed successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to renew book: " + e.getMessage());
            }
        }
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

    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == Back_btn) {
                applySceneTransition(Back_btn, "/fxml/MemberView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        Stage primaryStage = (Stage) close_btn.getScene().getWindow();
        primaryStage.close();
    }

    public void minimize() {
        Stage stage = (Stage) minus_btn.getScene().getWindow();
        stage.setIconified(true);
    }

}