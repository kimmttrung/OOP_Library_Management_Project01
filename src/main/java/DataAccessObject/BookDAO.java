package DataAccessObject;

import Entity.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import management.libarymanagement.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BookDAO {
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public ObservableList<Book> getAllBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6));
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
        return books;
    }

    public ArrayList<Book> getListBook() {
        ArrayList<Book> listBook = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6));
                listBook.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listBook;
    }

    public ArrayList<Book> getBookByName(String name) {
        ArrayList<Book> listBook = getListBook();
        String sql = "SELECT * FROM books WHERE name LIKE '%" + name + "%'";

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6));
                listBook.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
        return listBook;
    }

    public Book getBookByID(int bookID) {
        Book book = null;
        String sql = "SELECT * FROM books WHERE bookID = ?";

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
        return book;
    }

    public void updateBook(Book book) {
        String sql = "UPDATE books SET name = ?, author = ?, publisher = ?, publishedDate = ? WHERE bookID = ?";

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setString(1, book.getName());
            pst.setString(2, book.getAuthor());
            pst.setString(3, book.getPublisher());
            pst.setString(4, book.getPublishedDate());
            pst.setInt(5, book.getBookID());
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
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

    public boolean insertBook(Book book) {
        String sql = "INSERT INTO books (name, author, publisher, publishedDate, image) VALUES (?, ?, ?, ?, ?)";

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setString(1, book.getName());
            pst.setString(2, book.getAuthor());
            pst.setString(3, book.getPublisher());
            pst.setString(4, book.getPublishedDate());
            pst.setString(5, book.getImage());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
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
        return false;
    }

    public boolean deleteBook(int bookID) {
        String sql = "DELETE FROM books WHERE bookID = " + bookID;

        try {
            conn = DataBase.getConnection();
            pst = conn.prepareStatement(sql);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
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
        return false;
    }
}
