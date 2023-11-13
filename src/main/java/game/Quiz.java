package game;

import base.Word;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.*;

public class Quiz extends MyGame implements Initializable {
    @FXML
    private Label questionLabel;
    @FXML
    private RadioButton opt1;
    @FXML
    private RadioButton opt2;
    @FXML
    private RadioButton opt3;
    @FXML
    private RadioButton opt4;
    private ToggleGroup toggleGroup;
    private final List<String> meaning = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isAnswered = false;
        toggleGroup = new ToggleGroup();
        opt1.setToggleGroup(toggleGroup);
        opt2.setToggleGroup(toggleGroup);
        opt3.setToggleGroup(toggleGroup);
        opt4.setToggleGroup(toggleGroup);
        for (Word word : questionList) {
            meaning.add(word.getWord_explain());
        }
        updateQuestion();
    }

    public void updateQuestion() {
        toggleGroup.selectToggle(null);
        questionLabel.setText("What does '" + questionList.get(currentQuestionIndex).getWord_target() + "' mean?");
        getOptions();
    }

    public void getOptions() {
        List<String> tmp = new ArrayList<>(meaning);
        String correctOption = tmp.remove(currentQuestionIndex);
        List<String> optionList = new ArrayList<>();
        optionList.add(correctOption);
        optionList.addAll(getRandomStrings(tmp, 3));
        Collections.shuffle(optionList);
        opt1.setText(optionList.get(0));
        opt2.setText(optionList.get(1));
        opt3.setText(optionList.get(2));
        opt4.setText(optionList.get(3));
    }

    public List<String> getRandomStrings(List<String> stringList, int count) {
        List<String> randomStrings = new ArrayList<>();
        Random random = new Random();
        int size = stringList.size();
        while (randomStrings.size() < count && randomStrings.size() < size) {
            int randomIndex = random.nextInt(size);
            String randomString = stringList.get(randomIndex);
            if (!randomStrings.contains(randomString)) {
                randomStrings.add(randomString);
            }
        }
        return randomStrings;
    }

    public void checkAnswer() {
        Toggle selectedToggle = toggleGroup.getSelectedToggle();
        if (selectedToggle != null) {
            RadioButton selectedOption = (RadioButton) selectedToggle;
            String selectedAnswer = selectedOption.getText();
            if (selectedAnswer.equals(questionList.get(currentQuestionIndex).getWord_explain())) {
                correctAnswer++;
            } else {
                wrongAnswer++;
            }
            isAnswered = true;
        }
    }
}
