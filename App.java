import java.sql.*;

class Constants {
    public static final int ADMIN_UID = 1234;
    public static final String ADMIN_PASS = "password";
    public static final String END = "\u001B[0m";
    public static final String WARNING = "\u001B[33m";
    public static final String ERROR = "\u001B[31m";
    public static final String MESSAGE = "\u001B[32m";
    public static final String INFO = "\u001B[37m";
    public static final String DETAIL = "\u001B[34m";
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

public class App extends MenuHandler{
    public static void main(String[] args) {
        Connection conn = null;
        try {
            int adminID = 0;
            String pass = null;
            // Class.forName("org.sqlite.JDBC");
            conn = DriverManager
                    .getConnection("jdbc:sqlite:./database/university.db");

            createTable(conn);
            boolean startMenu = true;
            while (startMenu) {
                
                System.out.println(Constants.WARNING + "[*] Welcome to the University System.");
                System.out.println(Constants.DETAIL + "[1] Admin Login" + Constants.END);
                System.out.println(Constants.DETAIL + "[2] Student Login" + Constants.END);
                System.out.println(Constants.DETAIL + "[3] Exit" + Constants.END);

                int choice = Integer.parseInt(System.console().readLine());

                switch (choice) {
                    case 1:
                        System.out.println(Constants.INFO + "[*] Enter Admin ID:" + Constants.END);
                        try {
                            adminID = Integer.parseInt(System.console().readLine());
                        } catch (Exception e) {
                            System.out.println(Constants.ERROR + "[-] Admin ID should be integer only" + Constants.END);
                            continue;
                        }
                        System.out.println(Constants.INFO + "[*] Enter Admin password:" + Constants.END);

                        try {
                            pass = System.console().readLine();

                        } catch (Exception e) {
                            System.out.println(Constants.ERROR + "[-] Admin password should be string only" + Constants.END);
                            continue;
                        }

                        if (adminID == Constants.ADMIN_UID && pass.equals(Constants.ADMIN_PASS)) {
                            adminMenu(conn);
                        } else {
                            System.out.println(Constants.ERROR + "[-] Invalid Admin ID. Please try again." + Constants.END);
                        }
                        break;

                    case 2:
                        System.out.println(Constants.INFO + "[*] Enter Student ID:" + Constants.END);
                        int studentID = Integer.parseInt(System.console().readLine());

                        if (checkUserExists(conn, studentID)) {
                            studentMenu(conn, studentID);
                        } else {
                            System.out.println(Constants.ERROR + "[-] User not found. Please try again." + Constants.END);
                        }
                        break;

                    case 3:
                        startMenu = false;
                        break;

                    default:
                        System.out.println(Constants.ERROR + "[-] Invalid choice. Please try again." + Constants.END);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}