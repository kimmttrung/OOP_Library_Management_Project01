package DataAccessObject;

import Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Database.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) để thực hiện các thao tác với cơ sở dữ liệu liên quan đến đối tượng User.
 * Cung cấp các phương thức để lấy, thêm, cập nhật, và xóa các bản ghi người dùng từ cơ sở dữ liệu.
 */
public class UserDAO {
    private final Connection connect = DataBase.getInstance().getConnection(); // Kết nối tới cơ sở dữ liệu

    /**
     * Lấy tất cả người dùng từ cơ sở dữ liệu.
     *
     * @return một ObservableList chứa tất cả các đối tượng User từ cơ sở dữ liệu.
     */
    public ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList(); // Danh sách chứa tất cả người dùng
        String sql = "SELECT * FROM users"; // Câu lệnh SQL để lấy tất cả người dùng

        try (PreparedStatement pst = connect.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            // Duyệt qua tất cả kết quả trả về từ câu lệnh SQL
            while (rs.next()) {
                User user = new User();
                // Lấy thông tin từ ResultSet và gán vào đối tượng User
                user.setUserName(rs.getString("username"));
                user.setId(rs.getInt("id"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setRegistrationDate(rs.getString("registrationDate"));
                users.add(user); // Thêm người dùng vào danh sách
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ khi có lỗi xảy ra
        }
        return users; // Trả về danh sách người dùng
    }

    /**
     * Tìm một người dùng theo tên người dùng.
     *
     * @param username tên người dùng cần tìm.
     * @return đối tượng User nếu tìm thấy, ngược lại trả về null.
     */
    public User findUser(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?"; // Câu lệnh SQL để tìm người dùng theo tên

        try (PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setString(1, username); // Đặt giá trị cho tham số trong câu lệnh SQL
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs); // Ánh xạ kết quả từ ResultSet thành đối tượng User
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có lỗi
        }
        return user; // Trả về đối tượng User nếu tìm thấy hoặc null nếu không tìm thấy
    }

    /**
     * Tìm một người dùng theo ID.
     *
     * @param id ID của người dùng cần tìm.
     * @return đối tượng User nếu tìm thấy, ngược lại trả về null.
     */
    public User findUserById(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?"; // Câu lệnh SQL để tìm người dùng theo ID

        try (PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setInt(1, id); // Đặt giá trị cho tham số ID trong câu lệnh SQL
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs); // Ánh xạ kết quả từ ResultSet thành đối tượng User
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có lỗi
        }
        return user; // Trả về đối tượng User nếu tìm thấy hoặc null nếu không tìm thấy
    }

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu.
     *
     * @param user đối tượng User cần thêm.
     * @return true nếu người dùng được thêm thành công, false nếu không thành công.
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, phoneNumber, registrationDate) VALUES(?,?,?,?)"; // Câu lệnh SQL để thêm người dùng mới
        try (PreparedStatement pst = connect.prepareStatement(sql)) {
            // Đặt giá trị cho các tham số trong câu lệnh SQL
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getPhoneNumber());
            pst.setString(4, user.getRegistrationDate());

            int affectedRows = pst.executeUpdate(); // Thực thi câu lệnh SQL
            return affectedRows > 0; // Nếu có ít nhất 1 dòng bị ảnh hưởng, trả về true
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có lỗi
        }
        return false; // Trả về false nếu không thêm được người dùng
    }

    /**
     * Xóa người dùng khỏi cơ sở dữ liệu theo ID.
     *
     * @param id ID của người dùng cần xóa.
     * @return true nếu người dùng được xóa thành công, false nếu không thành công.
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?"; // Câu lệnh SQL để xóa người dùng theo ID
        try (PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setInt(1, id); // Đặt giá trị cho tham số ID trong câu lệnh SQL

            int affectedRows = pst.executeUpdate(); // Thực thi câu lệnh SQL
            return affectedRows > 0; // Nếu có ít nhất 1 dòng bị ảnh hưởng, trả về true
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có lỗi
        }
        return false; // Trả về false nếu không xóa được người dùng
    }

    /**
     * Cập nhật thông tin của một người dùng trong cơ sở dữ liệu.
     *
     * @param user đối tượng User chứa thông tin cập nhật.
     * @return true nếu thông tin người dùng được cập nhật thành công, false nếu không thành công.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, phoneNumber = ? WHERE id = ?"; // Câu lệnh SQL để cập nhật thông tin người dùng
        try (PreparedStatement pst = connect.prepareStatement(sql)) {
            // Đặt giá trị cho các tham số trong câu lệnh SQL
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getPhoneNumber());
            pst.setInt(4, user.getId());

            return pst.executeUpdate() > 0; // Nếu có ít nhất 1 dòng bị ảnh hưởng, trả về true
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có lỗi
        }
        return false; // Trả về false nếu không cập nhật được thông tin người dùng
    }

    /**
     * Phương thức hỗ trợ để ánh xạ kết quả từ ResultSet thành đối tượng User.
     *
     * @param rs đối tượng ResultSet chứa dữ liệu người dùng.
     * @return đối tượng User đã được ánh xạ từ ResultSet.
     * @throws SQLException nếu xảy ra lỗi khi ánh xạ dữ liệu từ ResultSet.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserName(rs.getString("username"));
        user.setId(rs.getInt("id"));
        user.setPassword(rs.getString("password"));
        user.setPhoneNumber(rs.getString("phoneNumber"));
        user.setRegistrationDate(rs.getString("registrationDate"));
        return user; // Trả về đối tượng User đã được ánh xạ
    }
}
