package main;

import base.*;
import game.Game;
import game.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static main.DictionaryApplication.wordTrie;

public class GameController {
    public static final String DB_URL = "jdbc:mysql://localhost:3310/dictionary";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "123456789";
    public static final int MAX_QUES = 10;
    public static final int MIN_QUES = 4;
    private int cnt = 0;

    @FXML
    private Game game;
    @FXML
    private Button removeBtn, removeAllBtn, quizBtn, hangmanBtn;
    @FXML
    private ListView<String> listView = new ListView<>();
    @FXML
    private TextField textField = new TextField();
    @FXML
    private TableView<Word> tableView = new TableView<>();
    @FXML
    private TableColumn<Word, String> wTargetCol  = new TableColumn<>();
    @FXML
    private TableColumn<Word, String> wExplainCol = new TableColumn<>();
    private final ObservableList<String> wordList = FXCollections.observableArrayList();
    private final ObservableList<Word> QList = FXCollections.observableArrayList();
    public void setUpGameController() {
        setUp();
        setUpTable();
        search();
    }

    public void search() {
        textField.setOnKeyReleased(event -> {
            String searchQuery = textField.getText();
            searchWords(searchQuery);
        });
    }

    public void setUp() {
        cnt = 0;
        searchWords("");
        displayWord();
    }

    private void displayWord() {
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            double padding = 10;
            double clickX = event.getX();
            double clickY = event.getY();
            double leftPadding = listView.getInsets().getLeft();
            double topPadding = listView.getInsets().getTop();

            if (clickX < leftPadding || clickX > (listView.getWidth() - leftPadding - padding) ||
                    clickY < topPadding || clickY > (listView.getHeight() - topPadding - padding)) {
                event.consume();
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && cnt < MAX_QUES) {
                String selectedWord = listView.getSelectionModel().getSelectedItem();
                String meaning = "";
                if (!checkDuplicate(selectedWord)){
                    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "SELECT * FROM vocabulary WHERE word_target = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, selectedWord);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            meaning = resultSet.getString("word_explain");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    QList.add(new Word(selectedWord, meaning));
                    cnt++;
                } else {
                    Alert duplicateAlert = new Alert(Alert.AlertType.ERROR);
                    duplicateAlert.setHeaderText("Trùng lặp từ!");
                    duplicateAlert.setContentText("Không thể chọn từ này!");
                    duplicateAlert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Vượt quá số câu hỏi cho phép!");
                alert.setContentText("Không thể chọn quá " + MAX_QUES + " từ!");
                alert.show();
            }
        });
    }

    private boolean checkDuplicate(String newWord) {
        for (Word w : QList) {
            if (w.getWord_target().equals(newWord)) {
                return true;
            }
        }
        return false;
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

    private void searchWords(String searchQuery) {
        wordList.clear();
        try {
            List<String> words = getAllWordWithPrefix(wordTrie.getRoot(), searchQuery);
            wordList.addAll(words);
            listView.setItems(wordList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayWord();
    }

    private void setUpTable() {
        QList.clear();
        wTargetCol.setCellValueFactory(new PropertyValueFactory<Word, String>("word_target"));
        wExplainCol.setCellValueFactory(new PropertyValueFactory<Word, String>("word_explain"));
        tableView.setItems(QList);
    }

    public void startGame() {
        Stage gameStage = new Stage();
        Scene scene = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/game/gamescreen.fxml"));
            scene = new Scene(fxmlLoader.load());
            game = fxmlLoader.getController();
        } catch (Exception e) {
            System.out.println("ERROR IN INIT");
            e.printStackTrace();
        }
        gameStage.initModality(Modality.APPLICATION_MODAL);
        gameStage.setTitle("Game");
        gameStage.setScene(scene);
        gameStage.setMinHeight(500);
        gameStage.setMinWidth(600);
        gameStage.show();
        gameStage.setOnCloseRequest(e -> {
            gameStage.close();
        });
        game.startTimer();
        game.questionCount.setText("Question: " + "1/" + QList.size());
        game.setQuestionList(QList);
    }

    public void startQuizGame(ActionEvent event) {
        if (QList.size() < MIN_QUES) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Không đủ số từ tối thiểu!");
            alert.setContentText("Vui lòng chọn thêm từ để bắt đầu!");
            alert.show();
        } else {
            startGame();
            game.quizGame();
        }
    }

    public void startHangmanGame(ActionEvent event) {
        if (QList.size() < MIN_QUES) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Không đủ số từ tối thiểu!");
            alert.setContentText("Vui lòng chọn thêm từ để bắt đầu!");
            alert.show();
        } else {
            startGame();
            game.hangManGame();
        }
    }

    public void removeSelectedRow(ActionEvent event) {
        Word selectedWord = tableView.getSelectionModel().getSelectedItem();
        if (selectedWord == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Không thể loại bỏ từ!");
            alert.setContentText("Hãy chọn từ cần loại bỏ!");
            alert.show();
            return;
        } else {
            tableView.getItems().remove(selectedWord);
            QList.remove(selectedWord);
            cnt--;
        }
    }

    public void removeAll(ActionEvent event) {
        if (QList.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Không hợp lệ!");
            alert.setContentText("Không có từ để loại bỏ!");
            alert.show();
            return;
        } else {
            cnt = 0;
            tableView.getItems().removeAll(QList);
            QList.removeAll();
        }
    }
}
