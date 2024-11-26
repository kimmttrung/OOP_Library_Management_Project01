package Controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class BaseDashBoardControl {
    protected double x = 0;
    protected double y = 0;

    // Phương thức chung cho chuyển cảnh
    protected void applySceneTransition(Button sourceButton, String fxmlPath) {
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

    protected void applySceneTransition1(Button sourceButton, String fxmlPath) {
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

            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

