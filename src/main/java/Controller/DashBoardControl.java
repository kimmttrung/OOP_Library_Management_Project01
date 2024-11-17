package Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Binding;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import management.libarymanagement.DataBase;

import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashBoardControl  {

    @FXML
    private PieChart pieChart;
    @FXML
    private Button borrowerDashBoard_btn;
    @FXML
    private Button borrowerBook_btn;
    @FXML
    private Button bars_btn;
    @FXML
    private Button arrow_btn;
    @FXML
    private Button signOut_btn;
    @FXML
    private ComboBox<?> take_gender;
    @FXML
    private Button bookAll_btn;
    @FXML
    private Button bookAll_dashBoard_btn;
    @FXML
    private Button minus_btn;
    @FXML
    private AnchorPane nav_from;
    @FXML
    private Button searchAPI_btn;
    @FXML
    private Button userAll_btn;
    @FXML
    private Button userAll_dashBoard_btn;
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
    private ImageView myImageView1, myImageView2, myImageView3, myImageView4, myImageView5;
    @FXML
    private ImageView myImageView6, myImageView7, myImageView8, myImageView9, myImageView10;

    private String comBox[] = {"Male", "Female", "Orther"};

    private double x = 0;
    private double y = 0;

    @FXML
    public void initialize() {
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
                Image image = new Image(imageLink);
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

    @FXML
    public void DownloadPages(ActionEvent event){
        try{
            if (event.getSource() == signOut_btn){
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/loginForm.fxml"));
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
            } else if (event.getSource() == borrowerBook_btn || event.getSource() == borrowerDashBoard_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/Borrower.fxml"));
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
                borrowerBook_btn.getScene().getWindow().hide();
            } else if (event.getSource() == userAll_btn || event.getSource() == userAll_dashBoard_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/userBook.fxml"));
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
                userAll_btn.getScene().getWindow().hide();
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

    public void updateCounts() {
        int bookCount = DataBase.getCount("books");
        int userCount = DataBase.getCount("user");
        int borrowerCount = DataBase.getCount("borrower");

        bookCountLabel.setText("" + bookCount);
        userCountLabel.setText("" + userCount);
        borrowerCountLabel.setText("" + borrowerCount);
    }
}