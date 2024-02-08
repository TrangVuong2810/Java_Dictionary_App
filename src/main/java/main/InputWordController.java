package main;

import base.CustomAlert;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Set;

import static main.DictionaryApplication.*;

public class InputWordController {
    @FXML
    private VBox addWord;
    @FXML
    private Label wordLabel;
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
    private Button executeButton;

    public void run() {
        wordLabel.setText("Add New Word");
        executeButton.setOnMouseClicked(event -> {
            String synonyms = stringTrim(synonymInput.getText());
            if (synonyms.isEmpty()) {
                Optional<ButtonType> result = CustomAlert.customConfirmation("ADD WORD",
                        "The synonyms field is empty, are you sure?");
                result.ifPresent(buttonType -> {
                    if (buttonType.getButtonData().equals(ButtonType.YES.getButtonData())) {
                        if(!addWord()) {
                            CustomAlert customAlert = new CustomAlert("ADD WORD ERROR",
                                    "Error occurred, please contact the developers for more information", Alert.AlertType.ERROR);
                        }
                    }
                });
            } else {
                if(!addWord()) {
                    CustomAlert customAlert = new CustomAlert("ADD WORD ERROR",
                            "Error occurred, please contact the developers for more information", Alert.AlertType.ERROR);
                }
            }
        });
    }

    public void run(String selectedWord) {
        wordLabel.setText("Edit Word");
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
            String sql = "SELECT * FROM vocabulary WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, selectedWord);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String word = resultSet.getString("word_target");
                String meaning = resultSet.getString("word_explain");
                String ipa = resultSet.getString("ipa");
                String wordType = resultSet.getString("word_type");
                String synonyms = resultSet.getString("synonyms");
                wordInput.setText(word);
                ipaInput.setText(ipa);
                meaningInput.setText(meaning);
                wordTypeInput.setText(wordType);
                if (synonyms.isEmpty()) {
                    synonymInput.setText("Không tìm thấy từ đồng nghĩa cho từ này");
                } else {
                    synonymInput.setText(synonyms);
                }
            } else {
                CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                        "Cannot find database related to this word, " +
                                "please contact the developers for more information", Alert.AlertType.WARNING);
                ipaInput.setText("Không tìm thấy phát âm cho từ này");
                meaningInput.setText("Không tìm thấy nghĩa cho từ này.");
                wordTypeInput.setText("Không tìm thấy loại từ cho từ này");
                synonymInput.setText("Không tìm thấy từ đồng nghĩa cho từ này");
            }
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                    "Error occurred related to database, " +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        executeButton.setOnMouseClicked(event -> {
            String synonyms = stringTrim(synonymInput.getText());
            if (synonyms.isEmpty()) {
                Optional<ButtonType> result = CustomAlert.customConfirmation("EDIT WORD",
                        "The synonyms field is empty, are you sure?");
                result.ifPresent(buttonType -> {
                    if (buttonType.getButtonData().equals(ButtonType.YES.getButtonData())) {
                        if(!editWord(selectedWord)) {
                            CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                                    "Error occurred, please contact the developers for more information", Alert.AlertType.ERROR);
                        }
                    }
                });
            } else {
                if(!editWord(selectedWord)) {
                    CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                            "Error occurred, please contact the developers for more information", Alert.AlertType.ERROR);
                }
            }
        });
    }

    public boolean editWord(String selectedWord) {
        String word_target = stringTrim(wordInput.getText());
        String word_explain = stringTrim(meaningInput.getText());
        String ipa = stringTrim(ipaInput.getText());
        String word_type = stringTrim(wordTypeInput.getText());
        String synonyms = stringTrim(synonymInput.getText());
        if (word_target.isEmpty() || word_explain.isEmpty() || ipa.isEmpty() || word_type.isEmpty()) {
            CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                    "Required fields must not be empty", Alert.AlertType.ERROR);
            return false;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
            java.lang.String deleteQuery = "DELETE FROM vocabulary WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, selectedWord);

            int rowsAffected = statement.executeUpdate();
            if (!(rowsAffected > 0)) {
                CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                        "Error in deleting old word," +
                                " please contact the developers for more information", Alert.AlertType.ERROR);
                return false;
            }

            String insert = "INSERT INTO vocabulary (word_target, word_explain, ipa, word_type, synonyms) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insert);
            statement.setString(1, word_target);
            statement.setString(2, word_explain);
            statement.setString(3, ipa);
            statement.setString(4, word_type);
            statement.setString(5, synonyms);

            rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                CustomAlert customAlert = new CustomAlert("EDIT WORD",
                        "Edit word successfully", Alert.AlertType.INFORMATION);
                wordTrie.delete(selectedWord);
                wordTrie.insert(word_target);
                return true;
            } else {
                CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                        "Error in adding word to database, " +
                                "please contact the developers for more information", Alert.AlertType.ERROR);
                return false;
            }
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("EDIT WORD ERROR",
                    "Error occurred related to database, " +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    public boolean addWord() {
        String word_target = stringTrim(wordInput.getText());
        String word_explain = stringTrim(meaningInput.getText());
        String ipa = stringTrim(ipaInput.getText());
        String word_type = stringTrim(wordTypeInput.getText());
        String synonyms = stringTrim(synonymInput.getText());
        if (word_target.isEmpty() || word_explain.isEmpty() || ipa.isEmpty() || word_type.isEmpty()) {
            CustomAlert customAlert = new CustomAlert("ADD WORD ERROR",
                    "Required fields must not be empty", Alert.AlertType.ERROR);
            return false;
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
            String sql = "SELECT word_target FROM vocabulary WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, word_target);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                CustomAlert customAlert = new CustomAlert("ADD WORD ERROR",
                        "Word already existed, please add new word", Alert.AlertType.ERROR);
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
                    CustomAlert customAlert = new CustomAlert("ADD WORD",
                            "Adding word successfully", Alert.AlertType.INFORMATION);
                    wordTrie.insert(word_target);
                    return true;
                } else {
                    CustomAlert customAlert = new CustomAlert("ADD WORD ERROR",
                            "Error in adding word to database, please contact" +
                                    "developers for more information", Alert.AlertType.ERROR);
                    return false;
                }
            }
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("ADD WORD ERROR",
                    "Error occurred related to database, " +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    public static String stringTrim(String input) {
        input = input.toLowerCase();

        String trimmedStart = input.replaceAll("^[\\s\\p{Punct}]+", "");

        String trimmed = trimmedStart.replaceAll("[\\s\\p{Punct}]+$", "");

        return trimmed;
    }
}
