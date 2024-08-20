module com.example.unimanagement {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.unimanagement to javafx.fxml;
    exports com.example.unimanagement;
}