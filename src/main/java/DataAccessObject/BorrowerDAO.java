package DataAccessObject;

import Database.DataBase;
import Entity.Borrower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The BorrowerDAO class provides methods to interact with the Borrowers table in the database.
 * It enables CRUD operations and custom queries for managing borrower data.
 */
public class BorrowerDAO {
    // Database connection variables
    private final Connection conn = DataBase.getInstance().getConnection();
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    /**
     * Fetches all borrowers from the database.
     *
     * @return an ObservableList of all borrowers.
     */
    public ObservableList<Borrower> getAllBorrowers() {
        ObservableList<Borrower> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM borrowers";

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = getUsernameById(userId);

                Borrower b = new Borrower(
                        rs.getInt("id"),
                        username,
                        userId,
                        rs.getInt("book_id"),
                        rs.getString("bookName"),
                        rs.getString("borrow_from"),
                        rs.getString("borrow_to"),
                        rs.getString("status")
                );
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    /**
     * Checks if a book with the specified ID is currently being borrowed with the given status.
     *
     * @param status the status of the borrowing (e.g., "processing").
     * @param bookId the ID of the book.
     * @return true if a borrower exists for the specified book and status, false otherwise.
     */
    public boolean hasBorrowingBook(String status, int bookId) {
        String sql = "SELECT 1 FROM borrowers WHERE status = ? AND book_id = ? LIMIT 1";

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true; // Nếu tìm thấy ít nhất 1 dòng, trả về true
                }
            }
        } catch (Exception e) {
            System.err.println("Error in hasBorrowerByStatusAndUserId: " + e.getMessage());
        } finally {
            closeResources(); // Đóng kết nối và các tài nguyên
        }

        return false; // Nếu không tìm thấy, trả về false
    }

    /**
     * Checks if a borrower exists with the specified status and user ID.
     *
     * @param status the status of the borrowing.
     * @param userId the ID of the user.
     * @return true if such a borrower exists, false otherwise.
     */
    public boolean hasBorrowerByStatusAndUserId(String status, int userId) {
        String sql = "SELECT 1 FROM borrowers WHERE status = ? AND user_id = ? LIMIT 1";

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true; // Nếu tìm thấy ít nhất 1 dòng, trả về true
                }
            }
        } catch (Exception e) {
            System.err.println("Error in hasBorrowerByStatusAndUserId: " + e.getMessage());
        } finally {
            closeResources(); // Đóng kết nối và các tài nguyên
        }

        return false; // Nếu không tìm thấy, trả về false
    }

    /**
     * Fetches borrowers by username using a LIKE search.
     *
     * @param username the username to search for.
     * @return a list of borrowers matching the username.
     */
    public ArrayList<Borrower> getBorrowerByUsername(String username) {
        ArrayList<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowers b JOIN users U ON B.user_id = U.id WHERE U.username LIKE ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,'%' + username + '%');
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String usernames = getUsernameById(userId);

                    Borrower b = new Borrower(
                            rs.getInt("id"),
                            usernames,
                            userId,
                            rs.getInt("book_id"),
                            rs.getString("bookName"),
                            rs.getString("borrow_from"),
                            rs.getString("borrow_to"),
                            rs.getString("status")
                    );
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerByUsername: " + e.getMessage());
        } finally {
            closeResources();
        }
        return list;
    }

    /**
     * Fetches a borrower by their ID.
     *
     * @param id the ID of the borrower.
     * @return a Borrower object, or null if not found.
     */
    public Borrower getBorrowerById(int id) {
        String sql = "SELECT * FROM borrowers WHERE id = ?";
        Borrower b = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = getUsernameById(userId);
                    b = new Borrower(rs.getInt("id"), username, id,
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

    /**
     * Fetches all borrowers for a given user ID.
     *
     * @param id the ID of the user.
     * @return a list of borrowers associated with the user ID.
     */
    public List<Borrower> getBorrowerListByUserId(int id) {
        List<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowers WHERE user_id = ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,String.valueOf(id));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String usernames = getUsernameById(userId);

                    Borrower b = new Borrower(
                            rs.getInt("id"),
                            usernames,
                            userId,
                            rs.getInt("book_id"),
                            rs.getString("bookName"),
                            rs.getString("borrow_from"),
                            rs.getString("borrow_to"),
                            rs.getString("status")
                    );
                    list.add(b);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getBorrowerById: " + e.getMessage());
        } finally {
            closeResources();
        }
        return list;
    }

    /**
     * Fetches the username associated with a given user ID.
     *
     * @param userId the ID of the user.
     * @return the username of the user, or null if not found.
     */
    private String getUsernameById(int userId) {
        String username = null;
        String sql = "SELECT username FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }

    /**
     * Updates the details of a borrower in the database.
     *
     * @param borrower the Borrower object with updated information.
     */
    public void updateBorrower(Borrower borrower) {
        String sql = "UPDATE borrowers SET user_id = ?, book_id = ?, bookName = ?, borrow_from = ?, borrow_to = ?, status = ? WHERE id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, borrower.getUser_id());
            ps.setInt(2, borrower.getBookId());
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

    /**
     * Inserts a new borrower record into the database.
     *
     * @param user_id     the ID of the user borrowing the book.
     * @param book_id     the ID of the book being borrowed.
     * @param bookName    the name of the book.
     * @param borrow_from the start date of the borrowing.
     * @param borrow_to   the end date of the borrowing.
     */
    public void insertBorrower(int user_id, int book_id, String bookName, String borrow_from, String borrow_to) {
        String sql = "INSERT INTO borrowers (user_id, book_id, bookName, borrow_from, borrow_to, status) VALUES (?, ?, ?, ?, ?, 'processing')";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.setInt(2, book_id);
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

    /**
     * Checks if a book is currently being borrowed with a "processing" status.
     *
     * @param bookID the ID of the book.
     * @return true if the book is being borrowed, false otherwise.
     */
    public boolean checkBookExists(int bookID) {
        String sql = "SELECT COUNT(*) FROM borrowers WHERE book_id = ? AND status = 'processing'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a user has reached the borrowing limit (more than 3 books with "processing" status).
     *
     * @param username the username of the user.
     * @return true if the limit is exceeded, false otherwise.
     */
    public boolean checkLimitStmt(String username) {
        String sql = "SELECT COUNT(*) FROM borrowers WHERE user_id = (SELECT id FROM users WHERE username = ?) AND status = 'processing'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Closes all open database resources, including Connection, PreparedStatement, and ResultSet.
     */
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
