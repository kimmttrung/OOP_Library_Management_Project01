package Database;

import Entity.Book;

import java.util.List;

class MockDatabase {
    public List<Book> getBooks() {
        return List.of(new Book("AME", "Test Book", "Author", "Publisher", "2024-01-01"));
    }

    // Các phương thức giả lập khác cho các hành động CRUD
}
