package Entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGetId() {
        User user = new User();
        user.setId(1);
        assertEquals(1, user.getId());
    }

    @Test
    void testSetId() {
        User user = new User();
        user.setId(2);
        assertEquals(2, user.getId());
    }

    @Test
    void testGetUserName() {
        User user = new User();
        user.setUserName("john_doe");
        assertEquals("john_doe", user.getUserName());
    }

    @Test
    void testSetUserName() {
        User user = new User();
        user.setUserName("jane_doe");
        assertEquals("jane_doe", user.getUserName());
    }

    @Test
    void testGetPassword() {
        User user = new User();
        user.setPassword("password123");
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetPassword() {
        User user = new User();
        user.setPassword("newpassword456");
        assertEquals("newpassword456", user.getPassword());
    }

    @Test
    void testGetPhoneNumber() {
        User user = new User();
        user.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", user.getPhoneNumber());
    }

    @Test
    void testSetPhoneNumber() {
        User user = new User();
        user.setPhoneNumber("987-654-3210");
        assertEquals("987-654-3210", user.getPhoneNumber());
    }

}
