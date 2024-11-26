package Entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testGetBookID() {
        Book book = new Book();
        book.setBookID(1);
        assertEquals(1, book.getBookID());
    }

    @Test
    void testSetBookID() {
        Book book = new Book();
        book.setBookID(2);
        assertEquals(2, book.getBookID());
    }

    @Test
    void testGetName() {
        Book book = new Book();
        book.setName("Effective Java");
        assertEquals("Effective Java", book.getName());
    }

    @Test
    void testSetName() {
        Book book = new Book();
        book.setName("Clean Code");
        assertEquals("Clean Code", book.getName());
    }

    @Test
    void testGetAuthor() {
        Book book = new Book();
        book.setAuthor("Joshua Bloch");
        assertEquals("Joshua Bloch", book.getAuthor());
    }

    @Test
    void testSetAuthor() {
        Book book = new Book();
        book.setAuthor("Robert C. Martin");
        assertEquals("Robert C. Martin", book.getAuthor());
    }

    @Test
    void testGetPublisher() {
        Book book = new Book();
        book.setPublisher("Addison-Wesley");
        assertEquals("Addison-Wesley", book.getPublisher());
    }

    @Test
    void testSetPublisher() {
        Book book = new Book();
        book.setPublisher("Pearson");
        assertEquals("Pearson", book.getPublisher());
    }

    @Test
    void testGetImage() {
        Book book = new Book();
        book.setImage("image.jpg");
        assertEquals("image.jpg", book.getImage());
    }

    @Test
    void testSetImage() {
        Book book = new Book();
        book.setImage("cover.jpg");
        assertEquals("cover.jpg", book.getImage());
    }

    @Test
    void testGetCategory() {
        Book book = new Book();
        book.setCategory("Programming");
        assertEquals("Programming", book.getCategory());
    }

    @Test
    void testSetCategory() {
        Book book = new Book();
        book.setCategory("Science Fiction");
        assertEquals("Science Fiction", book.getCategory());
    }

}
