package Controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import Database.DataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static Controller.AlertHelper.showConfirmationAlert;

public class DashBoardControl  {

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
    private AnchorPane nav_from;
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

    @FXML
    public void initialize() {
        setUpInit();
        updateCounts();
        setImageView();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(20), e -> scroll())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setImageView() {
        Image image = new Image(getClass().getResource("/image/profile.png").toExternalForm());
        circleProfile.setFill(new ImagePattern(image));

        List<ImageView> imageViews = Arrays.asList(
                myImageView1, myImageView2, myImageView3, myImageView4, myImageView5,
                myImageView6, myImageView7, myImageView8, myImageView9, myImageView10
        );

        loadImagesToImageViews(imageViews);
    }

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

    private List<String> getImageLinksFromDatabase() {
        List<String> imageLinks = new ArrayList<>();

        try (Connection connection = DataBase.getConnection()) {
            String sql = "SELECT image FROM books";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    String imageUrl = resultSet.getString("image");
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

    private void scroll() {
        double hValue = scrollPane.getHvalue();
        double increment = 0.001;

        if (hValue < 1) {
            scrollPane.setHvalue(hValue + increment);
        } else {
            scrollPane.setHvalue(0);
        }
    }

    private void setUpInit() {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Science", 40),
                        new PieChart.Data("History", 30),
                        new PieChart.Data("Technology", 20),
                        new PieChart.Data("Sports", 70)
                );
        pieChartData.forEach(data -> data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), ": ", data.pieValueProperty()
                        )
                )
        );

        pieChart.getData().addAll(pieChartData);

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
        int userCount = DataBase.getCount("user");
        int borrowerCount = DataBase.getCount("borrower");

        bookCountLabel.setText("" + bookCount);
        userCountLabel.setText("" + userCount);
        borrowerCountLabel.setText("" + borrowerCount);
    }
}