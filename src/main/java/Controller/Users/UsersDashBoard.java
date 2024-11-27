package Controller.Users;

import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import Database.DataBase;
import Entity.Book;
import Singleton.Session;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static Tools.AlertHelper.showConfirmationAlert;

/**
 * The UsersDashBoard class represents the main dashboard for a user.
 * It includes functionalities such as displaying a user profile,
 * loading frequently borrowed books, generating a bar chart of book categories,
 * and navigating to various sections of the application.
 */
public class UsersDashBoard extends BaseDashBoardControl {

    // FXML UI elements
    @FXML
    private Button close_btn;
    @FXML
    private Button minus_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button BookLibrary_btn;
    @FXML
    private Button SerachAPIUser_btn;
    @FXML
    private Button edit_btn;
    @FXML
    private Label UID;
    @FXML
    private BarChart<String, Double> chartUser;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView myImageView1, myImageView2, myImageView3, myImageView4, myImageView5;
    @FXML
    private ImageView myImageView6, myImageView7, myImageView8, myImageView9, myImageView10;

    private final Connection connection = DataBase.getInstance().getConnection();

    // DAO instance for book operations
    private final BookDAO bookDAO = new BookDAO();

    /**
     * Initializes the dashboard by setting user information, chart data,
     * profile images, and a smooth scrolling effect for the image carousel.
     */
    public void initialize() {
        setImageView();
        setChartUser();
        UID.setText("UID: " + Session.getInstance().getUserID());

        // Smooth scrolling effect for the scroll pane
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(20), e -> scroll())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Configures and populates the bar chart with sample data representing book categories and popularity.
     */
    private void setChartUser() {
        XYChart.Series<String, Double> series2 = new XYChart.Series<>();
        series2.setName("Happy New Year 2027");
        series2.getData().add(new XYChart.Data<>("Mystery", 500.0));
        series2.getData().add(new XYChart.Data<>("Sport", 300.0));
        series2.getData().add(new XYChart.Data<>("History", 200.0));
        series2.getData().add(new XYChart.Data<>("Poetry", 400.0));
        series2.getData().add(new XYChart.Data<>("Health", 700.0));
        series2.getData().add(new XYChart.Data<>("Romance", 100.0));
        series2.getData().add(new XYChart.Data<>("Biography", 150.0));
        series2.getData().add(new XYChart.Data<>("Python", 250.0));
        chartUser.getData().add(series2);
    }

    /**
     * Loads user profile images from the database and populates the ImageViews in the dashboard.
     * Uses a background thread to fetch data and updates the UI upon completion.
     */
    private void setImageView() {
        Task<List<String>> loadImagesTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return getImageLinksFromDatabase();
            }
        };

        loadImagesTask.setOnSucceeded(event -> {
            List<ImageView> imageViews = Arrays.asList(
                    myImageView1, myImageView2, myImageView3, myImageView4, myImageView5,
                    myImageView6, myImageView7, myImageView8, myImageView9, myImageView10
            );
            loadImagesToImageViews(imageViews);
        });

        new Thread(loadImagesTask).start();
    }

    /**
     * Loads images into the specified list of ImageViews using URLs retrieved from the database.
     *
     * @param imageViews the list of ImageViews to populate with images.
     */
    public void loadImagesToImageViews(List<ImageView> imageViews) {
        List<String> imageLinks = getImageLinksFromDatabase();

        for (int i = 0; i < imageViews.size(); i++) {
            if (i < imageLinks.size()) {
                String imageLink = imageLinks.get(i);
                Image image = new Image(imageLink, true);
                imageViews.get(i).setImage(image);
            } else {
                break;
            }
        }
    }

    /**
     * Fetches a list of image URLs from the database based on the most borrowed books.
     *
     * @return a list of image URLs.
     */
    private List<String> getImageLinksFromDatabase() {
        List<String> imageLinks = new ArrayList<>();
        Book book = new Book();

        try {
            String sql = "SELECT book_id FROM borrowers GROUP BY book_id ORDER BY COUNT(*) DESC";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    String bookId = resultSet.getString("book_id");
                    book = bookDAO.getBookByID(Integer.parseInt(bookId));
                    String imageUrl = book.getImage();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        imageLinks.add(imageUrl);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageLinks;
    }

    /**
     * Implements a smooth horizontal scrolling effect for the ScrollPane.
     */
    private void scroll() {
        double hValue = scrollPane.getHvalue();
        double increment = 0.001;

        if (hValue < 1) {
            scrollPane.setHvalue(hValue + increment);
        } else {
            scrollPane.setHvalue(0);
        }
    }

    /**
     * Handles navigation and sign-out actions based on button clicks.
     *
     * @param event the action event triggered by the user.
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
            } else if (event.getSource() == SerachAPIUser_btn) {
                applySceneTransition(SerachAPIUser_btn, "/fxml/Users/SearchAPIUser.fxml");
            } else if (event.getSource() == edit_btn) {
                applySceneTransition1(edit_btn, "/fxml/Users/EditInfor.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the application with a fade-out and scale-down transition.
     */
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

    /**
     * Minimizes the application window.
     */
    public void minimize() {
        Stage stage = (Stage) minus_btn.getScene().getWindow();
        stage.setIconified(true);
    }
}
