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
        d.init();
    }


    public static void main(String[] args) 
    { 
        for(String eachArg : args)
        {
            String[] r = eachArg.split("=");
            if(r[0].equals("-DStreamPi.startupRunnerFileName"))
                ClientInfo.getInstance().setRunnerFileName(r[1]);
            else if(r[0].equals("-DStreamPi.showShutDownButton"))
                ClientInfo.getInstance().setShowShutDownButton(r[1].equals("true"));
        }

        launch(args);
    }
}
