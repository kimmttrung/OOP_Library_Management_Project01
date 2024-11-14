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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

//implements Initializable

public class DashBoardControl  {

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
    private AnchorPane issue_from;

    @FXML
    private AnchorPane save_from;

    @FXML
    private AnchorPane return_from;

    @FXML
    private TextField take_FirstName;

    @FXML
    private TextField take_LastName;

    @FXML
    private Label take_author_label;

    @FXML
    private TextField take_bookTitle;

    @FXML
    private Button take_clearBtn;

    @FXML
    private ComboBox<?> take_gender;

    @FXML
    private Label take_genre_label;

    @FXML
    private ImageView take_imageView;

    @FXML
    private Label take_issuedDate;

    @FXML
    private Label take_save_label;

    @FXML
    private Button take_takeBtn;

    @FXML
    private Label take_title_label;
    @FXML
    private Button signOut_btn1;
    @FXML
    private Button dardBord_btn;
    @FXML
    private AnchorPane dardBord_from;
    @FXML
    private Button dardBordIn_btn;
    @FXML
    private Button dardBordOut_btn;
    @FXML
    private Button halfNav_dardBord_btn;

    //

    @FXML
    private Button bookAll_btn;

    @FXML
    private Button dashBoard_btn;

    @FXML
    private Button minus_btn;

    @FXML
    private AnchorPane nav_from;

    @FXML
    private Button searchAPI_btn;

    @FXML
    private Button userAll_btn;

    @FXML
    private Button bookAll_dashBoard_btn;

    @FXML
    private Button searchAPI_dashBoard_btn;

    @FXML
    private Button userAll_dashBoard_btn;

    @FXML
    private AnchorPane main_from;



    private BookControl bookControl = new BookControl();
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private String comBox[] = {"Male", "Female", "Orther"};

    @FXML
    void circle_image(MouseEvent event) {
    }

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

//    public void dardBordView(ActionEvent event){
//        if(event.getSource() == dardBordIn_btn) {
//            dardBord_from.setVisible(true);
//            dardBordIn_btn.setVisible(false);
//            dardBordOut_btn.setVisible(true);
//        }
//        if (event.getSource() == dardBordOut_btn) {
//            dardBord_from.setVisible(false);
//            dardBordOut_btn.setVisible(false);
//            dardBordIn_btn.setVisible(true);
//        }
//    }


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

    public void gender() {
        List<String> combo = new ArrayList<>();

        for (String data : comBox) {
            combo.add(data);
        }
        ObservableList list = FXCollections.observableArrayList(combo);

        take_gender.setItems(list);


    }

    public void abTakeButton(ActionEvent event) {

        if (event.getSource() == take_btn) {
            issue_from.setVisible(true);
            mainCenter_form.setVisible(false);
            return_from.setVisible(false);
            save_from.setVisible(false);
            dardBord_from.setVisible(false);

            issueBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            availableBook_btnn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_takeBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_availableBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
        }

    }

    public void navButtonDesign(ActionEvent event) {
        if(event.getSource() == dardBord_btn || event.getSource() == halfNav_dardBord_btn) {
            issue_from.setVisible(false);
            mainCenter_form.setVisible(false);
            return_from.setVisible(false);
            save_from.setVisible(false);
            dardBord_from.setVisible(true);
            dardBordIn_btn.setVisible(false);
            dardBordOut_btn.setVisible(true);


            dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            issueBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            availableBook_btnn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_takeBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_availableBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
        }

        if (event.getSource() == availableBook_btnn ) {

            mainCenter_form.setVisible(true);
            issue_from.setVisible(false);
            return_from.setVisible(false);
            save_from.setVisible(false);
            dardBord_from.setVisible(false);

            availableBook_btnn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            issueBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_availableBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_takeBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");

        } else if (event.getSource() == issueBooks_btn ) {

            mainCenter_form.setVisible(false);
            issue_from.setVisible(true);
            return_from.setVisible(false);
            save_from.setVisible(false);
            dardBord_from.setVisible(false);

            issueBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            availableBook_btnn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_takeBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_availableBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");


        } else if (event.getSource() == returnBooks_btn || event.getSource() == halfNav_returnBtn) {

            mainCenter_form.setVisible(false);
            issue_from.setVisible(false);
            return_from.setVisible(true);
            save_from.setVisible(false);
            dardBord_from.setVisible(false);

            returnBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            issueBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            availableBook_btnn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_returnBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_availableBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_takeBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");


        } else if (event.getSource() == savedBooks_btn || event.getSource() == halfNav_saveBtn) {

            mainCenter_form.setVisible(false);
            issue_from.setVisible(false);
            return_from.setVisible(false);
            save_from.setVisible(true);
            dardBord_from.setVisible(false);

            savedBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            issueBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooks_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            availableBook_btnn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_saveBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_availableBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_takeBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_dardBord_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #344275, #3a6389);");


        }
    }

//    @Override
//    public void initialize(URL location, ResourceBundle resources){
//        setUpTableColumns();
//        setUpBookSelectionListener();
//        loadBooks();
//        gender();
//    }

    // SOWN DATA BOOK
//    private void setUpTableColumns() {
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
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