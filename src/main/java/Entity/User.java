package Entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class User {
    private Integer id;
    private String userName;
    private String password;
    private String phoneNumber;
    private String registrationDate;

    public User() {
        this.registrationDate = getCurrentDate();
    }

//    public User(String userName, String password) {
//        this.userName = userName;
//        this.password = password;
//        this.registrationDate = getCurrentDate();
//    }

    public User(String userName, String password, String phoneNumber) {
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.registrationDate = getCurrentDate();
    }

    public User(String userName, String password, String phoneNumber, String registrationDate) {
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
    }

    public User(Integer userId, String userName, String phoneNumber) {
        this.id = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }

    public User(Integer userId, String userName, String password, String phoneNumber) {
        this.id = userId;
        this.password = password;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = getCurrentDate();
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

    public String getPassword() {return this.password; }

    public void setPassword(String password) {this.password = password; }

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