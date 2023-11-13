package base;

import javafx.scene.control.Alert;

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
            java.lang.String sql = "SELECT * FROM " + tableName + " ORDER BY id;";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                super.add((String) resultSet.getString("word_target"));
            }
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("INIT HISTORY/BOOKMARK ERROR",
                    "Error occurred related to " + tableName + " database, " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    //if no dup, return true;
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
            CustomAlert customAlert = new CustomAlert("CHECK DUP ERROR",
                    "Error occurred related to " + tableName + " database, " +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean add(String word) {
        boolean added = false;
        if (!checkDuplicateValue(word)) return false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String addSQL = "INSERT INTO " + tableName + " (word_target)" +
                    " VALUES (?);";
            PreparedStatement addStatement = connection.prepareStatement(addSQL);
            addStatement.setString(1, (java.lang.String) word);

            int rowsAffected = addStatement.executeUpdate();

            if (rowsAffected > 0) {

                super.add(word);
                added = true;
                System.out.println("add word to " + tableName + " database successfully.");
            } else {
                CustomAlert customAlert = new CustomAlert("ADDING WORD ERROR",
                        "Error occurred in adding word in " + tableName + " database, " + "\n" +
                                "please contact the developers for more information", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("ADDING WORD ERROR",
                    "Error occurred related to " + tableName + "  database, " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return added;
    }

    @Override
    public void clear() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            java.lang.String clearQuery = "TRUNCATE TABLE " + tableName;
            PreparedStatement statement = connection.prepareStatement(clearQuery);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                super.clear();
                System.out.println("clear "+ tableName + " database successfully.");
            } else {
                CustomAlert customAlert = new CustomAlert("TRUNCATE ERROR",
                        "Error occurred in truncating database, " + "\n" +
                                "please contact the developers for more information", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("TRUNCATE ERROR",
                    "Error occurred related to " + tableName + " database, " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
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
            }
            else {
                CustomAlert customAlert = new CustomAlert("DELETING ERROR",
                        "Error occurred in getting word id in " + tableName + " database, " + "\n" +
                                "please contact the developers for more information", Alert.AlertType.ERROR);
            }

            java.lang.String deleteQuery = "DELETE FROM " + tableName + " WHERE word_target = ?";
            statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, word);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                updateID(id);
                CustomAlert.popUp("word in " + tableName + " database delete successfully.");
                del = super.remove(word);
            } else {
                CustomAlert customAlert = new CustomAlert("DELETING ERROR",
                        "Error occurred in deleting word in " + tableName + " database, " + "\n" +
                                "please contact the developers for more information", Alert.AlertType.ERROR);
            }

            updateID(id);
        } catch (Exception e) {
            CustomAlert customAlert = new CustomAlert("DELETING ERROR",
                    "Error occurred related to " + tableName + " database, " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
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
            CustomAlert customAlert = new CustomAlert("UPDATING ID ERROR",
                    "Error occurred related to " + tableName + " database, " + "\n" +
                            "please contact the developers for more information", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

}
