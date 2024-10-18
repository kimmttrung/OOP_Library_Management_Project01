package Controller;

import Entity.Book;
import Entity.BookDAO;
import Entity.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class BookController {

    @FXML
    private TextField bookIDField;

    @FXML
    private TextField bookAuthorField;

    @FXML
    private TextField bookPublisherField;

    @FXML
    private TextField bookTitleField;

    @FXML
    private TextField bookYearField;

    public void updateBook() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Update Book");
        alert.setHeaderText(null);

        if (bookTitleField.getText().isEmpty()) {
            alert.setContentText("Please enter Book's Name");
            alert.showAndWait();
            return;
        }
        if (bookAuthorField.getText().isEmpty()) {
            alert.setContentText("Please enter Author");
            alert.showAndWait();
            return;
        }
        if (bookPublisherField.getText().isEmpty()) {
            alert.setContentText("Please enter Publisher");
            alert.showAndWait();
            return;
        }
        if (bookYearField.getText().isEmpty()) {
            alert.setContentText("Please enter Year");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Update");
        confirmAlert.setHeaderText("Are you sure you want to update the book?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String bookName = bookTitleField.getText();
            String bookAuthor = bookAuthorField.getText();
            String bookPublisher = bookPublisherField.getText();
            String bookYear = bookYearField.getText();

            Book newBook = new Book(bookName, bookAuthor, bookPublisher, bookYear, null);
            BookDAO bookDAO = new BookDAO();

            if (bookDAO.insertBook(newBook)) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Book updated successfully!");
                successAlert.showAndWait();
            } else {
                alert.setContentText("Error updating book. Please try again.");
                alert.showAndWait();
            }
        }
    }

    public void deleteBook() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Delete Book");
        alert.setHeaderText(null);

        if (bookIDField.getText().isEmpty()) {
            alert.setContentText("Please enter BookID you want to delete");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this Book?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int bookId = Integer.parseInt(bookIDField.getText());

            BookDAO bookDAO = new BookDAO();

            if (bookDAO.deleteBook(bookId)) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Delete book successfully!");
                successAlert.showAndWait();
            } else {
                alert.setContentText("Error deleting book. Please try again later!.");
                alert.showAndWait();
            }
        }
    }

}
