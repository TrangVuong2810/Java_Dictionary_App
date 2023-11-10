package base;

import javafx.scene.control.Alert;

import java.sql.*;

import static main.DictionaryApplication.*;
import static main.SearchCompController.*;

public class WordHistoryLinkedList<String> extends WordLinkedList<String>{
    static private final short MAX_SIZE = 21;
    public WordHistoryLinkedList(String tableName) {
        super(tableName);
    }

    @Override
    public boolean add(String word) {
        if(super.add(word)) {
            if (size() > MAX_SIZE) {
                removeFirst();
                updateHistoryDatabase();
            }
        }
        return true;
    }

    public void updateHistoryDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String deleteQuery = "DELETE FROM history WHERE id = 1";
            try (Statement statement = connection.createStatement()) {
                int rowsAffected = statement.executeUpdate(deleteQuery);

                if (!(rowsAffected > 0)) {
                    CustomAlert customAlert = new CustomAlert("HISTORY ERROR",
                            "Error deleting word in database, " + "\n" +
                                    "please contact the developers for more information", Alert.AlertType.ERROR);
                }
            }

            java.lang.String updateQuery = "UPDATE history SET id = id - 1";
            try (Statement statement = connection.createStatement()) {
                int rowsAffected = statement.executeUpdate(updateQuery);

                if (rowsAffected > 0) {
                    System.out.println("update history database successfully");
                } else {
                    CustomAlert customAlert = new CustomAlert("HISTORY ERROR",
                            "Error updating id in database, " + "\n" +
                                    "please contact the developers for more information", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("HISTORY ERROR",
                    "Error occurred related to database, " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

    }
}
