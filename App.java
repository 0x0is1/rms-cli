import java.sql.*;

public class App {

    private static void createTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "uid INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "section TEXT NOT NULL,"
                + "course TEXT NOT NULL"
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
            System.out.println("Admin Menu:");
            System.out.println("1. Add User");
            System.out.println("2. Delete User");
            System.out.println("3. Check Requests");
            System.out.println("4. Log out");

            int choice = Integer.parseInt(System.console().readLine());

            if (choice == 1) {
                System.out.println("Enter UID:");
                int uid = Integer.parseInt(System.console().readLine());
                System.out.println("Enter Name:");
                String name = System.console().readLine();
                System.out.println("Enter Section:");
                String section = System.console().readLine();
                System.out.println("Enter Course:");
                String course = System.console().readLine();

                addUser(conn, uid, name, section, course);

            } else if (choice == 2) {
                System.out.println("Enter UID:");
                int uid = Integer.parseInt(System.console().readLine());

                deleteUser(conn, uid);

            } else if (choice == 3) {
                ResultSet rs = checkRequests(conn);

                while (rs.next()) {
                    System.out.println("Request ID: " + rs.getInt("id"));
                    System.out.println("UID: " + rs.getInt("uid"));
                    System.out.println("Title: " + rs.getString("title"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("Status: " + rs.getString("status"));
                    System.out.println("Reply: " + rs.getString("reply"));
                    System.out.println();
                }

                rs.close();

                System.out.println("Enter Request ID to reply to, or 0 to return to menu:");
                int requestID = Integer.parseInt(System.console().readLine());

                if (requestID != 0) {
                    System.out.println("Enter reply:");
                    String reply = System.console().readLine();

                    replyToRequest(conn, requestID, reply);
                }

            } else if (choice == 4) {
                break;

            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addUser(Connection conn, int uid, String name, String section, String course) throws SQLException {
        String sql = "INSERT INTO users (uid, name, section, course) VALUES (?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);
        pstmt.setString(2, name);
        pstmt.setString(3, section);
        pstmt.setString(4, course);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println("User added.");
    }

private static void deleteUser(Connection conn, int uid) throws SQLException {
        String sql = "DELETE FROM users WHERE uid = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println("User deleted.");
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

        System.out.println("Request updated.");
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
            System.out.println("Student Menu:");
            System.out.println("1. Log Request");
            System.out.println("2. Check Request Status");
            System.out.println("3. Log out");

            int choice = Integer.parseInt(System.console().readLine());

            if (choice == 1) {
                System.out.println("Enter Title:");
                String title = System.console().readLine();
                System.out.println("Enter Description:");
                String description = System.console().readLine();

                logRequest(conn, uid, title, description);

            } else if (choice == 2) {
                ResultSet rs = checkRequestStatus(conn, uid);

                while (rs.next()) {
                    System.out.println("Request ID: " + rs.getInt("id"));
                    System.out.println("UID: " + rs.getInt("uid"));
                    System.out.println("Title: " + rs.getString("title"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("Status: " + rs.getString("status"));
                    System.out.println("Reply: " + rs.getString("reply"));
                    System.out.println();
                }

                rs.close();

            } else if (choice == 3) {
                break;

            } else {
                System.out.println("Invalid choice. Please try again.");
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

        System.out.println("Request logged.");
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
            // Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:/home/whitehats/discord-bot/javaproject2/database/university.db");

            createTable(conn);

            while (true) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("Welcome to the University System.");
                System.out.println("1. Admin Login");
                System.out.println("2. Student Login");
                System.out.println("3. Exit");

                int choice = Integer.parseInt(System.console().readLine());

                if (choice == 1) {
                    System.out.println("Enter Admin ID:");
                    int adminID = Integer.parseInt(System.console().readLine());

                    if (adminID == 1234) { // Replace with actual admin ID
                        adminMenu(conn);
                    } else {
                        System.out.println("Invalid Admin ID. Please try again.");
                    }

                } else if (choice == 2) {
                    System.out.println("Enter Student ID:");
                    int studentID = Integer.parseInt(System.console().readLine());

                    if (checkUserExists(conn, studentID)) {
                        studentMenu(conn, studentID);
                    } else {
                        System.out.println("User not found. Please try again.");
                    }

                } else if (choice == 3) {
                    break;

                } else {
                    System.out.println("Invalid choice. Please try again.");
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