import java.sql.*;

public class SQLHandler {
    public static void createTable(Connection conn) throws SQLException {
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

    public static void addUser(Connection conn, int uid, String name, String section, String course, String password)
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

        System.out.println(Constants.MESSAGE + "[+] User added." + Constants.END);
    }

    public static void deleteUser(Connection conn, int uid) throws SQLException {
        String sql = "DELETE FROM users WHERE uid = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println(Constants.MESSAGE + "[+] User deleted." + Constants.END);
    }

    public static ResultSet checkRequests(Connection conn) throws SQLException {
        String sql = "SELECT * FROM requests";

        Statement stmt = conn.createStatement();

        return stmt.executeQuery(sql);
    }

    public static void replyToRequest(Connection conn, int requestID, String reply) throws SQLException {
        String sql = "UPDATE requests SET status = 'closed', reply = ? WHERE id = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, reply);
        pstmt.setInt(2, requestID);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println(Constants.MESSAGE + "[+] Request updated." + Constants.END);
    }

    public static boolean checkUserExists(Connection conn, int uid) throws SQLException {
        String sql = "SELECT uid FROM users WHERE uid = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);

        ResultSet rs = pstmt.executeQuery();

        boolean exists = rs.next();

        rs.close();
        pstmt.close();

        return exists;
    }

    public static void logRequest(Connection conn, int uid, String title, String description) throws SQLException {
        String sql = "INSERT INTO requests (uid, title, description) VALUES (?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);
        pstmt.setString(2, title);
        pstmt.setString(3, description);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println(Constants.INFO + "[+] Request logged." + Constants.END);
    }

    public static ResultSet checkRequestStatus(Connection conn, int uid) throws SQLException {
        String sql = "SELECT * FROM requests WHERE uid = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, uid);

        return pstmt.executeQuery();
    }
}
