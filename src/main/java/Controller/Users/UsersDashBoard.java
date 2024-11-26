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

public class UsersDashBoard extends BaseDashBoardControl {

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

    BookDAO bookDAO = new BookDAO();


    public void initialize() {
        setImageView();
        setChartUser();
        UID.setText("UID: " + Session.getInstance().getUserID());

        // Create a timeline to implement smooth scrolling effect
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(20), e -> scroll())  // Scroll every 20 milliseconds
        );
        timeline.setCycleCount(Timeline.INDEFINITE);  // Repeat the scroll indefinitely
        timeline.play();
    }

    private void setChartUser() {
        XYChart.Series<String, Double> series2 = new XYChart.Series<>();
        series2.setName("Happy New Year 2027");
        series2.getData().add(new XYChart.Data("Mystery ", 500));
        series2.getData().add(new XYChart.Data("Sport ", 300));
        series2.getData().add(new XYChart.Data("History ", 200));
        series2.getData().add(new XYChart.Data("Poetry ", 400));
        series2.getData().add(new XYChart.Data("Health ", 700));
        series2.getData().add(new XYChart.Data("Romance ", 100));
        series2.getData().add(new XYChart.Data("Biography  ", 150));
        series2.getData().add(new XYChart.Data("Python ", 250));
        chartUser.getData().add(series2);
    }

    private void setImageView() {
        // Set the profile image in the circle profile
        // Create  Task to load image links from the database in the background
        Task<List<String>> loadImagesTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return getImageLinksFromDatabase();  // Retrieve image URLs from the database
            }
        };

        // Once the Task is complete, load the images into the ImageViews
        loadImagesTask.setOnSucceeded(event -> {
            List<ImageView> imageViews = Arrays.asList(
                    myImageView1, myImageView2, myImageView3, myImageView4, myImageView5,
                    myImageView6, myImageView7, myImageView8, myImageView9, myImageView10
            );
            loadImagesToImageViews(imageViews);  // Load images into the ImageViews
        });

        // Start the background task on a new thread
        new Thread(loadImagesTask).start();
    }

    public void loadImagesToImageViews(List<ImageView> imageViews) {
        // Fetch image URLs from the database
        List<String> imageLinks = getImageLinksFromDatabase();

        // Loop through the ImageViews and load the images from the links
        for (int i = 0; i < imageViews.size(); i++) {
            if (i < imageLinks.size()) {
                String imageLink = imageLinks.get(i);
                Image image = new Image(imageLink, true);  // Load the image
                imageViews.get(i).setImage(image);  // Set the image in the corresponding ImageView
            } else {
                break;
            }
        }
    }

    private List<String> getImageLinksFromDatabase() {
        // Create a list to store the image URLs fetched from the database
        List<String> imageLinks = new ArrayList<>();
        Book book = new Book();

        try (Connection connection = DataBase.getConnection()) {
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

        return imageLinks;  // Return the list of image links
    }

    private void scroll() {
        // Get the current horizontal scroll position of the ScrollPane
        double hValue = scrollPane.getHvalue();
        double increment = 0.001;  // Scroll increment for smooth scrolling

        // If the scroll is not at the end, scroll by a small increment
        if (hValue < 1) {
            scrollPane.setHvalue(hValue + increment);
        } else {
            // If at the end, reset the scroll to the beginning
            scrollPane.setHvalue(0);
        }
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
