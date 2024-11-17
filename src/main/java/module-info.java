module management.libarymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.desktop;

    opens management.libarymanagement to javafx.fxml;
    opens Controller to javafx.fxml;
    opens DataAccessObject;
    opens API;
    opens Entity;
    opens Database;

    exports management.libarymanagement;
    exports Controller;
    exports DataAccessObject;
    exports API;
    exports Entity;
    exports Database;
}