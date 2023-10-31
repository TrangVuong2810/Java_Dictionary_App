package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

import static main.DictionaryApplication.wordBookmark;

public class BookmarkController {
    @FXML
    private SplitPane searchComp;
    @FXML
    private SearchCompController searchCompController;

    public BookmarkController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchComp.fxml"));
            fxmlLoader.load();
            searchCompController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setUp() {
        searchCompController.setUp(wordBookmark);
    }

    public void search() {
        searchCompController.search(wordBookmark);
    }
}
