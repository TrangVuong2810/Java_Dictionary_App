package main;

import base.CustomAlert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;

public class EditWordController {
    @FXML
    private InputWordController inputWordController;

    public EditWordController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("inputWord.fxml"));
            fxmlLoader.load();
            inputWordController = fxmlLoader.getController();
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("INIT EDIT WORD ERROR",
                    "Error occurred in initiating edit word screen " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void run(String selectedWord) {
        inputWordController.run(selectedWord);
    }
}
