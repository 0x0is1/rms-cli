import java.sql.*;

public class MenuHandler extends SQLHandler {
    
    public static void studentMenu(Connection conn, int uid) throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            
            System.out.println(Constants.WARNING + "[!] Student Menu:" + Constants.END);
            System.out.println(Constants.DETAIL + "[1] Log Request" + Constants.END);
            System.out.println(Constants.DETAIL + "[2] Check Request Status" + Constants.END);
            System.out.println(Constants.DETAIL + "[3] Log out" + Constants.END);

            int choice = Integer.parseInt(System.console().readLine());
            
            switch (choice) {
                case 1:
                    Constants.clear();
                    System.out.println(Constants.DETAIL + "[*] Enter Title:" + Constants.END);
                    String title = System.console().readLine();
                    System.out.println(Constants.DETAIL + "[*] Enter Description:" + Constants.END);
                    String description = System.console().readLine();
                    logRequest(conn, uid, title, description);
                    break;

                case 2:
                    Constants.clear();
                    ResultSet rs = checkRequestStatus(conn, uid);
                    while (rs.next()) {
                        
                        System.out.println(Constants.WARNING + "[!] Request ID: " + rs.getInt("id") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] UID: " + rs.getInt("uid") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Title: " + rs.getString("title") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Description: " + rs.getString("description") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Status: " + rs.getString("status") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Reply: " + rs.getString("reply") + Constants.END);
                        System.out.println();
                    }
                    rs.close();
                    break;

                case 3:
                    Constants.clear();
                    loggedIn = false;
                    break;

                default:
                    Constants.clear();
                    System.out.println(Constants.ERROR + "[-] Invalid choice. Please try again." + Constants.END);
                    break;
            }
        }
    }
    public static void adminMenu(Connection conn) throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            
            System.out.println(Constants.INFO + "[*] Admin Menu:" + Constants.END);
            System.out.println(Constants.DETAIL + "[1] Add User" + Constants.END);
            System.out.println(Constants.DETAIL + "[2] Delete User" + Constants.END);
            System.out.println(Constants.DETAIL + "[3] Check Requests" + Constants.END);
            System.out.println(Constants.DETAIL + "[4] Log out" + Constants.END);

            int choice = Integer.parseInt(System.console().readLine());
            
            int uid = 0;
            switch (choice) {
                case 1:
                    Constants.clear();
                    System.out.println(Constants.DETAIL + "[*] Enter UID:" + Constants.END);
                    uid = Integer.parseInt(System.console().readLine());
                    System.out.println(Constants.DETAIL + "[*] Enter Name:" + Constants.END);
                    String name = System.console().readLine();
                    System.out.println(Constants.DETAIL + "[*] Enter Section:" + Constants.END);
                    String section = System.console().readLine();
                    System.out.println(Constants.DETAIL + "[*] Enter Course:" + Constants.END);
                    String course = System.console().readLine();
                    System.out.println(Constants.DETAIL + "[*] Enter password:" + Constants.END);
                    String password = System.console().readLine();
                    addUser(conn, uid, name, section, course, password);
                    break;

                case 2:
                    Constants.clear();
                    System.out.println("[*] Enter UID:" + Constants.END);
                    uid = Integer.parseInt(System.console().readLine());
                    deleteUser(conn, uid);
                    break;

                case 3:
                    Constants.clear();
                    ResultSet rs = checkRequests(conn);
                    while (rs.next()) {
                        System.out.println(Constants.WARNING + "[!] Request ID: " + rs.getInt("id") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] UID: " + rs.getInt("uid") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Title: " + rs.getString("title") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Description: " + rs.getString("description") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Status: " + rs.getString("status") + Constants.END);
                        System.out.println(Constants.MESSAGE + "[*] Reply: " + rs.getString("reply") + Constants.END);
                        System.out.println();
                    }
                    rs.close();
                    System.out.println(Constants.INFO + "[!] Enter Request ID to reply to, or 0 to return to menu:" + Constants.END);
                    int requestID = Integer.parseInt(System.console().readLine());
                    if (requestID != 0) {
                        Constants.clear();
                        System.out.println(Constants.DETAIL + "[*] Enter reply:" + Constants.END);
                        String reply = System.console().readLine();
                        replyToRequest(conn, requestID, reply);
                    }
                    Constants.clear();
                    break;

                case 4:
                    Constants.clear();
                    loggedIn = false;
                    break;

                default:
                    Constants.clear();
                    System.out.println(Constants.ERROR + "[-] Invalid choice. Please try again." + Constants.END);
                    break;
            }
        }
    }

}
