package Controller.Admin;

import API.QRCodeGenerator;
import Controller.BaseDashBoardControl;
import DataAccessObject.BorrowerDAO;
import Entity.Book;
import DataAccessObject.BookDAO;
import Tools.DateStringFormatter;
import com.google.zxing.WriterException;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static Tools.AlertHelper.showAlert;
import static Tools.AlertHelper.showConfirmationAlert;
import static Animation.ColorTransitionExample.addColorTransition;

/**
 * BookControl is a controller class for managing the book-related functionality in the library
 * management application. It integrates with the JavaFX framework and handles UI events,
 * database interactions, and QR code generation for books.
 * <p>
 * This class provides functionality to:
 *     <li>Display books in a table with details such as ID, title, author, publisher, and published date.</li>
 *     <li>Insert, update, and delete books from the database.</li>
 *     <li>Search books by title.</li>
 *     <li>Generate QR codes for selected books.</li>
 * It interacts with {@link BookDAO} and {@link BorrowerDAO} for database operations and uses a background
 * thread for tasks to ensure a responsive UI.
 * </p>
 */
public class BookControl extends BaseDashBoardControl {
    @FXML
    private Button borrowerBook_btn;
    @FXML
    private Button arrow_btn;
    @FXML
    private TableColumn<?, ?> authorColumn;
    @FXML
    private Button bars_btn;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private Button dashBoard_btn;
    @FXML
    private TableColumn<?, ?> idColumn;
    @FXML
    private Button minus_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private Button close_btn;
    @FXML
    private TableColumn<?, ?> publishedDateColumn;
    @FXML
    private TableColumn<?, ?> publisherColumn;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private Button userAll_btn;
    @FXML
    private ImageView bookImageView, qrCodeImageView;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TextField bookIDField, bookTitleField, bookAuthorField, bookPublisherField;
    @FXML
    private TextField bookIDAdjField, bookTitleAdjField, bookAuthorAdjField, bookYearAdjField, bookPublisherAdjField;
    @FXML
    private Circle circleProfile;
    @FXML
    private TextField findBookField;
    @FXML
    private AnchorPane main_from;

    // DAO classes for database operations
    private final BookDAO bookDAO = new BookDAO();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();

    // Observable list to hold book data
    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    // Formatter for date strings
    private final DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method sets up the UI, initializes table columns, loads books into the table,
     * and sets up listeners for user interactions.
     */
    @FXML
    public void initialize() {
        // Set initial translation for the navigation pane and visibility for buttons
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        // Set up table columns, load books, and create QR code directory
        setUpTableColumns();
        setUpBookSelectionListener();
        createQRCodeDirectory();
        loadBooks();
        addColorTransition(main_from);
    }

