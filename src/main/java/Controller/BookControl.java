package Controller;

import API.GoogleBooksAPI;
import API.QRCodeGenerator;
import Entity.Book;
import DataAccessObject.BookDAO;
import Entity.Borrower;
import com.google.zxing.WriterException;
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
import java.util.ArrayList;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;


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
    private TextField bookIDField, bookTitleField, bookAuthorField, bookYearField, bookPublisherField;
    @FXML
    private TextField bookIDAdjField, bookTitleAdjField, bookAuthorAdjField, bookYearAdjField, bookPublisherAdjField;
    @FXML
    private Circle circleProfile;
    @FXML
    private TextField findBookField;

    private double x = 0;
    private double y = 0;

    private final BookDAO bookDAO = new BookDAO();
    private final ObservableList<Book> searchResults = FXCollections.observableArrayList();
    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        setUpTableColumns();
        setUpBookSelectionListener();
        loadBooks();
        createQRCodeDirectory();
    }

    @FXML
    private void loadBooks() {
        bookList = FXCollections.observableArrayList(bookDAO.getAllBooks());
        bookTable.setItems(bookList);
    }

    private void setUpTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));
    }

    private void setUpBookSelectionListener() {
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            int bookId = newSelection.getBookID();
            Book selectedBook = bookDAO.getBookByID(bookId);

            if (selectedBook == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book not found.");
                return;
            }

            String imageLink = selectedBook.getImage();
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });

    }

    @FXML
    private void insertBook() {
        if (isAnyFieldEmpty(bookTitleField, bookAuthorField, bookPublisherField, bookYearField)) {
            showAlert(Alert.AlertType.ERROR, "Insert Book", "All fields are required.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Insert", "Are you sure you want to insert the book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Book newBook = new Book(
                    bookTitleField.getText(),
                    bookAuthorField.getText(),
                    bookPublisherField.getText(),
                    bookYearField.getText(),
                    null
            );

            if (bookDAO.insertBook(newBook)) {
                loadBooks();
                showAlert(Alert.AlertType.INFORMATION, "Insert Success", "Book inserted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Insert Error", "Error inserting book. Please try again.");
            }
        }
    }

    @FXML
    private void updateBook() {
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

            Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to update the book?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Book updatedBook = new Book(
                        selectedBook.getBookID(),
                        bookTitleAdjField.getText().isEmpty() ? selectedBook.getName() : bookTitleAdjField.getText(),
                        bookAuthorAdjField.getText().isEmpty() ? selectedBook.getAuthor() : bookAuthorAdjField.getText(),
                        bookPublisherAdjField.getText().isEmpty() ? selectedBook.getPublisher() : bookPublisherAdjField.getText(),
                        bookYearAdjField.getText().isEmpty() ? selectedBook.getPublishedDate() : bookYearAdjField.getText(),
                        selectedBook.getImage()
                );

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
        if (bookIDField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Delete Book", "Please enter Book ID you want to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Delete", "Are you sure you want to delete this Book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int bookId = Integer.parseInt(bookIDField.getText());
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
        String bookFindName = findBookField.getText();

        if (bookFindName.isEmpty()) {
            loadBooks();
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
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Generate QR Code", "Please select a book to generate QR code.");
            return;
        }

        // Chuỗi dữ liệu mã hóa trong QR Code (VD: JSON)
        String qrData = String.format(
                "{ \"bookID\": %d, \"title\": \"%s\", \"author\": \"%s\", \"publisher\": \"%s\", \"publishedDate\": \"%s\" }",
                selectedBook.getBookID(),
                selectedBook.getName(),
                selectedBook.getAuthor(),
                selectedBook.getPublisher(),
                selectedBook.getPublishedDate()
        );

        // Đường dẫn lưu file QR Code
        String filePath = "src/main/resources/qr_codes/book_" + selectedBook.getBookID() + ".png";

        try {
            QRCodeGenerator.generateQRCodeImage(qrData, 200, 200, filePath);
            showAlert(Alert.AlertType.INFORMATION, "QR Code Generated", "QR Code saved at: " + filePath);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Generate QR Code", "Error generating QR Code: " + e.getMessage());
        }

        Image qrImage = new Image(new File(filePath).toURI().toString());
        qrCodeImageView.setImage(qrImage);
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
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/loginForm.fxml"));
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


            } else if (event.getSource() == borrowerBook_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Borrower.fxml"));
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
                borrowerBook_btn.getScene().getWindow().hide();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        System.exit(0);
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
