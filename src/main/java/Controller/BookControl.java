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

public class BookControl {

    private BookDAO bookDAO = new BookDAO();
    private SearchBooks searchBooks = new SearchBooks();
    private ObservableList<Book> searchResults = FXCollections.observableArrayList();

    public ObservableList<Book> getAllBooks() {
        return FXCollections.observableArrayList(bookDAO.getAllBooks());
    }

    public void searchBooks(String query) {
        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
        try {
            String jsonData = googleBooksAPI.fetchBooksData(query);

            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");
            if (items == null || items.size() == 0) {
                return;
            }
            searchBooks.deleteSearchedBook();
            searchResults.clear();

            for (int i = 0; i < items.size(); i++) {
                JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                String isbn = volumeInfo.has("industryIdentifiers") ?
                        volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "Unknown";
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
                String imageLink = volumeInfo.has("imageLinks") ?
                        volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : null;

                Book book = new Book(isbn, title, authors, publisher, publishedDate, imageLink);
                searchBooks.insertBook(book);
                searchResults.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Book> getSearchResults() {
        return searchResults;
    }

    public boolean updateBook(Book book) {
        //insert book information in the database
        return bookDAO.insertBook(book);
    }

    public boolean deleteBook(int bookId) {
        //delete book from the database based on ID
        return bookDAO.deleteBook(bookId);
    }
}
