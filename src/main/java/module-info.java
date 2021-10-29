module ucf.assignments {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens ucf.assignments to javafx.fxml;
    exports ucf.assignments;
}