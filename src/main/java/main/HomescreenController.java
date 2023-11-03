package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

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
    private Button addWordButton;


    @FXML
    private LookupController lookupController;
    @FXML
    private HistoryController historyController;
    @FXML
    private BookmarkController bookmarkController;
    @FXML
    private ParaTransController paraTransController;
    @FXML
    private AddWordController addWordController;

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
    private Node addWordScene;
    @FXML
    private StackPane mainPane;

    public void close() {
        paraTransController.close();
    }

    public void resetStyleNav() {
        lookupButton.getStyleClass().removeAll("active");
        historyButton.getStyleClass().removeAll("active");
        gameButton.getStyleClass().removeAll("active");
        bookmarkButton.getStyleClass().removeAll("active");
        paraTransButton.getStyleClass().removeAll("active");
        addWordButton.getStyleClass().removeAll("active");
    }
    public void setLookupScreen() {
        resetStyleNav();
        lookupButton.getStyleClass().add("active");
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
        resetStyleNav();
        historyButton.getStyleClass().add("active");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(historyScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        historyController.setUp();
        historyController.search();
    }

    public void setGameScene() {
        resetStyleNav();
        gameButton.getStyleClass().add("active");
        //System.out.println("this is game scene");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(gameScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBookmarkScene() {
        resetStyleNav();
        bookmarkButton.getStyleClass().add("active");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(bookmarkScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bookmarkController.setUp();
        bookmarkController.search();
    }

    public void setParTransScene() {
        resetStyleNav();
        paraTransButton.getStyleClass().add("active");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(paraTransScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        paraTransController.setUp();
        paraTransController.run();
    }

    public void setAddWordScene() {
        resetStyleNav();
        addWordButton.getStyleClass().add("active");
        try {
            mainPane.getChildren().clear();
            mainPane.getChildren().add(addWordScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addWordController.run();
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
            historyController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game.fxml"));
            gameScene = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bookmark.fxml"));
            bookmarkScene = fxmlLoader.load();
            bookmarkController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("paraTrans.fxml"));
            paraTransScene = fxmlLoader.load();
            paraTransController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addWord.fxml"));
            addWordScene = fxmlLoader.load();
            addWordController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
