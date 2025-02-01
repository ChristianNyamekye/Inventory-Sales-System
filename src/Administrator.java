import java.text.*;
import java.io.*;
import java.sql.*;
import java.util.*;

class Administrator {
    private static Connection con = null; // Connection object for connecting to the database
    private static Scanner input = new Scanner(System.in); // Scanner object for user input
    private static String title = "Operations for administrator menu"; // Title for the menu
    private static String[] optionList = { // Array of menu options
        "Create all tables",
        "Delete all tables",
        "Load from datafile",
        "Show content of a table",
        "Return to the main menu"
    };

    public static void main () {
        Administrator.con = Main.connectToOracle(); // Connect to the MySQL database

        while (true) {
            int option = Main.getMenuChoice(title, optionList); // Get the user's menu choice
            try {
                switch(option) {
                    case 1:
                        createAllTable(); // Option to create all tables
                        break;
                    case 2:
                        deleteAllTable(); // Option to delete all tables
                        break;
                    case 3:
                        loadDataFile(); // Option to load data from files
                        break;
                    case 4:
                        showContentOfRecord(); // Option to show the content of a table
                        break;
                    case 5:
                        return; // Option to return to the main menu
                }
            }
            catch (SQLException e) {
                System.out.println(e); // Print any SQL exceptions that occur
            }
        }
    }

    // private static void createAllTable() throws SQLException {
    //     System.out.print("Processing...");
    //     Statement stmt = con.createStatement();
    
    //     // creating category table
    //     String categoryTableSql = "CREATE TABLE IF NOT EXISTS category (cid integer NOT NULL, cName char(20), CONSTRAINT categoryKey PRIMARY KEY (cid))";
    //     stmt.executeUpdate(categoryTableSql);
    
    //     // creating manufacturer table
    //     String manufacturerTableSql = "CREATE TABLE IF NOT EXISTS manufacturer (mid integer NOT NULL, mName char(20), mAddress char(50), mPhoneNumber integer, CONSTRAINT manufacturerKey PRIMARY KEY (mid))";
    //     stmt.executeUpdate(manufacturerTableSql);
    
    //     // creating part table
    //     String partTableSql = "CREATE TABLE IF NOT EXISTS part (pid integer NOT NULL, pName char(20), pPrice integer, mid integer NOT NULL, cid integer NOT NULL, pWarrantyPeriod integer, pAvailableQuantity integer, CONSTRAINT partKey PRIMARY KEY (pid), FOREIGN KEY (mid) REFERENCES manufacturer(mid), FOREIGN KEY (cid) REFERENCES category(cid))";
    //     stmt.executeUpdate(partTableSql);
    
    //     // creating salesperson table
    //     String salespersonTableSql = "CREATE TABLE IF NOT EXISTS salesperson (sid integer NOT NULL, sName char(20), sAddress char(50), sPhoneNumber integer, sExperience integer, CONSTRAINT salespersonKey PRIMARY KEY (sid))";
    //     stmt.executeUpdate(salespersonTableSql);
    
    //     // creating transaction table
    //     String transactionTableSql = "CREATE TABLE IF NOT EXISTS transaction (tid integer NOT NULL, pid integer NOT NULL, sid integer NOT NULL, tDate date, CONSTRAINT transactionKey PRIMARY KEY (tid), FOREIGN KEY (pid) REFERENCES part(pid), FOREIGN KEY (sid) REFERENCES salesperson(sid))";
    //     stmt.executeUpdate(transactionTableSql);
    
    //     System.out.println("Done. Database is initialized.");
    // }

