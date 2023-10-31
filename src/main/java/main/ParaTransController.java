package main;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParaTransController {
    @FXML
    private Button swapButton;
    @FXML
    private Label sourceLangLabel;
    @FXML
    private Label targetLangLabel;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private Button translateButton;
    @FXML
    private Button sourceAudioButton;
    @FXML
    private Button targetAudioButton;

    private static final String AUDIO_PATH_EN = "src/main/resources/audio/ParaEN";
    private static final String AUDIO_PATH_VI = "src/main/resources/audio/ParaVI";
    private ExecutorService executorService;
    private MediaPlayer mediaPlayerSource;
    private MediaPlayer mediaPlayerTarget;
    private String sourceLang;
    private String targetLang;

    public void setUp(){
        sourceLangLabel.setText("Tiếng Anh");
        sourceLang = "en";
        targetLangLabel.setText("Tiếng Việt");
        targetLang = "vi";
    }

    public void initializeExecutorService() {
        executorService = Executors.newFixedThreadPool(2);
    }

    public void run() {
        swapButton.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String temp = sourceLang;
                sourceLang = targetLang;
                targetLang = temp;
                if (sourceLang.equals("en")) sourceLangLabel.setText("Tiếng Anh");
                else sourceLangLabel.setText("Tiếng Việt");
                if (targetLang.equals("vi")) targetLangLabel.setText("Tiếng Việt");
                else targetLangLabel.setText("Tiếng Anh");
            }
        });

        initializeExecutorService();
        handlePlayAudio();
        translateButton.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                if (mediaPlayerTarget != null) mediaPlayerTarget.dispose();
                if (mediaPlayerSource != null) mediaPlayerSource.dispose();
                String searchQuery = inputTextArea.getText();
                clearOutputTextArea();
                translate(searchQuery);
            }
        });
    }

    ChangeListener<String> inputTextChangeListener = (observable, oldValue, newValue) -> {
        if (mediaPlayerSource != null) {
            if (mediaPlayerSource.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayerSource.stop();
            }
            mediaPlayerSource.dispose();
        }
    };

    public void handlePlayAudio() {
        sourceAudioButton.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                inputTextArea.textProperty().removeListener(inputTextChangeListener);
                inputTextArea.textProperty().addListener(inputTextChangeListener);
                if (mediaPlayerSource != null) {
                    if (mediaPlayerSource.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayerSource.stop();
                    }
                    mediaPlayerSource.play();
                }
            }
        });
        targetAudioButton.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                if (mediaPlayerTarget != null) {
                    if (mediaPlayerTarget.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayerTarget.stop();
                    }
                    mediaPlayerTarget.play();
                }
            }
        });
    }

    public void translate(String searchQuery) {
        Runnable translationTask = () -> {
            performTranslation(searchQuery, sourceLang, targetLang);
        };

        Runnable textToSpeechTask;
        if (sourceLang.equals("en")) {
            textToSpeechTask = () -> connectTextToSpeech(searchQuery, "en-us", AUDIO_PATH_EN);
        }
        else {
            textToSpeechTask = () -> connectTextToSpeech(searchQuery, "vi-vn", AUDIO_PATH_VI);
        }

        executorService.submit(textToSpeechTask);
        executorService.submit(translationTask);
    }


    public void close() {
        if (executorService != null) executorService.shutdownNow();
        if (mediaPlayerSource != null) {
            if (mediaPlayerSource.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayerSource.stop();
            }
            mediaPlayerSource.dispose();
        }
        if (mediaPlayerTarget != null) {
            if (mediaPlayerTarget.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayerTarget.stop();
            }
            mediaPlayerTarget.dispose();
        }
        inputTextArea.textProperty().removeListener(inputTextChangeListener);
        System.out.println("SHUT DOWN PARA");
    }

    public void connectTextToSpeech(String text, String lang, String filePath) {
        String apiKey = "9296283913e24e449e7b8cfa8366c29a";
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlString = "http://api.voicerss.org/?key=" + apiKey + "&hl=" + lang + "&src=" + encodedText;
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                File audioFile = new File(filePath);
                OutputStream outputStream = new FileOutputStream(audioFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                if (filePath.equals(AUDIO_PATH_EN)) {
                    if (sourceLang.equals("en")) mediaPlayerSource = new MediaPlayer(new Media(audioFile.toURI().toString()));
                    else mediaPlayerTarget = new MediaPlayer(new Media(audioFile.toURI().toString()));
                }
                else {
                    if (sourceLang.equals("vi")) {
                        System.out.println("EYYY");
                        mediaPlayerSource = new MediaPlayer(new Media(audioFile.toURI().toString()));
                    }
                    else mediaPlayerTarget = new MediaPlayer(new Media(audioFile.toURI().toString()));
                }
                //mediaPlayer.play();

                outputStream.close();
                inputStream.close();
            } else {
                System.out.println("Request failed with response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            System.out.println("ERROR IN PARA AUDIO");
            e.printStackTrace();
        }
    }

    public void clearOutputTextArea() {
        outputTextArea.clear();
    }

    public void performTranslation(String text, String sourceLang, String targetLang) {
        try {
            String API_URL = "https://translate.googleapis.com/translate_a/single";
            String API_PARAMS = "?client=gtx&dt=t&ie=UTF-8&oe=UTF-8&otf=1&ssel=0&tsel=0&kc=7" +
                    "&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss";
            String[] sentences = text.split("\\.");

            StringBuilder translationBuilder = new StringBuilder();

            for (String s : sentences) {
                String sentence = s.trim();
                if (sentence.isEmpty()) {
                    continue;
                }
                String encodedSentence = URLEncoder.encode(sentence.trim(), "UTF-8");
                String urlStr = API_URL + API_PARAMS + "&sl=" + sourceLang + "&tl=" + targetLang + "&q=" + encodedSentence;

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStreamReader.close();
                    inputStream.close();

                    translationBuilder.append(parseTranslation(response.toString())).append(". ");
                } else {
                    System.out.println("Request failed with response code: " + responseCode);
                }
                connection.disconnect();
            }

            String translation = translationBuilder.toString().trim();

            outputTextArea.setText(translation);

            if (targetLang.equals("en")) {
                connectTextToSpeech(translation, "en-us", AUDIO_PATH_EN);
            }
            else {
                connectTextToSpeech(translation, "vi-vn", AUDIO_PATH_VI);
            }

        } catch (Exception e) {
            System.out.println("ERROR IN API GG TRANS AND VI_VN AUDIO");
            e.printStackTrace();
        }
    }

    private static String parseTranslation(String response) {
        if (response.isEmpty()) {
            return "";
        }
        int startIndex = response.indexOf("[[\"") + 3;
        int endIndex = response.indexOf("\"", startIndex);
        if (endIndex != -1) {
            return response.substring(startIndex, endIndex);
        } else {
            return null;
        }
    }

}

