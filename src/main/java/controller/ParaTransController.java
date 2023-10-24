package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParaTransController {
    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea outputTextArea;

    private static final String API_ENDPOINT = "https://api.example.com/text-to-speech";
    private static final String AUDIO_PATH = "src/main/resource/audio/ParaAudio";
    private ExecutorService executorService;
    private TextToSpeechConnector textToSpeechENConnector;
    private TextToSpeechConnector textToSpeechVNConnector;
    private MediaPlayer mediaPlayer;

    public ParaTransController() {
        executorService = Executors.newFixedThreadPool(3);
        textToSpeechENConnector = new TextToSpeechConnector();
        textToSpeechVNConnector = new TextToSpeechConnector();
    }

    public void run() {
        Runnable translationTask = () -> {
            // Thực hiện việc dịch văn bản ở đây
            //String translation = performTranslation(originalText);
            performTranslation();
            //outputTextArea.setText(translation);
        };

        // Tác vụ phát ra giọng nói
        Runnable textToSpeechENTask = () -> {
            // Thực hiện việc phát ra giọng nói từ văn bản đã dịch
            //String translation = translationTextArea.getText();
            //performTranslation();
            //textToSpeechConnector.connect(translation);
            System.out.println("TEXT - SPEECH EN");
        };

        Runnable textToSpeechVNTask = () -> {
            // Thực hiện việc phát ra giọng nói từ văn bản đã dịch
            //String translation = translationTextArea.getText();
            //performTranslation();
            //textToSpeechConnector.connect(translation);
            System.out.println("TEXT - SPEECH VN");
        };

        // Khởi chạy các tác vụ trong ExecutorService
        executorService.submit(translationTask);
        executorService.submit(textToSpeechENTask);
        executorService.submit(textToSpeechVNTask);

        // Tắt ExecutorService sau khi hoàn thành các tác vụ
        executorService.shutdown();
    }

//    public void stop() {
//        executorService.shutdownNow();
//    }

    private String translateWord(String word) {
        // Thực hiện kết nối với API dịch và trả về kết quả dịch
//        try {
//            // Tạo URL kết nối với API và truyền tham số từ cần dịch
//            URL url = new URL(API_ENDPOINT + "?word=" + word);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            // Đọc và xử lý phản hồi từ API
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String response = reader.readLine();
//            System.out.println("Translation response: " + response);
//
//            // Đóng kết nối
//            reader.close();
//            connection.disconnect();
//
//            return response;
//        } catch (Exception e) {
//            System.out.println("ERROR IN TRANSLATING WORD IN PARA");
//            e.printStackTrace();
//        }

        return "TEST PERFORM TRANSLATION";
    }

    private class TextToSpeechConnector {
        public void connect(String text) {
            String apiKey = "9296283913e24e449e7b8cfa8366c29a";
            // Thực hiện kết nối với API Text-to-Speech và phát ra giọng nói
            try {
                String encodedText = URLEncoder.encode(text, "UTF-8");
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
    }

    public static void performTranslation(String text, String sourceLang, String targetLang) throws IOException, URISyntaxException {
        try {
            StringBuilder URLBuilder = new StringBuilder();
            URLBuilder.append("https://script.google.com/macros/s/AKfycbxpSOoe9lzov3rfNhV5qet6zAyDHvC9fbQbvhi_R_LFLSljaHe94QMStRtMmzy1Kc0g/exec");
            URLBuilder.append("?q=");
            URLBuilder.append(URLEncoder.encode(text, StandardCharsets.UTF_8));
            URLBuilder.append("&source=");
            URLBuilder.append(URLEncoder.encode(sourceLang, StandardCharsets.UTF_8));
            URLBuilder.append("&target=");
            URLBuilder.append(URLEncoder.encode(targetLang, StandardCharsets.UTF_8));

            String urlString = URLBuilder.toString();
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
            scanner.close();
        }
        catch (Exception e) {

        }
    }
}

