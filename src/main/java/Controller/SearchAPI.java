package Controller;

import API.GoogleBooksAPI;
import DataAccessObject.BookDAO;
import Entity.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;


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
    private ImageView bookImageView;
    @FXML
    private TableView<Book> searchBookTable;
    @FXML
    private Button dashBoard_btn;
    @FXML
    private TableColumn<?, ?> categoryColumn;
    @FXML
    private Button minus_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private TableColumn<?, ?> publishedDateColumn;
    @FXML
    private TableColumn<?, ?> publisherColumn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button take_btn;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private Button userAll_btn;
    @FXML
    private Button borrowerBook_btn;
    @FXML
    private TextField searchField;
    @FXML
    private Label titleLabel, authorLabel, publisherLabel, categoriesLabel;
    @FXML
    private BarChart<String, Double> chart;

    private double x = 0;
    private double y = 0;

    private final BookDAO bookDAO = new BookDAO();
    private final ObservableList<Book> searchResults = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        series1.setName("Happy New Year 2025");
        series1.getData().add(new XYChart.Data("Mystery ", 500));
        series1.getData().add(new XYChart.Data("Sport ", 300));
        series1.getData().add(new XYChart.Data("History ", 200));
        series1.getData().add(new XYChart.Data("Poetry ", 400));
        series1.getData().add(new XYChart.Data("Health ", 700));
        series1.getData().add(new XYChart.Data("Romance  ", 100));
        series1.getData().add(new XYChart.Data("Biography  ", 150));
        series1.getData().add(new XYChart.Data("Travel  ", 250));

        chart.getData().add(series1);

        nav_from.setTranslateX(-335);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        setUpTableColumns();
        setUpBookSelectionListener();
        loadSearchResults();
    }

    private void setUpTableColumns() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
    }

    private void setUpBookSelectionListener() {
        searchBookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            titleLabel.setText(newSelection.getName());
            authorLabel.setText(newSelection.getAuthor());
            publisherLabel.setText(newSelection.getPublisher());
            categoriesLabel.setText(newSelection.getCategory());

            String imageLink = newSelection.getImage();
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
                String category = "Unknown";
                if (volumeInfo.has("categories") && volumeInfo.get("categories").isJsonArray() && volumeInfo.getAsJsonArray("categories").size() > 0) {
                    category = volumeInfo.getAsJsonArray("categories").get(0).getAsString();
                }
                String imageLink = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                        ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                        : null;

                Book book = new Book(title, authors, publisher, publishedDate, imageLink, category);
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
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == bookAll_btn) {
                applySceneTransition(bookAll_btn, "/fxml/BookView.fxml");
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