package game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Hangman extends MyGame {
    @FXML
    private Button qBtn, wBtn, eBtn, rBtn, tBtn, yBtn, uBtn, iBtn, oBtn, pBtn
            , aBtn, sBtn, dBtn, fBtn, gBtn, hBtn, jBtn, kBtn, lBtn
            , zBtn, xBtn, cBtn, vBtn, bBtn, nBtn, mBtn;
    private List<Button> letterButtons ;
    private enum WWordStatus {
        CORRECT,
        INCORRECT
    }
    private String currentWordState;
    private int wrongGuessCount;
    @FXML
    private Label blankLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private ImageView imageView;
    private final Image image0 = new Image(getClass().getResourceAsStream("/hangman/hangman0.png"));
    private final Image image1 = new Image(getClass().getResourceAsStream("/hangman/hangman1.png"));
    private final Image image2 = new Image(getClass().getResourceAsStream("/hangman/hangman2.png"));
    private final Image image3 = new Image(getClass().getResourceAsStream("/hangman/hangman3.png"));
    private final Image image4 = new Image(getClass().getResourceAsStream("/hangman/hangman4.png"));
    private final Image image5 = new Image(getClass().getResourceAsStream("/hangman/hangman5.png"));
    private final Image image6 = new Image(getClass().getResourceAsStream("/hangman/hangman6.png"));

    public void init() {
        letterButtons = new ArrayList<>(Arrays.asList(qBtn, wBtn, eBtn, rBtn, tBtn, yBtn, uBtn, iBtn, oBtn, pBtn
                , aBtn, sBtn, dBtn, fBtn, gBtn, hBtn, jBtn, kBtn, lBtn
                , zBtn, xBtn, cBtn, vBtn, bBtn, nBtn, mBtn));
        wrongGuessCount = 0;
        updateQuestion();
    }

    public void updateQuestion() {
        setDisable(false);
        wrongGuessCount = 0;
        isAnswered = false;
        String meaning = questionList.get(currentQuestionIndex).getWord_explain();
        String wordToGuess = questionList.get(currentQuestionIndex).getWord_target();
        currentWordState = createHiddenWord(wordToGuess);
        imageView.setImage(image0);
        questionLabel.setText("Từ mang nghĩa '" + meaning + "'");
        blankLabel.setText(currentWordState);
    }

    public void setDisable(boolean isDisabled) {
        for (int i = 0; i < letterButtons.size(); i++) {
            if (isDisabled) {
                letterButtons.get(i).getStyleClass().add("disabled");
            }
            else {
                letterButtons.get(i).getStyleClass().removeAll("disabled");
            }
            letterButtons.get(i).setDisable(isDisabled);
        }
    }

    @Override
    public void checkAnswer() {
        return;
    }

    public String createHiddenWord(String wordToGuess) {
        int letter_cnt = wordToGuess.length();
        StringBuilder blank = new StringBuilder();
        blank.append("_ ".repeat(letter_cnt));
        return blank.toString();
    }

    public void checkLetter(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String wordToGuess = questionList.get(currentQuestionIndex).getWord_target();

        char letter = clickedButton.getId().charAt(0);

        switch (checkWordStatus(wordToGuess, currentWordState, letter)) {
            case CORRECT -> {
                StringBuilder updatedWord = new StringBuilder(currentWordState);
                for (int i = 0; i < wordToGuess.length(); i++) {
                    if (letter == wordToGuess.charAt(i)) {
                        updatedWord.setCharAt(i * 2, letter);
                    }
                }
                currentWordState = updatedWord.toString();
                blankLabel.setText(currentWordState);
                clickedButton.getStyleClass().add("disabled");
                clickedButton.setDisable(true);
                if (!currentWordState.contains("_")) {
                    correctAnswer++;
                    showQuestionResultAlert("Chúc mừng!", "Bạn đã đoán đúng!\nClick Next để tiếp tục");
                    setDisable(true);
                }
                break;
            }
            case INCORRECT -> {
                wrongGuessCount++;
                updateImage(wrongGuessCount);
                clickedButton.getStyleClass().add("disabled");
                clickedButton.setDisable(true);
                if (wrongGuessCount == 6) {
                    showQuestionResultAlert("Rất tiếc!", "Bạn đã hết lượt đoán từ\nClick Next để tiếp tục");
                    setDisable(true);
                    wrongAnswer++;
                }
                break;
            }
        }
    }

    private void updateImage(int wrongGuessCount) {
        switch (wrongGuessCount) {
            case 1 -> imageView.setImage(image1);
            case 2 -> imageView.setImage(image2);
            case 3 -> imageView.setImage(image3);
            case 4 -> imageView.setImage(image4);
            case 5 -> imageView.setImage(image5);
            case 6 -> imageView.setImage(image6);
        }
    }

    private WWordStatus checkWordStatus(String wordToGuess, String currentWordState, char letter) {
        if (wordToGuess.indexOf(letter) != -1) {
            return WWordStatus.CORRECT;
        } else {
            return WWordStatus.INCORRECT;
        }
    }

    private void showQuestionResultAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
