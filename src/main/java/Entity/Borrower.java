package Entity;

import Tools.DateStringFormatter;

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

    public Borrower() {
    }

    public Borrower(int id, String username, int bookId, String status){
        this.id = id;
        this.username = username;
        this.bookId = bookId;
        this.borrow_from = null;
        this.borrow_to = null;
        this.status = status;
    }

    public Borrower(int id, int user_id, int bookId, String bookName, String borrow_from, String borrow_to, String status) {
        this.id = id;
        this.user_id = user_id;
        this.bookId = bookId;
        this.bookName = bookName;
        this.borrow_from = borrow_from;
        this.borrow_to = borrow_to;
        this.status = status;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int book_id) {
        this.bookId = book_id;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBorrow_from() {
        return borrow_from;
    }

    public void setBorrow_from(String borrow_from) {
        this.borrow_from = borrow_from;
    }

    public String getBorrow_to() {
        return borrow_to;
    }

    public void setBorrow_to(String borrow_to) {
        this.borrow_to = borrow_to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Borrower_Table{" + "username=" + username + ", book_id=" + bookId + ", borrow_from=" + borrow_from + ", borrow_to=" + borrow_to + ", status=" + status + '}';
    }
}