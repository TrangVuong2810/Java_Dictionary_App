package base;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Popup;

import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.Optional;

public class CustomAlert {
    public CustomAlert(String header, String content, Alert.AlertType alertType) {
        Platform.runLater(() -> {


            Alert alert = new Alert(alertType);
            alert.initModality(Modality.APPLICATION_MODAL);
            if (alertType.equals(Alert.AlertType.NONE) || alertType.equals(Alert.AlertType.INFORMATION)) {
                alert.setTitle("NOTIFICATION");
            }
            else if (alertType.equals(Alert.AlertType.ERROR)) {
                alert.setTitle("ERROR");
            }
            else if (alertType.equals(Alert.AlertType.WARNING)) {
                alert.setTitle("WARNING");
            }
            alert.setHeaderText(header);
            alert.setContentText(content);

            alert.showAndWait();
        });
    }

    public static Optional<ButtonType> customConfirmation(String header, String content) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("CONFIRMATION");
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType yesButton = new ButtonType("Yes", ButtonType.YES.getButtonData());
        ButtonType noButton = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(yesButton, noButton);

        return alert.showAndWait();

    }

    public static void popUp(String message) {
        Platform.runLater(() -> {

            Window ownerWindow = null;
            for (Window window : Stage.getWindows()) {
                if (window.isShowing()) {
                    ownerWindow = window;
                    break;
                }
            }
            Popup popup = new Popup();
            Label label = new Label(message);
            label.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 10px; -fx-text-color: white;");
            popup.getContent().add(label);

            // Hiển thị Popup
            popup.show(ownerWindow);

            // Đặt thời gian tự động biến mất sau 2 giây
            Duration duration = Duration.seconds(2);
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(duration);
            delay.setOnFinished(event -> popup.hide());
            delay.play();
        });
    }
}
