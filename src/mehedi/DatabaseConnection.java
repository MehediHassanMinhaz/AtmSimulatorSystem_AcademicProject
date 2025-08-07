package mehedi;

import java.sql.*;

public class DatabaseConnection {
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

    // // 5️⃣ Close All Connections (Important!)
    // public void close() {
    //     try {
    //         if (statement != null) statement.close();
    //         if (connection != null) connection.close();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }
}

/*  JDBC 5-step process:
========================


    // 5️⃣ Close All Connections (Important!)
    rs.close();
    stmt.close();
    con.close();
 */
