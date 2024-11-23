package Controller;

import DataAccessObject.BookDAO;
import DataAccessObject.BorrowerDAO;
import DataAccessObject.UserDAO;
import Entity.Book;
import Entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.util.Optional;

import static Controller.AlertHelper.showAlert;
import static Controller.AlertHelper.showConfirmationAlert;

public class MemberBorrowControl extends BaseDashBoardControl {
    @FXML
    private Button cancel_btn;
    @FXML
    private ImageView bookImageView;
    @FXML
    private TextField bookIDField;
    @FXML
    private Label nameLabel, bookNameLabel;
    @FXML
    private TextField borrowerIDField;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TextField commentField;

    private double x = 0;
    private double y = 0;

    private BorrowerDAO borrowerDAO = new BorrowerDAO();
    private DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");
    BookDAO bookDAO = new BookDAO();

    @FXML
    private void borrowBook() {
        String borrowerId = borrowerIDField.getText();
        String bookId = bookIDField.getText();
        LocalDate borrowToDate = toDatePicker.getValue();

        // Validate input fields
        if (borrowerId.isEmpty() || bookId.isEmpty() || borrowToDate == null) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both user's ID, Book's ID and return Day.");
            return;
        }

        // Ensure the return date is after today
        LocalDate today = LocalDate.now();
        if (!borrowToDate.isAfter(today)) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Return date must be after today.");
            return;
        }

        // Format the return date
        String formattedReturnDate = dateFormatter.formatDate(borrowToDate);

        // Confirm borrowing action
        Optional<ButtonType> result = showConfirmationAlert("Confirm Borrow", "Are you sure you want to borrow this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Check if the borrower and book exist, and if the book is available
            BookDAO bookDAO = new BookDAO();
            UserDAO userBorrow = new UserDAO();
            Book borrowBook = bookDAO.getBookByID(Integer.parseInt(bookId));
            User borrower = userBorrow.findUserById(Integer.parseInt(borrowerId));

            // Handle various error scenarios
            if (borrower == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "User not found.");
                return;
            } else if (borrowBook == null) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book not found.");
                return;
            } else if (borrowerDAO.checkBookExists(borrowBook.getBookID())) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book already being borrowed.");
                return;
            } else if (borrowerDAO.checkLimitStmt(borrower.getUserName())) {
                showAlert(Alert.AlertType.ERROR, "Borrow Book", "Book limit reached.");
                return;
            }

            // Add borrower record to the database and reload borrowers
            String bookName = borrowBook.getName();
            borrowerDAO.insertBorrower(Integer.parseInt(borrowerId), Integer.parseInt(bookId), bookName, dateFormatter.formatDate(today), formattedReturnDate);
            checkBookInformation();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
        }
    }

    @FXML
    private void checkBookInformation() {
        String borrowerId = borrowerIDField.getText();
        String bookId = bookIDField.getText();

        // Validate input fields
        if (borrowerId.isEmpty() || bookId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both user's ID, Book's ID.");
            return;
        }

        BookDAO bookDAO = new BookDAO();
        UserDAO userBorrow = new UserDAO();
        Book borrowBook = bookDAO.getBookByID(Integer.parseInt(bookId));
        User borrower = userBorrow.findUserById(Integer.parseInt(borrowerId));

        if (borrower == null) {
            showAlert(Alert.AlertType.ERROR, "Information", "User not found.");
            return;
        } else if (borrowBook == null) {
            showAlert(Alert.AlertType.ERROR, "Information", "Book not found.");
            return;
        }

        nameLabel.setText(borrower.getUserName());
        bookNameLabel.setText(borrowBook.getName());
        String imageLink = borrowBook.getImage();
        Image image = (imageLink != null && !imageLink.isEmpty())
                ? new Image(imageLink)
                : new Image(getClass().getResource("/image/defaultBook.png").toExternalForm());
        bookImageView.setImage(image);
    }

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

    @FXML
    public void DownloadPages(ActionEvent event) {
        try {
            if (event.getSource() == cancel_btn) {
                applySceneTransition2(cancel_btn, "/fxml/MemberView.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
