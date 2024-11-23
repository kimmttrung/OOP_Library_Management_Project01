package Controller;

import API.QRCodeGenerator;
import DataAccessObject.BorrowerDAO;
import Entity.Book;
import DataAccessObject.BookDAO;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;
import static Animation.ColorTransitionExample.addColorTransition;


public class BookControl {
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

    private double x = 0;
    private double y = 0;

    private final BookDAO bookDAO = new BookDAO();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");

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

    private void updateBookImage(Book book) {
        // Update the image of the selected book
        String imageLink = book.getImage();
        Image image = (imageLink != null && !imageLink.isEmpty())
                ? new Image(imageLink)
                : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
        bookImageView.setImage(image);
    }

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

        // Confirm update
        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to update the book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create an updated Book object with values from fields
            Book updatedBook = new Book(
                    selectedBook.getBookID(),
                    bookTitleAdjField.getText().isEmpty() ? selectedBook.getName() : bookTitleAdjField.getText(),
                    bookAuthorAdjField.getText().isEmpty() ? selectedBook.getAuthor() : bookAuthorAdjField.getText(),
                    bookPublisherAdjField.getText().isEmpty() ? selectedBook.getPublisher() : bookPublisherAdjField.getText(),
                    bookYearAdjField.getText().isEmpty() ? selectedBook.getPublishedDate() : bookYearAdjField.getText(),
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

    private void createQRCodeDirectory() {
        File directory = new File("src/main/resources/qr_codes");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private boolean isAnyFieldEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) return true;
        }
        return false;
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

    public void minimize() {
        Stage stage = (Stage) minus_btn.getScene().getWindow();
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
