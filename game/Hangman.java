package game;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Hangman extends MyGame implements Initializable{
    private enum WordStatus {
        CORRECT,
        INCORRECT,
        ALREADY_GUESSED
    }
    private String currentWordState;
    private int wrongGuessCount;
    @FXML
    private Label blankLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField input;
    @FXML
    private Button checkBtn;
    private final Image image0 = new Image(getClass().getResourceAsStream("/hangman/hangman0.png"));
    private final Image image1 = new Image(getClass().getResourceAsStream("/hangman/hangman1.png"));
    private final Image image2 = new Image(getClass().getResourceAsStream("/hangman/hangman2.png"));
    private final Image image3 = new Image(getClass().getResourceAsStream("/hangman/hangman3.png"));
    private final Image image4 = new Image(getClass().getResourceAsStream("/hangman/hangman4.png"));
    private final Image image5 = new Image(getClass().getResourceAsStream("/hangman/hangman5.png"));
    private final Image image6 = new Image(getClass().getResourceAsStream("/hangman/hangman6.png"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wrongGuessCount = 0;
        updateQuestion();
    }

    public void updateQuestion() {
        input.setDisable(false);
        checkBtn.setDisable(false);
        wrongGuessCount = 0;
        isAnswered = false;
        String meaning = questionList.get(currentQuestionIndex).getWord_explain();
        String wordToGuess = questionList.get(currentQuestionIndex).getWord_target();
        currentWordState = createHiddenWord(wordToGuess);
        imageView.setImage(image0);
        questionLabel.setText("Từ mang nghĩa '" + meaning + "'");
        blankLabel.setText(currentWordState);
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

    public void checkLetter() {
        String wordToGuess = questionList.get(currentQuestionIndex).getWord_target();
        String guess = input.getText().toLowerCase();
        if (guess.length() != 1 || !Character.isLetter(guess.charAt(0))) {
            input.clear();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Không hợp lệ!");
            alert.setContentText("Vui lòng chỉ nhập 1 kí tự là chữ cái!");
            alert.show();
            return;
        }
        char letter = guess.charAt(0);

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
                if (!currentWordState.contains("_")) {
                    correctAnswer++;
                    showQuestionResultAlert("Chúc mừng!", "Bạn đã đoán đúng!\nClick Next để tiếp tục");
                    input.setDisable(true);
                    checkBtn.setDisable(true);
                }
                break;
            }
            case INCORRECT -> {
                wrongGuessCount++;
                updateImage(wrongGuessCount);
                if (wrongGuessCount == 6) {
                    showQuestionResultAlert("Rất tiếc!", "Bạn đã hết lượt đoán từ\nClick Next để tiếp tục");
                    input.setDisable(true);
                    checkBtn.setDisable(true);
                    wrongAnswer++;
                }
                break;
            }
            case ALREADY_GUESSED -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Bạn đã đoán chữ cái này rồi!");
                alert.show();
                break;
            }
        }
        input.clear();
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

    private WordStatus checkWordStatus(String wordToGuess, String currentWordState, char letter) {
        if (currentWordState.indexOf(letter) != -1) {
            return WordStatus.ALREADY_GUESSED;
        } else if (wordToGuess.indexOf(letter) != -1) {
            return WordStatus.CORRECT;
        } else {
            return WordStatus.INCORRECT;
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
