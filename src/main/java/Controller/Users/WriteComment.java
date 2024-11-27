package Controller.Users;

import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import Entity.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static Tools.AlertHelper.showAlert;

/**
 * The WriteComment class allows users to add comments to books in the system.
 * This class extends the BaseDashBoardControl to utilize its scene transition functionality.
 */
public class WriteComment extends BaseDashBoardControl {
    // UI elements defined in the FXML
    @FXML
    private Button cancel_btn;
    @FXML
    private TextField bookIDField;
    @FXML
    private TextField commentField;

    // DAO object for database operations related to books
    BookDAO bookDAO = new BookDAO();

    /**
     * Handles the action of writing a comment for a book.
     * Validates user input, checks if the book exists, and adds the comment to the database.
     * Displays appropriate alerts for success or error conditions.
     */
    @FXML
    private void writeComment() {
        try {
            // Retrieve input values
            String comment = commentField.getText();
            String bookIDText = bookIDField.getText();

            // Validate the comment field
            if (comment == null || comment.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Comment", "Comment is empty.");
                return;
            }

            // Validate the book ID field
            if (bookIDText == null || bookIDText.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Comment", "Book ID is empty.");
                return;
            }

            // Parse the book ID
            Integer bookID = Integer.parseInt(bookIDText);

            // Check if the book exists in the database
            Book book = bookDAO.getBookByID(bookID);
            if (book == null) {
                showAlert(Alert.AlertType.ERROR, "Comment", "Book not found.");
                return;
            }

            // Add the comment to the database
            bookDAO.addComment(bookID, comment);
            showAlert(Alert.AlertType.INFORMATION, "Comment", "Comment added successfully!");

            // Clear the input fields after successful addition
            commentField.clear();
            bookIDField.clear();

        } catch (NumberFormatException e) {
            // Handle invalid book ID input
            showAlert(Alert.AlertType.ERROR, "Comment", "Invalid Book ID. Please enter a valid number.");
        } catch (Exception e) {
            // Handle any unexpected errors
            showAlert(Alert.AlertType.ERROR, "Comment", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Overrides the applySceneTransition method to handle hiding the current stage
     * without performing any additional animations or transitions.
     *
     * @param sourceButton the button triggering the scene transition.
     * @param fxmlPath     the path to the FXML file for the new scene.
     */
    @Override
    protected void applySceneTransition(Button sourceButton, String fxmlPath) {
        Stage currentStage = (Stage) sourceButton.getScene().getWindow();
        currentStage.hide();
    }

    /**
     * Handles the action for downloading pages or navigating back to the MemberView scene.
     *
     * @param event the action event triggered by the user's interaction.
     */
    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == cancel_btn) {
                // Navigate back to MemberView scene
                applySceneTransition(cancel_btn, "/fxml/Users/MemberView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
