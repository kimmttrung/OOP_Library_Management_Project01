package Entity;

/**
 * The {@code Book} class represents a book entity with details such as its ID, name, author,
 * publisher, published date, image, categories, and language. This class provides constructors,
 * getters, setters, and a `toString` method for easy manipulation and representation of book data.
 */
public class Book {
    private int bookID;
    private String name;
    private String author;
    private String publisher;
    private String publishedDate;
    private String image;
    private String categories;
    private String language;

    /**
     * Default constructor for creating an empty {@code Book} object.
     */
    public Book() {
    }

    /**
     * Constructs a {@code Book} object with specified details (excluding bookID, categories, and language).
     */
    public Book(String name, String author, String publisher, String publishedDate, String image) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.image = image;
    }

    /**
     * Constructs a {@code Book} object with specified details including bookID (excluding categories and language).
     */
    public Book(int bookID, String name, String author, String publisher, String publishedDate, String image) {
        this.bookID = bookID;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.image = image;
    }

    /**
     * Constructs a {@code Book} object with specified details including categories (excluding bookID and language).
     */
    public Book(String name, String author, String publisher, String publishedDate, String image, String categories) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.image = image;
        this.categories = categories;
    }

    /**
     * Constructs a {@code Book} object with specified details including categories and language (excluding bookID).
     */
    public Book(String name, String author, String publisher, String publishedDate, String image, String categories, String language) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.image = image;
        this.categories = categories;
        this.language = language;
    }

    // Getters and setters

    /**
     * Gets the unique identifier of the book.
     *
     * @return The book's ID.
     */
    public int getBookID() {
        return bookID;
    }

    /**
     * Sets the unique identifier of the book.
     *
     * @param bookID The book's ID.
     */
    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    /**
     * Gets the name of the book.
     *
     * @return The book's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the book.
     *
     * @param name The book's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the author of the book.
     *
     * @return The book's author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author The book's author.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the publisher of the book.
     *
     * @return The book's publisher.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     *
     * @param publisher The book's publisher.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Gets the published date of the book.
     *
     * @return The book's published date.
     */
    public String getPublishedDate() {
        return publishedDate;
    }

    /**
     * Gets the URL or path to the image of the book.
     *
     * @return The book's image.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the URL or path to the image of the book.
     *
     * @param image The book's image.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the categories of the book.
     *
     * @return The book's categories.
     */
    public String getCategory() {
        return categories;
    }

    /**
     * Sets the categories of the book.
     *
     * @param categories The book's categories.
     */
    public void setCategory(String categories) {
        this.categories = categories;
    }

    /**
     * Gets the language of the book.
     *
     * @return The book's language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language of the book.
     *
     * @param language The book's language.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    // Other methods

    /**
     * Returns a string representation of the book's essential details.
     *
     * @return A string containing the book's name, author, publisher, and published date.
     */
    @Override
    public String toString() {
        return "Book [Name=" + name + ", Author=" + author + ", Publisher=" + publisher + ", PublishedDate=" + publishedDate + "]";
    }

    /**
     * Gets the default ID value (currently hardcoded to 1).
     *
     * @return A hardcoded ID value of 1.
     */
    public int getId() {
        return 1;
    }
}
