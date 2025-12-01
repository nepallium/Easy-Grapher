package Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Model.Function;
import Model.RootFinder;
import edu.jas.arith.Roots;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class PrimaryController implements Initializable{
    @Getter
    private static PrimaryController primaryController;

    private static final int canvasWidth = 600;
    private static final int canvasHeight = 600;

    private static double xScale = 200;
    private static double yScale = 200;

    private static double xOffset = 0;
    private static double yOffset = 0;

    int centerX = canvasWidth / 2;
    int centerY = canvasHeight / 2;

    private static double prevMouseX = 0;
    private static double prevMouseY = 0;

    private static Double coordinate_x;
    private static Double coordinate_y;

    private boolean interceptToggle;

    @Getter
    private Function firstFunction;
    @Setter
    private Function secondFunction;
    @Getter
    @Setter
    private Function firstFDerivative;
    @Getter
    @Setter
    private Function secondFDerivative;

    @Getter
    @Setter
    private Color Function1Color;
    @Getter
    @Setter
    private Color Function2Color;

    @FXML
    public StackPane graphPane;
    @FXML
    public Canvas AxesCanvas;
    @FXML
    public Canvas FunctionCanvas1;
    @FXML
    public Canvas FunctionCanvas2;
    @FXML
    public Canvas Derivative1Canvas;
    @FXML
    public Canvas Derivative2Canvas;
    @FXML
    public Canvas RootCanvas;
    @FXML
    public AnchorPane root;
    @FXML
    private Button showInterceptsBtn;
    @FXML
    private Label showInterceptsLabel;

    private GraphicsContext AxesGc;
    private GraphicsContext Function1Gc;
    private GraphicsContext Function2Gc;
    private GraphicsContext Derivative1Gc;
    private GraphicsContext Derivative2Gc;
    private GraphicsContext RootGc;

    private Function hoveredFunction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AxesGc = AxesCanvas.getGraphicsContext2D();
        Function1Gc = FunctionCanvas1.getGraphicsContext2D();
        Function2Gc = FunctionCanvas2.getGraphicsContext2D();
        Derivative1Gc = Derivative1Canvas.getGraphicsContext2D();
        Derivative2Gc = Derivative2Canvas.getGraphicsContext2D();
        RootGc = RootCanvas.getGraphicsContext2D();

        Function1Color = Color.valueOf("000000");
        Function2Color = Color.valueOf("000000");;

        firstFunction = new Function(null);
        secondFunction = new Function(null);
        firstFDerivative = new Function(null);
        secondFDerivative = new Function(null);

        drawFunction(Function1Gc, firstFunction, Function1Color, 1);
        drawFunction(Function2Gc, secondFunction, Function2Color, 1);
        drawAxes(AxesGc);
        drawAxeIncrements(AxesGc);
        drawLines(AxesGc);
        drawFunction(Derivative1Gc, firstFDerivative, Color.BLACK, 0.5);
        drawFunction(Derivative2Gc, secondFDerivative, Color.BLACK, 0.5);

        RootCanvas.toFront();

        primaryController = this;

        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setResizable(false);
        });
    }

    /**
     * saves current mouse position (to know distance dragged when panning, and whether the user is hovering on a function or not)
     * @param press the corresponding mouse press event
     */
    @FXML
    private void handleMousePress(MouseEvent press) {
        prevMouseX = press.getX();
        prevMouseY = press.getY();

        boolean inRange = isNearFunction(firstFunction);

        if (inRange) {
            hoveredFunction = firstFunction;
            return;
        }

        inRange = isNearFunction(secondFunction);

        if (inRange) {
            hoveredFunction = secondFunction;
        }
    }

    /**
     * removes any point on a hovered function that is being displayed
     * @param release the corresponding mouse release event
     */
    @FXML
    private void handleMouseRelease(MouseEvent release) {
        hoveredFunction = null;
        redraw();
    }

    /**
     * updates graph offset (x, y) or shows point of a function, based on whether the mouse is hovered on a function or not
     * @param drag the corresponding mouse drag event
     */
    @FXML
    private void handleMouseDrag(MouseEvent drag) {
        if (hoveredFunction == null) {
            double delta_x = drag.getX() - prevMouseX;
            double delta_y = drag.getY() - prevMouseY;

            xOffset -= delta_x / xScale;
            yOffset += delta_y / yScale;

            prevMouseX = drag.getX();
            prevMouseY = drag.getY();
        } else {
            coordinate_x = xFromPixel(drag.getX());
            coordinate_y = hoveredFunction.valueAt(coordinate_x);
        }

        redraw();
    }

    /**
     * changes scale of the graph based on mouse scroll
     * @param scroll the corresponding mouse scroll event
     */
    @FXML
    private void handleMouseScroll(ScrollEvent scroll) {
        if (scroll.getDeltaY() < 0) {
            xScale *= 0.9;
            yScale *= 0.9;
        } else {
            xScale /= 0.9;
            yScale /= 0.9;
        }

//        System.out.println(firstFunction.getExprStr() + ", " + secondFunction.getExprStr());
        redraw();
    }

    /**
     * Event triggered when user toggles show intercepts: toggles intercepts until one of the functions change
     * @param e the corresponding button event
     */
    @FXML
    public void handleShowIntercepts(ActionEvent e) {
        interceptToggle = true;

        drawIntercepts(RootGc);
    }

    // COMPUTE OFFSETS

    /**
     * converts a pixel amount to the corresponding mathematical x value (based on the function, scale, offset)
     * @param px the pixel value of x in the graph
     * @return the corresponding mathematical value of x at the specific spot on the canvas
     */
    double xFromPixel(double px) {
        return (px - centerX) / xScale + xOffset;
    }

    /**
     * converts a pixel amount to the corresponding mathematical y value (based on the function, scale, offset)
     * @param py the pixel value of y in the graph
     * @return the corresponding mathematical value of y at the specific spot on the canvas
     */
    double yFromPixel(double py) {
        return (centerY - py) / yScale + yOffset;
    }

    /**
     * converts a mathematical x value to its corresponding value on the graph (in pixels, distance from left canvas)
     * @param x the mathematical value of x
     * @return the pixel value of where the x should be
     */
    double xToPixel(double x) {
        return centerX + (x - xOffset) * xScale;
    }

    /**
     * converts a mathematical y value to its corresponding value on the graph (in pixels, distance from top of canvas)
     * @param y the mathematical value of y
     * @return the pixel value of where the y should be
     */
    double yToPixel(double y) {
        return centerY - (y - yOffset) * yScale;
    }

    // REFRESH METHOD

    /**
     * resets the graph and updates it based on changes in offset, scale, etc.
     */
    public void redraw() {
        AxesGc.clearRect(0, 0, canvasWidth, canvasHeight);
        Function1Gc.clearRect(0, 0, canvasWidth, canvasHeight);
        Function2Gc.clearRect(0, 0, canvasWidth, canvasHeight);
        Derivative1Gc.clearRect(0, 0, canvasWidth, canvasHeight);
        Derivative2Gc.clearRect(0, 0, canvasWidth, canvasHeight);
        RootGc.clearRect(0, 0, canvasWidth, canvasHeight);

        drawAxes(AxesGc);
        drawAxeIncrements(AxesGc);
        drawLines(AxesGc);
        drawFunction(Function1Gc, firstFunction, Function1Color, 1);
        drawFunction(Function2Gc, secondFunction, Function2Color, 1);
        drawFunction(Derivative1Gc, firstFDerivative, Function1Color, 0.3);
        drawFunction(Derivative2Gc, secondFDerivative, Function2Color, 0.3);

        if (interceptToggle) drawIntercepts(RootGc);
        drawCoordinate(RootGc);
    }

    // DRAWING LOGIC

    /**
     * checks if the mouse is within range of a function to display the current hovered point
     * @param function the function to check
     * @return true or false: the mouse is within range
     */
    private boolean isNearFunction(Function function) {
        double pixelRange = 8;

        double x = xFromPixel(prevMouseX);
        double y = function.valueAt(x);

        double pixeled_y = yToPixel(y);

        double distance = Math.abs(pixeled_y - prevMouseY);

        return distance < pixelRange;
    }

    /**
     * finds the ideal tick step based on the scale that is easy to navigate/understand
     * @param scale the current scale of the graph
     * @return the tick step (math distance between ticks) that the graph should follow
     */
    private double computeTickStep(double scale) {
        double targetPixels = 140;

        double mathUnit = targetPixels / scale;

        double exp = Math.pow(10, Math.floor(Math.log10(mathUnit)));
        double base = mathUnit / exp;

        double closestPerfect;
        if (base < 1.5)      closestPerfect = 1;
        else if (base < 3.5) closestPerfect = 2;
        else if (base < 7.5) closestPerfect = 5;
        else                     closestPerfect = 10;

        return closestPerfect * exp;
    }

    /**
     * gets the amount of squares that should be between two ticks on a graph based on the scale
     * @param scale the current scale of the graph
     * @return the number of squares that should be between two ticks
     */
    private double getBase(double scale) {
        double targetPixels = 140;

        double mathUnit = targetPixels / scale;
        double exp = Math.pow(10, Math.floor(Math.log10(mathUnit)));

        double base;
        if (mathUnit / exp < 3.5) base = 4;
        else base = 5;

        return base;
    }

    /**
     * gets the degree of a value (10^x) / amount of zeros
     * @param scale the current scale of the graph
     * @return the degree of the value
     */
    private int getExp(double scale) {
        double targetPixels = 140;

        double mathUnit = targetPixels / scale;

        double exp = Math.floor(Math.log10(mathUnit));

        return (int) exp;
    }

    /**
     * draws the x and y axes
     * @param gc the graphics context in which the axes should be contained
     */
    public void drawAxes(GraphicsContext gc) {
        gc.setLineWidth(2);
        gc.setStroke(Color.BLACK);

        double xAxisWithOffset = xToPixel(0);
        double yAxisWithOffset = yToPixel(0);

        if (xAxisWithOffset >= 0 && xAxisWithOffset <= canvasWidth) {
            gc.strokeLine(xAxisWithOffset, 0, xAxisWithOffset, canvasHeight);
        }

        if (yAxisWithOffset >= 0 && yAxisWithOffset <= canvasHeight) {
            gc.strokeLine(0, yAxisWithOffset, canvasWidth, yAxisWithOffset);
        }
    }

    /**
     * draws the current hovered point if the user is mouse down on a function
     * @param gc the graphics context in which the point should be contained
     */
    public void drawCoordinate(GraphicsContext gc) {
        if (hoveredFunction == null) return;

        double pixeled_x = xToPixel(coordinate_x);
        double pixeled_y = yToPixel(coordinate_y);

        gc.setFill(Color.RED);
        gc.fillOval(pixeled_x - 4, pixeled_y - 4, 8, 8);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(14));
        gc.fillText(
                String.format("(%.3f, %.3f)", coordinate_x, coordinate_y),
                pixeled_x + 10, pixeled_y - 10
        );
    }

    /**
     * draws the ticks for the vertical and horizontal axes (if they should be in the graph)
     * @param gc the graphics context in which the ticks should be contained
     */
    public void drawAxeIncrements(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));

        double xStep = computeTickStep(xScale);
        double yStep = computeTickStep(yScale);

        double min_x = xFromPixel(0);
        double max_x = xFromPixel(canvasWidth);

        double firstX = Math.ceil(min_x / xStep) * xStep;
        double axisYPixel = yToPixel(0);

        int exp = getExp(xScale);

        for (double x = firstX; x <= max_x; x += xStep) {
            double converted_x = xToPixel(x);

            if (Math.abs(converted_x - xToPixel(0)) < 1 ) {
                continue;
            }

            if (axisYPixel >= 0 && axisYPixel <= canvasHeight) {
                gc.fillText((exp > -3) ? String.format("%.2f", x): String.format("%1.0f * 10^%d", x / Math.pow(10, exp), exp)
                        , converted_x + 3, axisYPixel - 3);
                gc.strokeLine(converted_x, axisYPixel - 6, converted_x, axisYPixel + 6);
            }
        }

        double min_y = yFromPixel(canvasHeight);
        double max_y = yFromPixel(0);

        double firstY = Math.ceil(min_y / yStep) * yStep;
        double axisXPixel = xToPixel(0);

        for (double y = firstY; y <= max_y; y += yStep) {
            double converted_y = yToPixel(y);

            if (Math.abs(converted_y - yToPixel(0)) < 1 ) {
                continue;
            }

            if (axisXPixel >= 0 && axisXPixel <= canvasWidth) {
                gc.fillText((exp > -3) ? String.format("%.2f", y): String.format("%.0f * 10^%d", y / Math.pow(10, exp), exp)
                        , axisXPixel + 4, converted_y - 4);
                gc.strokeLine(axisXPixel - 6, converted_y, axisXPixel + 6, converted_y);
            }
        }

        gc.fillText("0.00", xToPixel(0) + 5, yToPixel(0) - 5);
    }

    /**
     * fills the pane with squares to replicate graph paper
     * @param gc the graphics context in which the squares should be contained
     */
    public void drawLines(GraphicsContext gc) {
        double xStep = computeTickStep(xScale);
        double yStep = computeTickStep(yScale);

        double squarePerTick = getBase(xScale);

        double minorX = xStep / squarePerTick;
        double minorY = yStep / squarePerTick;

        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);
        gc.setGlobalAlpha(0.2);

        drawLinesX(gc, minorX);
        drawLinesY(gc, minorY);

        gc.setGlobalAlpha(0.5);

        drawLinesX(gc, xStep);
        drawLinesY(gc, yStep);

        gc.setGlobalAlpha(1.0);
    }

    /**
     * draws all vertical lines of the graph to replicate graph paper
     * @param gc the corresponding graphics context
     * @param step the distance between each line
     */
    private void drawLinesX(GraphicsContext gc, double step) {
        double min_x = xFromPixel(0);
        double max_x = xFromPixel(canvasWidth);

        double firstX = Math.ceil(min_x / step) * step;

        for (double x = firstX; x <= max_x; x += step) {
            double converted_x = xToPixel(x);
            gc.strokeLine(converted_x, 0, converted_x, canvasHeight);
        }
    }

    /**
     * draws all horizontal lines of the graph to replicate graph paper
     * @param gc the corresponding graphics context
     * @param step the distance between each line
     */
    private void drawLinesY(GraphicsContext gc, double step) {
        double min_y = yFromPixel(canvasHeight);
        double max_y = yFromPixel(0);

        double firstY = Math.ceil(min_y / step) * step;

        for (double y = firstY; y <= max_y; y += step) {
            double converted_y = yToPixel(y);
            gc.strokeLine(0, converted_y, canvasWidth, converted_y);
        }
    }

    /**
     * Draws a function in a corresponding canvas
     * @param gc the graphics context of the canvas in which the function will be contained
     * @param f the function when drawing
     * @param color the color of the curve
     * @param opacity the opacity of the curve (1 for regular functions, 0.5 for derivatives)
     */
    public void drawFunction(GraphicsContext gc, Function f, Color color, double opacity) {
        gc.setLineWidth(3);
        gc.setStroke(color);
        gc.setGlobalAlpha(opacity);

        for (int pixeled_x = 0; pixeled_x < canvasWidth - 1; pixeled_x++) {

            double x1_coordinate = xFromPixel(pixeled_x);
            double x2_coordinate = xFromPixel(pixeled_x + 1);

            double mathY1 = f.valueAt(x1_coordinate);
            double mathY2 = f.valueAt(x2_coordinate);

            double y1_coordinate = yToPixel(mathY1);
            double y2_coordinate = yToPixel(mathY2);

            if (Math.abs(y2_coordinate - y1_coordinate) > canvasHeight) {
                continue;
            }

            gc.strokeLine(pixeled_x, y1_coordinate, pixeled_x + 1, y2_coordinate);
        }

        gc.setGlobalAlpha(1.0);
    }

    /**
     * Draws intercepts of the two functions (firstFunction, secondFunction)
     * @param gc The graphics context of the canvas in which intercepts should be contained
     */
    private void drawIntercepts(GraphicsContext gc) {
        if (firstFunction == null || secondFunction == null ||
                !firstFunction.isValid() || !secondFunction.isValid()) {
            showInterceptsLabel.setText("Please input two valid functions first");
            return;
        }

        double min = -20, max = 20, step = 0.05;
        int decimals = 2;
        List<Double> roots = RootFinder.findAllRoots(firstFunction, secondFunction, min, max, step, decimals);

        RootGc.setStroke(Color.BLACK);
        RootGc.setFill(Color.WHITE);

        for (Double root : roots) {
            RootGc.fillOval(xToPixel(root) - 7, yToPixel(firstFunction.valueAt(root)) - 7, 14, 14);
            RootGc.strokeOval(xToPixel(root) - 7, yToPixel(firstFunction.valueAt(root)) - 7, 14, 14);
        }

        if (roots.isEmpty()) {
            showInterceptsLabel.setText("Could not find any roots within visible canvas!");
        } else {
            showInterceptsLabel.setText("Success! Roots at : " + roots);
        }
    }

    public void setFirstFunction(Function firstFunction) {
        this.firstFunction = firstFunction;
        interceptToggle = false;
    }

    public void setSecondFunction(Function secondFunction) {
        this.secondFunction = secondFunction;
        interceptToggle = false;
    }
}
