package Entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class User {
    private int id;
    private String userName;
    private String phoneNumber;
    private String registrationDate;
    private String avt;

    public User() {
        this.registrationDate = getCurrentDate();
    }

    public User(String userName, String phoneNumber, String avt) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = getCurrentDate();
        this.avt = avt;
    }

    public User(String userName, String phoneNumber, String registrationDate, String avt) {
        this.id = id;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.avt = avt;
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

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }
}
