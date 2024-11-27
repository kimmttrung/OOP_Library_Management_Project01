package Controller.Admin;

import API.GoogleBooksAPI;
import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import Entity.Book;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Optional;

import static Tools.AlertHelper.showAlert;
import static Tools.AlertHelper.showConfirmationAlert;
import static Animation.ColorTransitionExample.addColorTransition;

/**
 * Controller class for handling the search functionality using the Google Books API in the admin dashboard.
 * Allows users to search for books, view book details, and save selected books to the database.
 */
public class SearchAPI extends BaseDashBoardControl {
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
    private Button close_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private TableColumn<?, ?> publishedDateColumn;
    @FXML
    private TableColumn<?, ?> publisherColumn;
    @FXML
    private Button signOut_btn;
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
    @FXML
    private AnchorPane searchAPI_from;

    private final BookDAO bookDAO = new BookDAO();
    private final ObservableList<Book> searchResults = FXCollections.observableArrayList();

    /**
     * Initializes the UI components, sets up the bar chart, table columns, and navigation panel.
     */
    @FXML
    public void initialize() {
        nav_from.setTranslateX(-335);
        // Set initial UI state for navigation panel and buttons
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);

        // Set up the table columns, listener for book selection, and load initial search results
        setUpTableColumns();
        setUpBookSelectionListener();
        setUpChart();
        loadSearchResults();
        addColorTransition(searchAPI_from);
    }

    /**
     * Sets up the table columns for displaying book information.
     */
    private void setUpTableColumns() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
    }

    /**
     * Adds a listener to handle book selection from the table and update the UI with book details.
     */
    private void setUpBookSelectionListener() {
        // Add a listener for when a book is selected from the table
        searchBookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                bookImageView.setImage(new Image(getClass().getResource("/image/defaultBook.png").toExternalForm()));
                return;
            }

            // If a book is selected, update the book details in the UI
            titleLabel.setText(newSelection.getName());
            authorLabel.setText(newSelection.getAuthor());
            publisherLabel.setText(newSelection.getPublisher());
            categoriesLabel.setText(newSelection.getCategory());

            // Set the image of the selected book, or default if not available
            String imageLink = newSelection.getImage();
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });
    }

    private void setUpChart() {
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
    }

    /**
     * Loads the search results into the table view.
     */
    private void loadSearchResults() {
        // Populate the table with the list of search results
        searchBookTable.setItems(FXCollections.observableArrayList(searchResults));
    }

    /**
     * Handles the book search operation triggered by the user.
     */
    @FXML
    private void searchBook() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Book", "Please enter a search query.");
            return;
        }
        // Proceed with searching books based on the query and update the results
        searchBooks(query);
        loadSearchResults();
    }

    /**
     * Performs the search operation asynchronously using the Google Books API.
     * @param query The search query entered by the user.
     */
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
                        String imageLink = volumeInfo.has("imageLinks") && volumeInfo.getAsJsonObject("imageLinks").has("thumbnail")
                                ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                                : null;

                        Book book = new Book(title, authors, publisher, publishedDate, imageLink, category);
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

    /**
     * Saves the selected book to the database.
     */
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

    /**
     * Handles navigation between different pages in the application.
     *
     * @param event The triggered ActionEvent.
     */
    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == bookAll_btn) {
                applySceneTransition(bookAll_btn, "/fxml/Admin/BookView.fxml");
            } else if (event.getSource() == dashBoard_btn) {
                applySceneTransition(dashBoard_btn, "/fxml/Admin/DashBoardView.fxml");
            } else if (event.getSource() == borrowerBook_btn) {
                applySceneTransition(borrowerBook_btn, "/fxml/Admin/BorrowerView.fxml");
            } else if (event.getSource() == userAll_btn) {
                applySceneTransition(userAll_btn, "/fxml/Admin/UserView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application with a fade-out animation.
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

    /**
     * Animates and hides the navigation bar.
     */
    public void sliderArrow() {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.5), nav_from);
        slide.setToX(-320);
        slide.setOnFinished(event -> {
            bars_btn.setVisible(true);
            arrow_btn.setVisible(false);
        });
        slide.play();
    }

    public void sliderBars() {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.5), nav_from);
        slide.setToX(0);
        slide.setOnFinished(event -> {
            arrow_btn.setVisible(true);
            bars_btn.setVisible(false);
        });
        slide.play();
    }
}