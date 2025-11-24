package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX App
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Easy Grapher");

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/InputMenu.fxml")));

            Scene scene = new Scene(root);

            stage.setScene(scene);
        } catch (IOException err) {
            System.out.println("Error loading main fxml file, " + err.getMessage());
        }

        stage.show();
    }

}