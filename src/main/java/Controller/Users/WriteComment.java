package Controller.Users;

import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import Entity.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static Tools.AlertHelper.showAlert;

public class WriteComment extends BaseDashBoardControl {
    @FXML
    private Button cancel_btn;
    @FXML
    private TextField bookIDField;
    @FXML
    private TextField commentField;

    BookDAO bookDAO = new BookDAO();

    @FXML
    private void writeComment() {
        try {
            String comment = commentField.getText();
            String bookIDText = bookIDField.getText();

            if (comment == null || comment.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Comment", "Comment is empty.");
                return;
            }

            if (bookIDText == null || bookIDText.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Comment", "Book ID is empty.");
                return;
            }

            Integer bookID = Integer.parseInt(bookIDText);

            // Check if book exits
            Book book = bookDAO.getBookByID(bookID);
            if (book == null) {
                showAlert(Alert.AlertType.ERROR, "Comment", "Book not found.");
                return;
            }

            // Add comment to database
            bookDAO.addComment(bookID, comment);
            showAlert(Alert.AlertType.INFORMATION, "Comment", "Comment added successfully!");

            commentField.clear();
            bookIDField.clear();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Comment", "Invalid Book ID. Please enter a valid number.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Comment", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void applySceneTransition(Button sourceButton, String fxmlPath) {
        Stage currentStage = (Stage) sourceButton.getScene().getWindow();
        currentStage.hide();
    }

    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == cancel_btn) {
                applySceneTransition(cancel_btn, "/fxml/Users/MemberView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
