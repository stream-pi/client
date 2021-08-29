// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client;

import com.stream_pi.client.info.StartupFlags;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.stream_pi.client.controller.Controller;
import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application
{
    public void start(final Stage stage) {
        final Controller d = new Controller();
        final Scene s = new Scene((Parent)d);
        stage.setScene(s);
        d.setHostServices(this.getHostServices());
        d.init();
    }
    
    public static void main(final String[] args) {
        for (final String eachArg : args) {
            if (eachArg.startsWith("Stream-Pi")) {
                final String[] r = eachArg.split("=");
                final String arg = r[0];
                final String val = r[1];
                final String s = arg;
                switch (s) {
                    case "Stream-Pi.startupRunnerFileName": {
                        StartupFlags.RUNNER_FILE_NAME = val;
                        break;
                    }
                    case "Stream-Pi.showShutDownButton": {
                        StartupFlags.IS_SHOW_SHUT_DOWN_BUTTON = val.equals("true");
                        break;
                    }
                    case "Stream-Pi.isXMode": {
                        StartupFlags.IS_X_MODE = val.equals("true");
                        break;
                    }
                    case "Stream-Pi.isShowFullScreenToggleButton": {
                        StartupFlags.SHOW_FULLSCREEN_TOGGLE_BUTTON = val.equals("true");
                        break;
                    }
                    case "Stream-Pi.defaultFullScreenMode": {
                        StartupFlags.DEFAULT_FULLSCREEN_MODE = val.equals("true");
                        break;
                    }
                    case "Stream-Pi.enableScreenSaverFeature": {
                        StartupFlags.SCREEN_SAVER_FEATURE = val.equals("true");
                        break;
                    }
                    case "Stream-Pi.appendPathBeforeRunnerFileToOvercomeJPackageLimitation": {
                        StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION = val.equals("true");
                        break;
                    }
                }
            }
        }
        launch(args);
    }
}
