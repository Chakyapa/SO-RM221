module org.example.solab1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.solab1 to javafx.fxml;
    exports org.example.solab1;
}