package base;

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
                updateHistoryDatabase(word);
            }
        }
        return super.add(word);
    }

    public void updateHistoryDatabase(String word) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String deleteQuery = "DELETE FROM history WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, (java.lang.String) word);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("update history database successfully");
            } else {
                System.out.println("error in updating history database");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
