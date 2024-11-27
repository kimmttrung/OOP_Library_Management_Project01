package Controller.Users;

import API.QRCodeGenerator;
import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import Entity.Book;
import Singleton.Session;
import com.google.zxing.WriterException;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Tools.AlertHelper.showAlert;
import static Tools.AlertHelper.showConfirmationAlert;

/**
 * Controller class for managing the user interface and interactions
 * in the "User Book List" view of the application.
 * This view allows users to browse books, search by name, generate QR codes,
 * and view comments for selected books. It also includes navigation controls.
 */
public class UserBookList extends BaseDashBoardControl {

    @FXML
    private Button borrowUser_btn;
    @FXML
    private Button close_btn;
    @FXML
    private Button commentUser_btn;
    @FXML
    private Button minus_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button DashBoardUser_btn;
    @FXML
    private Button SerachAPIUser_btn;
    @FXML
    private Button cancel_btn;
    @FXML
    private TableColumn<?, ?> authorColumn;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<?, ?> idColumn;
    @FXML
    private TableColumn<?, ?> publishedDateColumn;
    @FXML
    private TableColumn<?, ?> publisherColumn;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private ImageView bookImageView, qrCodeImageView;
    @FXML
    private TextField findBookField;
    @FXML
    private ListView<String> commentListView;
    @FXML
    private Button Return_btn;
    @FXML
    private Button Back_btn;
    @FXML
    private Label UID;

    private final BookDAO bookDAO = new BookDAO(); // Data access object for book data
    private ObservableList<Book> bookList = FXCollections.observableArrayList(); // Observable list of books

    /**
     * Initializes the controller by setting up table columns, book selection listener,
     * loading books into the table, and preparing the QR code directory.
     */
    @FXML
    public void initialize() {
        setUpTableColumns();
        setUpBookSelectionListener();
        loadBooks();
        createQRCodeDirectory();
        UID.setText("UID: " + Session.getInstance().getUserID());
    }

    /**
     * Loads books from the database into the table view in a background thread
     * to ensure the UI remains responsive.
     */
    @FXML
    private void loadBooks() {
        // Load books in a background thread to keep the UI responsive
        Task<Void> loadBooksTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Fetch all books from the database
                bookList = FXCollections.observableArrayList(bookDAO.getAllBooks());
                return null;
            }

            @Override
            protected void succeeded() {
                // Set the book list to the table view once data is loaded
                bookTable.setItems(bookList);
            }

            @Override
            protected void failed() {
                // Show an error alert if loading books fails
                showAlert(Alert.AlertType.ERROR, "Load Books", "Failed to load books.");
            }
        };

        // Start the task in a separate thread
        Thread loadBooksThread = new Thread(loadBooksTask);
        loadBooksThread.setDaemon(true);
        loadBooksThread.start();
    }

    /**
     * Sets up table columns to display book attributes.
     */
    private void setUpTableColumns() {
        // Define how each column will fetch data from the Book object
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
    }

    /**
     * Sets up a listener for selecting a book in the table view
     * to update the book image and details.
     */
    private void setUpBookSelectionListener() {
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            Book selectedBook = bookDAO.getBookByID(newSelection.getBookID());
            if (selectedBook == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book not found.");
                return;
            }

            updateBookImage(selectedBook);
        });
    }

    /**
     * Updates the displayed book image for the selected book.
     *
     * @param book the selected book
     */
    private void updateBookImage(Book book) {
        String imageLink = book.getImage();
        Image image = (imageLink != null && !imageLink.isEmpty())
                ? new Image(imageLink)
                : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
        bookImageView.setImage(image);
    }

    /**
     * Searches for books by name based on user input and updates the table view.
     */
    @FXML
    private void searchBookByName() {
        String bookFindName = findBookField.getText();

        if (bookFindName.isEmpty()) {
            loadBooks();
            return;
        }

        ArrayList<Book> foundBooks = bookDAO.getBooksByName(bookFindName);

        if (foundBooks != null && !foundBooks.isEmpty()) {
            bookTable.setItems(FXCollections.observableArrayList(foundBooks));
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Search Book", "No books found with the provided name.");
        }
    }

    /**
     * Generates a QR code for the selected book and displays it in the QR code image view.
     */
    @FXML
    private void generateQRCode() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Generate QR Code", "Please select a book to generate QR code.");
            return;
        }

        Task<Void> generateQRCodeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String qrData = String.format(
                        "{ \"bookID\": %d, \"title\": \"%s\", \"author\": \"%s\", \"publisher\": \"%s\", \"publishedDate\": \"%s\" }",
                        selectedBook.getBookID(),
                        selectedBook.getName(),
                        selectedBook.getAuthor(),
                        selectedBook.getPublisher(),
                        selectedBook.getPublishedDate()
                );

                String filePath = "src/main/resources/qr_codes/book_" + selectedBook.getBookID() + ".png";

                QRCodeGenerator.generateQRCodeImage(qrData, 200, 200, filePath);
                Image qrImage = new Image(new File(filePath).toURI().toString());
                qrCodeImageView.setImage(qrImage);

                return null;
            }

            @Override
            protected void succeeded() {
                showAlert(Alert.AlertType.INFORMATION, "QR Code Generated", "QR Code saved successfully.");
            }

            @Override
            protected void failed() {
                showAlert(Alert.AlertType.ERROR, "Generate QR Code", "Error generating QR Code.");
            }
        };

        Thread qrCodeThread = new Thread(generateQRCodeTask);
        qrCodeThread.setDaemon(true);
        qrCodeThread.start();
    }

    /**
     * Creates a directory for storing QR code images if it doesn't already exist.
     */
    private void createQRCodeDirectory() {
        File directory = new File("src/main/resources/qr_codes");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Displays comments for the selected book in the list view.
     */
    @FXML
    private void showComments() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Comments", "Please select a book to view comments.");
            return;
        }

        int bookID = selectedBook.getBookID();
        try {
            List<String> comments = bookDAO.getCommentsForBook(bookID);
            commentListView.getItems().setAll(comments);

            if (comments.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Comments", "No comments found for this book.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Comments", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles navigation actions for various buttons in the view.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == SerachAPIUser_btn) {
                applySceneTransition(SerachAPIUser_btn, "/fxml/Users/SearchAPIUser.fxml");
            } else if (event.getSource() == borrowUser_btn) {
                applySceneTransition1(borrowUser_btn, "/fxml/Users/BorrowUser.fxml");
            } else if (event.getSource() == commentUser_btn) {
                applySceneTransition1(commentUser_btn, "/fxml/Users/CommentBook.fxml");
            } else if (event.getSource() == cancel_btn) {
                applySceneTransition(cancel_btn, "/fxml/Users/MemberView.fxml");
            } else if (event.getSource() == DashBoardUser_btn) {
                applySceneTransition(DashBoardUser_btn, "/fxml/Users/DashBoardUser.fxml");
            } else if (event.getSource() == Return_btn) {
                applySceneTransition(Return_btn, "/fxml/Users/ListBookBorrowed.fxml");
            } else if (event.getSource() == Back_btn) {
                applySceneTransition(Back_btn, "/fxml/Users/MemberView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application with a fade-out and scale-down animation.
     */
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

    /**
     * Minimizes the application window.
     */
    public void minimize() {
        Stage stage = (Stage) minus_btn.getScene().getWindow();
        stage.setIconified(true);
    }
}