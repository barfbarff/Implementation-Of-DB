package shop.example;

import org.h2.tools.DeleteDbFiles;

import java.sql.*;

public class H2Example {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:./h2Example";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try {
            cleanUp();
            sqlExamples();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void sqlExamples() throws SQLException {
        //Bad Code, for your own exercise, consider splitting into separate methods. ;)
        Connection connection = getDBConnection();
        try {
            //stages changes until commit
            connection.setAutoCommit(false);

            //Statement to create a new table
            String createQuery = "CREATE TABLE EXAMPLE(id int primary key, whatever varchar(255))";
            PreparedStatement createStatement = connection.prepareStatement(createQuery);
            createStatement.executeUpdate();
            createStatement.close();

            //Statement to inserts a new record into the table
            String insertQuery = "INSERT INTO EXAMPLE" + "(id, whatever) values" + "(?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, 1);
            insertStatement.setString(2, "RandomValue");
            insertStatement.executeUpdate();
            insertStatement.close();

            //Statement to return all results from a table
            String selectQuery = "SELECT * FROM EXAMPLE";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet rs = selectStatement.executeQuery();
            while (rs.next()) {
                System.out.println("Id "+rs.getInt("id")+" Whatever "+rs.getString("whatever"));
            }
            selectStatement.close();

            //Statement to insert a new record or update an existing record
            String mergeQuery = "MERGE INTO EXAMPLE values" + "(?,?)";
            PreparedStatement mergeStatement = connection.prepareStatement(mergeQuery);
            mergeStatement.setInt(1, 1);
            mergeStatement.setString(2, "RandomValue");
            mergeStatement.executeUpdate();
            mergeStatement.close();

            //Repeat of the selection query to varify results of mergeQuery
            selectStatement = connection.prepareStatement(selectQuery);
            rs = selectStatement.executeQuery();
            while (rs.next()) {
                System.out.println("Id "+rs.getInt("id")+" Whatever "+rs.getString("whatever"));
            }
            selectStatement.close();

            //Persists all changes
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Dont forget!
            connection.close();
        }
    }

    private static void cleanUp(){
        //Deletes all files of Database "h2Example" in directory ".". Good for testing!
        DeleteDbFiles.execute(".", "h2Example", true);
    }

    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }
}
