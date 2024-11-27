package Animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * The {@code ColorTransitionExample} class provides a utility method to add a smooth
 * color transition effect to an {@link AnchorPane}. The background of the pane cycles
 * through multiple colors in a continuous animation.
 */
public class ColorTransitionExample {

    /**
     * Adds a color transition animation to the background of the specified {@link AnchorPane}.
     * The animation cycles through a sequence of colors and repeats indefinitely with auto-reverse.
     *
     * @param region The {@link AnchorPane} to which the color transition animation will be applied.
     *               A {@link Rectangle} is added as the background for the animation.
     */
    public static void addColorTransition(AnchorPane region) {
        // Create a Rectangle to serve as the background
        Rectangle background = new Rectangle(region.getWidth(), region.getHeight());
        background.setFill(Color.web("#A8D5BA")); // Initial color
        region.getChildren().add(0, background);

        // Synchronize the size of the Rectangle with the AnchorPane's dimensions
        region.widthProperty().addListener((obs, oldWidth, newWidth) -> background.setWidth(newWidth.doubleValue()));
        region.heightProperty().addListener((obs, oldHeight, newHeight) -> background.setHeight(newHeight.doubleValue()));

        // Create a Timeline for the color transition
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(background.fillProperty(), Color.web("#A8D5BA"))), // Green
                new KeyFrame(Duration.seconds(3), new KeyValue(background.fillProperty(), Color.web("#FFD97D"))), // Yellow
                new KeyFrame(Duration.seconds(6), new KeyValue(background.fillProperty(), Color.web("#85C7F2"))), // Blue
                new KeyFrame(Duration.seconds(9), new KeyValue(background.fillProperty(), Color.web("#FFABAB")))  // Pink
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.setAutoReverse(true); // Reverse colors
        timeline.play(); // Start the animation
    }
}
