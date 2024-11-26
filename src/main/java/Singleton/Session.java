package Singleton;

/**
 * The Session class is a singleton used to manage the current user's session.
 * It stores the userID of the logged-in user and ensures that only one instance
 * of the Session class is created during the application's lifecycle.
 */
public class Session {

    // Static instance to hold the single instance of the class
    private static Session instance;

    // Field to store the user ID of the current session
    private Integer userID;

    /**
     * Private constructor to prevent instantiation from other classes.
     */
    private Session() {}

    /**
     * Returns the single instance of the Session class.
     * If the instance is null, it initializes it.
     *
     * @return the singleton instance of the Session class
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session(); // Lazy initialization
        }
        return instance;
    }

    /**
     * Retrieves the user ID of the current session.
     *
     * @return the user ID of the current session, or null if not set
     */
    public Integer getUserID() {
        return userID;
    }

    /**
     * Sets the user ID for the current session.
     *
     * @param userID the ID of the user to set
     */
    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
