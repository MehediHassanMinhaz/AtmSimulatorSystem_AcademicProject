package mehedi;

import java.sql.*;

public class DatabaseConnection implements AutoCloseable {
    Connection connection;
    Statement statement;

    public DatabaseConnection() {
        try {
            // 1️⃣ Register the Driver (optional in JDBC 4.0+)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2️⃣ Create Connection to the Database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankManagementSystemDB", "root", "my5QL_p@$5-w0rd");

            // 3️⃣ Create a Statement Object
            statement = connection.createStatement();


        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();  // Driver class not found
        } catch (SQLException sqle) {
            sqle.printStackTrace();  // Database connection error
        }
    }

    @Override
    public void close() throws Exception {
        if (statement != null) try { statement.close(); } catch (Exception ignore) {}
        if (connection != null) try { connection.close(); } catch (Exception ignore) {}
    }
}
