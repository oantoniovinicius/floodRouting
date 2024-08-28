import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import control.mainControl;

public class Principal extends Application{

    public static void main(String[] args) {
      launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("view/fxmlMain.fxml"));
      Parent fxmlStart = loader.load();
      mainControl controller = loader.getController();
      controller.setPane((Pane) fxmlStart);
    

    //stagePrimary.getIcons().add(new Image("./assets/icon.png"));
      primaryStage.setTitle("Flood Routing");
      primaryStage.setScene(new Scene(fxmlStart));
      primaryStage.setResizable(false);
      primaryStage.show();
    }
    
}