    private static void createAllTable() throws SQLException {
        System.out.print("Processing...");
        Statement stmt = con.createStatement();

        // Creating category table
        String categoryTableSql = "CREATE TABLE category ("
                + "cid NUMBER NOT NULL, "
                + "cName VARCHAR2(20), "
                + "CONSTRAINT categoryKey PRIMARY KEY (cid))";
        stmt.executeUpdate(categoryTableSql);

        // Creating manufacturer table
        String manufacturerTableSql = "CREATE TABLE manufacturer ("
                + "mid NUMBER NOT NULL, "
                + "mName VARCHAR2(20), "
                + "mAddress VARCHAR2(50), "
                + "mPhoneNumber NUMBER, "
                + "CONSTRAINT manufacturerKey PRIMARY KEY (mid))";
        stmt.executeUpdate(manufacturerTableSql);

        // Creating part table
        String partTableSql = "CREATE TABLE part ("
                + "pid NUMBER NOT NULL, "
                + "pName VARCHAR2(20), "
                + "pPrice NUMBER, "
                + "mid NUMBER NOT NULL, "
                + "cid NUMBER NOT NULL, "
                + "pWarrantyPeriod NUMBER, "
                + "pAvailableQuantity NUMBER, "
                + "CONSTRAINT partKey PRIMARY KEY (pid), "
                + "CONSTRAINT fk_part_mid FOREIGN KEY (mid) REFERENCES manufacturer(mid), "
                + "CONSTRAINT fk_part_cid FOREIGN KEY (cid) REFERENCES category(cid))";
        stmt.executeUpdate(partTableSql);

        // Creating salesperson table
        String salespersonTableSql = "CREATE TABLE salesperson ("
                + "sid NUMBER NOT NULL, "
                + "sName VARCHAR2(20), "
                + "sAddress VARCHAR2(50), "
                + "sPhoneNumber NUMBER, "
                + "sExperience NUMBER, "
                + "CONSTRAINT salespersonKey PRIMARY KEY (sid))";
        stmt.executeUpdate(salespersonTableSql);

        // Creating transaction table
        String transactionTableSql = "CREATE TABLE transaction ("
                + "tid NUMBER NOT NULL, "
                + "pid NUMBER NOT NULL, "
                + "sid NUMBER NOT NULL, "
                + "tDate DATE, "
                + "CONSTRAINT transactionKey PRIMARY KEY (tid), "
                + "CONSTRAINT fk_transaction_pid FOREIGN KEY (pid) REFERENCES part(pid), "
                + "CONSTRAINT fk_transaction_sid FOREIGN KEY (sid) REFERENCES salesperson(sid))";
        stmt.executeUpdate(transactionTableSql);

        System.out.println("Done. Database is initialized.");
    }

    private static void deleteAllTable() throws SQLException {
        System.out.print("Processing...");
        Statement stmt = con.createStatement();

        try {
            // Dropping transaction table
            String transactionTableSql = "DROP TABLE transaction CASCADE CONSTRAINTS";
            stmt.executeUpdate(transactionTableSql);
        } catch (SQLException e) {
            System.out.println("Warning: Table 'transaction' does not exist or could not be dropped.");
        }

        try {
            // Dropping part table
            String partTableSql = "DROP TABLE part CASCADE CONSTRAINTS";
            stmt.executeUpdate(partTableSql);
        } catch (SQLException e) {
            System.out.println("Warning: Table 'part' does not exist or could not be dropped.");
        }

        try {
            // Dropping salesperson table
            String salespersonTableSql = "DROP TABLE salesperson CASCADE CONSTRAINTS";
            stmt.executeUpdate(salespersonTableSql);
        } catch (SQLException e) {
            System.out.println("Warning: Table 'salesperson' does not exist or could not be dropped.");
        }

        try {
            // Dropping manufacturer table
            String manufacturerTableSql = "DROP TABLE manufacturer CASCADE CONSTRAINTS";
            stmt.executeUpdate(manufacturerTableSql);
        } catch (SQLException e) {
            System.out.println("Warning: Table 'manufacturer' does not exist or could not be dropped.");
        }

        try {
            // Dropping category table
            String categoryTableSql = "DROP TABLE category CASCADE CONSTRAINTS";
            stmt.executeUpdate(categoryTableSql);
        } catch (SQLException e) {
            System.out.println("Warning: Table 'category' does not exist or could not be dropped.");
        }

        System.out.println("Done. Database is removed.");
    }

