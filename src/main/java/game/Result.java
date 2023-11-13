package game;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class Result {
    @FXML
    private PieChart pieChart;
    @FXML
    private Label corrects;
    @FXML
    private Label totalTimer;
    public void setTotalTimer(String totalTime) {
        totalTimer.setText(totalTime);
    }

    public void setCorrects(String score) {
        corrects.setText(score);
    }

    public void setPieChart(int correctAnswers, int wrongAnswers, int unansweredQuestions) {
        PieChart.Data correctData = new PieChart.Data("Correct", correctAnswers);
        PieChart.Data wrongData = new PieChart.Data("Wrong", wrongAnswers);
        PieChart.Data unansweredData = new PieChart.Data("Unanswered", unansweredQuestions);
        pieChart.getData().addAll(correctData, wrongData, unansweredData);
    }
}
