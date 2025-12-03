/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Main;


import Model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EasyGrapher extends Application {

    private static final int canvasWidth = 800;
    private static final int canvasHeight = 800;

    private static double xScale = 200;
    private static double yScale = 200;

    private static double xOffset = 0;
    private static double yOffset = 0;

    int centerX = canvasWidth / 2;
    int centerY = canvasHeight / 2;

    private static double prevMouseX = 0;
    private static double prevMouseY = 0;

    private static int lineCycle = 4;
    private static double squareCycle = 1;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hello world!!");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox inputs = new VBox();
        inputs.setStyle("-fx-border-color: red; -fx-border-width: 2;");

        VBox graphAndTitle = new VBox();
        graphAndTitle.setStyle("-fx-border-color: blue; -fx-border-width: 2;");

        Canvas axesCanvas = new Canvas(800, 800);
        GraphicsContext axesGc = axesCanvas.getGraphicsContext2D();

        Canvas graphCanvas = new Canvas(800, 800);
        GraphicsContext graphGc = graphCanvas.getGraphicsContext2D();

        StackPane canvasContainer = new StackPane(axesCanvas, graphCanvas);
        canvasContainer.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        canvasContainer.setOnMousePressed((MouseEvent mousePress) -> {
            prevMouseX = mousePress.getX();
            prevMouseY = mousePress.getY();
        });

        canvasContainer.setOnMouseDragged(event -> {
            double delta_x = event.getX() - prevMouseX;
            double delta_y = event.getY() - prevMouseY;

            xOffset -= delta_x / xScale;
            yOffset += delta_y / yScale;

            prevMouseX = event.getX();
            prevMouseY = event.getY();

            redraw(axesGc, graphGc);
        });

        canvasContainer.setOnScroll((ScrollEvent scroll) -> {
//            System.out.println(scroll.getDeltaX() + ", " + scroll.getDeltaY());

            if (scroll.getDeltaY() < 0) {
                xScale *= 0.9;
                yScale *= 0.9;
                squareCycle = (squareCycle - 0.25) % 1 + 1;
            } else {
                xScale /= 0.9;
                yScale /= 0.9;
                squareCycle = (squareCycle + 0.25) % 1 + 1;
            }

            if (xScale <= 1) {
                xScale = 1;
            }

            if (yScale <= 1) {
                yScale = 1;
            }

            lineCycle++;
            redraw(axesGc, graphGc);

            System.out.println("SCALE_X: " + xScale);
            System.out.println("SCALE_Y: " + yScale);
        });

        graphAndTitle.getChildren().add(canvasContainer);

        drawFunction(graphGc);
        drawAxes(axesGc);
        drawAxeIncrements(axesGc);
        drawLines(axesGc);

        System.out.println("Hello world");

        root.getChildren().addAll(inputs, graphAndTitle);
        Scene scene = new Scene(root, 1200, 800);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    // COMPUTE OFFSETS

    double xFromPixel(double px) {
        return (px - centerX) / xScale + xOffset;
    }

    double yFromPixel(double py) {
        return (centerY - py) / yScale + yOffset;
    }

    double xToPixel(double x) {
        return centerX + (x - xOffset) * xScale;
    }

    double yToPixel(double y) {
        return centerY - (y - yOffset) * yScale;
    }

    // REFRESH METHOD

    private void redraw(GraphicsContext axesGc, GraphicsContext graphGc) {
        axesGc.clearRect(0, 0, canvasWidth, canvasHeight);
        graphGc.clearRect(0, 0, canvasWidth, canvasHeight);

        drawAxes(axesGc);
        drawAxeIncrements(axesGc);
        drawLines(axesGc);
        drawFunction(graphGc);
    }

    // DRAWING LOGIC

    private double computeTickStep(double scale) {
        double targetPixels = 80;

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

    private double getBase(double scale) {
        double targetPixels = 80;

        double mathUnit = targetPixels / scale;
        double exp = Math.pow(10, Math.floor(Math.log10(mathUnit)));

        double base;
        if (mathUnit / exp < 3.5) base = 4;
        else base = 5;

        return base;
    }

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

    public void drawAxeIncrements(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));

        double xStep = computeTickStep(xScale);
        double yStep = computeTickStep(yScale);

        double min_x = xFromPixel(0);
        double max_x = xFromPixel(canvasWidth);

        double firstX = Math.ceil(min_x / xStep) * xStep;
        double axisYPixel = yToPixel(0);

        for (double x = firstX; x <= max_x; x += xStep) {
            double converted_x = xToPixel(x);
            if (axisYPixel >= 0 && axisYPixel <= canvasHeight) {
                gc.fillText(String.format("%.2f", x), converted_x + 3, axisYPixel - 3);
                gc.strokeLine(converted_x, axisYPixel - 6, converted_x, axisYPixel + 6);
            }
        }

        double min_y = yFromPixel(canvasHeight);
        double max_y = yFromPixel(0);

        double firstY = Math.ceil(min_y / yStep) * yStep;
        double axisXPixel = xToPixel(0);

        for (double y = firstY; y <= max_y; y += yStep) {
            double converted_y = yToPixel(y);
            if (axisXPixel >= 0 && axisXPixel <= canvasWidth) {
                gc.fillText(String.format("%.2f", y), axisXPixel + 4, converted_y - 4);
                gc.strokeLine(axisXPixel - 6, converted_y, axisXPixel + 6, converted_y);
            }
        }
    }

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

    private void drawLinesX(GraphicsContext gc, double step) {
        double min_x = xFromPixel(0);
        double max_x = xFromPixel(canvasWidth);

        double firstX = Math.ceil(min_x / step) * step;

        for (double x = firstX; x <= max_x; x += step) {
            double converted_x = xToPixel(x);
            gc.strokeLine(converted_x, 0, converted_x, canvasHeight);
        }
    }

    private void drawLinesY(GraphicsContext gc, double step) {
        double min_y = yFromPixel(canvasHeight);
        double max_y = yFromPixel(0);

        double firstY = Math.ceil(min_y / step) * step;

        for (double y = firstY; y <= max_y; y += step) {
            double converted_y = yToPixel(y);
            gc.strokeLine(0, converted_y, canvasWidth, converted_y);
        }
    }


    public void drawFunction(GraphicsContext gc) {
        gc.setLineWidth(2);
        gc.setStroke(Color.BLUE);

        Function f = new Function("x^3", true);

        for (int pixeled_x = 0; pixeled_x < canvasWidth - 1; pixeled_x++) {

            double x1_coordinate = xFromPixel(pixeled_x);
            double x2_coordinate = xFromPixel(pixeled_x + 1);

            double mathY1 = f.valueAt(x1_coordinate);
            double mathY2 = f.valueAt(x2_coordinate);

            double y1_coordinate = yToPixel(mathY1);
            double y2_coordinate = yToPixel(mathY2);

            gc.strokeLine(pixeled_x, y1_coordinate, pixeled_x + 1, y2_coordinate);
        }
    }
}