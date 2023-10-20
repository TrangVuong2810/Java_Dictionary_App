package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomescreenController implements Initializable {
    @FXML
    private Button lookupButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button gameButton;
    @FXML
    private Button bookmarkButton;
    @FXML
    private Button paraTransButton;


    @FXML
    private LookupController lookupController;

    @FXML
    private Node lookupScene;
    @FXML
    private Node historyScene;
    @FXML
    private Node gameScene;
    @FXML
    private Node bookmarkScene;
    @FXML
    private Node paraTransScene;
    @FXML
    private StackPane mainPane;
    //private StackPane idk;

    public void setLookupScreen() {
        //System.out.println("this is lookup scene");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(lookupScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lookupController.setUp();
        lookupController.search();
    }

    public void setHistoryScene() {
        //System.out.println("this is history scene");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(historyScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGameScene() {
        //System.out.println("this is game scene");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(gameScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBookmarkScene() {
        //System.out.println("this is bookmark scene");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(bookmarkScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParTransScene() {
        //System.out.println("This is gg trans scene");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(paraTransScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("lookup.fxml"));
            lookupScene = fxmlLoader.load();
            lookupController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("history.fxml"));
            historyScene = fxmlLoader.load();
//            historyController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game.fxml"));
            gameScene = fxmlLoader.load();
//            bookmarkController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bookmark.fxml"));
            bookmarkScene = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("paraTrans.fxml"));
            paraTransScene = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        searchButton.getStyleClass().add("active");
//        mainContent.getChildren().setAll(searchPane);
    }
}
