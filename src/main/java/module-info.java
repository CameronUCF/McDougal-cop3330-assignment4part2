module ucf.assignments {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.base;


    opens ucf.assignments to javafx.fxml, com.google.gson;
    exports ucf.assignments;
}