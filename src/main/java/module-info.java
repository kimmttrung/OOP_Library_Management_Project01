module management.libarymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires animatefx;

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