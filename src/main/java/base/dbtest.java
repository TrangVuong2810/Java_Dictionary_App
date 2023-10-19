package base;
import java.sql.*;

public class dbtest {
    public static void test() {
//        try {
//            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/classicmodels", "root", "28102004");
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers WHERE customerNumber = '103'");
//
//            // Hiển thị kết quả truy vấn trên giao diện
//            while (resultSet.next()) {
//                String customerName = resultSet.getString("customerName");
//                String  num = resultSet.getString("customerNumber");
//                String phone = resultSet.getString("phone");
//
////            resultTextArea.appendText("Word: " + word + "\n");
////            resultTextArea.appendText("Definition: " + definition + "\n");
////            resultTextArea.appendText("Part of Speech: " + partOfSpeech + "\n\n");
//                System.out.println(customerName);
//                System.out.println(num);
//                System.out.println(phone);
//            }
//
//            resultSet.close();
//            statement.close();
//            connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        test();
    }

}
