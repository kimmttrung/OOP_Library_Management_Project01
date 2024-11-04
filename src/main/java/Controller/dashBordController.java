package Controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.TableView;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

//import static jdk.xml.internal.SecuritySupport.getResource;

public class dashBordController implements Initializable {


    @FXML
    private Button arrow_btn;

    @FXML
    private Button availableBook_btn;

    @FXML
    private ImageView available_image;

    @FXML
    private TableView<?> available_table;

    @FXML
    private Button bars_btn;

    @FXML
    private Button close_btn;

    @FXML
    private TableColumn<?, ?> col_ab_author;

    @FXML
    private TableColumn<?, ?> col_ab_bookType;

    @FXML
    private TableColumn<?, ?> col_ab_booksTitle;

    @FXML
    private TableColumn<?, ?> col_ab_publishedDate;

    @FXML
    private Button find_btn;

    @FXML
    private Button issueBooks_btn;

    @FXML
    private Button minimize;

    @FXML
    private AnchorPane nav_form;

    @FXML
    private Button returnBooks_btn;

    @FXML
    private Button save_btn;

    @FXML
    private Button savedBooks_btn;

    @FXML
    private Button signOut_btn;

    @FXML
    private Button take_btn;

    @FXML
    private AnchorPane mainCenter_form;

    @FXML
    void circle_image(MouseEvent event) {
    }

    private double x = 0;
    private double y = 0;

    public void sliderArrow() {
        // Tạo một đối tượng TranslateTransition để thực hiện hiệu ứng trượt
        TranslateTransition slide = new TranslateTransition();

        // Đặt thời gian thực hiện hiệu ứng trượt là 0.5 giây
        slide.setDuration(Duration.seconds(.5));

        // Đặt đối tượng cần thực hiện hiệu ứng là nav_form
        slide.setNode(nav_form);

        // Thiết lập vị trí đích của hiệu ứng trượt theo trục X
        slide.setToX(-224);

        TranslateTransition slide1 = new TranslateTransition();

        slide.setDuration(Duration.seconds(.5));
        slide.setNode(mainCenter_form);
        mainCenter_form.setMinWidth(509+224);
        slide.setToX(-224);

        slide1.play();

        // Thiết lập hành động sau khi hoàn tất hiệu ứng trượt
        slide.setOnFinished((ActionEvent event) -> {

            // Ẩn nút mũi tên sau khi trượt
            arrow_btn.setVisible(false);

            // Hiển thị nút thanh ngang sau khi trượt
            bars_btn.setVisible(true);

        });

        slide.play();
    }

    public void slideBars() {
        TranslateTransition slide = new TranslateTransition();

        // Đặt thời gian thực hiện hiệu ứng trượt là 0.5 giây
        slide.setDuration(Duration.seconds(.5));

        // Đặt đối tượng cần thực hiện hiệu ứng là nav_form
        slide.setNode(nav_form);

        // Thiết lập vị trí đích của hiệu ứng trượt theo trục X
        slide.setToX(0);

        TranslateTransition slide1 = new TranslateTransition();

        slide.setDuration(Duration.seconds(.5));
        slide.setNode(mainCenter_form);
        slide.setToX(0);

        // Thiết lập hành động sau khi hoàn tất hiệu ứng trượt
        slide.setOnFinished((ActionEvent event) -> {

            // Ẩn nút mũi tên sau khi trượt
            arrow_btn.setVisible(true);

            // Hiển thị nút thanh ngang sau khi trượt
            bars_btn.setVisible(false);

        });

        slide.play();
    }

    @FXML
    public void logout(ActionEvent event){
        try{
            if (event.getSource() == signOut_btn){

                Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashBord.fxml"));

                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((javafx.scene.input.MouseEvent e) -> {
                    x = e.getSceneX();
                    y = e.getSceneY();
                });

                root.setOnMouseDragged((javafx.scene.input.MouseEvent e) -> {
                    stage.setX(e.getScreenX() - x);
                    stage.setY(e.getScreenY() - y);
                });


                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();


                signOut_btn.getScene().getWindow().hide();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void exit(){
        System.exit(0);
    }

    public void minimize(){
        Stage stage = (Stage)minimize.getScene().getWindow();
        stage.setIconified(true);

    }
    @Override
    public void initialize(URL location, ResourceBundle resources){


    }


}
