package Controller.Users;

import API.GoogleBooksAPI;
import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import Entity.Book;
import Singleton.Session;
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

import static Tools.AlertHelper.showAlert;
import static Tools.AlertHelper.showConfirmationAlert;

/**
 * Controller class responsible for managing the user interface and interactions for searching books through the Google Books API.
 * This includes displaying search results in a table, handling user actions like searching, saving selected books to the database,
 * and managing visual elements such as a bar chart and book images.
 */
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

    /**
     * Initializes the UI elements, sets up table columns, book selection listener, and loads the initial search results.
     */
    public void initialize() {
        setUpTableColumns();
        setUpBookSelectionListener();
        loadSearchResults();
        setUpChart();
        UID.setText("UID: " + Session.getInstance().getUserID());
    }

    /**
     * Sets up the columns in the book search results table.
     */
    private void setUpTableColumns() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
    }

    /**
     * Sets up a listener for when a book is selected from the search results table,
     * updating the displayed image of the selected book.
     */
    private void setUpBookSelectionListener() {
        searchBookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }
            String imageLink = newSelection.getImage();
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });
    }

    /**
     * Sets up the bar chart to display a summary of book categories.
     */
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

    /**
     * Loads the search results into the table.
     */
    private void loadSearchResults() {
        searchBookTable.setItems(FXCollections.observableArrayList(searchResults));
    }

    /**
     * Initiates a search for books based on the user's input query and updates the table with the results.
     */
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

    /**
     * Searches for books using the Google Books API in the background and updates the results.
     *
     * @param query the search query provided by the user.
     */
    private void searchBooks(String query) {
        Task<Void> searchBooksTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
                try {
                    String jsonData = googleBooksAPI.fetchBooksData(query);
                    JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
                    JsonArray items = jsonObject.has("items") ? jsonObject.getAsJsonArray("items") : null;
                    if (items == null || items.isEmpty()) return null;

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
                        String language = volumeInfo.has("language") ? volumeInfo.get("language").getAsString() : "Unknown";
                        String imageLink = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                                ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                                : null;

                        Book book = new Book(title, authors, publisher, publishedDate, imageLink, category, language);
                        searchResults.add(book);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                loadSearchResults();
            }

            @Override
            protected void failed() {
                super.failed();
                showAlert(Alert.AlertType.ERROR, "Search Error", "Error occurred while searching for books.");
            }
        };
        Thread searchBooksThread = new Thread(searchBooksTask);
        searchBooksThread.setDaemon(true);
        searchBooksThread.start();
    }

    /**
     * Saves the selected book from the search results to the database.
     */
    @FXML
    private void saveSelectedBook() {
        Book selectedBook = searchBookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Save Book", "Please select a book to save.");
            return;
        }

        Task<Void> saveBookTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
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
        Thread saveBookThread = new Thread(saveBookTask);
        saveBookThread.setDaemon(true);
        saveBookThread.start();
    }

    /**
     * Handles page navigation and sign-out actions based on the button clicked by the user.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == BookLibrary_btn) {
                applySceneTransition(BookLibrary_btn, "/fxml/Users/MemberView.fxml");
            } else if (event.getSource() == DashBoardUser_btn) {
                applySceneTransition(DashBoardUser_btn, "/fxml/Users/DashBoardUser.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application with a fade-out and scale-down animation.
     */
    public void exit() {
        Stage primaryStage = (Stage) close_btn.getScene().getWindow();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> Platform.exit());
        fadeOut.play();
    }

    /**
     * Minimizes the current application window.
     */
    public void minimize() {
        Stage stage = (Stage) minus_btn.getScene().getWindow();
        stage.setIconified(true);
    }
}
