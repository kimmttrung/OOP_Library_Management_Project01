package Controller;

import API.GoogleBooksAPI;
import DataAccessObject.BookDAO;
import Entity.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;

public class SearchAPIUser extends BaseDashBoardControl {
    @FXML
    private Button BookLibrary_btn;
    @FXML
    private Button DashBoardUser_btn;
    @FXML
    private BarChart<String, Double> chartAPIUser2;
    @FXML
    private Button close_btn;
    @FXML
    private Button minus_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private TableColumn<?, ?> authorColumn;
    @FXML
    private ImageView bookImageView;
    @FXML
    private TableView<Book> searchBookTable;
    @FXML
    private TableColumn<?, ?> categoryColumn;
    @FXML
    private TableColumn<?, ?> publishedDateColumn;
    @FXML
    private TableColumn<?, ?> publisherColumn;
    @FXML
    private TableColumn<?, ?> titleColumn;
    @FXML
    private TableColumn<?, ?> languageColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label UID;

    private final BookDAO bookDAO = new BookDAO();
    private final ObservableList<Book> searchResults = FXCollections.observableArrayList();

    public void initialize() {
        // Set up the table columns, listener for book selection, and load initial search results
        setUpTableColumns();
        setUpBookSelectionListener();
        loadSearchResults();
        setUpChart();
        UID.setText("UID: " + Session.getInstance().getUserID());
    }

    private void setUpTableColumns() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
    }

    private void setUpBookSelectionListener() {
        // Add a listener for when a book is selected from the table
        searchBookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }
            // Set the image of the selected book, or default if not available
            String imageLink = newSelection.getImage();
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });
    }

    private void setUpChart() {
        XYChart.Series<String, Double> series2 = new XYChart.Series<>();
        series2.getData().add(new XYChart.Data("Fiction", 600));
        series2.getData().add(new XYChart.Data("Adventure", 450));
        series2.getData().add(new XYChart.Data("Science", 300));
        series2.getData().add(new XYChart.Data("Fantasy", 800));
        series2.getData().add(new XYChart.Data("Cooking", 500));
        series2.getData().add(new XYChart.Data("Travel", 350));
        series2.getData().add(new XYChart.Data("Self-Help", 400));
        series2.getData().add(new XYChart.Data("Technology", 550));

        chartAPIUser2.getData().add(series2);
    }

    private void loadSearchResults() {
        // Populate the table with the list of search results
        searchBookTable.setItems(FXCollections.observableArrayList(searchResults));
    }

    @FXML
    private void searchBook() {
        // Search for books based on the user's query
        String query = searchField.getText();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Book", "Please enter a search query.");
            return;
        }

        // Proceed with searching books based on the query and update the results
        searchBooks(query);
        loadSearchResults();
    }

    private void searchBooks(String query) {
        // Perform the search operation asynchronously using a background task
        Task<Void> searchBooksTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Use the GoogleBooksAPI to fetch book data based on the query
                GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
                try {
                    // Fetch JSON data from the API
                    String jsonData = googleBooksAPI.fetchBooksData(query);
                    JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
                    JsonArray items = jsonObject.has("items") ? jsonObject.getAsJsonArray("items") : null;

                    // If no books found, exit early
                    if (items == null || items.isEmpty()) return null;

                    // Clear previous search results and populate new results
                    searchResults.clear();

                    // Loop through each book item in the JSON response
                    for (JsonElement item : items) {
                        JsonObject volumeInfo = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                        // Extract book details from the JSON data
                        String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
                        String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                        String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
                        String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
                        String category = "Unknown";
                        if (volumeInfo.has("categories") && volumeInfo.get("categories").isJsonArray() && volumeInfo.getAsJsonArray("categories").size() > 0) {
                            category = volumeInfo.getAsJsonArray("categories").get(0).getAsString();
                        }
                        String language = volumeInfo.has("language") ? volumeInfo.get("language").getAsString() : "Unknown";
                        String imageLink = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                                ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                                : null;

                        // Create a Book object and add it to the search results list
                        Book book = new Book(title, authors, publisher, publishedDate, imageLink, category, language);
                        searchResults.add(book);
                    }
                } catch (Exception e) {
                    e.printStackTrace();  // Log any exceptions
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // Once the search is complete, update the UI with the new search results
                loadSearchResults();
            }

            @Override
            protected void failed() {
                super.failed();
                // Show an error alert if the search operation fails
                showAlert(Alert.AlertType.ERROR, "Search Error", "Error occurred while searching for books.");
            }
        };

        // Execute the search task in a new thread (background operation)
        Thread searchBooksThread = new Thread(searchBooksTask);
        searchBooksThread.setDaemon(true); // Make it a daemon thread so it doesn't block application exit
        searchBooksThread.start();
    }

    @FXML
    private void saveSelectedBook() {
        // Attempt to save the selected book to the database
        Book selectedBook = searchBookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Save Book", "Please select a book to save.");
            return;
        }

        // Perform the save operation asynchronously using a background task
        Task<Void> saveBookTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Insert the selected book into the database
                boolean isSaved = bookDAO.insertBook(selectedBook);
                if (!isSaved) {
                    throw new Exception("Failed to save the book.");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                showAlert(Alert.AlertType.INFORMATION, "Save Book", "Book saved successfully!");
            }

            @Override
            protected void failed() {
                super.failed();
                showAlert(Alert.AlertType.ERROR, "Save Book", "Failed to save the book.");
            }
        };

        // Execute the save task in a new thread
        Thread saveBookThread = new Thread(saveBookTask);
        saveBookThread.setDaemon(true); // Daemon thread so it doesn't block app exit
        saveBookThread.start();
    }

    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == BookLibrary_btn) {
                applySceneTransition(BookLibrary_btn, "/fxml/MemberView.fxml");
            } else if (event.getSource() == DashBoardUser_btn) {
                applySceneTransition(DashBoardUser_btn, "/fxml/DashBoardUser.fxml");
            }
        } catch (Exception e) {
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
