package StreamPiClient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        primaryStage.setTitle("StreamPi");
        primaryStage.setScene(new Scene(root, Integer.parseInt(config.get("width")), Integer.parseInt(config.get("height"))));
        primaryStage.show();
    }

    public static HashMap<String,String> config;

    public static void main(String[] args) {
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true" );
        config = new HashMap<>();
        String[] configArray = io.readFileArranged("config","::");
        config.put("width",configArray[0]);
        config.put("height",configArray[1]);
        config.put("bg_colour",configArray[2]);
        config.put("server_ip",configArray[3]);
        config.put("server_port",configArray[4]);
        config.put("device_nick_name",configArray[5]);
        config.put("animations_mode",configArray[6]);
        config.put("debug_mode",configArray[7]);
        config.put("each_action_size",configArray[8]);
        config.put("each_action_padding",configArray[9]);
        launch(args);
    }
}
