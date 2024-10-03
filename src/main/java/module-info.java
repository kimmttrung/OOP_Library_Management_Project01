module management.libarymanagement {
    requires javafx.controls;
    requires javafx.fxml;


    opens management.libarymanagement to javafx.fxml;
    exports management.libarymanagement;
}