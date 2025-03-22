package ui;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class Controller {
	
	@FXML
	private TextArea textArea;
	
	@FXML
	public void initialize() {
        System.out.println("FXML загружен, textArea = " + textArea);
	}
	
	public void appendText(String text) {
		if (textArea != null) {
			textArea.appendText(text + "\n");
		}else {
			System.out.println("Ошибка: textArea не инициализирован");
		}
	}

}
