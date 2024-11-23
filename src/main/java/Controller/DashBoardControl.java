package Controller;

import DataAccessObject.BookDAO;
import Entity.Book;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import Database.DataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static Controller.AlertHelper.showConfirmationAlert;
import static Animation.ColorTransitionExample.addColorTransition;

public class DashBoardControl extends BaseDashBoardControl {

    @FXML
    private Button backLeft_btn;
    @FXML
    private PieChart pieChart;
    @FXML
    private Button borrowerBook_btn;
    @FXML
    private Button bars_btn;
    @FXML
    private Button arrow_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private Button bookAll_btn;
    @FXML
    private Button minus_btn;
    @FXML
    private Button close_btn;
    @FXML
    private AnchorPane nav_from, dashBoardView_from;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button userAll_btn;
    @FXML
    private Label bookCountLabel;
    @FXML
    private Label userCountLabel;
    @FXML
    private Label borrowerCountLabel;
    @FXML
    private Circle circleProfile;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button bookAll_dashBoard_btn, borrowerDashBoard_btn, userAll_dashBoard_btn;
    @FXML
    private ImageView myImageView1, myImageView2, myImageView3, myImageView4, myImageView5;
    @FXML
    private ImageView myImageView6, myImageView7, myImageView8, myImageView9, myImageView10;

    private double x = 0;
    private double y = 0;

    BookDAO bookDAO = new BookDAO();

    @FXML
    public void initialize() {
        setUpInit();
        updateCounts();
        setImageView();
        addColorTransition(dashBoardView_from);

        // Create a timeline to implement smooth scrolling effect
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(20), e -> scroll())  // Scroll every 20 milliseconds
        );
        timeline.setCycleCount(Timeline.INDEFINITE);  // Repeat the scroll indefinitely
        timeline.play();
    }

    private void setImageView() {
        // Set the profile image in the circle profile
        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));

        // Create a Task to load image links from the database in the background
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

    private void setUpInit() {
        // Set up initial pie chart data for different categories
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Science", 40),
                        new PieChart.Data("History", 30),
                        new PieChart.Data("Technology", 20),
                        new PieChart.Data("Sports", 70)
                );

        pieChartData.forEach(data -> data.nameProperty().bind(
                Bindings.concat(
                        data.getName(), ": ", data.pieValueProperty()  // Bind name and value for display
                )
        ));

        pieChart.getData().addAll(pieChartData);

        // Set up the initial state for navigation and buttons
        nav_from.setTranslateX(-320);
        bars_btn.setVisible(true);
        arrow_btn.setVisible(false);
    }

    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == searchAPI_btn) {
                applySceneTransition(searchAPI_btn, "/fxml/SearchView.fxml");
            } else if (event.getSource() == bookAll_btn || event.getSource() == bookAll_dashBoard_btn) {
                applySceneTransition(bookAll_btn, "/fxml/BookView.fxml");
            } else if (event.getSource() == borrowerBook_btn || event.getSource() == borrowerDashBoard_btn) {
                applySceneTransition(borrowerBook_btn, "/fxml/BorrowerView.fxml");
            } else if (event.getSource() == userAll_btn || event.getSource() == userAll_dashBoard_btn) {
                applySceneTransition(userAll_btn, "/fxml/UserView.fxml");
            } else if (event.getSource() == backLeft_btn) {
                applySceneTransition(backLeft_btn, "/fxml/DashBoardUser.fxml");
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

    public void updateCounts() {
        int bookCount = DataBase.getCount("books");
        int userCount = DataBase.getCount("users");
        int borrowerCount = DataBase.getCount("borrowers");

        bookCountLabel.setText("" + bookCount);
        userCountLabel.setText("" + userCount);
        borrowerCountLabel.setText("" + borrowerCount);
    }
}