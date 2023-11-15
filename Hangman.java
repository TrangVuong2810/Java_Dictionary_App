package game;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

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
    private List<Button> letterButtons;
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
    private final Image imageCorrect = new Image(getClass().getResourceAsStream("/hangman/hangman-correct.png"));
    private final Image imageWrong = new Image(getClass().getResourceAsStream("/hangman/hangman-wrong.png"));

    public void init() {
        letterButtons = new ArrayList<>(Arrays.asList(qBtn, wBtn, eBtn, rBtn, tBtn, yBtn, uBtn, iBtn, oBtn, pBtn
                , aBtn, sBtn, dBtn, fBtn, gBtn, hBtn, jBtn, kBtn, lBtn
                , zBtn, xBtn, cBtn, vBtn, bBtn, nBtn, mBtn));
        wrongGuessCount = 0;
        for (Button button : letterButtons) {
            button.setOnKeyPressed(new KeyPressHandler());
        }
        getTotalQuestion();
        updateQuestion();
    }

    public void updateQuestion() {
        setDisable(false);
        setFocus();
        wrongGuessCount = 0;
        String wordToGuess = doubleQuestion.get(currentQuestionIndex).getWord_target();
        currentWordState = createHiddenWord(wordToGuess);
        imageView.setImage(image0);
        if (doubleQuestion.get(currentQuestionIndex).getWord_explain() != null) {
            String meaning = doubleQuestion.get(currentQuestionIndex).getWord_explain();
            questionLabel.setText("Từ mang nghĩa '" + meaning + "'");
        } else {
            String ipa = doubleQuestion.get(currentQuestionIndex).getIpa();
            questionLabel.setText("Từ có phát âm: " + ipa);
        }
        blankLabel.setText(currentWordState);
    }

    public void setFocus() {
        for (Button button : letterButtons) {
            button.requestFocus();
        }
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
        String wordToGuess = doubleQuestion.get(currentQuestionIndex).getWord_target();

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
                    imageView.setImage(imageCorrect);
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
            case 6 -> imageView.setImage(imageWrong);
        }
    }

    private WWordStatus checkWordStatus(String wordToGuess, String currentWordState, char letter) {
        if (wordToGuess.indexOf(letter) != -1) {
            return WWordStatus.CORRECT;
        } else {
            return WWordStatus.INCORRECT;
        }
    }

    private class KeyPressHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            KeyCode keycode = keyEvent.getCode();
            if (keycode.isLetterKey()) {
                String letter = keycode.toString();
                System.out.println(letter);
                Button clickedButton = findButton(letter);
                if (clickedButton != null) {
                    clickedButton.fire();
                }
            }
        }
    }

    private Button findButton(String letter) {
        for (Button button : letterButtons) {
            if (Character.toString(button.getId().charAt(0)).equalsIgnoreCase(letter)) {
                return button;
            }
        }
        return null;
    }
}
