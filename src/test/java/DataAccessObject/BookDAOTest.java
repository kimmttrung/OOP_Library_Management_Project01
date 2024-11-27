package DataAccessObject;

import static org.junit.jupiter.api.Assertions.*;

import Database.MockDatabase;
import Entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class BookDAOTest {

    private BookDAO bookDAO;
    private MockDatabase mockDatabase;

    @BeforeEach
    void setUp() {
        // Khởi tạo MockDatabase với dữ liệu mô phỏng
        mockDatabase = new MockDatabase();
        bookDAO = new BookDAO(mockDatabase); // Sử dụng MockDatabase thay vì Database thực tế
    }

    @Test
    void getAllBooks() {
        // Giả lập kết quả trả về từ phương thức getAllBooks
        List<Book> books = bookDAO.getAllBooks();

        assertNotNull(books); // Kiểm tra không trả về null
        assertEquals(1, books.size()); // Kiểm tra số lượng sách là 1
        assertEquals("Test Book", books.get(0).getName()); // Kiểm tra tên sách
    }

    @Test
    void getBooksByName() {
        // Giả lập việc tìm sách theo tên
        List<Book> books = bookDAO.getBooksByName("Test Book");

        assertNotNull(books); // Kiểm tra không trả về null
        assertEquals(1, books.size()); // Kiểm tra số lượng sách là 1
        assertEquals("Test Book", books.get(0).getName()); // Kiểm tra tên sách
    }

    @Test
    void getBookByID() {
        // Giả lập việc lấy sách theo ID
        Book book = bookDAO.getBookByID(1);

        assertNotNull(book); // Kiểm tra không trả về null
        assertEquals(1, book.getId()); // Kiểm tra ID sách là 1
        assertEquals("Test Book", book.getName()); // Kiểm tra tên sách
    }

//    @Test
//    void updateBook() {
//        // Giả lập việc cập nhật thông tin sách
//        boolean result = bookDAO.updateBook(1, "Updated Book");
//
//        assertTrue(result); // Kiểm tra kết quả trả về là true
//    }

    @Test
    void insertBook() {
        // Giả lập việc thêm sách mới
        boolean result = bookDAO.insertBook("New Book");

        assertTrue(result); // Kiểm tra kết quả trả về là true
    }

    @Test
    void deleteBook() {
        // Giả lập việc xóa sách
        boolean result = bookDAO.deleteBook(1);

        assertTrue(result); // Kiểm tra kết quả trả về là true
    }

    @Test
    void getCommentsForBook() {
        // Giả lập việc lấy bình luận cho sách
        List<String> comments = bookDAO.getCommentsForBook(1);

        assertNotNull(comments); // Kiểm tra không trả về null
        assertEquals(1, comments.size()); // Kiểm tra số lượng bình luận là 1
        assertEquals("Great book!", comments.get(0)); // Kiểm tra nội dung bình luận
    }
}
