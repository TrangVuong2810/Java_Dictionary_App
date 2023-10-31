package main;

import base.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static main.DictionaryApplication.wordBookmark;
import static main.DictionaryApplication.wordHistory;

public class SearchCompController {
    public static final String DB_URL = "jdbc:mysql://localhost:3307/dictionary";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "28102004";

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

    public void search(Object collection) {
        textField.setOnKeyReleased(event -> {
            String searchQuery = textField.getText();
            searchWords(searchQuery, collection);
        });
    }

    public void setUp(Object collection) {
        searchWords("", collection);
        displayWord(collection);
    }

    private void searchWords(String searchQuery, Object collection) {
        wordList.clear();
        try  {
            List<String> words = new ArrayList<>();
            if (collection instanceof Trie) {
                words = getAllWordWithPrefix(DictionaryApplication.wordTrie.getRoot(), searchQuery);
            } else if (collection instanceof WordHistoryLinkedList) {
                words = getAllWordWithPrefix(wordHistory, searchQuery);
            } else if (collection instanceof WordBookmarkLinkedList) {
                words = getAllWordWithPrefix(wordBookmark, searchQuery);
            } else {
                throw new IllegalArgumentException("Loại dữ liệu không được hỗ trợ");
            }

            wordList.addAll(words);
            listView.getItems().setAll(wordList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayWord(collection);

    }
    public void displayWord(Object collection) {
        if (collection instanceof Trie) {
            listView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    String selectedWord = listView.getSelectionModel().getSelectedItem();
                    wordCompController.displayWord(selectedWord);

                    wordHistory.add(selectedWord);
                }
            });
        } else if (collection instanceof WordHistoryLinkedList ||
                    collection instanceof WordBookmarkLinkedList) {
            listView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    String selectedWord = listView.getSelectionModel().getSelectedItem();
                    wordCompController.displayWord(selectedWord);
                }
            });
        } else {
            throw new IllegalArgumentException("Loại dữ liệu không được hỗ trợ");
        }

    }

    private List<String> getAllWordWithPrefix(TrieNode node, String prefix) {
        List<String> matchedWords = new ArrayList<>();
        if (node == null) {
            return matchedWords;
        }

        recurseNode(node, prefix, "", matchedWords);

        return matchedWords;
    }

    private void recurseNode(TrieNode node, String prefix, String currentWord,
                             List<String> words) {
        if (node.isEndOfWord()) {
            words.add(currentWord);
        }
        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            char c = entry.getKey();
            TrieNode childNode = entry.getValue();

            if (currentWord.length() < prefix.length() && c == prefix.charAt(currentWord.length())) {
                recurseNode(childNode, prefix, currentWord + c, words);
            } else if (currentWord.length() >= prefix.length()) {
                recurseNode(childNode, prefix, currentWord + c, words);
            }
        }
    }


    private List<String> getAllWordWithPrefix(WordLinkedList<String> words, String prefix) {
        List<String> matchedWords = new LinkedList<>();

        for (String word : words) {
            if (word.startsWith(prefix)) {
                matchedWords.add(word);
            }
        }

        return matchedWords;
    }

}
