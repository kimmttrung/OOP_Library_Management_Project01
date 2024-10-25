//package Controller;
//
//import API.GoogleBooksAPI;
//import Entity.Book;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class SearchBook extends BookControl{
//    @FXML
//    protected TableView<Book> searchResultsTable; // New TableView for search results
//    @FXML
//    protected TableColumn<Book, String> searchTitleColumn;
//    @FXML
//    protected TableColumn<Book, String> searchAuthorColumn;
//    @FXML
//    protected TableColumn<Book, String> searchPublisherColumn;
//    @FXML
//    protected TableColumn<Book, String> searchPublishedDateColumn;
//    @FXML
//    protected TableColumn<Book, String> searchISBNColumn;
//    @FXML
//    private ImageView bookImageView;
//    @FXML
//    public void initialize() {
//        setUpSearchResultsTableColumns(); // Set up new search results table columns
//        loadBooks();
//    }
//
//
//    private void setUpSearchResultsTableColumns() {
//        searchISBNColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
//        searchTitleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        searchAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
//        searchPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
//        searchPublishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
//    }
//
//    private void setUpBookSelectionListener() {
//        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//            String imageLink = (newSelection != null) ? newSelection.getImage() : null;
//            Image image = (imageLink != null && !imageLink.isEmpty())
//                    ? new Image(imageLink)
//                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
//            bookImageView.setImage(image);
//        });
//    }
//
//    @FXML
//    private void searchBook() {
//        String needBook = searchField.getText();
//        performBookSearch(needBook);
//    }
//
//    private void performBookSearch(String query) {
//        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
//        try {
//            String jsonData = googleBooksAPI.fetchBooksData(query);
//            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
//            JsonArray items = jsonObject.getAsJsonArray("items");
//
//            if (items == null || items.size() == 0) {
//                showAlert(Alert.AlertType.INFORMATION, "No Results Found", "No books found for the search query: " + query);
//                return;
//            }
//
//            ObservableList<Book> searchResults = FXCollections.observableArrayList();
//            for (int i = 0; i < items.size(); i++) {
//                JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
//                String isbn = volumeInfo.has("industryIdentifiers") ?
//                        volumeInfo.getAsJsonArray("industryIdentifiers").get(0).getAsJsonObject().get("identifier").getAsString() : "Unknown";
//                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
//                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
//                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
//                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
//                String imageLink = volumeInfo.has("imageLinks") ?
//                        volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : null;
//
//                Book book = new Book(isbn, title, authors, publisher, publishedDate, imageLink);
//                searchResults.add(book);
//            }
//
//            updateSearchResultsTable(searchResults);
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Search Error", "An error occurred while searching for books.");
//        }
//    }
//
//    private void updateSearchResultsTable(ObservableList<Book> searchResults) {
//        searchResultsTable.setItems(searchResults);
//    }
//}
