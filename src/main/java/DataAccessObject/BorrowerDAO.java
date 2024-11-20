package DataAccessObject;

import Database.DataBase;
import Entity.Book;
import Entity.Borrower;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BorrowerDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public ArrayList<Borrower> getListBorrowerByPage(ArrayList<Borrower> list, int start, int end) {
        ArrayList<Borrower> arr = new ArrayList<>();
        for (int i = start; i < end && i < list.size(); ++i) {
            arr.add(list.get(i));
        }
        return arr;
    }

    public void deleteBorrower(String id) {
        String sql = "DELETE FROM borrower WHERE id = ?";
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error in deleteBorrower: " + e.getMessage());
        }
    }

    public ArrayList<Borrower> getBorrowerByStatusAndUsername(String status, String username) {
        ArrayList<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrower WHERE status = ? AND username = ?";
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Borrower b = new Borrower(rs.getInt("id"), rs.getString("username"),
                            rs.getInt("book_id"), rs.getString("bookName"),
                            rs.getString("borrow_from"),
                            rs.getString("borrow_to"), rs.getString("status"));
                    if (!"processing".equals(status)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = dateFormat.parse(rs.getString("borrow_to"));
                        if (date != null && date.before(new Date())) {
                            b.setLate(true);
                        }
                    }
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerByStatusAndUsername: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<Borrower> getBorrowerByStatus(String status) {
        ArrayList<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrower WHERE status = ?";
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Borrower b = new Borrower(rs.getInt("id"), rs.getString("username"),
                            rs.getInt("book_id"), rs.getString("bookName")
                            , rs.getString("borrow_from"),
                            rs.getString("borrow_to"), rs.getString("status"));
                    if (!"processing".equals(status)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = dateFormat.parse(rs.getString("borrow_to"));
                        if (date != null && date.before(new Date())) {
                            b.setLate(true);
                        }
                    }
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerByStatus: " + e.getMessage());
        }
        return list;
    }

    public ArrayList<Borrower> getBorrowerByStatusAndUserId(String status, String username) {
        ArrayList<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrower WHERE status = ? AND username = ?";
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Borrower b = new Borrower(rs.getInt("id"), rs.getString("username"),
                            rs.getInt("book_id"), rs.getString("bookName"), rs.getString("borrow_from"),
                            rs.getString("borrow_to"), rs.getString("status"));
                    list.add(b);
                    rs.close();
                }
                ps.close();
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerByStatusAndUserId: " + e.getMessage());
        }
        return list;
    }

    public Borrower getBorrowerById(String id) {
        String sql = "SELECT * FROM borrower WHERE id = ?";
        Borrower b = null;
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    b = new Borrower(rs.getInt("id"), rs.getString("username"),
                            rs.getInt("book_id"), rs.getString("bookName"), rs.getString("borrow_from"),
                            rs.getString("borrow_to"), rs.getString("status"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerById: " + e.getMessage());
        }
        return b;
    }

    public void updateBorrower(Borrower borrower) {
        String sql = "UPDATE borrower SET username = ?, book_id = ?, bookName = ?, borrow_from = ?, borrow_to = ?, status = ? WHERE id = ?";
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, borrower.getUsername());
            ps.setInt(2, borrower.getBookid());
            ps.setString(3, borrower.getBookName());
            ps.setString(4, borrower.getBorrow_from());
            ps.setString(5, borrower.getBorrow_to());
            ps.setString(6, borrower.getStatus());
            ps.setInt(7, borrower.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error in updateBorrower: " + e.getMessage());
        }
    }

    public void insertBorrower(String username, String bookid, String bookName, String borrow_from, String borrow_to) {
        String sql = "INSERT INTO borrower (username, book_id, bookName, borrow_from,borrow_to, status) VALUES (?, ?, ?, ?, ?,  'processing')";
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, bookid);
            ps.setString(3, bookName);
            ps.setString(4, borrow_from);
            ps.setString(5, borrow_to);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error in insertBorrower: " + e.getMessage());
        }
    }

    public boolean checkBookExists(int bookID) {
        String sql = "SELECT COUNT(*) FROM borrower WHERE book_id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
//    public boolean insertBorrowHistory(int memberId, int bookId, String borrowDate) {
//        String query = "INSERT INTO BorrowHistory (memberId, bookId, borrowDate) VALUES (?, ?, ?)";
//
//        try (Connection conn = DataBase.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, memberId);
//            stmt.setInt(2, bookId);
//            stmt.setString(3, borrowDate);
//
//            int rowsAffected = stmt.executeUpdate();
//            return rowsAffected > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
