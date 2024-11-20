package DataAccessObject;

import Database.DataBase;
import Entity.Borrower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BorrowerDAO {
    // Database connection variables
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    // Get all borrowers from the database
    public ObservableList<Borrower> getAllBorrowers() {
        ObservableList<Borrower> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM borrower";

        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                // Add borrower to the list
                Borrower b = new Borrower(rs.getInt("id"), rs.getString("username"),
                        rs.getInt("book_id"), rs.getString("bookName"),
                        rs.getString("borrow_from"), rs.getString("borrow_to"), rs.getString("status"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    // Delete borrower by ID
    public void deleteBorrower(String id) {
        String sql = "DELETE FROM borrower WHERE id = ?";
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error in deleteBorrower: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    // Get borrowers by status and username
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
                    // Add borrower to the list
                    Borrower b = new Borrower(rs.getInt("id"), rs.getString("username"),
                            rs.getInt("book_id"), rs.getString("bookName"),
                            rs.getString("borrow_from"), rs.getString("borrow_to"), rs.getString("status"));
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerByStatusAndUsername: " + e.getMessage());
        } finally {
            closeResources();
        }
        return list;
    }

    // Get borrowers by status
    public ArrayList<Borrower> getBorrowerByStatus(String status) {
        ArrayList<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrower WHERE status = ?";

        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Add borrower to the list
                    Borrower b = new Borrower(rs.getInt("id"), rs.getString("username"),
                            rs.getInt("book_id"), rs.getString("bookName"),
                            rs.getString("borrow_from"), rs.getString("borrow_to"), rs.getString("status"));
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerByStatus: " + e.getMessage());
        } finally {
            closeResources();
        }
        return list;
    }

    // Get borrower by ID
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
                            rs.getInt("book_id"), rs.getString("bookName"),
                            rs.getString("borrow_from"), rs.getString("borrow_to"), rs.getString("status"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerById: " + e.getMessage());
        } finally {
            closeResources();
        }
        return b;
    }

    // Update borrower details
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
        } finally {
            closeResources();
        }
    }

    // Insert a new borrower record
    public void insertBorrower(String username, String bookid, String bookName, String borrow_from, String borrow_to) {
        String sql = "INSERT INTO borrower (username, book_id, bookName, borrow_from, borrow_to, status) VALUES (?, ?, ?, ?, ?, 'processing')";
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
        } finally {
            closeResources();
        }
    }

    // Check if the book is already borrowed and being processed
    public boolean checkBookExists(int bookID) {
        String sql = "SELECT COUNT(*) FROM borrower WHERE book_id = ? AND status = 'processing'";
        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if a user has reached the borrowing limit
    public boolean checkLimitStmt(String userName) {
        String sql = "SELECT COUNT(*) FROM borrower WHERE username = ? AND status = 'processing'";
        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to close database resources
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
