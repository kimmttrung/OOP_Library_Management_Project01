package Entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The {@code User} class represents a user in the library system. It contains information about the
 * user such as their username, password, phone number, and registration date. This class provides constructors,
 * getters, setters, and a method to retrieve the current date for user registration.
 */
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String phoneNumber;
    private String registrationDate;

    /**
     * Default constructor that initializes the user with the current date as the registration date.
     */
    public User() {
        this.registrationDate = getCurrentDate();
    }

    /**
     * Constructs a {@code User} object with the specified username, password, and phone number.
     * The registration date is set to the current date.
     */
    public User(String userName, String password, String phoneNumber) {
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.registrationDate = getCurrentDate();
    }

    /**
     * Constructs a {@code User} object with the specified username, password, phone number, and registration date.
     */
    public User(String userName, String password, String phoneNumber, String registrationDate) {
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
    }

    /**
     * Constructs a {@code User} object with the specified userId, username, and phone number.
     * The password and registration date are not provided and will be left empty or null.
     */
    public User(Integer userId, String userName, String phoneNumber) {
        this.id = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructs a {@code User} object with the specified userId, username, password, phone number,
     * and automatically generated registration date.
     */
    public User(Integer userId, String userName, String password, String phoneNumber) {
        this.id = userId;
        this.password = password;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = getCurrentDate();
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user’s unique ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id The user’s unique ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the user.
     *
     * @param userName The username of the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return The phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber The phone number of the user.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the registration date of the user.
     *
     * @return The registration date of the user in the format "dd/MM/yyyy".
     */
    public String getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Sets the registration date of the user.
     *
     * @param registrationDate The registration date of the user.
     */
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Retrieves the current date in the format "dd/MM/yyyy".
     *
     * @return The current date as a string.
     */
    private String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.now();
        return date.format(formatter);
    }
}
