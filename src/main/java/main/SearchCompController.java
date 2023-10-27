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
            } else if (collection instanceof WordLinkedList) {
                //words = getAllWordWithPrefix(wordHistory, searchQuery);
            } else {
                throw new IllegalArgumentException("Loại dữ liệu không được hỗ trợ");
            }


            wordList.addAll(words);
            listView.setItems(wordList);
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
        } else if (collection instanceof WordHistoryLinkedList) {
            listView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    String selectedWord = listView.getSelectionModel().getSelectedItem();
                    wordCompController.displayWord(selectedWord);
                }
            });
        } else if (collection instanceof WordLinkedList) {
            //words = getAllWordWithPrefix(wordHistory, searchQuery);
        } else {
            throw new IllegalArgumentException("Loại dữ liệu không được hỗ trợ");
        }

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
