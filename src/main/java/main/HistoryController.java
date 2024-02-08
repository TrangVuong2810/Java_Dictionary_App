package main;

import base.CustomAlert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;

import java.util.Set;

import static main.DictionaryApplication.wordHistory;

public class HistoryController {
    @FXML
    private SplitPane searchComp;
    @FXML
    private SearchCompController searchCompController;

    public HistoryController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchComp.fxml"));
            fxmlLoader.load();
            searchCompController = fxmlLoader.getController();
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("INIT HISTORY SCREEN ERROR",
                    "Error occurred in initiating history scene " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    public void setUp() {
        searchCompController.setUp(wordHistory);
    }

    public void search() {
        searchCompController.search(wordHistory);
    }

}
