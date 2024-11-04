package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import management.libarymanagement.DataBase;
import java.sql.*;

public class LoginControl {

    @FXML
    private Button login_Btn; // Nút "Đăng nhập" trên giao diện

    @FXML
    private TextField userName; // Trường nhập tên người dùng

    @FXML
    private PasswordField passWord; // Trường nhập mật khẩu

    @FXML
    private Button minimizeBtn; // Nút để thu nhỏ cửa sổ

    @FXML
    private Button exitBtn; // Nút để thoát cửa sổ

    private Connection connect; // Kết nối với cơ sở dữ liệu
    private PreparedStatement pst; // Chuẩn bị câu lệnh SQL
    private Statement statement; // Thực hiện các câu lệnh SQL
    private ResultSet resultSet; // Lưu kết quả truy vấn SQL

    @FXML
    public void login() {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password= ?"; // Câu lệnh SQL để kiểm tra tài khoản

        try {
            // Kết nối tới cơ sở dữ liệu
            connect = DataBase.getConnection();

            // Chuẩn bị câu lệnh SQL với tham số
            pst = connect.prepareStatement(sql);
            pst.setString(1, userName.getText()); // Thiết lập tham số 1 là tên người dùng
            pst.setString(2, passWord.getText()); // Thiết lập tham số 2 là mật khẩu

            // Thực hiện truy vấn và lưu kết quả
            resultSet = pst.executeQuery();

            Alert alert; // Khởi tạo biến cảnh báo

            // Kiểm tra nếu tên người dùng hoặc mật khẩu bị bỏ trống
            if (userName.getText().isEmpty() || passWord.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter all the fields"); // Hiển thị thông báo yêu cầu nhập đủ các trường
                alert.showAndWait();
            } else {
                // Nếu tên người dùng và mật khẩu đúng
                if (resultSet.next()) {
                    // Xác nhận khi đăng nhập thành công
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("admin Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Login");
                    alert.showAndWait();

                    // Tải giao diện mới khi đăng nhập thành công
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/test1.fxml"));
                    Parent root = loader.load();

                    // Đóng cửa sổ đăng nhập hiện tại
                    Stage currentStage = (Stage) login_Btn.getScene().getWindow();
                    currentStage.close();

                    // Mở cửa sổ mới
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                } else {
                    // Thông báo lỗi nếu tên người dùng hoặc mật khẩu sai
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Username or Password is Incorrect");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng các kết nối, câu lệnh và kết quả sau khi hoàn thành
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {
                // Xử lý lỗi nếu có trong khi đóng tài nguyên
            }
        }
    }

    @FXML
    public void minimize() {
        // Thu nhỏ cửa sổ hiện tại
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void exit() {
        // Đóng cửa sổ hiện tại
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
}
