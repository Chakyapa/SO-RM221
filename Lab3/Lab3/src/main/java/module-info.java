module com.example.lab3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.lab3 to javafx.fxml;
    exports com.example.lab3;
}