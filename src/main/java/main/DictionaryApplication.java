package main;

import base.Trie;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class DictionaryApplication extends Application {
    public static final String DB_URL = "jdbc:mysql://localhost:3307/dictionary";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "28102004";

    public static Trie wordTrie;

    @FXML
    private HomescreenController homescreenController;

    public DictionaryApplication() {
        wordTrie = new Trie();
    }

    public void loadFromDatabase() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "SELECT * FROM vocabulary";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String word = resultSet.getString("word_target");
                wordTrie.insert(word);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("ERROR IN LOAD FROM DATABASE");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homescreen.fxml"));
            scene = new Scene(fxmlLoader.load());
            homescreenController = fxmlLoader.getController();
        } catch (Exception e) {
            System.out.println("ERROR IN INIT HOMESCREEN");
            e.printStackTrace();
        }
        loadFromDatabase();

        //name of the window
        stage.setTitle("Dictionary Application");

        //put the scene into the stage
        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(800);

        //display
        stage.show();
        stage.setOnCloseRequest(e -> homescreenController.close());
    }


    public static void main(String[] args) {
        launch();
    }


}