package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class BookmarkController {
    @FXML
    private WordCompController wordCompController;
    @FXML
    private ListView<String> listView = new ListView<>();
    @FXML
    private TextField textField = new TextField();

    public BookmarkController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("wordComp.fxml"));
            wordCompController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
