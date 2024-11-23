package Animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ColorTransitionExample {
    public static void addColorTransition(AnchorPane region) {
        // Tạo Rectangle làm nền
        Rectangle background = new Rectangle(region.getWidth(), region.getHeight());
        background.setFill(Color.web("#A8D5BA")); // Màu bắt đầu
        region.getChildren().add(0, background);

        // Đồng bộ kích thước của Rectangle với AnchorPane
        region.widthProperty().addListener((obs, oldWidth, newWidth) -> background.setWidth(newWidth.doubleValue()));
        region.heightProperty().addListener((obs, oldHeight, newHeight) -> background.setHeight(newHeight.doubleValue()));

        // Tạo Timeline để chuyển màu
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(background.fillProperty(), Color.web("#A8D5BA"))), // Xanh lá
                new KeyFrame(Duration.seconds(3), new KeyValue(background.fillProperty(), Color.web("#FFD97D"))), // Vàng
                new KeyFrame(Duration.seconds(6), new KeyValue(background.fillProperty(), Color.web("#85C7F2"))), // Xanh dương
                new KeyFrame(Duration.seconds(9), new KeyValue(background.fillProperty(), Color.web("#FFABAB")))  // Hồng
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Lặp vô hạn
        timeline.setAutoReverse(true); // Quay ngược màu
        timeline.play(); // Chạy hiệu ứng
    }
}
