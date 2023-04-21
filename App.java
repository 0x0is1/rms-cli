import java.sql.*;

public class App {
    private static final int ADMIN_UID = 1234;
    private static final String ADMIN_PASS = "password";
    private static final String END = "\u001B[0m";
    private static final String WARNING = "\u001B[33m";
    private static final String ERROR = "\u001B[31m";
    private static final String MESSAGE = "\u001B[32m";
    private static final String INFO = "\u001B[37m";
    private static final String DETAIL = "\u001B[34m";

    private static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void createTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "uid INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "section TEXT NOT NULL,"
                + "course TEXT NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS requests ("
                + "id INTEGER PRIMARY KEY,"
                + "uid INTEGER NOT NULL,"
                + "title TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "status TEXT DEFAULT 'open',"
                + "reply TEXT DEFAULT '',"
                + "FOREIGN KEY(uid) REFERENCES users(uid)"
                + ");";

        stmt.execute(sql);
        stmt.close();
    }

    private static void adminMenu(Connection conn) throws SQLException {
        while (true) {
            System.out.println(INFO + "[*] Admin Menu:" + END);
            System.out.println(DETAIL + "[1] Add User" + END);
            System.out.println(DETAIL + "[2] Delete User" + END);
            System.out.println(DETAIL + "[3] Check Requests" + END);
            System.out.println(DETAIL + "[4] Log out" + END);

            int choice = Integer.parseInt(System.console().readLine());
            int uid = 0;
            switch (choice) {
                case 1:
                    System.out.println(DETAIL + "[*] Enter UID:" + END);
                    uid = Integer.parseInt(System.console().readLine());
                    System.out.println(DETAIL + "[*] Enter Name:" + END);
                    String name = System.console().readLine();
                    System.out.println(DETAIL + "[*] Enter Section:" + END);
                    String section = System.console().readLine();
                    System.out.println(DETAIL + "[*] Enter Course:" + END);
                    String course = System.console().readLine();
                    System.out.println(DETAIL + "[*] Enter password:" + END);
                    String password = System.console().readLine();
                    addUser(conn, uid, name, section, course, password);
                    break;

                case 2:
                    System.out.println("[*] Enter UID:" + END);
                    uid = Integer.parseInt(System.console().readLine());
                    deleteUser(conn, uid);
                    break;

                case 3:
                    ResultSet rs = checkRequests(conn);
                    while (rs.next()) {
                        System.out.println(WARNING + "[!] Request ID: " + rs.getInt("id") + END);
                        System.out.println(MESSAGE + "[*] UID: " + rs.getInt("uid") + END);
                        System.out.println(MESSAGE + "[*] Title: " + rs.getString("title") + END);
                        System.out.println(MESSAGE + "[*] Description: " + rs.getString("description") + END);
                        System.out.println(MESSAGE + "[*] Status: " + rs.getString("status") + END);
                        System.out.println(MESSAGE + "[*] Reply: " + rs.getString("reply") + END);
                        System.out.println();
                    }
                    rs.close();
                    System.out.println(INFO + "[!] Enter Request ID to reply to, or 0 to return to menu:" + END);
                    int requestID = Integer.parseInt(System.console().readLine());
                    if (requestID != 0) {
                        System.out.println(DETAIL + "[*] Enter reply:" + END);
                        String reply = System.console().readLine();
                        replyToRequest(conn, requestID, reply);
                    }
                    break;

                case 4:
                    break;

                default:
                    System.out.println(ERROR + "[-] Invalid choice. Please try again." + END);
            }
        }
    }

    private static void addUser(Connection conn, int uid, String name, String section, String course, String password)
            throws SQLException {
        String sql = "INSERT INTO users (uid, name, section, course, password) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);
        pstmt.setString(2, name);
        pstmt.setString(3, section);
        pstmt.setString(4, course);
        pstmt.setString(5, password);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println(MESSAGE + "[+] User added." + END);
    }

    private static void deleteUser(Connection conn, int uid) throws SQLException {
        String sql = "DELETE FROM users WHERE uid = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println(MESSAGE + "[+] User deleted." + END);
    }

    private static ResultSet checkRequests(Connection conn) throws SQLException {
        String sql = "SELECT * FROM requests";

        Statement stmt = conn.createStatement();

        return stmt.executeQuery(sql);
    }

    private static void replyToRequest(Connection conn, int requestID, String reply) throws SQLException {
        String sql = "UPDATE requests SET status = 'closed', reply = ? WHERE id = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, reply);
        pstmt.setInt(2, requestID);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println(MESSAGE + "[+] Request updated." + END);
    }

    private static boolean checkUserExists(Connection conn, int uid) throws SQLException {
        String sql = "SELECT uid FROM users WHERE uid = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);

        ResultSet rs = pstmt.executeQuery();

        boolean exists = rs.next();

        rs.close();
        pstmt.close();

        return exists;
    }

    private static void studentMenu(Connection conn, int uid) throws SQLException {
        while (true) {
            System.out.println(WARNING + "[!] Student Menu:" + END);
            System.out.println(DETAIL + "[1] Log Request" + END);
            System.out.println(DETAIL + "[2] Check Request Status" + END);
            System.out.println(DETAIL + "[3] Log out" + END);

            int choice = Integer.parseInt(System.console().readLine());

            switch (choice) {
                case 1:
                    System.out.println(DETAIL + "[*] Enter Title:" + END);
                    String title = System.console().readLine();
                    System.out.println(DETAIL + "[*] Enter Description:" + END);
                    String description = System.console().readLine();
                    logRequest(conn, uid, title, description);
                    break;

                case 2:
                    ResultSet rs = checkRequestStatus(conn, uid);
                    while (rs.next()) {
                        System.out.println(WARNING + "[!] Request ID: " + rs.getInt("id") + END);
                        System.out.println(MESSAGE + "[*] UID: " + rs.getInt("uid") + END);
                        System.out.println(MESSAGE + "[*] Title: " + rs.getString("title") + END);
                        System.out.println(MESSAGE + "[*] Description: " + rs.getString("description") + END);
                        System.out.println(MESSAGE + "[*] Status: " + rs.getString("status") + END);
                        System.out.println(MESSAGE + "[*] Reply: " + rs.getString("reply") + END);
                        System.out.println();
                    }
                    rs.close();
                    break;

                case 3:
                    break;

                default:
                    System.out.println(ERROR + "[-] Invalid choice. Please try again." + END);
                    break;
            }
        }
    }

    private static void logRequest(Connection conn, int uid, String title, String description) throws SQLException {
        String sql = "INSERT INTO requests (uid, title, description) VALUES (?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);
        pstmt.setString(2, title);
        pstmt.setString(3, description);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println(INFO + "[+] Request logged." + END);
    }

    private static ResultSet checkRequestStatus(Connection conn, int uid) throws SQLException {
        String sql = "SELECT * FROM requests WHERE uid = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);

        return pstmt.executeQuery();
    }

    public static void main(String[] args) {
        Connection conn = null;
        try {
            int adminID = 0;
            String pass = null;
            // Class.forName("org.sqlite.JDBC");
            conn = DriverManager
                    .getConnection("jdbc:sqlite:/home/whitehats/discord-bot/javaproject2/database/university.db");

            createTable(conn);

            while (true) {
                System.out.println(WARNING + "[*] Welcome to the University System.");
                System.out.println(DETAIL + "[1] Admin Login" + END);
                System.out.println(DETAIL + "[2] Student Login" + END);
                System.out.println(DETAIL + "[3] Exit" + END);

                int choice = Integer.parseInt(System.console().readLine());

                switch (choice) {
                    case 1:
                        System.out.println(INFO + "[*] Enter Admin ID:" + END);
                        try {
                            adminID = Integer.parseInt(System.console().readLine());
                        } catch (Exception e) {
                            System.out.println(ERROR + "[-] Admin ID should be integer only" + END);
                            continue;
                        }
                        System.out.println(INFO + "[*] Enter Admin password:" + END);

                        try {
                            pass = System.console().readLine();

                        } catch (Exception e) {
                            System.out.println(ERROR + "[-] Admin password should be string only" + END);
                            continue;
                        }

                        if (adminID == ADMIN_UID && pass.equals(ADMIN_PASS)) {
                            adminMenu(conn);
                        } else {
                            System.out.println(ERROR + "[-] Invalid Admin ID. Please try again." + END);
                        }
                        break;

                    case 2:
                        System.out.println(INFO + "[*] Enter Student ID:" + END);
                        int studentID = Integer.parseInt(System.console().readLine());

                        if (checkUserExists(conn, studentID)) {
                            studentMenu(conn, studentID);
                        } else {
                            System.out.println(ERROR + "[-] User not found. Please try again." + END);
                        }
                        break;

                    case 3:
                        break;

                    default:
                        System.out.println(ERROR + "[-] Invalid choice. Please try again." + END);
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