package Tools;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * The {@code AlertHelper} class provides utility methods for displaying
 * various types of alerts in a JavaFX application. These methods simplify
 * the creation and display of alert dialogs for different scenarios such as
 * information, confirmation, and success messages.
 */
public class AlertHelper {

    /**
     * Displays a general alert dialog with the specified type, title, and message.
     *
     * @param alertType the type of the alert (e.g., {@link Alert.AlertType#INFORMATION},
     *                  {@link Alert.AlertType#WARNING}, {@link Alert.AlertType#ERROR})
     * @param title     the title of the alert dialog
     * @param message   the message content of the alert dialog
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation alert dialog with the specified title and message,
     * and waits for the user's response.
     *
     * @param title   the title of the confirmation dialog
     * @param message the message content of the confirmation dialog
     * @return an {@link Optional} containing the {@link ButtonType} of the user's response
     *         (e.g., {@link ButtonType#OK} or {@link ButtonType#CANCEL})
     */
    public static Optional<ButtonType> showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    /**
     * Displays a success alert dialog with the specified title and content message.
     * This method is specifically designed for showing success messages using the
     * {@link Alert.AlertType#INFORMATION} type.
     *
     * @param title   the title of the success alert
     * @param content the content message of the success alert
     */
    public static void showSuccessAlert(String title, String content) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(title);
        successAlert.setHeaderText(null);
        successAlert.setContentText(content);
        successAlert.showAndWait();
    }
}
