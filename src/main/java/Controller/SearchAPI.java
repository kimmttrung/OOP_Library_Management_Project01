package Controller;

import API.GoogleBooksAPI;
import DataAccessObject.BookDAO;
import Entity.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import static Controller.AlertHelper.showAlert;


public class SearchAPI {
    @FXML
    private Button arrow_btn;
    @FXML
    private TableColumn<?, ?> authorColumn;
    @FXML
    private Button bars_btn;
    @FXML
    private Button bookAll_btn;
    @FXML
    private Button bookAll_dashBoard_btn;
    @FXML
    private ImageView bookImageView;
    @FXML
    private TableView<Book> searchBookTable;
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
    private Button save_btn;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button take_btn;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private Button userAll_btn;
    @FXML
    private Button userAll_dashBoard_btn;
    @FXML
    private Button borrowerBook_btn;
    @FXML
    private Button borrowerDashBoard_btn;
    @FXML
    private TextField searchField;

    private double x = 0;
    private double y = 0;

    private final BookDAO bookDAO = new BookDAO();
    private final ObservableList<Book> searchResults = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        setUpTableColumns();
        setUpBookSelectionListener();
        loadSearchResults();
    }

    private void setUpTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
    }

    private void setUpBookSelectionListener() {
        searchBookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String imageLink = (newSelection != null) ? newSelection.getImage() : null;
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });
    }

    private void loadSearchResults() {
//        if (searchResults.isEmpty()) {
//            showAlert(Alert.AlertType.INFORMATION, "Search Book", "No books found for the given query.");
//        }
        searchBookTable.setItems(FXCollections.observableArrayList(searchResults));
    }

    @FXML
    private void searchBook() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Book", "Please enter a search query.");
            return;
        }

        searchBooks(query);
        loadSearchResults();
    }

    private void searchBooks(String query) {
        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
        try {
            String jsonData = googleBooksAPI.fetchBooksData(query);
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonArray items = jsonObject.has("items") ? jsonObject.getAsJsonArray("items") : null;

            if (items == null || items.isEmpty()) return;

            searchResults.clear();

            for (JsonElement item : items) {
                JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
                String imageLink = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                        ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                        : null;

                Book book = new Book(title, authors, publisher, publishedDate, imageLink);
                searchResults.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveSelectedBook() {
        Book selectedBook = searchBookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Save Book", "Please select a book to save.");
            return;
        }

        boolean isSaved = bookDAO.insertBook(selectedBook);
        if (isSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Save Book", "Book saved successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Book", "Failed to save the book.");
        }
    }


    private boolean isAnyFieldEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) return true;
        }
        return false;
    }

    @FXML
    public void DownloadPages(ActionEvent event){
        try{
            if (event.getSource() == signOut_btn){
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


            } else if (event.getSource() == bookAll_btn || event.getSource() == bookAll_dashBoard_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/availableBook.fxml"));
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
                bookAll_btn.getScene().getWindow().hide();
            } else if (event.getSource() == borrowerBook_btn || event.getSource() == borrowerDashBoard_btn) {
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
            } else if (event.getSource() == userAll_btn || event.getSource() == userAll_dashBoard_btn) {
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
        slide.setToX(-335);

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