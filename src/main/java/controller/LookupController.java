package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

public class LookupController {
    @FXML
    private SplitPane searchComp;
    @FXML
    private SearchCompController searchCompController;

    public LookupController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchComp.fxml"));
            searchCompController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp() {
        searchCompController.setUp();
    }

    public void search() {
        searchCompController.search();
    }
}
