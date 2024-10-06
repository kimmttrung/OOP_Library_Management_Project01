 package management.libarymanagement;

 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Parent;
 import javafx.scene.Scene;
 import javafx.stage.Stage;

 import java.io.IOException;

 public class LibaryManagement extends Application {
     @Override
     public void start(Stage stage) throws IOException {
         Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

         Scene scene = new Scene(root);

         stage.setTitle("Libary Management");

         stage.setScene(scene);

         stage.show();
     }

     public static void main(String[] args) {
         launch();
     }
 }