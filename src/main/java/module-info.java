module management.libarymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;

    opens management.libarymanagement to javafx.fxml;
    exports management.libarymanagement;
}