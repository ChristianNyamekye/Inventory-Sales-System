import java.text.SimpleDateFormat;
import java.sql.*;
import java.util.Date;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Salesperson {
    private static Scanner input = new Scanner(System.in);
    private static int Transaction = 0;
    private static boolean available = false;
    public static void main(){
        do{
            try{
                System.out.println("What kinds of operation would you like to perform");
                System.out.println("1. Search for parts");
                System.out.println("2. Sell a part");
                System.out.println("3. Return to the main menu");
                System.out.print("Enter Your Choice: ");
                short choice = input.nextShort();
                switch(choice){
                    case 1:
                        searchForParts();
                        break;
                    case 2:
                        transact();
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("[ERROR] Invalid Input");
                }
            }catch(Exception e){
                System.out.println(e);
                input.next();
            }
        }while(true);
    }
    private static void searchForParts() {
        System.out.println("Choose the search criterion");
        System.out.println("1. Part Name");
        System.out.println("2. Manufacturer Name");
        System.out.print("Choose the search criterion: ");
        short choice = input.nextShort();
        input.nextLine();
        System.out.print("Type in the Search Keyword: ");
        String keyword = input.nextLine();
        System.out.println("Choose ordering:");
        System.out.println("1. By price, ascending order");
        System.out.println("2. By price, descending order");
        System.out.print("Choose the search criterion: ");
        short choice1 = input.nextShort();

        if ((choice == 1 || choice == 2) && (choice1 == 1 || choice1 == 2)) {
            try {
                Connection con = Main.connectToOracle();
                String query = "SELECT P.pid, P.pName, M.mName, C.cName, P.pAvailableQuantity, P.pWarrantyPeriod, P.pPrice " +
                            "FROM part P " +
                            "JOIN manufacturer M ON P.mid = M.mid " +
                            "JOIN category C ON P.cid = C.cid ";

                // Add search criteria
                if (choice == 1) {
                    query += "WHERE LOWER(P.pName) LIKE ? ";
                } else if (choice == 2) {
                    query += "WHERE LOWER(M.mName) LIKE ? ";
                }

                // Add ordering
                if (choice1 == 1) {
                    query += "ORDER BY P.pPrice ASC";
                } else if (choice1 == 2) {
                    query += "ORDER BY P.pPrice DESC";
                }

                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, "%" + keyword.toLowerCase() + "%"); // Use case-insensitive search

                ResultSet rs = pstmt.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();

                // Print column headers
                System.out.print("| ");
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.print(rsmd.getColumnName(i) + " | ");
                }
                System.out.println();

                // Print rows
                while (rs.next()) {
                    System.out.print("| ");
                    for (int i = 1; i <= columnsNumber; i++) {
                        String columnValue = rs.getString(i);
                        System.out.print(columnValue + " | ");
                    }
                    System.out.println();
                }
                System.out.println("End of query.");
            } catch (Exception e) {
                System.out.println("[Error]: " + e.getMessage());
            }
        } else {
            System.out.println("[ERROR] Invalid Input");
        }
    }

    private static void transact() {
        System.out.print("Enter The Part ID: ");
        int pID = input.nextInt();
        System.out.print("Enter The Salesperson ID: ");
        int sID = input.nextInt();

        try {
            Connection con = Main.connectToOracle();

            // Check part availability
            String query = "SELECT P.pAvailableQuantity, P.pName " +
                        "FROM part P " +
                        "WHERE P.pid = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, pID);
            ResultSet rsPart = pstmt.executeQuery();

            // Check salesperson existence
            query = "SELECT S.sid " +
                    "FROM salesperson S " +
                    "WHERE S.sid = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, sID);
            ResultSet rsSales = pstmt.executeQuery();

            // Validate both part and salesperson
            if (rsPart.next() && rsSales.next()) {
                int quantity = rsPart.getInt("pAvailableQuantity");
                String pName = rsPart.getString("pName");

                if (quantity > 0) {
                    // Fetch the next transaction ID
                    query = "SELECT NVL(MAX(T.tid), 0) + 1 AS nextTid FROM transaction T";
                    pstmt = con.prepareStatement(query);
                    ResultSet rsTransaction = pstmt.executeQuery();
                    rsTransaction.next();
                    int nextTid = rsTransaction.getInt("nextTid");

                    // Insert transaction
                    String insert = "INSERT INTO transaction (tid, pid, sid, tDate) " +
                                    "VALUES (?, ?, ?, ?)";
                    pstmt = con.prepareStatement(insert);
                    pstmt.setInt(1, nextTid);
                    pstmt.setInt(2, pID);
                    pstmt.setInt(3, sID);

                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    pstmt.setDate(4, sqlDate);
                    pstmt.executeUpdate();

                    // Update part availability
                    String update = "UPDATE part " +
                                    "SET pAvailableQuantity = pAvailableQuantity - 1 " +
                                    "WHERE pid = ?";
                    pstmt = con.prepareStatement(update);
                    pstmt.setInt(1, pID);
                    pstmt.executeUpdate();

                    System.out.println("Product: " + pName +
                                    " (id: " + pID + ") Remaining Quantity: " + (quantity - 1));
                } else {
                    System.out.println("Transaction failed!");
                    System.out.println("Product: " + pName +
                                    " (id: " + pID + ") Remaining Quantity: 0");
                }
            } else {
                System.out.println("Unavailable part ID or salesperson ID.");
            }
        } catch (Exception e) {
            System.out.println("[Error]: " + e.getMessage());
        }
    }
}
