package Entity;

import Tools.DateStringFormatter;

/**
 * The {@code Borrower} class represents a borrower in the library system. It contains information
 * about the borrower, such as their username, the book they borrowed, the borrow period, and their current
 * borrowing status. This class provides constructors, getters, setters, and a `toString` method
 * to represent the borrower details.
 */
public class Borrower {
    private int id;
    private String username;
    private int user_id;
    private int bookId;
    private String bookName;
    private String borrow_from;
    private String borrow_to;
    private String status;
    private final DateStringFormatter dateFormatter = new DateStringFormatter("yyyy-MM-dd");

    /**
     * Default constructor for creating an empty {@code Borrower} object.
     */
    public Borrower() {
    }

    /**
     * Constructs a {@code Borrower} object with the specified id, username, bookId, and status.
     * This constructor assumes that the borrow period is not set yet.
     */
    public Borrower(int id, String username, int bookId, String status) {
        this.id = id;
        this.username = username;
        this.bookId = bookId;
        this.borrow_from = null;
        this.borrow_to = null;
        this.status = status;
    }

    /**
     * Constructs a {@code Borrower} object with the specified id, user_id, bookId, bookName,
     * borrow_from, borrow_to, and status.
     */
    public Borrower(int id, int user_id, int bookId, String bookName, String borrow_from, String borrow_to, String status) {
        this.id = id;
        this.user_id = user_id;
        this.bookId = bookId;
        this.bookName = bookName;
        this.borrow_from = borrow_from;
        this.borrow_to = borrow_to;
        this.status = status;
    }

    /**
     * Constructs a {@code Borrower} object with the specified id, username, user_id, bookId, bookName,
     * borrow_from, borrow_to, and status.
     */
    public Borrower(int id, String username, int user_id, int bookId, String bookName, String borrow_from, String borrow_to, String status) {
        this.id = id;
        this.user_id = user_id;
        this.username = username;
        this.bookId = bookId;
        this.bookName = bookName;
        this.borrow_from = borrow_from;
        this.borrow_to = borrow_to;
        this.status = status;
    }

    // Getters and Setters

    /**
     * Gets the unique identifier of the borrower.
     *
     * @return The borrower's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the borrower.
     *
     * @param id The borrower's ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user's ID.
     */
    public int getUser_id() {
        return user_id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param user_id The user's ID.
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets the username of the borrower.
     *
     * @return The username of the borrower.
     */
    public String getUserName() {
        return username;
    }

    /**
     * Sets the username of the borrower.
     *
     * @param username The username of the borrower.
     */
    public void setUserName(String username) {
        this.username = username;
    }

    /**
     * Gets the ID of the book being borrowed.
     *
     * @return The book's ID.
     */
    public int getBookId() {
        return bookId;
    }

    /**
     * Sets the ID of the book being borrowed.
     *
     * @param book_id The book's ID.
     */
    public void setBookId(int book_id) {
        this.bookId = book_id;
    }

    /**
     * Gets the name of the book being borrowed.
     *
     * @return The book's name.
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Gets the start date of the borrow period.
     *
     * @return The borrow start date.
     */
    public String getBorrow_from() {
        return borrow_from;
    }

    /**
     * Sets the start date of the borrow period.
     *
     * @param borrow_from The borrow start date.
     */
    public void setBorrow_from(String borrow_from) {
        this.borrow_from = borrow_from;
    }

    /**
     * Gets the end date of the borrow period.
     *
     * @return The borrow end date.
     */
    public String getBorrow_to() {
        return borrow_to;
    }

    /**
     * Sets the end date of the borrow period.
     *
     * @param borrow_to The borrow end date.
     */
    public void setBorrow_to(String borrow_to) {
        this.borrow_to = borrow_to;
    }

    /**
     * Gets the current status of the borrower (e.g., "borrowed", "returned").
     *
     * @return The borrower's status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the borrower.
     *
     * @param status The borrower's status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    // Other methods

    /**
     * Returns a string representation of the borrower's essential details.
     *
     * @return A string containing the borrower's username, book ID, borrow dates, and status.
     */
    @Override
    public String toString() {
        return "Borrower_Table{" + "username=" + username + ", book_id=" + bookId + ", borrow_from=" + borrow_from + ", borrow_to=" + borrow_to + ", status=" + status + '}';
    }
}
