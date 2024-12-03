package Controller.Users;

import Controller.BaseDashBoardControl;
import DataAccessObject.BookDAO;
import DataAccessObject.BorrowerDAO;
import DataAccessObject.UserDAO;
import Entity.Book;
import Entity.User;
import Singleton.Session;
import Tools.DateStringFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

import static Tools.AlertHelper.showAlert;
import static Tools.AlertHelper.showConfirmationAlert;

/**
 * Controller class for handling user book borrowing functionality in the library system.
 */
public class UserBorrow extends BaseDashBoardControl {

    @FXML
    private Button cancel_btn;

    @FXML
    private ImageView bookImageView;

    @FXML
    private TextField bookIDField;

    @FXML
    private Label nameLabel, bookNameLabel;

    @FXML
    private Label borrowerIDLabel;

    @FXML
    private DatePicker toDatePicker;

    private final BorrowerDAO borrowerDAO = new BorrowerDAO();
    private final DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");
    private final BookDAO bookDAO = new BookDAO();

    /**
     * Initializes the controller after the FXML file has been loaded.
     * Sets the borrower ID label with the current user's ID from the session.
     */
    @FXML
    public void initialize() {
        borrowerIDLabel.setText(String.valueOf(Session.getInstance().getUserID()));
    }

    /**
     * Handles the borrowing of a book by a user.
     * Validates input fields, checks user and book details, and adds a borrowing record to the database.
     */
    @FXML
    private void borrowBook() {
        String borrowerId = borrowerIDLabel.getText();
        String bookId = bookIDField.getText();
        LocalDate borrowToDate = toDatePicker.getValue();

        if (borrowerId.isEmpty() || bookId.isEmpty() || borrowToDate == null) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both user's ID, Book's ID and return Day.");
            return;
        }

        LocalDate today = LocalDate.now();
        if (!borrowToDate.isAfter(today)) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Return date must be after today.");
            return;
        }

        String formattedReturnDate = dateFormatter.formatDate(borrowToDate);

        Optional<ButtonType> result = showConfirmationAlert("Confirm Borrow", "Are you sure you want to borrow this book?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Book borrowBook = bookDAO.getBookByID(Integer.parseInt(bookId));
            User borrower = new UserDAO().findUserById(Integer.parseInt(borrowerId));

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

            borrowerDAO.insertBorrower(Integer.parseInt(borrowerId), Integer.parseInt(bookId), borrowBook.getName(), dateFormatter.formatDate(today), formattedReturnDate);
            checkBookInformation();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
        }
    }

    /**
     * Checks the information of the selected book and borrower.
     * Updates UI with the retrieved details including book image, book name, and user name.
     */
    @FXML
    private void checkBookInformation() {
        String borrowerId = borrowerIDLabel.getText();
        String bookId = bookIDField.getText();

        if (borrowerId.isEmpty() || bookId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both user's ID, Book's ID.");
            return;
        }

        Book borrowBook = bookDAO.getBookByID(Integer.parseInt(bookId));
        User borrower = new UserDAO().findUserById(Integer.parseInt(borrowerId));

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

    /**
     * Handles the transition between scenes triggered by the given button.
     *
     * @param sourceButton The button that triggered the transition.
     * @param fxmlPath     The path of the FXML file for the target scene.
     */
    @Override
    protected void applySceneTransition(Button sourceButton, String fxmlPath) {
        Stage currentStage = (Stage) sourceButton.getScene().getWindow();
        currentStage.hide();
    }

    /**
     * Handles the action triggered by the "Cancel" button.
     * Returns to the Member View scene.
     *
     * @param event The action event triggered by the button click.
     */
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
