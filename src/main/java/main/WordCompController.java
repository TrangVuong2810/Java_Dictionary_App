package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.*;

import java.io.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static main.SearchCompController.*;

public class WordCompController {
    @FXML
    private Button pronunButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextArea wordMeaning;
    @FXML
    private Label wordLabel;
    @FXML
    private TextField ipaLabel;
    @FXML
    private Label wordTypeLabel;

    private MediaPlayer mediaPlayer;

    private static final String AUDIO_PATH = "src/resources/audio/audio";

    public void displayWord(String selectedWord) {
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
                ipaLabel.setText(ipa);
                wordMeaning.setText(meaning);
                wordTypeLabel.setText(wordType);
            } else {
                ipaLabel.setText("Không tìm thấy phát âm cho từ này");
                wordMeaning.setText("Không tìm thấy nghĩa cho từ này.");
                wordTypeLabel.setText("Không tìm thấy loại từ cho từ này");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pronunButton.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                mediaPlayer.play();
            }
        });
    }


    public void bookmarkWord() {

    }

    public void deleteWord() {

    }

    public void editWord() {

    }

    @FXML
    private TextArea textArea;

    public void getPronunAudio(String selectedWord) {
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

                outputStream.close();
                inputStream.close();
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}