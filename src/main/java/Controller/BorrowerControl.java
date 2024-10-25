package Controller;

import DataAccessObject.BorrowerDAO;
import Entity.Borrower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class BorrowerControl {

    @FXML
    private TextField borrowerIDField, borrowerUsernameField, bookIDField, statusField, fromDateField, toDateField;
    @FXML
    private TableView<Borrower> borrowerTable;
    @FXML
    private TableColumn<Borrower, Integer> idColumn;
    @FXML
    private TableColumn<Borrower, String> usernameColumn, statusColumn, fromColumn, toColumn;
    @FXML
    private TableColumn<Borrower, Integer> bookIdColumn;

    private ObservableList<Borrower> borrowerList = FXCollections.observableArrayList();
    private BorrowerDAO borrowerDAO = new BorrowerDAO();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_to"));
        loadBorrowers();
    }

    private void loadBorrowers() {
        borrowerList = FXCollections.observableArrayList(borrowerDAO.getBorrowerByStatus("processing"));
        borrowerTable.setItems(borrowerList);
    }

    @FXML
    private void borrowBook() {
        String username = borrowerUsernameField.getText();
        String bookId = bookIDField.getText();

        if (username.isEmpty() || bookId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Borrow Book", "Please enter both Username and Book ID.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Borrow");
        confirmAlert.setHeaderText("Are you sure you want to borrow this book?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            borrowerDAO.insertBorrower(username, bookId);
            loadBorrowers();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
        }
    }

    @FXML
    private void returnBook() {
        String borrowerId = borrowerIDField.getText();

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Return Book", "Please enter Borrower ID.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Return");
        confirmAlert.setHeaderText("Are you sure you want to return this book?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Borrower borrower = borrowerDAO.getBorrowerById(borrowerId);
            if (borrower != null) {
                borrower.setStatus("returned");
                borrowerDAO.updateBorrower(borrower);
                loadBorrowers();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Borrower not found.");
            }
        }
    }

    @FXML
    private void renewBook() {
        String borrowerId = borrowerIDField.getText();
        int additionalDays = 7; // Number of days for renewal

        if (borrowerId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Renew Book", "Please enter Borrower ID.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Renewal");
        confirmAlert.setHeaderText("Are you sure you want to renew this book?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Borrower borrower = borrowerDAO.getBorrowerById(borrowerId);
            if (borrower != null && borrower.getStatus().equals("processing")) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date currentDueDate = dateFormat.parse(borrower.getBorrow_to());
                    Date newDueDate = new Date(currentDueDate.getTime() + (long) additionalDays * 24 * 60 * 60 * 1000);
                    borrower.setBorrow_to(dateFormat.format(newDueDate));
                    borrowerDAO.updateBorrower(borrower);
                    loadBorrowers();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book renewed successfully.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Unable to renew book: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Borrower record not found or book not eligible for renewal.");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

