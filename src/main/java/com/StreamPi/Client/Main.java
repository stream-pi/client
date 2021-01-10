package com.StreamPi.Client;

import com.StreamPi.Client.Controller.Controller;
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


    public static void main(String[] args) {    
        launch(args);
    }
}
