package Controller;

import API.GoogleBooksAPI;
import Entity.Book;
import DataAccessObject.BookDAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


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
    private Button bookAll_btn;
    @FXML
    private TableView<?> bookTable;
    @FXML
    private Button close_btn;
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
    private Button search_btn;
    @FXML
    private Button signOut_btn;

    @FXML
    private TableColumn<?, ?> titleColumn;

    @FXML
    private Button userAll_btn;

    private double x = 0;
    private double y = 0;

    @FXML
    public void DownloadPages(ActionEvent event){
        try{
            if (event.getSource() == signOut_btn){
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void exit(){
        System.exit(0);
    }

    public void minimize(){
        Stage stage = (Stage)minus_btn.getScene().getWindow();
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

    private BookDAO bookDAO = new BookDAO();
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

            searchResults.clear();

            for (int i = 0; i < items.size(); i++) {
                JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
                String imageLink = volumeInfo.has("imageLinks") ?
                        volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : null;

                Book book = new Book(title, authors, publisher, publishedDate, imageLink);
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

    public boolean saveBookToDatabase(Book book) {
        return bookDAO.insertBook(book);
    }


//    private BookControl bookControl = new BookControl();
//    private ObservableList<Book> bookList = FXCollections.observableArrayList();
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources){
//        setUpTableColumns();
//        setUpBookSelectionListener();
//        loadBooks();
//    }
//
//    private void setUpTableColumns() {
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
//        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
//        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
//        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
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
//    private void loadBooks() {
//        bookList = FXCollections.observableArrayList(bookControl.getAllBooks());
//        bookTable.setItems(bookList);
//    }
//
//    @FXML
//    private void searchBook() {
//        String query = searchField.getText();
//        if (query.isEmpty()) {
//            showAlert(Alert.AlertType.WARNING, "Search Book", "Please enter a search query.");
//            return;
//        }
//
//        bookControl.searchBooks(query);
//        loadSearchResults();
//    }
//
//    private void loadSearchResults() {
//        bookList = FXCollections.observableArrayList(bookControl.getSearchResults());
//        bookTable.setItems(bookList);
//    }
//
//    @FXML
//    private void saveSelectedBook() {
//        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
//        if (selectedBook == null) {
//            showAlert(Alert.AlertType.WARNING, "Save Book", "Please select a book to save.");
//            return;
//        }
//
//        boolean isSaved = bookControl.saveBookToDatabase(selectedBook);
//        if (isSaved) {
//            showAlert(Alert.AlertType.INFORMATION, "Save Book", "Book saved successfully!");
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Save Book", "Failed to save the book.");
//        }
//    }
//
//    @FXML
//    private void updateBook() {
//        if (isAnyFieldEmpty(bookTitleField, bookAuthorField, bookPublisherField, bookYearField)) {
//            showAlert(Alert.AlertType.ERROR, "Update Book", "All fields are required.");
//            return;
//        }
//
//        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to update the book?");
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            Book newBook = new Book(
//                    bookTitleField.getText(),
//                    bookAuthorField.getText(),
//                    bookPublisherField.getText(),
//                    bookYearField.getText(),
//                    null
//            );
//
//            if (bookControl.updateBook(newBook)) {
//                loadBooks();  // Reload books in the table view
//                showAlert(Alert.AlertType.INFORMATION, "Update Success", "Book updated successfully.");
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Update Error", "Error updating book. Please try again.");
//            }
//        }
//    }
//
//    @FXML
//    private void deleteBook() {
//        if (bookIDField.getText().isEmpty()) {
//            showAlert(Alert.AlertType.ERROR, "Delete Book", "Please enter Book ID you want to delete.");
//            return;
//        }
//
//        Optional<ButtonType> result = showConfirmationAlert("Confirm Delete", "Are you sure you want to delete this Book?");
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            int bookId = Integer.parseInt(bookIDField.getText());
//            if (bookControl.deleteBook(bookId)) {
//                loadBooks();
//                showAlert(Alert.AlertType.INFORMATION, "Delete Success", "Book deleted successfully.");
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Delete Error", "Error deleting book. Please try again.");
//            }
//        }
//    }
//
//    private boolean isAnyFieldEmpty(TextField... fields) {
//        for (TextField field : fields) {
//            if (field.getText().trim().isEmpty()) return true;
//        }
//        return false;
//    }
//
//    private void showAlert(Alert.AlertType alertType, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    private Optional<ButtonType> showConfirmationAlert(String title, String message) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        return alert.showAndWait();
//    }
}
