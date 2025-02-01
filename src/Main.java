import java.sql.*;
import java.util.*;

public class Main {
    public static String dbAddress = "jdbc:oracle:thin:@db18.cse.cuhk.edu.hk:1521:oradb";
    public static String dbUsername = "h077";
    public static String dbPassword = "BowofVuv";
    public static String title = "Main menu";
    public static String[] optionList = {
        "Operations for administrator",
        "Operations for salesperson",
        "Operations for manager",
        "Exit this program"
    };

    public static Connection connectToOracle() {
        Connection con = null;
        try {
            System.out.println("Loading Oracle JDBC driver...");
            Class.forName("oracle.jdbc.driver.OracleDriver"); // Load the Oracle JDBC driver
            System.out.println("Connecting to Oracle database...");
            con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword); // Establish a connection
            System.out.println("Connection established.");
        } catch (ClassNotFoundException e) {
            System.out.println("[Error]: Oracle JDBC Driver not found!");
            System.exit(0); // Exit if the driver is not found
        } catch (SQLException e) {
            System.out.println("[Error]: Failed to connect to database. Details: " + e.getMessage());
        }
        return con;
    }

    public static int getMenuChoice(String title, String[] optionList) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;
    
        // Get a valid option from the user
        while (option < 1 || option > optionList.length) {
            System.out.println("\n-----" + title + "-----");
            System.out.println("What kinds of operations would you like to perform?");
            for (int i = 0; i < optionList.length; i++) {
                System.out.println((i + 1) + ". " + optionList[i]);
            }
            try {
                System.out.print("Enter Your Choice: ");
                String input = scanner.nextLine();
                option = Integer.parseInt(input); // Parse the user input as an integer
                if (option < 1 || option > optionList.length) throw new Exception();
            } catch (Exception e) {
                System.out.println("[Error]: Invalid input!"); // Print an error message for invalid input
            }
        }
        return option;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to sales system!");
        
        while(true) {
            int option = getMenuChoice(title, optionList);
            switch(option) {
                case 1:
                    Administrator.main();
                    break;
                case 2:
                    Salesperson.main();
                    break;
                case 3:
                    Manager.main();
                    break;
                case 4:
                    return;
            }
        }
    }

}