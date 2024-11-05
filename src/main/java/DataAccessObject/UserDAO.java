package DataAccessObject;

import Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import management.libarymanagement.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDAO {

    Connection connect = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM user";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("username"));
                user.setId(rs.getInt("id"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setRegistrationDate(rs.getString("registrationDate"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public User findUser(String username) {
        User user = null;
        String sql = "SELECT * FROM user WHERE username = ?";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setString(1, username);
            rs = pst.executeQuery();
            while (rs.next()) {
                user = new User();
                user.setUserName(rs.getString("username"));
                user.setId(rs.getInt("id"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setRegistrationDate(rs.getString("registrationDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public User findUserbyID(int id) {
        User user = null;
        String sql = "SELECT * FROM user WHERE id = ?";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                user = new User();
                user.setUserName(rs.getString("username"));
                user.setId(rs.getInt("id"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setRegistrationDate(rs.getString("registrationDate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO user(username, phoneNumber, registrationDate) VALUES(?,?,?)";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPhoneNumber());
            pst.setString(3, user.getRegistrationDate());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setInt(1, id);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE user SET phoneNumber = ? WHERE id = ?";

        try {
            connect = DataBase.getConnection();
            pst = connect.prepareStatement(sql);
            pst.setString(1, user.getPhoneNumber());
            pst.setInt(2, user.getId());
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
