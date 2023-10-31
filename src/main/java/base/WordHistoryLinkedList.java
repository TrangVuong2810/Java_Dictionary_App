package base;

import java.sql.*;

import static main.SearchCompController.*;

public class WordHistoryLinkedList extends WordLinkedList<String>{
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
        return super.add(word);
    }

    public void updateHistoryDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String deleteQuery = "DELETE FROM history WHERE id = 1";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(deleteQuery);
            }
            java.lang.String updateQuery = "UPDATE history SET id = id - 1";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(updateQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