    /**
     * Loads books from the database and populates the table.
     * This operation is performed on a background thread to ensure a responsive UI.
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
     * Configures the columns of the table to display book attributes.
     */
    private void setUpTableColumns() {
        // Define how each column will fetch data from the Book object
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        // Set a default profile image to the profile circle
        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));
    }

    /**
     * Sets a listener for book selection in the table to update UI components
     * with the selected book's details.
     */
    private void setUpBookSelectionListener() {
        // Set a listener for book selection in the table
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // If no book is selected, set a default image
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            bookIDAdjField.setText(String.valueOf(newSelection.getBookID()));
            bookTitleAdjField.setText(newSelection.getName());
            bookAuthorAdjField.setText(newSelection.getAuthor());
            bookPublisherAdjField.setText(newSelection.getPublisher());
            bookYearAdjField.setText(newSelection.getPublishedDate());
            bookIDField.setText(String.valueOf(newSelection.getBookID()));

            // Fetch selected book details and update the image
            int bookId = newSelection.getBookID();
            Book selectedBook = bookDAO.getBookByID(bookId);

            if (selectedBook == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book not found.");
                return;
            }

            // Update the image for the selected book
            updateBookImage(selectedBook);
        });
    }

    /**
     * Updates the image displayed for a selected book.
     *
     * @param book The selected book whose image is to be updated.
     */
    private void updateBookImage(Book book) {
        // Update the image of the selected book
        String imageLink = book.getImage();
        Image image = (imageLink != null && !imageLink.isEmpty())
                ? new Image(imageLink)
                : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
        bookImageView.setImage(image);
    }

    /**
     * Inserts a new book into the database based on user inputs from the UI.
     */
    @FXML
    private void insertBook() {
        // Validate fields before proceeding with insertion
        LocalDate bookYearField = toDatePicker.getValue();
        if (isAnyFieldEmpty(bookTitleField, bookAuthorField, bookPublisherField) || bookYearField == null) {
            showAlert(Alert.AlertType.ERROR, "Insert Book", "All fields are required.");
            return;
        }

        String formattedBookDate = dateFormatter.formatDate(bookYearField);

        // Confirm insertion
        Optional<ButtonType> result = showConfirmationAlert("Confirm Insert", "Are you sure you want to insert the book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create new Book object with data from fields
            Book newBook = new Book(
                    bookTitleField.getText(),
                    bookAuthorField.getText(),
                    bookPublisherField.getText(),
                    formattedBookDate,
                    null
            );

            // Insert the book and update the list
            if (bookDAO.insertBook(newBook)) {
                loadBooks();
                showAlert(Alert.AlertType.INFORMATION, "Insert Success", "Book inserted successfully.");
                bookTitleField.clear();
                bookAuthorField.clear();
                bookPublisherField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Insert Error", "Error inserting book. Please try again.");
            }
        }
    }

    /**
     * Updates the details of an existing book in the database based on user inputs.
     */
    @FXML
    private void updateBook() {
        // Check if book ID field is empty
        if (isAnyFieldEmpty(bookIDAdjField)) {
            showAlert(Alert.AlertType.ERROR, "Update Book", "Please enter a valid book ID and Information you want to update.");
            return;
        }

        int bookId = Integer.parseInt(bookIDAdjField.getText());
        Book selectedBook = bookDAO.getBookByID(bookId);

        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Update Book", "No book selected for update.");
            return;
        }

        // Validate date format if the date field is not empty
        String newDate = bookYearAdjField.getText();
        if (!newDate.isEmpty() && !dateFormatter.isValidDate(newDate)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "The date format must be yyyy-MM-dd.");
            return;
        }

        // Confirm update
        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to update the book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create an updated Book object with values from fields
            Book updatedBook = new Book(
                    selectedBook.getBookID(),
                    bookTitleAdjField.getText().isEmpty() ? selectedBook.getName() : bookTitleAdjField.getText(),
                    bookAuthorAdjField.getText().isEmpty() ? selectedBook.getAuthor() : bookAuthorAdjField.getText(),
                    bookPublisherAdjField.getText().isEmpty() ? selectedBook.getPublisher() : bookPublisherAdjField.getText(),
                    newDate.isEmpty() ? selectedBook.getPublishedDate() : newDate,
                    selectedBook.getImage()
            );

            // Update the book and refresh the list
            if (bookDAO.updateBook(updatedBook)) {
                loadBooks();
                showAlert(Alert.AlertType.INFORMATION, "Update Success", "Book updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "Error updating book. Please try again.");
            }
        }
    }

    /**
     * Deletes a book from the database after validating user input and confirming the action.
     */
    @FXML
    private void deleteBook() {
        // Validate book ID before deletion
        if (bookIDField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Delete Book", "Please enter Book ID you want to delete.");
            return;
        }

        int bookId = Integer.parseInt(bookIDField.getText());
        if (borrowerDAO.hasBorrowingBook("processing", bookId)) {
            showAlert(Alert.AlertType.ERROR, "Delete Book", "Book are being borrowed.");
            return;
        }
        // Confirm deletion
        Optional<ButtonType> result = showConfirmationAlert("Confirm Delete", "Are you sure you want to delete this Book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (bookDAO.deleteBook(bookId)) {
                loadBooks();
                showAlert(Alert.AlertType.INFORMATION, "Delete Success", "Book deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Error", "Error deleting book. Please try again.");
            }
        }
    }

    /**
     * Searches for books by title and displays the results in the table.
     */
    @FXML
    private void searchBookByName() {
        // Fetch books based on the search term
        String bookFindName = findBookField.getText();

        if (bookFindName.isEmpty()) {
            loadBooks(); // If no search term, reload all books
            return;
        }

        ArrayList<Book> foundBooks = bookDAO.getBooksByName(bookFindName);

        if (foundBooks != null && !foundBooks.isEmpty()) {
            ObservableList<Book> bookList = FXCollections.observableArrayList(foundBooks);
            bookTable.setItems(bookList);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Search Book", "No books found with the provided name.");
        }
    }

    /**
     * Generates a QR code for the selected book, containing its details as JSON.
     * The QR code is saved as an image file and displayed in the UI.
     */
    @FXML
    private void generateQRCode() {
        // Get selected book to generate QR Code
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Generate QR Code", "Please select a book to generate QR code.");
            return;
        }

        // Generate QR Code in a background thread
        Task<Void> generateQRCodeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Format book data as JSON for the QR Code
                String qrData = String.format(
                        "{ \"bookID\": %d, \"title\": \"%s\", \"author\": \"%s\", \"publisher\": \"%s\", \"publishedDate\": \"%s\" }",
                        selectedBook.getBookID(),
                        selectedBook.getName(),
                        selectedBook.getAuthor(),
                        selectedBook.getPublisher(),
                        selectedBook.getPublishedDate()
                );

                // Generate QR Code and save it to the specified file path
                String filePath = "src/main/resources/qr_codes/book_" + selectedBook.getBookID() + ".png";

                try {
                    QRCodeGenerator.generateQRCodeImage(qrData, 200, 200, filePath);
                    Image qrImage = new Image(new File(filePath).toURI().toString());
                    qrCodeImageView.setImage(qrImage);
                } catch (WriterException | IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Generate QR Code", "Error generating QR Code: " + e.getMessage());
                }

                return null;
            }

            @Override
            protected void succeeded() {
                // Show success message once the QR Code is generated
                showAlert(Alert.AlertType.INFORMATION, "QR Code Generated", "QR Code saved successfully.");
            }

            @Override
            protected void failed() {
                // Show error message if QR Code generation fails
                showAlert(Alert.AlertType.ERROR, "Generate QR Code", "Error generating QR Code.");
            }
        };

        Thread qrCodeThread = new Thread(generateQRCodeTask);
        qrCodeThread.setDaemon(true);
        qrCodeThread.start();
    }

    /**
     * Creates a directory for saving QR code images if it does not already exist.
     */
    private void createQRCodeDirectory() {
        File directory = new File("src/main/resources/qr_codes");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Checks if any of the provided text fields are empty.
     *
     * @param fields Text fields to check for emptiness.
     * @return True if any field is empty, false otherwise.
     */
    private boolean isAnyFieldEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) return true;
        }
        return false;
    }

    /**
     * Handles navigation between different pages in the application.
     *
     * @param event The triggered ActionEvent.
     */
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
            } else if (event.getSource() == borrowerBook_btn) {
                applySceneTransition(borrowerBook_btn, "/fxml/Admin/BorrowerView.fxml");
            } else if (event.getSource() == userAll_btn) {
                applySceneTransition(userAll_btn, "/fxml/Admin/UserView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application with a fade-out animation.
     */
    public void exit() {
        Stage primaryStage = (Stage) close_btn.getScene().getWindow();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> Platform.exit());
        fadeOut.play();
    }

    /**
     * Minimizes the current application window.
     */
    public void minimize() {
        Stage stage = (Stage) minus_btn.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Animates and hides the navigation bar.
     */
    public void sliderArrow() {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.5), nav_from);
        slide.setToX(-320);
        slide.setOnFinished(event -> {
            bars_btn.setVisible(true);
            arrow_btn.setVisible(false);
        });
        slide.play();
    }

    public void sliderBars() {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.5), nav_from);
        slide.setToX(0);
        slide.setOnFinished(event -> {
            arrow_btn.setVisible(true);
            bars_btn.setVisible(false);
        });
        slide.play();
    }
}
