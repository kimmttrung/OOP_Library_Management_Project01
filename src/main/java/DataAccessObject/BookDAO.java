package DataAccessObject;

import Entity.Book;
import Database.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Fetch all books and return them as an ObservableList
    public ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("bookID"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("publishedDate"),
                        rs.getString("image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, rs);
        }

        return books;
    }

    // Fetch books by name using LIKE search
    public ArrayList<Book> getBooksByName(String name) {
        ArrayList<Book> listBook = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE name LIKE ?";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + name + "%"); // Wildcard search for book names
            rs = pst.executeQuery();
            while (rs.next()) {
                listBook.add(new Book(
                        rs.getInt("bookID"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("publishedDate"),
                        rs.getString("image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, rs);
        }

        return listBook;
    }

    // Fetch a book by its ID
    public Book getBookByID(int bookID) {
        Book book = null;
        String sql = "SELECT * FROM books WHERE bookID = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, bookID);
            rs = pst.executeQuery();
            if (rs.next()) {
                book = new Book(
                        rs.getInt("bookID"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("publishedDate"),
                        rs.getString("image")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, rs);
        }

        return book;
    }

    // Update an existing book's details
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET name = ?, author = ?, publisher = ?, publishedDate = ? WHERE bookID = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setString(1, book.getName());
            pst.setString(2, book.getAuthor());
            pst.setString(3, book.getPublisher());
            pst.setString(4, book.getPublishedDate());
            pst.setInt(5, book.getBookID());

            // Execute the update and check if any rows were affected
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, null);
        }
        return false;
    }

    // Insert a new book into the database
    public boolean insertBook(Book book) {
        String sql = "INSERT INTO books (name, author, publisher, publishedDate, image) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setString(1, book.getName());
            pst.setString(2, book.getAuthor());
            pst.setString(3, book.getPublisher());
            pst.setString(4, book.getPublishedDate());
            pst.setString(5, book.getImage());

            // Execute the insert and check if any rows were affected
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, null);
        }
        return false;
    }

    // Delete a book by its ID
    public boolean deleteBook(int bookID) {
        String sql = "DELETE FROM books WHERE bookID = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, bookID);

            // Execute the delete and check if any rows were affected
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, null);
        }
        return false;
    }

    public void addComment(int bookId, String comment) {
        String sql = "INSERT INTO comments (book_id, comment_text) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setString(2, comment);
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getCommentsForBook(int bookId) {
        List<String> comments = new ArrayList<>();
        String sql = "SELECT comment_text FROM comments WHERE book_id = ? ORDER BY created_at DESC";

        try (Connection connection = DataBase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(resultSet.getString("comment_text"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return comments;
    }

    // Utility method for closing resources
    private void closeResources(Connection conn, PreparedStatement pst, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
