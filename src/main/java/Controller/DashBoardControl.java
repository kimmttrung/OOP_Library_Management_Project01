package Controller;

import DataAccessObject.BookDAO;
import DataAccessObject.SearchBooks;
import Entity.Book;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashBoardControl implements Initializable {

    @FXML
    private Button bars_btn;
    @FXML
    private Button arrow_btn;
    @FXML
    private ImageView availableBook_btn;
    @FXML
    private Button close_btn;
    @FXML
    private Button find_btn;
    @FXML
    private Button availableBook_btnn;
    @FXML
    private Button issueBooks_btn;
    @FXML
    private Button minimize;
    @FXML
    private Button returnBooks_btn;
    @FXML
    private Button save_btn;
    @FXML
    private Button savedBooks_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button take_btn;
    @FXML
    private ImageView bookImageView;
    @FXML
    private TextField searchField;
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
    private TextField bookTitleField;
    @FXML
    private TextField bookAuthorField;
    @FXML
    private TextField bookYearField;
    @FXML
    private TextField bookPublisherField;
    @FXML
    private TextField bookIDField;
    @FXML
    private AnchorPane nav_form;
    @FXML
    private AnchorPane mainCenter_form;
    @FXML
    private Button halfNav_availableBtn;

    @FXML
    private AnchorPane halfNav_form;

    @FXML
    private Button halfNav_returnBtn;

    @FXML
    private Button halfNav_saveBtn;

    @FXML
    private Button halfNav_takeBtn;

    @FXML
    private Circle smallCircle_image;

    @FXML
    private AnchorPane valueBook;

    private BookControl bookControl = new BookControl();
    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    void circle_image(MouseEvent event) {
    }

    private double x = 0;
    private double y = 0;

    @FXML
    public void logout(ActionEvent event){
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void exit(){
        System.exit(0);
    }

    public void minimize(){
        Stage stage = (Stage)minimize.getScene().getWindow();
        stage.setIconified(true);

    }
    public void sliderArrow() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(nav_form);
        slide.setToX(-287);

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(.5));
        slide1.setNode(mainCenter_form);
        slide1.setToX(-140);

        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(.5));
        slide2.setNode(halfNav_form);
        slide2.setToX(0);

        slide.setOnFinished((ActionEvent event) -> {
            arrow_btn.setVisible(false);
            bars_btn.setVisible(true);
        });

        slide2.play();
        slide1.play();
        slide.play();
    }
    public void sliderBars() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(nav_form);
        slide.setToX(0);

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(.5));
        slide1.setNode(mainCenter_form);
        slide1.setToX(0);

        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(.5));
        slide2.setNode(halfNav_form);
        slide2.setToX(-135);


        slide.setOnFinished((ActionEvent event) -> {
            arrow_btn.setVisible(true);
            bars_btn.setVisible(false);
        });

        slide2.play();
        slide1.play();
        slide.play();

    }

    public void avalueBooks1() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(mainCenter_form);
        slide.setToX(-1500);

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(.5));
        slide1.setNode(valueBook);
        slide1.setToX(0);

        slide.setOnFinished((ActionEvent event) -> {
            availableBook_btnn.setVisible(true);
            halfNav_availableBtn.setVisible(true);
        });

        slide1.play();
        slide.play();
    }
    public void avalueBooks2() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(mainCenter_form);
        slide.setToX(0);

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(.5));
        slide1.setNode(valueBook);
        slide1.setToX(-1500);

        slide.setOnFinished((ActionEvent event) -> {
            issueBooks_btn.setVisible(true);
            halfNav_takeBtn.setVisible(true);
        });

        slide1.play();
        slide.play();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources){
        setUpTableColumns();
        setUpBookSelectionListener();
        loadBooks();
    }

    private void setUpTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
    }

    private void setUpBookSelectionListener() {
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            String imageLink = (newSelection != null) ? newSelection.getImage() : null;
            Image image = (imageLink != null && !imageLink.isEmpty())
                    ? new Image(imageLink)
                    : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
            bookImageView.setImage(image);
        });
    }

    @FXML
    private void loadBooks() {
        bookList = FXCollections.observableArrayList(bookControl.getAllBooks());
        bookTable.setItems(bookList);
    }

    @FXML
    private void searchBook() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Book", "Please enter a search query.");
            return;
        }

        bookControl.searchBooks(query);
        loadSearchResults();
    }

    private void loadSearchResults() {
        bookList = FXCollections.observableArrayList(bookControl.getSearchResults());
        bookTable.setItems(bookList);
    }

    @FXML
    private void updateBook() {
        if (isAnyFieldEmpty(bookTitleField, bookAuthorField, bookPublisherField, bookYearField)) {
            showAlert(Alert.AlertType.ERROR, "Update Book", "All fields are required.");
            return;
        }

        Optional<ButtonType> result = showConfirmationAlert("Confirm Update", "Are you sure you want to update the book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Book newBook = new Book(
                    bookTitleField.getText(),
                    bookAuthorField.getText(),
                    bookPublisherField.getText(),
                    bookYearField.getText(),
                    null
            );

            if (bookControl.updateBook(newBook)) {
                loadBooks();  // Reload books in the table view
                showAlert(Alert.AlertType.INFORMATION, "Update Success", "Book updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "Error updating book. Please try again.");
            }
        }
    }

    @FXML
    private void deleteBook() {
        if (bookIDField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Delete Book", "Please enter Book ID you want to delete.");
            return;
        }
        
        Optional<ButtonType> result = showConfirmationAlert("Confirm Delete", "Are you sure you want to delete this Book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int bookId = Integer.parseInt(bookIDField.getText());
            if (bookControl.deleteBook(bookId)) {
                loadBooks();  // Reload books in the table view
                showAlert(Alert.AlertType.INFORMATION, "Delete Success", "Book deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Error", "Error deleting book. Please try again.");
            }
        }
    }

    private boolean isAnyFieldEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) return true;
        }
        return false;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}