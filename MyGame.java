package game;

import base.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class MyGame {
    protected String question;
    private static final Random RANDOM = new Random();
    protected List<Word> questionList = Game.questionList;
    protected List<Word> doubleQuestion = new ArrayList<>();
    protected int currentQuestionIndex;
    protected int correctAnswer;
    protected int wrongAnswer;

    public void getTotalQuestion() {
        doubleQuestion.addAll(questionList);
        for (int i = 0; i < questionList.size(); i++) {
            String word_target = questionList.get(i).getWord_target();
            String ipa = questionList.get(i).getIpa();
            doubleQuestion.add(new Word(word_target, ipa, 1));
        }
        Collections.shuffle(doubleQuestion);
    }
    public abstract void updateQuestion();
    public abstract void checkAnswer();

}
