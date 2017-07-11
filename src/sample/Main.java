package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static String deviceName;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Video");
        primaryStage.setScene(new Scene(root, 600, 200));
        primaryStage.show();
    }


    public static void main(String[] args) {
        deviceName = args.length > 0 ? args[0] : "";
        launch(args);
    }
}
