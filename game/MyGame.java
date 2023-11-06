package game;

import base.Word;

import java.util.List;

public abstract class MyGame {
    protected List<Word> questionList = Game.questionList;
    protected int currentQuestionIndex;
    protected int correctAnswer;
    protected int wrongAnswer;
    protected boolean isAnswered;

    public abstract void updateQuestion();
    public abstract void checkAnswer();
}
