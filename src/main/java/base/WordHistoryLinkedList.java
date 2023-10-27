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

    public void delete(String word) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int id = 0;
            java.lang.String getIDQuery = "SELECT id FROM history WHERE word = ?";
            PreparedStatement statement = connection.prepareStatement(getIDQuery);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            else {
                System.out.println("FAIL TO GET WORD ID IN HISTORY DATABASE");
            }

            java.lang.String deleteQuery = "DELETE FROM history WHERE word = ?";
            statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, (java.lang.String) word);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("word in history delete successfully.");
            } else {
                System.out.println("error in deleting word in history database");
            }

            updateID(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateID(int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String updateIDQuery = "UPDATE history SET id = id - 1 WHERE id > ?";
            PreparedStatement statement = connection.prepareStatement(updateIDQuery);
            statement.setString(1, java.lang.String.valueOf(id));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("update id in history database successfully.");
            } else {
                System.out.println("error in updating id in history database");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
