module management.libarymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.desktop;
    //requires fontawesomefx;

    opens management.libarymanagement to javafx.fxml;
    opens Controller to javafx.fxml;
    opens DataAccessObject;
    opens API;
    opens Entity;

    exports management.libarymanagement;
    exports Controller;
    exports DataAccessObject;
    exports API;
    exports Entity;
}