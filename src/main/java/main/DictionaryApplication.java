package main;

import base.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DictionaryApplication extends Application {
    public static final String DB_URL = "jdbc:mysql://localhost:3307/dictionary";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "28102004";

    public static Trie wordTrie;
    public static WordLinkedList<String> wordBookmark = new WordBookmarkLinkedList<>("bookmark");
    public static WordLinkedList<String> wordHistory = new WordHistoryLinkedList<>("history");

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
            CustomAlert customAlert = new CustomAlert("INIT APP ERROR",
                    "Error occurred related to database, " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
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
            CustomAlert customAlert = new CustomAlert("INIT HOMESCREEN ERROR",
                    "Error occurred initiating homescreen " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        loadFromDatabase();
        wordHistory.setUp();
        wordBookmark.setUp();

        stage.setTitle("Dictionary Application");

        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(800);

        stage.show();
        stage.setOnCloseRequest(e -> homescreenController.close());
    }


    public static void main(String[] args) {
        launch();
    }


}