package Controller;

import Model.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InputMenuController {
    @FXML
    TextField fctInput1;

    @FXML
    private void onFctSubmit(ActionEvent event) {
        Function f = new Function(fctInput1.getText());
        if (f.isValid()) {
            System.out.println("good function!");
        } else {
            System.out.println("bad function");
        }
    }
}
