module management.libarymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.desktop;

    opens management.libarymanagement to javafx.fxml;
    exports management.libarymanagement;
    exports Controller;
    opens Controller to javafx.fxml;
}