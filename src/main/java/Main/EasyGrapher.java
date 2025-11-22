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

    private static double xScale = 60;
    private static double yScale = 60;

    private static double xOffset = 0;
    private static double yOffset = 0;

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
        GraphicsContext graphGc = axesCanvas.getGraphicsContext2D();

        StackPane canvasContainer = new StackPane(axesCanvas, graphCanvas);
        canvasContainer.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        canvasContainer.setOnScroll((ScrollEvent scroll) -> {
            System.out.println(scroll.getDeltaX() + ", " + scroll.getDeltaY());

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

            axesGc.clearRect(0, 0, 800, 800);
            graphGc.clearRect(0, 0, 800, 800);
            lineCycle++;
            drawAxes(axesGc);
            drawAxeIncrements(axesGc);
            drawFunction(graphGc);
            drawLines(axesGc);

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

        canvasContainer.setOnMouseDragged((MouseEvent mouseMovement) -> {
            System.out.println("X: " + mouseMovement.getX());
            System.out.println("Y: " + mouseMovement.getY());
            System.out.println("Z: " + mouseMovement.getZ());
        });
    }

//    public String reduce(double decimal) {
//        int ctr = 0;
//
//        if (Math.abs(decimal) >= 1) {
//            return "" + (int) decimal;
//        }
//
//        String text = "" + decimal;
//
//        System.out.println(decimal);
//
//        while (!Character.isDigit(text.charAt(ctr)) && text.charAt(ctr) != '0') {
//            System.out.println("VAL: " + text.substring(0, ctr));
//            ctr += 1;
//        }
//
//        return text.substring(0, ctr);
//    }

    public void drawAxes(GraphicsContext gc) {
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.setGlobalAlpha(1.0);

        int originX = canvasWidth / 2;
        int originY = canvasHeight / 2;

        gc.strokeLine(0, originY, canvasWidth, originY);
        gc.strokeLine(originX, 0, originX, canvasHeight);
    }

    public void drawAxeIncrements(GraphicsContext gc) {
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.setGlobalAlpha(1.0);

        int originX = canvasWidth / 2;
        int originY = canvasHeight / 2;

        gc.fillText("0", originX - 10, originY + 15);

        for (double h = 0; h < (double) canvasWidth / 2; h += xScale * Math.max(Math.round(60 / xScale), 1) * squareCycle) {
            if (h != 0) {
//                String text = reduce(h / xScale);
                String text = String.format("%.2f", h / xScale);

                gc.fillText("-" + text, originX - h - 10, originY + 20);
                gc.fillText(text, originX + h - 10, originY + 20);
                gc.fillText("-" + text, originX - 20, originY + h + 10);
                gc.fillText(text, originX - 20, originY - h + 10);
            }

            gc.strokeLine(originX - h, originY - 5, originX - h, originY + 5);
            gc.strokeLine(originX + h, originY - 5, originX + h, originY + 5);
            gc.strokeLine(originX - 5, originY + h, originX + 5, originY + h);
            gc.strokeLine(originX - 5, originY - h, originX + 5, originY - h);
        }
    }

    public void drawLines(GraphicsContext gc) {
        gc.setLineWidth(1);
        gc.setStroke(Color.GRAY);
        gc.setGlobalAlpha(0.2);


        int originX = canvasWidth / 2;
        int originY = canvasHeight / 2;

        for (double h = 0; h < (double) canvasWidth / 2; h += xScale * Math.max(Math.round(60 / xScale), 1) / (lineCycle % 3 + 3) * squareCycle) {
            if (h / xScale % 1 == 0) {
                continue;
            }

            gc.strokeLine(originX - h, 0, originX - h, canvasHeight);
            gc.strokeLine(originX + h, 0, originX + h, canvasHeight);
            gc.strokeLine(0, originY + h, canvasWidth, originY + h);
            gc.strokeLine(0, originY - h, canvasWidth, originY - h);
        }

        gc.setGlobalAlpha(0.5);

        for (double h = 0; h < (double) canvasWidth / 2; h += xScale * Math.max(Math.round(60 / xScale), 1) * squareCycle) {
            gc.strokeLine(originX - h, 0, originX - h, canvasHeight);
            gc.strokeLine(originX + h, 0, originX + h, canvasHeight);
            gc.strokeLine(0, originY + h, canvasWidth, originY + h);
            gc.strokeLine(0, originY - h, canvasWidth, originY - h);
        }
    }

    public void drawFunction(GraphicsContext gc) {
        gc.setLineWidth(2);
        gc.setStroke(Color.BLUE);
        gc.setGlobalAlpha(1.0);

        Function f = new Function("x^3");

        int originX = canvasWidth / 2;
        int originY = canvasHeight / 2;

        for (double x = -originX; x < canvasWidth - originX; x++) {
            double mathX1 = x / xScale;
            double mathX2 = (x + 1) / xScale;

            double mathY1 = f.valueAt(mathX1);
            double mathY2 = f.valueAt(mathX2);

            double canvasX1 = originX + x;
            double canvasY1 = originY - mathY1 * yScale;
            double canvasX2 = originX + x + 1;
            double canvasY2 = originY - mathY2 * yScale;

            gc.strokeLine(canvasX1, canvasY1, canvasX2, canvasY2);
        }
    }
}