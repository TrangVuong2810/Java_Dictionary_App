package main;

import base.TrieNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

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

    private final ObservableList<String> wordList = FXCollections.observableArrayList();

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

    public void setUp() {
        searchWords("");
    }

    private void searchWords(String searchQuery) {
        wordList.clear();

        try  {
            List<String> matchedWords = new ArrayList<>();
            matchedWords = getAllWordWithPrefix(DictionaryApplication.wordTrie.getRoot(), searchQuery);

            wordList.addAll(matchedWords);
            listView.setItems(wordList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedWord = listView.getSelectionModel().getSelectedItem();
                wordCompController.displayWord(selectedWord);
            }
        });


    }

    private List<String> getAllWordWithPrefix(TrieNode node, String prefix) {

        List<String> matchedWords = new ArrayList<>();

        if(node == null) return matchedWords;

        recurseNode(node, prefix, "", matchedWords, true);

        return matchedWords;

    }

    private void recurseNode(TrieNode node, String prefix, String currentWord,
                             List<String> words, boolean matches) {

        if(node.isEndOfWord() && matches) {
            words.add(currentWord);
        }

        if (matches) {
            for(Character c : node.getChildren().keySet()) {

                String newWord = currentWord + c;

                boolean newMatches = newWord.startsWith(prefix);

                recurseNode(node.getChildren().get(c), prefix, newWord, words, newMatches);

            }
        }

    }

}
