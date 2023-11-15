package main;

import base.CustomAlert;
import com.fasterxml.jackson.core.JsonToken;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    private boolean isTargetAudioAvailable = false;

    public void setUp(){
        sourceLangLabel.setText("Tiếng Anh");
        sourceLang = "en";
        targetLangLabel.setText("Tiếng Việt");
        targetLang = "vi";
        //mediaPlayerSource = new MediaPlayer();
        //mediaPlayerTarget = new MediaPlayer();
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


    public void playSourceAudio(int cnt) {
        System.out.println("EY: " + cnt);
        if (mediaPlayerSource != null) {
            sourceAudioButton.getStyleClass().removeAll("loading");
            mediaPlayerSource.play();
        } else if (cnt == 5) {
            sourceAudioButton.getStyleClass().removeAll("loading");
            CustomAlert customAlert = new CustomAlert("AUDIO ERROR",
                    "Error in source audio file, please check your connection\n" +
                    "or contact devs for more information", Alert.AlertType.ERROR);
        } else {
            System.out.println("WAIT 1 SEC");
            Thread waitThread = new Thread(() -> {
                if (mediaPlayerSource == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(() -> {
                    playSourceAudio(cnt + 1);
                });
            });
            waitThread.start();
        }
    }


    public void handlePlayAudio() {
        sourceAudioButton.setOnMouseClicked(event -> {
            inputTextArea.textProperty().removeListener(inputTextChangeListener);
            inputTextArea.textProperty().addListener(inputTextChangeListener);
            if (event.getClickCount() == 1) {
                if (mediaPlayerSource != null) {
                    mediaPlayerSource.stop();
                    mediaPlayerSource.play();
                } else {
                    sourceAudioButton.getStyleClass().add("loading");
                    playSourceAudio(0);

                }
            }
        });
        targetAudioButton.setOnMouseClicked(event -> {
            if (!isTargetAudioAvailable) {
                CustomAlert customAlert = new CustomAlert("AUDIO ERROR",
                        "Please check your target text area\n" +
                        "for more info, contact devs", Alert.AlertType.ERROR);
                return;
            }
            if (event.getClickCount() == 1) {
                if (mediaPlayerTarget != null) {
                    mediaPlayerTarget.stop();
                    mediaPlayerTarget.play();
                } else {
                    targetAudioButton.getStyleClass().add("loading");
                    playSourceAudio(0);
                }
            }
        });
    }

    public void translate(String searchQuery) {
        Runnable translationTask = () -> {
            translateButton.getStyleClass().add("loading");
            performTranslation(searchQuery, sourceLang, targetLang);
        };

         executorService.submit(() -> {
            if (sourceLang.equals("en")) {
                connectTextToSpeech(searchQuery, "en-us", AUDIO_PATH_EN);
            }
            else {
                connectTextToSpeech(searchQuery, "vi-vn", AUDIO_PATH_VI);
            }
        });

        executorService.submit(translationTask);
    }


    public void close() {
        translateButton.getStyleClass().removeAll("loading");
        if (executorService != null) executorService.shutdownNow();
        if (mediaPlayerSource != null) {
            mediaPlayerSource.stop();
            mediaPlayerSource.dispose();
        }
        if (mediaPlayerTarget != null) {
            mediaPlayerTarget.stop();
            mediaPlayerTarget.dispose();
        }
        inputTextArea.textProperty().removeListener(inputTextChangeListener);
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
                    if (sourceLang.equals("en")) {
                        mediaPlayerSource = new MediaPlayer(new Media(audioFile.toURI().toString()));
                        mediaPlayerSource.setOnReady(() -> {
                            sourceAudioButton.getStyleClass().removeAll("loading");
                        });
                    }
                    else {
                        mediaPlayerTarget = new MediaPlayer(new Media(audioFile.toURI().toString()));
                        mediaPlayerTarget.setOnReady(() -> {
                            targetAudioButton.getStyleClass().removeAll("loading");
                        });
                    }
                }
                else {
                    if (sourceLang.equals("vi")) {
                        mediaPlayerSource = new MediaPlayer(new Media(audioFile.toURI().toString()));
                        mediaPlayerSource.setOnReady(() -> {
                            sourceAudioButton.getStyleClass().removeAll("loading");
                        });
                    }
                    else {
                        mediaPlayerTarget = new MediaPlayer(new Media(audioFile.toURI().toString()));
                        mediaPlayerTarget.setOnReady(() -> {
                            targetAudioButton.getStyleClass().removeAll("loading");
                        });
                    }
                }
                outputStream.close();
                inputStream.close();
            } else {
                CustomAlert customAlert = new CustomAlert("AUDIO ERROR",
                        "Unable to connect to audio API, response code: " + responseCode +
                        "\nPlease contact devs for more info", Alert.AlertType.ERROR);
                System.out.println("Request failed with response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("AUDIO ERROR",
                    "Unable to connect to audio API, response code: " +
                            "\nPlease contact devs for more info", Alert.AlertType.ERROR);
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


                System.out.println("CHECK");
                int responseCode = connection.getResponseCode();
                System.out.println("HUHHHH");
                System.out.println(responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("WAIT WUT");
                    isTargetAudioAvailable = true;
                    reader.close();
                    inputStreamReader.close();
                    inputStream.close();

                    translationBuilder.append(parseTranslation(response.toString())).append(". ");
                } else {
                    CustomAlert customAlert = new CustomAlert("TRANSLATION ERROR",
                            "Unable to connect to translate API\n Request fail with response" +
                            "code: " + responseCode + "\nPlease contact devs for more info", Alert.AlertType.ERROR);
                    System.out.println("Request failed with response code: " + responseCode);
                    isTargetAudioAvailable = false;
                }
                connection.disconnect();
            }

            String translation = translationBuilder.toString().trim();

            outputTextArea.setText(translation);

            if (isTargetAudioAvailable) {
                if (targetLang.equals("en")) {
                    connectTextToSpeech(translation, "en-us", AUDIO_PATH_EN);
                }
                else {
                    connectTextToSpeech(translation, "vi-vn", AUDIO_PATH_VI);
                }
            }

        } catch (Exception e) {
            Platform.runLater(() -> {
                CustomAlert customAlert = new CustomAlert("ERROR",
                        "Please check your connection\n" +
                                "for more info, contact the devs", Alert.AlertType.ERROR);
        });
            e.printStackTrace();

        } finally {
            translateButton.getStyleClass().removeAll("loading");
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

