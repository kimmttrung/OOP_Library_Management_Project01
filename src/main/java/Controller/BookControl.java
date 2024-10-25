package Controller;

import API.GoogleBooksAPI;
import DataAccessObject.SearchBooks;
import Entity.Book;
import DataAccessObject.BookDAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class BookControl {

    @FXML
    protected TextField bookIDField;
    @FXML
    protected TextField bookAuthorField;
    @FXML
    protected TextField bookPublisherField;
    @FXML
    protected TextField bookTitleField;
    @FXML
    protected TextField bookYearField;
    @FXML
    protected TableView<Book> bookTable;
    @FXML
    protected TableColumn<Book, Integer> idColumn;
    @FXML
    protected TableColumn<Book, String> titleColumn;
    @FXML
    protected TableColumn<Book, String> authorColumn;
    @FXML
    protected TableColumn<Book, String> publisherColumn;
    @FXML
    protected TableColumn<Book, String> publishedDateColumn;
    @FXML
    private ImageView bookImageView;
    @FXML
    protected TextField searchField;

    protected ObservableList<Book> bookList = FXCollections.observableArrayList();
    protected BookDAO bookDAO = new BookDAO();
    SearchBooks searchBooks = new SearchBooks();

    @FXML
    public void initialize() {
        setUpTableColumns();
        setUpBookSelectionListener();
        loadBooks();
    }

    private void setUpTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
    }

    private void setUpBookSelectionListener() {
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String imageLink = (newSelection != null) ? newSelection.getImage() : null;
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });
    }

    public void loadBooks() {
        bookList = FXCollections.observableArrayList(bookDAO.getAllBooks());
        bookTable.setItems(bookList);
    }

    @FXML
    private void searchBook() {
        String needBook = searchField.getText();
        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
        try {
            // Lấy dữ liệu từ Google Books API
            String jsonData = googleBooksAPI.fetchBooksData(needBook);

            // Phân tích JSON
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");
            if (items == null || items.size() == 0) {
                showAlert(Alert.AlertType.INFORMATION, "No Results Found", "No books found for the search query: " + needBook);
                return;
            }
            searchBooks.deleteSearchedBook();

            for (int i = 0; i < items.size(); i++) {
                JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                String isbn = volumeInfo.has("industryIdentifiers") ?
                        volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "Unknown"; // Get ISBN
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
                String imageLink = volumeInfo.has("imageLinks") ?
                        volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : null;

                Book book = new Book(isbn, title, authors, publisher, publishedDate, imageLink);
                searchBooks.insertBook(book);
                loadSearchBooks();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSearchBooks() {
        bookList = FXCollections.observableArrayList(searchBooks.getSearchedBooks());
        bookTable.setItems(bookList);
    }

    @FXML
    private void updateBook() {
        if (isAnyFieldEmpty(bookTitleField, bookAuthorField, bookPublisherField, bookYearField)) {
            showAlert(Alert.AlertType.ERROR, "Update Book", "All fields are required.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to update the book?");
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
                showAlert(Alert.AlertType.INFORMATION, "Update Success", "Book updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "Error updating book. Please try again.");
            }
        }
    }

    @FXML
    private void deleteBook() {
        if (bookIDField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Delete Book", "Please enter Book ID.");
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

    private boolean isAnyFieldEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) return true;
        }
        return false;
    }

    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Optional<ButtonType> showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }





    @FXML
    protected Button returnBooks_btn;

    public void searchBooks() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/test1.fxml"));
        Parent root = loader.load();

        Stage currentStage = (Stage) returnBooks_btn.getScene().getWindow();
        currentStage.close();

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
