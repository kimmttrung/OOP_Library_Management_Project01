package Controller;

import API.QRCodeGenerator;
import DataAccessObject.BookDAO;
import DataAccessObject.BorrowerDAO;
import DataAccessObject.UserDAO;
import Entity.Book;
import Entity.User;
import com.google.zxing.WriterException;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
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

public class Member {

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
    private Button cancel_btn1;
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

    private double x = 0;
    private double y = 0;

    private final BookDAO bookDAO = new BookDAO();
    private BorrowerDAO borrowerDAO = new BorrowerDAO();
    private final ObservableList<Book> searchResults = FXCollections.observableArrayList();
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Set up table columns, load books, and create QR code directory
        setUpTableColumns();
        setUpBookSelectionListener();
        loadBooks();
        createQRCodeDirectory();
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
    }

    private void setUpBookSelectionListener() {
        // Set a listener for book selection in the table
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // If no book is selected, set a default image
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

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

        @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == SerachAPIUser_btn) {
                applySceneTransition(SerachAPIUser_btn, "/fxml/SearchAPIUser.fxml");
            } else if (event.getSource() == borrowUser_btn) {
                applySceneTransition1(borrowUser_btn, "/fxml/BorrowUser.fxml");
            } else if (event.getSource() == commentUser_btn) {
                applySceneTransition1(commentUser_btn, "/fxml/CommentBook.fxml");
            } else if (event.getSource() == cancel_btn) {
                applySceneTransition(cancel_btn, "/fxml/MemberView.fxml");
            } else if (event.getSource() == cancel_btn1) {
                applySceneTransition(cancel_btn1, "/fxml/MemberView.fxml");
            } else if (event.getSource() == DashBoardUser_btn) {
                applySceneTransition(DashBoardUser_btn, "/fxml/DashBoardUser.fxml");
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

    private void applySceneTransition1(Button sourceButton, String fxmlPath) {
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

            newStage.show();
        } catch (IOException e) {
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
}
