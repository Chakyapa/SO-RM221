module org.example.solab1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens org.example.solab1 to javafx.fxml;
    exports org.example.solab1;
}