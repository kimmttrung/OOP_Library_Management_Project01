package Controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

import static Controller.AlertHelper.showConfirmationAlert;

public class ReturnBookControl extends BaseDashBoardControl{
    @FXML
    private Button Back_btn;
    @FXML
    private TableColumn<?, ?> authorColumn;
    @FXML
    private TableView<?> bookTable;

    @FXML
    private Button close_btn;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private Button minus_btn;

    @FXML
    private TableColumn<?, ?> publishedDateColumn;

    @FXML
    private TableColumn<?, ?> publisherColumn;

    @FXML
    private TableColumn<?, ?> titleColumn;

    @FXML
    private Button signOut_btn;


    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == signOut_btn) {
                Optional<ButtonType> result = showConfirmationAlert("Confirm Exit", "Are you sure you want to exit?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    applySceneTransition(signOut_btn, "/fxml/LoginForm.fxml");
                }
            } else if (event.getSource() == Back_btn) {
                applySceneTransition(Back_btn, "/fxml/MemberView.fxml");
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
