package game;

import base.Word;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int secondsElapsed;
    private Timeline timeline;
    public static List<Word> questionList = new ArrayList<>();

    @FXML
    private Button nextBtn;
    @FXML
    public HBox QuestionBox;
    @FXML
    private Quiz quiz;
    @FXML
    private Hangman hangman;
    @FXML
    private Node quizScene;
    @FXML
    private Node hangManScene;
    @FXML
    private Label timeLabel;
    @FXML
    public Label questionCount;

    public void quizGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("quiz.fxml"));
            quizScene = fxmlLoader.load();
            quiz = fxmlLoader.getController();
            QuestionBox.getChildren().add(quizScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hangManGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hangman.fxml"));
            hangManScene = fxmlLoader.load();
            hangman = fxmlLoader.getController();
            QuestionBox.getChildren().add(hangManScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed++;
            updateTimeLabel();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimeLabel() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        String time = String.format("%02d:%02d", minutes, seconds);
        timeLabel.setText(time);
    }

    public void setQuestionList(List<Word> questionList) {
        Game.questionList = questionList;
    }

    public List<Word> getQuestionList() {
        return questionList;
    }

    public void loadNextQuestion(ActionEvent event) {
        MyGame currentGame = getGame();
        currentGame.checkAnswer();
        if (currentGame.currentQuestionIndex < questionList.size()) {
            currentGame.currentQuestionIndex++;
            if (currentGame.currentQuestionIndex == questionList.size() - 1) {
                nextBtn.setText("Finish");
                questionCount.setText("Question: " + (currentGame.currentQuestionIndex + 1) + "/" + questionList.size());
            }
            if (currentGame.currentQuestionIndex == questionList.size()) {
                showResult(event);
            } else {
                currentGame.updateQuestion();
                questionCount.setText("Question: " + (currentGame.currentQuestionIndex + 1) + "/" + questionList.size());
            }
        }
    }

    public void showResult(ActionEvent event) {
        timeline.stop();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("result.fxml"));
            Parent root = loader.load();
            Result controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;
            String time = String.format("%02d:%02d", minutes, seconds);
            controller.setTotalTimer(time);
            controller.setCorrects(getGame().correctAnswer + "/" + questionList.size());
            int correctAnswer = getGame().correctAnswer;
            int wrongAnswer = getGame().wrongAnswer;
            int unAnsweredQuestion = questionList.size() - correctAnswer - wrongAnswer;
            controller.setPieChart(correctAnswer, wrongAnswer, unAnsweredQuestion);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyGame getGame() {
        if (quiz != null) {
            return quiz;
        }
        return hangman;
    }
}

