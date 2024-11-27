package Controller;

import API.GoogleBooksAPI;
import Controller.Admin.SearchAPI;
import DataAccessObject.BookDAO;
import Entity.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchAPITest {

    @InjectMocks
    private SearchAPI searchAPI;

    @Mock
    private BookDAO bookDAO;

    @Mock
    private GoogleBooksAPI googleBooksAPI;

    private ObservableList<Book> mockSearchResults;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Tạo danh sách sách giả lập
        mockSearchResults = FXCollections.observableArrayList(
                new Book("Book 1", "Author 1", "Publisher 1", "2024", null, "Category 1"),
                new Book("Book 2", "Author 2", "Publisher 2", "2023", null, "Category 2")
        );

        // Gán danh sách sách giả lập vào searchResults qua getter
    }

    @Test
    void testSearchBooks_ValidQuery() throws Exception {
        // Giả lập dữ liệu trả về từ GoogleBooksAPI
        String mockJsonResponse = """
            {
              "items": [
                {
                  "volumeInfo": {
                    "title": "Book 1",
                    "authors": ["Author 1"],
                    "publisher": "Publisher 1",
                    "publishedDate": "2024",
                    "categories": ["Category 1"],
                    "imageLinks": {"thumbnail": "http://example.com/image1.jpg"}
                  }
                },
                {
                  "volumeInfo": {
                    "title": "Book 2",
                    "authors": ["Author 2"],
                    "publisher": "Publisher 2",
                    "publishedDate": "2023",
                    "categories": ["Category 2"]
                  }
                }
              ]
            }
            """;

        when(googleBooksAPI.fetchBooksData("test query")).thenReturn(mockJsonResponse);

        // Thực thi phương thức tìm kiếm
        Task<Void> searchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                searchAPI.searchBooks("test query");
                return null;
            }
        };
        searchTask.run();

        // Sử dụng getter để kiểm tra kết quả
        assertEquals(2, searchAPI.getSearchResults().size());
        assertEquals("Book 1", searchAPI.getSearchResults().get(0).getName());
        assertEquals("Book 2", searchAPI.getSearchResults().get(1).getName());
    }

}
