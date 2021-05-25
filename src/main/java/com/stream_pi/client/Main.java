package com.stream_pi.client;

import com.stream_pi.client.controller.Controller;
import com.stream_pi.client.info.ClientInfo;

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
            if(!eachArg.startsWith("-DStream-Pi"))
                continue;

            String[] r = eachArg.split("=");
            String arg = r[0];
            String val = r[1];

            switch (arg) {
                case "-DStream-Pi.startupRunnerFileName":
                    ClientInfo.getInstance().setRunnerFileName(val);
                    break;
                case "-DStream-Pi.showShutDownButton":
                    ClientInfo.getInstance().setShowShutDownButton(val.equals("true"));
                    break;
                case "-DStream-Pi.isXMode":
                    ClientInfo.getInstance().setXMode(val.equals("true"));
                    break;
                case "-DStream-Pi.isShowFullScreenToggleButton":
                    ClientInfo.getInstance().setShowFullScreenToggleButton(val.equals("true"));
                    break;
                case "-DStream-Pi.defaultFullScreenMode":
                    ClientInfo.getInstance().setDefaultFullscreenMode(val.equals("true"));
                    break;
            }
        }

        launch(args);
    }
}
