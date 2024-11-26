package Entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BorrowerTest {

    @Test
    void testGetId() {
        Borrower borrower = new Borrower();
        borrower.setId(1);
        assertEquals(1, borrower.getId());
    }

    @Test
    void testSetId() {
        Borrower borrower = new Borrower();
        borrower.setId(2);
        assertEquals(2, borrower.getId());
    }

    @Test
    void testGetUser_id() {
        Borrower borrower = new Borrower();
        borrower.setUser_id(100);
        assertEquals(100, borrower.getUser_id());
    }

    @Test
    void testSetUser_id() {
        Borrower borrower = new Borrower();
        borrower.setUser_id(200);
        assertEquals(200, borrower.getUser_id());
    }

    @Test
    void testGetUserName() {
        Borrower borrower = new Borrower();
        borrower.setUserName("John Doe");
        assertEquals("John Doe", borrower.getUserName());
    }

    @Test
    void testSetUserName() {
        Borrower borrower = new Borrower();
        borrower.setUserName("Jane Doe");
        assertEquals("Jane Doe", borrower.getUserName());
    }

    @Test
    void testGetBookId() {
        Borrower borrower = new Borrower();
        borrower.setBookId(101);
        assertEquals(101, borrower.getBookId());
    }

    @Test
    void testSetBookId() {
        Borrower borrower = new Borrower();
        borrower.setBookId(202);
        assertEquals(202, borrower.getBookId());
    }

    @Test
    void testGetBorrow_from() {
        Borrower borrower = new Borrower();
        borrower.setBorrow_from("2023-01-01");
        assertEquals("2023-01-01", borrower.getBorrow_from());
    }

    @Test
    void testSetBorrow_from() {
        Borrower borrower = new Borrower();
        borrower.setBorrow_from("2023-02-01");
        assertEquals("2023-02-01", borrower.getBorrow_from());
    }

    @Test
    void testGetBorrow_to() {
        Borrower borrower = new Borrower();
        borrower.setBorrow_to("2023-02-28");
        assertEquals("2023-02-28", borrower.getBorrow_to());
    }

    @Test
    void testSetBorrow_to() {
        Borrower borrower = new Borrower();
        borrower.setBorrow_to("2023-03-01");
        assertEquals("2023-03-01", borrower.getBorrow_to());
    }

    @Test
    void testGetStatus() {
        Borrower borrower = new Borrower();
        borrower.setStatus("borrowed");
        assertEquals("borrowed", borrower.getStatus());
    }

    @Test
    void testSetStatus() {
        Borrower borrower = new Borrower();
        borrower.setStatus("returned");
        assertEquals("returned", borrower.getStatus());
    }

    @Test
    void testToString() {
        Borrower borrower = new Borrower(1, "John Doe", 100, 101, "Effective Java", "2023-01-01", "2023-02-01", "borrowed");

        String expected = "Borrower_Table{username=John Doe, book_id=101, borrow_from=2023-01-01, borrow_to=2023-02-01, status=borrowed}";
        assertEquals(expected, borrower.toString());
    }
}
