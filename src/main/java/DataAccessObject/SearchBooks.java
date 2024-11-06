//package DataAccessObject;
//
//import Entity.Book;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import management.libarymanagement.DataBase;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class SearchBooks {
//    Connection conn = null;
//    PreparedStatement pst = null;
//    ResultSet rs = null;
//
//    public ObservableList<Book> getSearchedBooks() {
//        ObservableList<Book> searchBooks = FXCollections.observableArrayList();
//        String sql = "SELECT * FROM searchBooks";
//
//        try {
//            conn = DataBase.getConnection();
//            pst = conn.prepareStatement(sql);
//            rs = pst.executeQuery();
//            while (rs.next()) {
//                Book book = new Book(
//                        rs.getString(1), rs.getString(2),
//                        rs.getString(3), rs.getString(4),
//                        rs.getString(5), rs.getString(6));
//                searchBooks.add(book);
//            }
//        }  catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (conn != null) {
//                    conn.close();
//                }
//                if (pst != null) {
//                    pst.close();
//                }
//                if (rs != null) {
//                    rs.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return searchBooks;
//    }
//
//    public boolean insertBook(Book book) {
//        String sql = "INSERT INTO searchBooks (isbn, name, author, publisher, publishedDate, image) VALUES (?, ?, ?, ?, ?, ?)";
//
//        try {
//            conn = DataBase.getConnection();
//            pst = conn.prepareStatement(sql);
//            pst.setString(1, book.getIsbn());
//            pst.setString(2, book.getName());
//            pst.setString(3, book.getAuthor());
//            pst.setString(4, book.getPublisher());
//            pst.setString(5, book.getPublishedDate());
//            pst.setString(6, book.getImage());
//
//            int affectedRows = pst.executeUpdate();
//            if (affectedRows > 0) {
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (pst != null) {
//                    pst.close();
//                }
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//    public void deleteSearchedBook() {
//        String sql = "DELETE FROM searchBooks";
//        try{
//            conn = DataBase.getConnection();
//            pst = conn.prepareStatement(sql);
//            pst.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
