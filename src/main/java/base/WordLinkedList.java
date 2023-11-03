package base;

import java.sql.*;
import java.util.LinkedList;

import static main.DictionaryApplication.*;
import static main.SearchCompController.*;

public class WordLinkedList<String> extends LinkedList<String>{
    protected String tableName;

    public WordLinkedList(String tableName) {
        this.tableName = tableName;
    }

    public void setUp() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String sql = "SELECT word_target FROM " + tableName;
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                super.add((String) resultSet.getString("word_target"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkDuplicateValue(String word) {
        boolean res = false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE word_target = ? ;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, (java.lang.String) word);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count == 0) res = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean add(String word) {
        boolean added = false;
        if (!checkDuplicateValue(word)) return false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String sql = "SELECT * FROM vocabulary WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, (java.lang.String) word);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                java.lang.String meaning = resultSet.getString("word_explain");
                java.lang.String ipa = resultSet.getString("ipa");
                java.lang.String wordType = resultSet.getString("word_type");


                java.lang.String addSQL = "INSERT INTO " + tableName + " (word_target, word_explain, ipa, word_type)" +
                                        " VALUES (?, ?, ?, ?);";
                PreparedStatement addStatement = connection.prepareStatement(addSQL);
                addStatement.setString(1, (java.lang.String) word);
                addStatement.setString(2, meaning);
                addStatement.setString(3, ipa);
                addStatement.setString(4, wordType);

                addStatement.executeUpdate();

                super.add(word);
                added = true;
            } else {
                System.out.println("ERROR IN ADDING WORD TO " + tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return added;
    }

    @Override
    public void clear() {
        super.clear();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String clearQuery = "TRUNCATE TABLE " + tableName;
            PreparedStatement statement = connection.prepareStatement(clearQuery);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("clear "+ tableName + " database successfully.");
            } else {
                System.out.println("error in clearing " + tableName + " database");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean delete(java.lang.String word) {
        boolean del = false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String deleteQuery = "DELETE FROM " + tableName + " WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, word);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("word in " +  tableName + " delete successfully.");
                del = super.remove(word);
            } else {
                System.out.println("error in deleting word in " + tableName + " database");
            }

        } catch (Exception e) {
            e.printStackTrace();
            del = false;
        }
        return del;
    }

}
