import java.sql.*;
import java.util.Scanner;

public class Manager {
    private static Connection con = null; // Connection object for connecting to the database
    private static Scanner input = new Scanner(System.in); // Scanner object for user input
    private static String title = "Operations for manager menu"; // Title for the menu
    private static String[] optionList = {
        "List all salespersons",
        "Count the no. of sales record of each salesperson under a specific range on years of experience",
        "Show the total sales value of each manufacturer",
        "Show the N most popular parts",
        "Return to the main menu"
    };

    public static void printTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        while (resultSet.next()) {
            System.out.print("| ");
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) {
                    System.out.print(" | ");
                }
                String columnValue = resultSet.getString(i); // Get the value of each column in the current record
                System.out.print(columnValue); // Print the column value
            }
            System.out.println(" |");
        }
    }

    public static void main() {
        Manager.con = Main.connectToOracle(); // Connect to the Oracle database

        while (true) {
            int option = Main.getMenuChoice(title, optionList); // Get the user's menu choice
            try {
                switch (option) {
                    case 1:
                        listAllSalespersons(); // Option to list salespersons in order
                        break;
                    case 2:
                        countSalesRecords(); // Option to count sales records
                        break;
                    case 3:
                        showTotalSalesValue(); // Option to show sales value
                        break;
                    case 4:
                        showPopularPart(); // Option to show popular parts
                        break;
                    case 5:
                        return; // Option to return to the main menu
                }
            } catch (SQLException e) {
                System.out.println(e); // Print any SQL exceptions that occur
            }
        }
    }

    public static void listAllSalespersons() throws SQLException {
        String string = "Choose ordering:\n" +
                        "1. By ascending order\n" +
                        "2. By descending order\n" +
                        "Choose the list ordering: ";

        try {
            System.out.print(string);
            int choice = input.nextInt();

            String query = "SELECT sid, sname, sphonenumber, sexperience " +
                           "FROM salesperson " +
                           "ORDER BY sexperience ";
            String order = "";

            switch (choice) {
                case 1:
                    order = "ASC";
                    break;
                case 2:
                    order = "DESC";
                    break;
                default:
                    order = "ASC";
                    System.out.println("[ERROR]: Invalid Input, default: ASC");
                    break;
            }

            query = query + " " + order;
            
            Statement sql = con.createStatement();
            ResultSet resultSet = sql.executeQuery(query); // Execute the SQL query
        
            System.out.println("| ID | Name | Mobile Phone | Years of Experience |");
            printTable(resultSet);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void countSalesRecords() throws SQLException {
        try {
            System.out.print("Type in the lower bound for years of experience: ");
            int lower = input.nextInt();

            System.out.print("Type in the upper bound for years of experience: ");
            int upper = input.nextInt();

            // Explicit JOIN instead of NATURAL JOIN
            String query = "SELECT s.sid, s.sname, s.sexperience, COUNT(t.sid) AS transaction_count " +
                        "FROM salesperson s " +
                        "JOIN transaction t ON s.sid = t.sid " +
                        "WHERE s.sexperience >= ? AND s.sexperience <= ? " +
                        "GROUP BY s.sid, s.sname, s.sexperience " +
                        "ORDER BY s.sid DESC";

            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, lower);
            pstmt.setInt(2, upper);

            ResultSet resultSet = pstmt.executeQuery(); // Execute the SQL query

            System.out.println("| ID | Name | Years of Experience | Number of Transactions |");
            printTable(resultSet);
            System.out.println("End of Query");

        } catch (Exception e) {
            System.out.println("[Error]: " + e.getMessage());
        }
    }
    
    public static void showTotalSalesValue() throws SQLException {
        String query = "SELECT mid, mname, SUM(pprice) AS total_sales_value " +
                       "FROM manufacturer NATURAL JOIN part NATURAL JOIN transaction " +
                       "GROUP BY mid, mname " +
                       "ORDER BY SUM(pprice) DESC";

        Statement sql = con.createStatement();
        ResultSet resultSet = sql.executeQuery(query); // Execute the SQL query

        System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Value |");
        printTable(resultSet);
        System.out.println("End of Query");
    }

    public static void showPopularPart() throws SQLException {
        System.out.print("Type in the number of parts: ");

        try {
            int number = input.nextInt();

            // Use explicit JOIN instead of NATURAL JOIN
            String query = "SELECT * FROM ( " +
                        "SELECT p.pid, p.pname, COUNT(t.pid) AS transaction_count " +
                        "FROM part p " +
                        "JOIN transaction t ON p.pid = t.pid " +
                        "GROUP BY p.pid, p.pname " +
                        "ORDER BY COUNT(t.pid) DESC " +
                        ") WHERE ROWNUM <= ?";

            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, number);

            ResultSet resultSet = pstmt.executeQuery(); // Execute the SQL query

            System.out.println("| Part ID | Part Name | No. of Transactions |");
            printTable(resultSet);
            System.out.println("End of Query");

        } catch (Exception e) {
            System.out.println("[Error]: " + e.getMessage());
        }
    }
}