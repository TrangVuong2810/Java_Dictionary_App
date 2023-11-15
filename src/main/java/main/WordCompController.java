package main;

import base.CustomAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

import static main.DictionaryApplication.*;

public class WordCompController {
    @FXML
    private Button wordPronunButton;
    @FXML
    private Button wordBookmarkButton;
    @FXML
    private Button wordEditButton;
    @FXML
    private Button wordDeleteButton;
    @FXML
    private TextArea wordMeaning;
    @FXML
    private Label wordLabel;
    @FXML
    private TextField ipaLabel;
    @FXML
    private Label wordTypeLabel;
    @FXML
    private Label wordSynonymLabel;
    @FXML
    public TextArea wordSynonym;

    private MediaPlayer mediaPlayer;

    private static final String AUDIO_PATH = "src/main/resources/audio/audio";
    private String deleteQuery;

    public void displayWord(String selectedWord) {
        wordSynonymLabel.getStyleClass().add("active");
        wordPronunButton.getStyleClass().add("active");
        wordBookmarkButton.getStyleClass().add("active");
        wordEditButton.getStyleClass().add("active");
        wordDeleteButton.getStyleClass().add("active");
        wordLabel.setText(selectedWord);

        Thread textToSpeechThread = new Thread(() -> {
            getPronunAudio(selectedWord);
        });
        textToSpeechThread.start();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
            String sql = "SELECT * FROM vocabulary WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, selectedWord);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String meaning = resultSet.getString("word_explain");
                String ipa = "/" + resultSet.getString("ipa") + "/";
                String wordType = resultSet.getString("word_type");
                String synonyms = resultSet.getString("synonyms");
                ipaLabel.setText(ipa);
                wordMeaning.setText(meaning);
                wordTypeLabel.setText(wordType);
                if (synonyms == null || synonyms.isEmpty()) {
                    wordSynonym.setText("Không tìm thấy từ đồng nghĩa cho từ này");
                } else {
                    wordSynonym.setText(synonyms);
                }


                wordHistory.add(selectedWord);
            } else {
                ipaLabel.setText("Không tìm thấy phát âm cho từ này");
                wordMeaning.setText("Không tìm thấy nghĩa cho từ này.");
                wordTypeLabel.setText("Không tìm thấy loại từ cho từ này");
                wordSynonym.setText("Không tìm thấy từ đồng nghĩa cho từ này");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        wordPronunButton.setOnMouseClicked(event -> {
            try {
                textToSpeechThread.join();
                if (event.getClickCount() == 1) {
                    if (mediaPlayer != null) {
                        mediaPlayer.play();
                    } else {
                        CustomAlert customAlert = new CustomAlert("WORD AUDIO ERROR",
                                "please check your connection or " + "\n" +
                                "contact devs for more info", Alert.AlertType.ERROR);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("HAVENT FINISHED GETTING AUDIO");
                wordPronunButton.getStyleClass().add("loading");
                e.printStackTrace();
            }
        });


        wordBookmarkButton.setOnMouseClicked(event -> {
            bookmarkWord(selectedWord);
        });

        wordDeleteButton.setOnMouseClicked(mouseEvent -> {
            deleteWord(selectedWord);
        });

        wordEditButton.setOnMouseClicked(mouseEvent -> {
            editWord(selectedWord);
        });
    }


    public void bookmarkWord(String selectedWord) {
        System.out.println("BOOKMARK WORD: " + selectedWord);
        boolean check = !wordBookmark.checkDuplicateValue(selectedWord);
        if (wordBookmark.add(selectedWord)) {
            if (check) CustomAlert.popUp("Un-bookmarked word");
            else CustomAlert.popUp("Bookmark word");
        }
    }

    public void resetStyle() {
        wordSynonymLabel.getStyleClass().removeAll("active");
        wordPronunButton.getStyleClass().removeAll("active");
        wordBookmarkButton.getStyleClass().removeAll("active");
        wordEditButton.getStyleClass().removeAll("active");
        wordDeleteButton.getStyleClass().removeAll("active");
    }

    public void deleteWord(String selectedWord) {
        wordTrie.delete(selectedWord);
        resetStyle();
        wordLabel.setText("");
        ipaLabel.clear();
        wordTypeLabel.setText("");
        wordMeaning.clear();
        wordSynonym.clear();
        Thread deleteWordInHistory = new Thread(() -> {
                if (wordHistory.contains(selectedWord)) {
                    Platform.runLater(() -> {
                        wordHistory.delete(selectedWord);
                    });
                }
            });
        Thread deleteWordInBookmark = new Thread(() -> {
                if (wordBookmark.contains(selectedWord)) {
                    Platform.runLater(() -> {
                        wordBookmark.delete(selectedWord);
                    });
                }
            });
        deleteWordInHistory.start();
        deleteWordInBookmark.start();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
            java.lang.String deleteQuery = "DELETE FROM vocabulary WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, selectedWord);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("word in vocabulary delete successfully.");
            } else {
                System.out.println("error in deleting word in vocabulary database");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void editWord(String selectedWord) {
        Stage editWindow = new Stage();
        Scene editScene = null;
        EditWordController editWordController = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editWord.fxml"));
            editScene = new Scene(fxmlLoader.load());
            editWordController = fxmlLoader.getController();
        } catch (Exception e) {
            System.out.println("ERROR IN INIT");
            e.printStackTrace();
        }
        editWindow.setTitle("Edit Word");
        editWindow.setScene(editScene);
        editWindow.initModality(Modality.APPLICATION_MODAL);
        editWindow.setMinWidth(600);
        editWindow.setMinHeight(500);
        editWindow.show();
        editWindow.setOnCloseRequest(e -> {
            editWindow.close();
        });

        editWordController.run(selectedWord);
    }

    public void getPronunAudio(String selectedWord) {
        if (selectedWord == null || selectedWord.isEmpty()) return;
        String apiKey = "9296283913e24e449e7b8cfa8366c29a";
        try {
            String encodedText = URLEncoder.encode(selectedWord, "UTF-8");
            String urlString = "http://api.voicerss.org/?key=" + apiKey + "&hl=en-us&src=" + encodedText;
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                File audioFile = new File(AUDIO_PATH);
                OutputStream outputStream = new FileOutputStream(audioFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                mediaPlayer = new MediaPlayer(new Media(audioFile.toURI().toString()));
                mediaPlayer.setOnReady(() -> {
                    wordPronunButton.getStyleClass().removeAll("loading");
                });

                outputStream.close();
                inputStream.close();
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            System.out.println("ERROR IN CONNECTIN TO VOICE API");

            e.printStackTrace();
        }
    }

    public String getSynonym(String selectedWord) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.api-ninjas.com/v1/thesaurus?word="+ selectedWord))
                    .header("Content-Type", "application/json")
                    .header("X-Api-Key", "x27YH/2rrJDXeMEQAUMk2g==UcM3p6vL2yOY0cXP")
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            //wordSynonym.setText(extractSynonyms(responseBody));
            return extractSynonyms(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String extractSynonyms(String input) {
        List<String> synonyms = new ArrayList<>();

        int startIndex = input.indexOf('[');
        int endIndex = input.indexOf(']');
        int cnt = 0;

        if (startIndex != -1 && endIndex != -1) {
            String synonymsStr = input.substring(startIndex + 1, endIndex);
            String[] words = synonymsStr.split(",\\s*");

            for (String word : words) {
                synonyms.add(word.replaceAll("\"", ""));
                cnt++;
                if (cnt >= 10) {
                    break;
                }
            }
        }
        return String.join(", ", synonyms);
    }

//    public static void main(String[] args) {
//        WordCompController wordCompController = new WordCompController();
//        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
//            String sql = "SELECT word_target, synonyms FROM vocabulary";
//            PreparedStatement statement = connection.prepareStatement(sql);
//
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                String word = resultSet.getString("word_target");
//                String synonyms = resultSet.getString("synonyms");
//                if (!synonyms.isEmpty()) {
//                    String insert = "UPDATE vocabulary SET synonyms = ? WHERE word_target = ?";
//                    statement = connection.prepareStatement(insert);
//                    statement.setString(1, wordCompController.getSynonym(word));
//                    statement.setString(2, word);
//
//                    int rowsAffected = statement.executeUpdate();
//                    if (rowsAffected > 0) {
//                        System.out.println("SUCCESS: " + word);
//                    } else {
//                        System.out.println("FAIL: " + word);
//
//                    }
//                }
//
//            }
//        }
//        catch (Exception e) {
//            System.out.println("ERROR");
//            e.printStackTrace();
//        }
//    }

}