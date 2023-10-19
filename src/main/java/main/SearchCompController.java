package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.*;

public class SearchCompController {
    protected static final String DB_URL = "jdbc:mysql://localhost:3307/dictionary";
    protected static final String DB_USER = "root";
    protected static final String DB_PASSWORD = "28102004";

    @FXML
    private VBox wordComp;
    @FXML
    private WordCompController wordCompController;
    @FXML
    private ListView<String> listView = new ListView<>();
    @FXML
    private TextField textField = new TextField();

    private final ObservableList<String> wordList = FXCollections.observableArrayList();;

    public SearchCompController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("wordComp.fxml"));
            wordCompController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void search() {
        textField.setOnKeyReleased(event -> {
            String searchQuery = textField.getText();
            searchWords(searchQuery);
        });
    }

    private void searchWords(String searchQuery) {
        wordList.clear();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT word_target FROM vocabulary WHERE word_target LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, searchQuery + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String word_targetString = resultSet.getString("word_target");
                wordList.add(word_targetString);
            }
            listView.setItems(wordList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedWord = listView.getSelectionModel().getSelectedItem();
                wordCompController.displayWord(selectedWord);
            }
        });


    }



}
