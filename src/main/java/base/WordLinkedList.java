package base;

import java.sql.*;
import java.util.LinkedList;

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
            int id = 0;
            java.lang.String getIDQuery = "SELECT id FROM " + tableName + " WHERE word_target = ?";
            PreparedStatement statement = connection.prepareStatement(getIDQuery);
            statement.setString(1, word);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
                System.out.println("ID: " + id);
            }
            else {
                System.out.println("FAIL TO GET WORD ID IN " + tableName + " DATABASE");
                return del;
            }

            java.lang.String deleteQuery = "DELETE FROM " + tableName + " WHERE word_target = ?";
            statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, word);
            System.out.println(deleteQuery);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("word in " +  tableName + " delete successfully.");
                updateID(id);
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

    private void updateID(int id) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String selectQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE id > ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, java.lang.String.valueOf(id));
            ResultSet resultSet = selectStatement.executeQuery();
            resultSet.next();

            int remainingRows = resultSet.getInt(1);
            if (remainingRows > 0) {
                java.lang.String updateIDQuery = "UPDATE " + tableName + " SET id = id - 1 WHERE id > ?";
                PreparedStatement statement = connection.prepareStatement(updateIDQuery);
                statement.setString(1, java.lang.String.valueOf(id));

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("update id in " + tableName + " database successfully.");
                } else {
                    System.out.println("error in updating id in " + tableName + " database");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
