package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DictionaryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DictionaryApplication.class.getResource("homescreen.fxml"));

        //2 last parameter == window size
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        //name of the window
        stage.setTitle("Dictionary Application");

        //put the scene into the stage
        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(800);

        //display
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}