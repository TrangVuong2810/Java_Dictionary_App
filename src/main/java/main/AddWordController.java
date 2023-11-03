package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static main.DictionaryApplication.*;

public class AddWordController {
    @FXML
    private TextField wordInput;
    @FXML
    private TextField meaningInput;
    @FXML
    private TextField wordTypeInput;
    @FXML
    private TextField ipaInput;
    @FXML
    private TextField synonymInput;
    @FXML
    private Button addWordButton;

    public void run() {
        addWordButton.setOnMouseClicked(event -> {
            if(addWord()) {
                System.out.println("add word successfully");
            }
            else {
                System.out.println("unable to add word");
            }
        });
    }

    public boolean addWord() {
        String word_target = stringTrim(wordInput.getText());
        String word_explain = stringTrim(meaningInput.getText());
        String ipa = stringTrim(ipaInput.getText());
        String word_type = stringTrim(wordTypeInput.getText());
        String synonyms = stringTrim(synonymInput.getText());
        if (word_target.isEmpty() || word_explain.isEmpty() || ipa.isEmpty() || word_type.isEmpty()) {
            System.out.println("INVALID INPUT");
            return false;
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
            String sql = "SELECT word_target FROM vocabulary WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, word_target);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("WORD EXISTED");
                return false;
            } else {
                String insert = "INSERT INTO vocabulary (word_target, word_explain, ipa, word_type, synonyms) VALUES (?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(insert);
                statement.setString(1, word_target);
                statement.setString(2, word_explain);
                statement.setString(3, ipa);
                statement.setString(4, word_type);
                statement.setString(5, synonyms);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("word in vocabulary added successfully.");
                    wordTrie.insert(word_target);
                    return true;
                } else {
                    System.out.println("error in adding word in vocabulary database");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String stringTrim(String input) {
        String trimmedStart = input.replaceAll("^[\\s\\p{Punct}]+", "");

        String trimmed = trimmedStart.replaceAll("[\\s\\p{Punct}]+$", "");

        return trimmed;
    }
}
