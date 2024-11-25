package Controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Optional;

import static Controller.AlertHelper.showConfirmationAlert;

public class DashBoardUserControl extends BaseDashBoardControl{

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
    private BarChart<String, Double> chartUser;
    @FXML
    private Button backRight_btn;

    public void initialize() {
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
            } else if (event.getSource() == SerachAPIUser_btn) {
                applySceneTransition(SerachAPIUser_btn, "/fxml/SearchAPIUser.fxml");
            } else if (event.getSource() == backRight_btn) {
                applySceneTransition(backRight_btn, "/fxml/DashBoardView.fxml");
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
