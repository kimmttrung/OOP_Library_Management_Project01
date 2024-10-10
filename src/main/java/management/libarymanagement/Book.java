package management.libarymanagement;

import java.sql.Date;

public class Book {
    private final String title;
    private final String author;
    private final Date date;
    private final String image;

    public Book(String title, String author, Date date, String image) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

}
