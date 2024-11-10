package Entity;

public class Book {
    private int bookID;
    private String isbn;
    private String name;
    private String author;
    private String publisher;
    private String publishedDate;
    private String image;

    public Book() {
    }

    public Book(String name, String author,String publisher, String publishedDate, String image) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.image = image;
    }

    public Book(int bookID, String name, String author,String publisher, String publishedDate, String image) {
        this.bookID = bookID;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.image = image;
    }

//    public Book(String isbn, String name, String author,String publisher, String publishedDate, String image) {
//        this.isbn = isbn;
//        this.name = name;
//        this.author = author;
//        this.publisher = publisher;
//        this.publishedDate = publishedDate;
//        this.image = image;
//    }

    public Book(int bookID, String name, String author,String publisher, String publishedDate) {
        this.bookID = bookID;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Book [Name=" + name + ", Author=" + author + ", Publisher=" + publisher + ", PublishedDate=" + publishedDate + "]";
    }
}
