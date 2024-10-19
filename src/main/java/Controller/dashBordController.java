package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.TableView;

import javax.swing.text.html.ImageView;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

//import static jdk.xml.internal.SecuritySupport.getResource;

public class dashBordController implements Initializable {


    @FXML
    private ImageView availableBook_btn;

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
    void circle_image(MouseEvent event) {
    }

    private double x = 0;
    private double y = 0;

    @FXML
    public void logout(ActionEvent event){
        try{
            if (event.getSource() == signOut_btn){

                Parent root = FXMLLoader.load(getClass().getResource("dashBord.fxml"));

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
