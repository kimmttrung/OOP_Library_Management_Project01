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

/**
 * The BaseDashBoardControl class provides reusable methods for scene transitions
 * with animations in a JavaFX application. It includes functionality for fade
 * transitions and draggable, transparent windows.
 */
public class BaseDashBoardControl {
    // Coordinates for tracking mouse drag movements
    protected double x = 0;
    protected double y = 0;

    /**
     * Applies a fade-out transition to the current scene, loads a new FXML scene,
     * and displays it with a fade-in animation. The new scene is also draggable
     * and displayed in a transparent stage.
     *
     * @param sourceButton the button triggering the scene transition.
     * @param fxmlPath     the path to the FXML file for the new scene.
     */
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

                // Enable dragging for the new scene
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

                // Apply fade-in animation to the new scene
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

    /**
     * Loads a new FXML scene and displays it in a transparent stage without any
     * transitions. The new scene is draggable.
     *
     * @param sourceButton the button triggering the scene transition.
     * @param fxmlPath     the path to the FXML file for the new scene.
     */
    protected void applySceneTransition1(Button sourceButton, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene newScene = new Scene(root);
            Stage newStage = new Stage();

            // Enable dragging for the new scene
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
