package Controller;

import Model.Function;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class InputMenuController {
    @FXML
    TextField fctInput1;
    @FXML
    TextField fctInput2;
    @FXML
    Label fctLabel1;
    @FXML
    Label fctLabel2;
    @FXML
    VBox fctContainer1;
    @FXML
    VBox fctContainer2;

    @FXML
    private void onFctSubmit(Event event) {
        Node node = (Node) event.getSource();
        Node parent = node.getParent();

        while (parent != null) {
            if (parent == fctContainer1) {
                displayGraph(fctInput1, fctLabel1);
                return;
            }
            if (parent == fctContainer2) {
                displayGraph(fctInput2, fctLabel2);
                return;
            }
            parent = parent.getParent();
        }
    }

    private void displayGraph(TextField tf, Label msg) {
        Function f = new Function(tf.getText());
        if (f.isValid()) {
            msg.setText("good function!");
        } else {
            msg.setText("Bad function");
        }
    }
}
