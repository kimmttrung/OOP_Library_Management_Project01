package Entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Borrower {
    private int id;
    private String username;
    private int bookid;
    private String borrow_from;
    private String borrow_to;
    private String status;
    private boolean late;

    public Borrower() {
    }

    public Borrower(int id, String username, int bookid, String borrow_from, String borrow_to, String status) {
        this.id = id;
        this.username = username;
        this.bookid = bookid;
        this.borrow_from = borrow_from;
        this.borrow_to = borrow_to;
        this.status = status;
        this.late= false;
    }

    public Borrower(int id, String username, int bookid, String status){
        this.id = id;
        this.username = username;
        this.bookid = bookid;
        this.borrow_from = null;
        this.borrow_to = null;
        this.status = status;
        this.late= false;
    }

    public boolean isLate() {
        return late;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getBorrow_from() {
        this.borrow_from = getCurrentDate();
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

    private String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.now();
        return date.format(formatter);
    }

    @Override
    public String toString() {
        return "Borrower_Table{" + "username=" + username + ", bookid=" + bookid + ", borrow_from=" + borrow_from + ", borrow_to=" + borrow_to + ", status=" + status + '}';
    }


}