    // Method to load data from files into the database
    private static void loadDataFile() throws SQLException {
        // Arrays to store the data from different files
        String[][] categoryInfo = new String[10000][2];
        String[][] manufacturerInfo = new String[10000][4];
        String[][] partInfo = new String[10000][7];
        String[][] salespersonInfo = new String[10000][5];
        String[][] transactionInfo = new String[10000][4];

        System.out.print("Please enter the folder path: ");
        String path = input.next();
        input.nextLine();
        System.out.print("Processing...\n");

        try {
            // Load data from files into respective arrays
            loadFileData("./src/" + path + "/category.txt", categoryInfo);
            loadFileData("./src/" + path + "/manufacturer.txt", manufacturerInfo);
            loadFileData("./src/" + path + "/part.txt", partInfo);
            loadFileData("./src/" + path + "/salesperson.txt", salespersonInfo);
            loadFileData("./src/" + path + "/transaction.txt", transactionInfo);
        } catch (Exception e) {
            System.out.print(e); // Print any exceptions that occur during file loading
        }

        try {
            Connection oracle = Main.connectToOracle(); // Connect to the MySQL database

            // PreparedStatements for inserting data into respective tables
            PreparedStatement categoryPS = oracle.prepareStatement("INSERT INTO category VALUES (?, ?)");
            PreparedStatement manufacturerPS = oracle.prepareStatement("INSERT INTO manufacturer VALUES (?, ?, ?, ?)");
            PreparedStatement partPS = oracle.prepareStatement("INSERT INTO part VALUES (?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement salespersonPS = oracle.prepareStatement("INSERT INTO salesperson VALUES (?, ?, ?, ?, ?)");
            PreparedStatement transactionPS = oracle.prepareStatement("INSERT INTO transaction VALUES (?, ?, ?, ?)");

            // Insert data into respective tables
            insertData(categoryInfo, categoryPS);
            insertData(manufacturerInfo, manufacturerPS);
            insertData(partInfo, partPS);
            insertData(salespersonInfo, salespersonPS);
            insertTransactionData(transactionInfo, transactionPS);

            System.out.println("Done! Data is inputted to the database!");
        } catch (Exception e) {
            System.out.println(e); // Print any exceptions that occur during data insertion
        }
    }

    // Method to load data from a file into a 2D array
    private static void loadFileData(String filePath, String[][] data) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int count = 0;
        while ((st = br.readLine()) != null) {
            data[count] = st.split("\t"); // Split the line into an array of values
            count++;
        }
        br.close();
    }

    // Method to insert data from a 2D array into a table using a PreparedStatement
    private static void insertData(String[][] data, PreparedStatement ps) throws SQLException {
        for (int i = 0; data[i][0] != null; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (isNumeric(data[i][j])) {
                    ps.setInt(j + 1, Integer.parseInt(data[i][j])); // Set the value as an integer parameter
                } else {
                    ps.setString(j + 1, data[i][j]); // Set the value as a string parameter
                }
            }
            ps.executeUpdate(); // Execute the SQL update to insert the record
        }
    }

    // Method to insert transaction data from a 2D array into the transaction table using a PreparedStatement
    private static void insertTransactionData(String[][] data, PreparedStatement ps) throws SQLException, ParseException {
        for (int i = 0; data[i][0] != null; i++) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = formatter.parse(data[i][3]); // Parse the date string into a java.util.Date object
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis()); // Convert java.util.Date to java.sql.Date
            for (int j = 0; j < 3; j++) {
                ps.setInt(j + 1, Integer.parseInt(data[i][j])); // Set the values as integer parameters
            }
            ps.setDate(4, sqlDate); // Set the date value as a parameter
            ps.executeUpdate(); // Execute the SQL update to insert the record
        }
    }

    // Method to check if a string is numeric
    private static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
    }
        return true;
    }

    // Method to show the content of a table in the database
    private static void showContentOfRecord() throws SQLException {
        System.out.print("Which table would you like to show: ");
        String opt = input.nextLine();
        System.out.println("Content of table " + opt + ":");

        String query = "";
        switch (opt) {
            case "category":
                query = "SELECT * FROM category"; // Remove the semicolon
                break;
            case "manufacturer":
                query = "SELECT * FROM manufacturer"; // Remove the semicolon
                break;
            case "part":
                query = "SELECT * FROM part"; // Remove the semicolon
                break;
            case "salesperson":
                query = "SELECT * FROM salesperson"; // Remove the semicolon
                break;
            case "transaction":
                query = "SELECT * FROM transaction"; // Remove the semicolon
                break;
            default:
                System.out.println("[ERROR] Invalid Input"); // Print an error message for an invalid input
                return;
        }

        try {
            Connection oracle = Main.connectToOracle(); // Connect to the Oracle database
            Statement sql = oracle.createStatement();
            ResultSet resultSet = sql.executeQuery(query); // Execute the SQL query

            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            // Print the column headers
            System.out.print("| ");
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) {
                    System.out.print(" | ");
                }
                System.out.print(rsmd.getColumnName(i));
            }
            System.out.println(" |");

            // Print the table content
            while (resultSet.next()) {
                System.out.print("| ");
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) {
                        System.out.print(" | ");
                    }
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println(" |");
            }
        } catch (SQLException e) {
            System.out.println("[SQL Error]: " + e.toString()); // Print any SQL exceptions that occur
        }
    }
}