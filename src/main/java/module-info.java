//module com.example.unimanagement {
//    requires javafx.controls;
//    requires javafx.fxml;
//
//    opens com.example.unimanagement to javafx.fxml;
//    exports com.example.unimanagement;
//
//}
open module com.example.unimanagement {
        requires java.desktop;
        requires java.sql;
        requires javafx.controls;
        requires javafx.fxml;
        requires jakarta.persistence;
        requires com.zaxxer.hikari;
        requires org.hibernate.orm.core;
    requires java.management;
}