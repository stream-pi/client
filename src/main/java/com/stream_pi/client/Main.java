package com.stream_pi.client;

import com.stream_pi.client.controller.Controller;
import com.stream_pi.client.info.ClientInfo;

import com.stream_pi.client.info.StartupFlags;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage stage)
    {
        Controller d = new Controller();
        Scene s = new Scene(d);
        stage.setScene(s);
        d.setHostServices(getHostServices());
        d.init();
    }


    public static void main(String[] args) 
    {
        for(String eachArg : args)
        {
            if(!eachArg.startsWith("Stream-Pi"))
                continue;

            String[] r = eachArg.split("=");
            String arg = r[0];
            String val = r[1];

            switch (arg) {
                case "Stream-Pi.startupRunnerFileName":
                    StartupFlags.RUNNER_FILE_NAME = val;
                    break;
                case "Stream-Pi.showShutDownButton":
                    StartupFlags.IS_SHOW_SHUT_DOWN_BUTTON = val.equals("true");
                    break;
                case "Stream-Pi.isXMode":
                    StartupFlags.IS_X_MODE = val.equals("true");
                    break;
                case "Stream-Pi.isShowFullScreenToggleButton":
                    StartupFlags.SHOW_FULLSCREEN_TOGGLE_BUTTON = val.equals("true");
                    break;
                case "Stream-Pi.defaultFullScreenMode":
                    StartupFlags.DEFAULT_FULLSCREEN_MODE = val.equals("true");
                    break;
                case "Stream-Pi.enableScreenSaverFeature":
                    StartupFlags.SCREEN_SAVER_FEATURE = val.equals("true");
                    break;
                case "Stream-Pi.appendPathBeforeRunnerFileToOvercomeJPackageLimitation":
                    StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION = val.equals("true");
                    break;
            }
        }

        launch(args);
    }
}
