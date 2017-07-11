package sample;

import boofcv.util.VisionTool;
import boofcv.util.ImageProcessor;
import boofcv.util.VideoApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.bytedeco.javacv.FrameGrabber;

import java.io.File;

public class Controller {


    @FXML private Pane centralPane;
    @FXML private TextField borderDivide;
    @FXML private Label info1;
    @FXML private Label info2;
    @FXML private Label info3;
    @FXML Button cam;
    @FXML Button video;

    private File file;

    @FXML
    protected void openAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(null);

        if (file != null){
            info1.setText("");
            info2.setText("Файл открыт, приступайте к обработке");
            info3.setText("");
            cam.setDisable(true);
        }
    }

    @FXML protected void camAction (ActionEvent event){
        video.setDisable(true);

        String deviceName = Main.deviceName;
        ImageProcessor imageProcessor = new VisionTool();
        (new VideoApp(imageProcessor, deviceName, 640, 480)).run();
    }

    @FXML protected void videoAction (ActionEvent event) throws FrameGrabber.Exception {
        if (file == null){
            info1.setText("");
            info2.setText("Сначала откройте файл");
            info3.setText("");
        }else {
            ImageProcessor imageProcessor = new VisionTool();
            (new VideoApp(imageProcessor, 640, 480)).runVideo(file);
        }
    }

}
