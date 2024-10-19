package Controller;

import API.GoogleBooksAPI;
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

import java.util.Optional;

public class BookController {

    @FXML
    private TextField bookIDField;

    @FXML
    private TextField bookAuthorField;

    @FXML
    private TextField bookPublisherField;

    @FXML
    private TextField bookTitleField;

    @FXML
    private TextField bookYearField;

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, Integer> idColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> publisherColumn;

    @FXML
    private TableColumn<Book, String> publishedDateColumn;

    @FXML
    private TextField searchField;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private BookDAO bookDAO = new BookDAO();


    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

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

            for (int i = 0; i < items.size(); i++) {
                JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";

                Book book = new Book(title, authors, publisher, publishedDate, null);
                bookDAO.insertBook(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
