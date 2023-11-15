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
    private final List<String> eword = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup = new ToggleGroup();
        opt1.setToggleGroup(toggleGroup);
        opt2.setToggleGroup(toggleGroup);
        opt3.setToggleGroup(toggleGroup);
        opt4.setToggleGroup(toggleGroup);
        for (Word word : questionList) {
            meaning.add(word.getWord_explain());
            eword.add(word.getWord_target());
        }
        getTotalQuestion();
        updateQuestion();
    }

    public void updateQuestion() {
        toggleGroup.selectToggle(null);
        if (doubleQuestion.get(currentQuestionIndex).getWord_explain() != null) {
            questionLabel.setText("What does '" + doubleQuestion.get(currentQuestionIndex).getWord_target() + "' mean?");
            getMeaningOptions();
        } else {
            questionLabel.setText("Which word have this ipa:  " + doubleQuestion.get(currentQuestionIndex).getIpa());
            getEWordOptions();
        }
    }

    public void getMeaningOptions() {
        List<String> tmp = new ArrayList<>(meaning);
        int index = 0;
        String correctOption = doubleQuestion.get(currentQuestionIndex).getWord_explain();
        for (int i = 0; i < meaning.size(); i++) {
            if (correctOption.equals(meaning.get(i))) {
                index = i;
                break;
            }
        }
        tmp.remove(index);
        List<String> optionList = new ArrayList<>();
        optionList.add(correctOption);
        optionList.addAll(getRandomStrings(tmp, 3));
        Collections.shuffle(optionList);
        opt1.setText(optionList.get(0));
        opt2.setText(optionList.get(1));
        opt3.setText(optionList.get(2));
        opt4.setText(optionList.get(3));
    }

    public void getEWordOptions() {
        List<String> tmp = new ArrayList<>(eword);
        int index = 0;
        String correctOption = doubleQuestion.get(currentQuestionIndex).getWord_target();
        for (int i = 0; i < eword.size(); i++) {
            if (correctOption.equals(eword.get(i))) {
                index = i;
                break;
            }
        }
        tmp.remove(index);
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
            if (doubleQuestion.get(currentQuestionIndex).getWord_explain() != null) {
                if (selectedAnswer.equals(doubleQuestion.get(currentQuestionIndex).getWord_explain())) {
                    correctAnswer++;
                } else {
                    wrongAnswer++;
                }
            } else {
                if (selectedAnswer.equals(doubleQuestion.get(currentQuestionIndex).getWord_target())) {
                    correctAnswer++;
                } else {
                    wrongAnswer++;
                }
            }
        }
    }
}
