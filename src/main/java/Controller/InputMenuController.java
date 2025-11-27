package Controller;

import Model.Function;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.matheclipse.core.expression.F;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InputMenuController implements Initializable {
    @FXML
    TextField fctInput1;
    @FXML
    TextField fctInput2;
    @FXML
    Label fctLabel1;
    @FXML
    Label fctLabel2;
    @FXML
    CheckBox derivativeBox1;
    @FXML
    CheckBox derivativeBox2;
    @FXML
    VBox fctContainer1;
    @FXML
    VBox fctContainer2;
    @FXML
    VBox keyboard;

    private TextField focusedInput;
    private final String[][] keyCharss = {
            {"AC", "del", "Enter"},
            {"sin", "cos", "tan", "^"},
            {"ln", "|a|", "(", ")"},
            {"asin", "acos", "atan", "√"},
            {"7", "8", "9", "÷"},
            {"4", "5", "6", "X"},
            {"1", "2", "3", "–"},
            {"0", ".", "=", "+"},
            {"x", "y", "π", "e"}
    };
    private final String[][] printedCharss = {
            {"", "", ""},
            {"sin(", "cos(", "tan(", "^"},
            {"ln(", "abs(", "(", ")"},
            {"asin(", "acos(", "atan(", "sqrt("},
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"0", ".", "=", "+"},
            {"x", "y", "pi", "e"}
    };
    private final String[] functions = {"sin(", "cos(", "tan(", "asin(", "acos(", "atan(", "ln(", "abs(", "sqrt("};
    private PrimaryController primaryController;

    private Function firstFunction;
    private Function secondFunction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

            fctInput1.textProperty().addListener((observable, oldValue, newValue) -> {
                pause.setOnFinished(this::onFctSubmit);
                pause.playFromStart();
            });

            fctInput2.textProperty().addListener((observable, oldValue, newValue) -> {
                pause.setOnFinished(this::onFctSubmit);
                pause.playFromStart();
            });

            primaryController = PrimaryController.getPrimaryController();

            fctInput1.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    focusedInput = fctInput1;
                }
            });
            fctInput2.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    focusedInput = fctInput2;
                }
            });

            derivativeBox1.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    firstFunction.evaluateDerivative();
                    Function derivative = firstFunction.getDerivative();
                    System.out.println(derivative.getExprStr());
                }
            });

            derivativeBox2.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    Function derivative = secondFunction.getDerivative();
                    System.out.println(derivative.getExprStr());
                }
            });

            initializeKeyboard();
        });
    }

    @FXML
    private void onFctSubmit(Event event) {

        if (focusedInput == fctInput1) {
            firstFunction = new Function(fctInput1.getText(),true);
            boolean fctTest = testGraph(firstFunction, fctLabel1);

            if (!fctTest) {
                primaryController.setFirstFunction(new Function(null));
                primaryController.redraw();
                return;
            }


            primaryController.setFirstFunction(firstFunction);
        } else if (focusedInput == fctInput2) {
            secondFunction = new Function(fctInput2.getText(),true);
            boolean fctTest = testGraph(secondFunction, fctLabel2);

            if (!fctTest) {
                primaryController.setSecondFunction(new Function(null));
                primaryController.redraw();
                return;
            }


            primaryController.setSecondFunction(secondFunction);
        }

        primaryController.redraw();
    }

    private boolean testGraph(Function f, Label msg) {
        if (f.isValid()) {
            msg.setText("good function!");
            return true;
        } else {
            msg.setText("Bad function");
            return false;
        }
    }

    /**
     * Initializes the on-screen keyboard
     */
    private void initializeKeyboard() {
        final double ROWWIDTH = keyboard.getWidth();
        final double SIZE = ROWWIDTH / 4;
        final double SPACING = 2;

        keyboard.setSpacing(SPACING);

        for (int i = 0; i < keyCharss.length; i++) {
            HBox row = new HBox();
            row.setSpacing(SPACING);

            for (int j = 0; j < keyCharss[i].length; j++) {

                String keyStr = keyCharss[i][j];
                Button keyBtn = new Button(keyStr);

                keyBtn.setPrefHeight(SIZE);

                switch (keyStr) {
                    case "AC" -> {
                        handleClear(keyBtn);
                        sizeFirstRowBtn(keyBtn);
                    }
                    case "del" -> {
                        handleDel(keyBtn);
                        sizeFirstRowBtn(keyBtn);
                    }
                    case "Enter" -> {
                        handleEnter(keyBtn);
                        sizeFirstRowBtn(keyBtn);
                    }
                    default -> {
                        handleAddChr(keyBtn, printedCharss[i][j]);
                        keyBtn.setPrefWidth(SIZE);
                    }

                }

                row.getChildren().add(keyBtn);
            }
            keyboard.getChildren().add(row);
        }
    }

    /**
     * Adjusts size of the first row's button to match
     * @param keyBtn the first row button
     */
    private void sizeFirstRowBtn(Button keyBtn) {
        keyBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(keyBtn, Priority.ALWAYS);
    }

    /**
     * Clears the focused text field
     * @param keyBtn the clear button
     */
    private void handleClear(Button keyBtn) {
        keyBtn.setOnAction(e -> {
            focusedInput.setText("");
            focusedInput.requestFocus();
        });
    }

    /**
     * Deletes previous character or function
     * @param keyBtn the delete button
     */
    private void handleDel(Button keyBtn) {
        keyBtn.setOnAction(e -> {
            String text = focusedInput.getText();
            if (text.isEmpty()) {
                focusedInput.requestFocus();
                return;
            }

            // Try to match last function first
            for (String func : functions) {
                if (text.endsWith(func)) {
                    focusedInput.setText(text.substring(0, text.length() - func.length()));
                    focusedInput.requestFocus();
                    focusedInput.positionCaret(focusedInput.getText().length());
                    return;
                }
            }

            // Otherwise, remove the last single character
            focusedInput.setText(text.substring(0, text.length() - 1));
            focusedInput.requestFocus();
            focusedInput.positionCaret(focusedInput.getText().length());
        });
    }

    /**
     * Submits the input by calling onFctSubmit
     * @param keyBtn the enter button
     */
    private void handleEnter(Button keyBtn) {
        keyBtn.setOnAction(this::onFctSubmit);
    }

    /**
     * Appends input to focused text field
     * @param keyBtn the pressed button
     * @param input the string to append
     */
    private void handleAddChr(Button keyBtn, String input) {
        keyBtn.setOnAction(e -> {
            focusedInput.setText(focusedInput.getText() + input);
            focusedInput.requestFocus();
            focusedInput.positionCaret(focusedInput.getText().length());

        });
    }
}
