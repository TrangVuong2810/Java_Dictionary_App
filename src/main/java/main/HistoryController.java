package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

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
