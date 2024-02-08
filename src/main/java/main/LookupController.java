package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

import java.util.Set;

import static main.DictionaryApplication.wordTrie;

public class LookupController {
    @FXML
    private SplitPane searchComp;
    @FXML
    private SearchCompController searchCompController;

    public LookupController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchComp.fxml"));
            fxmlLoader.load();
            searchCompController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp() {
        searchCompController.setUp(wordTrie);
    }

    public void search() {
        searchCompController.search(wordTrie);
    }

}
