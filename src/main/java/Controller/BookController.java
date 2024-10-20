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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Optional;

public class BookController {

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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String imageLink = newSelection.getImage();
                if (imageLink != null && !imageLink.isEmpty()) {
                    Image image = new Image(imageLink);
                    bookImageView.setImage(image);
                } else {
                    Image defaultImage = new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
                    bookImageView.setImage(defaultImage);
                }
            }
        });
        loadBooks();
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
            searchBooks.deleteSearchedBook();

            for (int i = 0; i < items.size(); i++) {
                JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
                String imageLink = volumeInfo.has("imageLinks") ?
                        volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : null;

                Book book = new Book(title, authors, publisher, publishedDate, imageLink);
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

    public void updateBook() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Update Book");
        alert.setHeaderText(null);

        if (bookTitleField.getText().isEmpty()) {
            alert.setContentText("Please enter Book's Name");
            alert.showAndWait();
            return;
        }
        if (bookAuthorField.getText().isEmpty()) {
            alert.setContentText("Please enter Author");
            alert.showAndWait();
            return;
        }
        if (bookPublisherField.getText().isEmpty()) {
            alert.setContentText("Please enter Publisher");
            alert.showAndWait();
            return;
        }
        if (bookYearField.getText().isEmpty()) {
            alert.setContentText("Please enter Year");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Update");
        confirmAlert.setHeaderText("Are you sure you want to update the book?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String bookName = bookTitleField.getText();
            String bookAuthor = bookAuthorField.getText();
            String bookPublisher = bookPublisherField.getText();
            String bookYear = bookYearField.getText();

            Book newBook = new Book(bookName, bookAuthor, bookPublisher, bookYear, null);

            if (bookDAO.insertBook(newBook)) {
                loadBooks();
            } else {
                alert.setContentText("Error updating book. Please try again.");
                alert.showAndWait();
            }
        }
    }

    public void deleteBook() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Delete Book");
        alert.setHeaderText(null);

        if (bookIDField.getText().isEmpty()) {
            alert.setContentText("Please enter BookID you want to delete");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this Book?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int bookId = Integer.parseInt(bookIDField.getText());
            if (bookDAO.deleteBook(bookId)) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Delete book successfully!");
                successAlert.showAndWait();
            } else {
                alert.setContentText("Error deleting book. Please try again later!.");
                alert.showAndWait();
            }
        }
    }
}
