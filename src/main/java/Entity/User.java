package Entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class User {
    private int id;
    private String userName;
    private String phoneNumber;
    private String registrationDate;

    public User() {
        this.registrationDate = getCurrentDate();
    }

    public User(String userName, String phoneNumber) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = getCurrentDate();
    }

    public User(String userName, String phoneNumber, String registrationDate) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegistrationDate() {
        this.registrationDate = getCurrentDate();
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    private String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.now();
        return date.format(formatter);
    }
}