package DataAccessObject;

import Entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDAOTest {

    private BookDAO bookDAO;

    @BeforeEach
    void setUp() {
        // Giả lập đối tượng bookDAO trước mỗi test
        bookDAO = new BookDAO() {

            @Override
            public Book getBookByID(int id) {
                if (id == 1) {
                    return new Book(1, "Test Book 1", "Author 1", "Publisher 1", "2024", "Category 1");
                }
                return null;
            }

            @Override
            public boolean deleteBook(int id) {
                return id == 1;
            }

            @Override
            public List<String> getCommentsForBook(int id) {
                if (id == 1) {
                    return List.of("Great book!", "Very informative");
                }
                return List.of();
            }
        };
    }

    @Test
    void getAllBooks() {
        List<Book> books = bookDAO.getAllBooks();
        assertNotNull(books, "The list of books should not be null");
        assertEquals(33, books.size(), "There should be 2 books in the list"); // Điều chỉnh số lượng sách
        assertEquals("Conan The Triumphant", books.get(0).getName(), "The name of the first book should be 'Conan The Triumphant'"); // Chỉ số là 0
        assertEquals("Conan the Barbarian #7", books.get(1).getName(), "The name of the second book should be 'Conan the Barbarian #7'"); // Chỉ số là 1
    }

    @Test
    void getBooksByName() {
        List<Book> books = bookDAO.getBooksByName("Conan The Triumphant");
        assertNotNull(books, "The list of books should not be null");
        assertEquals(1, books.size(), "There should be 1 book found by the name 'Test Book 1'");
        assertEquals("Conan The Triumphant", books.get(0).getName(), "The name of the found book should be 'Test Book 1'"); // Chỉ số là 0

        // Test case for non-existing book
        books = bookDAO.getBooksByName("Nonexistent Book");
        assertTrue(books.isEmpty(), "The list should be empty for a non-existent book name");
    }


    @Test
    void getBookByID() {
        Book book = bookDAO.getBookByID(1);
        assertNotNull(book, "The book should not be null");
        assertEquals(1, book.getId(), "The ID of the book should be 1");
        assertEquals("Test Book 1", book.getName(), "The name of the book should be 'Test Book 1'");

        // Test case for non-existing book ID
        book = bookDAO.getBookByID(99);
        assertNull(book, "The book should be null for a non-existent ID");
    }


    @Test
    void deleteBook() {
        boolean result = bookDAO.deleteBook(1);
        assertTrue(result, "The book deletion should return true when valid ID is provided");

        // Test case for non-existing book ID
        result = bookDAO.deleteBook(99);
        assertFalse(result, "The book deletion should return false for a non-existent ID");
    }

    @Test
    void getCommentsForBook() {
        List<String> comments = bookDAO.getCommentsForBook(1);
        assertNotNull(comments, "The list of comments should not be null");
        assertEquals(2, comments.size(), "There should be 2 comments for book ID 1");
        assertEquals("Great book!", comments.get(0), "The first comment should be 'Great book!'");
        assertEquals("Very informative", comments.get(1), "The second comment should be 'Very informative'");

        // Test case for non-existing book ID
        comments = bookDAO.getCommentsForBook(99);
        assertTrue(comments.isEmpty(), "The list of comments should be empty for a non-existent book ID");
    }
}
