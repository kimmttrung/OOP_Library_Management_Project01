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

/**
 * Data Access Object (DAO) class for managing books in the database.
 * Provides methods for CRUD operations and additional functionalities such as searching and managing comments.
 */
public class BookDAO {

    /**
     * Fetches all books from the database.
     *
     * @return an ObservableList of all books.
     */
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

    /**
     * Fetches books from the database using a partial name match.
     *
     * @param name the partial or full name of the book(s) to search for.
     * @return a list of books matching the search criteria.
     */
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

    /**
     * Fetches a book by its ID.
     *
     * @param bookID the ID of the book to fetch.
     * @return the Book object if found, or null if no such book exists.
     */
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

    /**
     * Updates the details of an existing book in the database.
     *
     * @param book the Book object containing updated information.
     * @return true if the update was successful, false otherwise.
     */
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

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, null);
        }
        return false;
    }

    /**
     * Inserts a new book into the database.
     *
     * @param book the Book object to be inserted.
     * @return true if the insertion was successful, false otherwise.
     */
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

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, null);
        }
        return false;
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookID the ID of the book to be deleted.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteBook(int bookID) {
        String sql = "DELETE FROM books WHERE bookID = ?";
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, bookID);

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pst, null);
        }
        return false;
    }

    /**
     * Adds a comment for a specific book.
     *
     * @param bookId  the ID of the book to which the comment belongs.
     * @param comment the text of the comment.
     */
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

    /**
     * Fetches all comments for a specific book, ordered by creation date.
     *
     * @param bookId the ID of the book.
     * @return a list of comments for the book.
     */
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

    /**
     * Closes the database resources.
     *
     * @param conn the Connection to be closed.
     * @param pst  the PreparedStatement to be closed.
     * @param rs   the ResultSet to be closed.
     */
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
