package com.example.lab2;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class HelloController {
    @FXML
    private TextArea textArea;

    @FXML
    public void initialize() {
        System.out.println("FXML загружен, textArea = " + textArea);
    }

    public void appendText(String text) {
        if (textArea != null) {
            textArea.appendText(text + "\n");
        } else {
            System.out.println("Ошибка: textArea не инициализирован");
        }
    }
}
