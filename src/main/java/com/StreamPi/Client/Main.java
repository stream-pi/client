package com.StreamPi.Client;

import com.StreamPi.Client.Controller.Controller;
import com.StreamPi.Client.Info.ClientInfo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage stage)
    {
        Controller d = new Controller();                                                        //Starts new dash instance

        Scene s = new Scene(d);                                                     //Starts new scene instance from dash
        stage.setScene(s);                                                          //Init Scene
        d.init();
        stage.show();
    }


    public static void main(String[] args) 
    { 
        for(String eachArg : args)
        {
            String[] r = eachArg.split("=");
            if(r[0].equals("-DStreamPi.startupRunnerFileName"))
                ClientInfo.getInstance().setRunnerFileName(r[1]);
            else if(r[0].equals("-DStreamPi.isFrameBufferMode"))
                ClientInfo.getInstance().setFrameBufferMode(r[1].equals("true"));
        }
        
         
        launch(args);
    }
}
