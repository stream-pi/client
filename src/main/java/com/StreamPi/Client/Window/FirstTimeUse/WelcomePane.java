package com.StreamPi.Client.Window.FirstTimeUse;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WelcomePane extends VBox{
    public WelcomePane()
    {
        getStyleClass().add("first_time_use_pane_welcome");

        Label label = new Label("Welcome to StreamPi!\nClick on 'Next' to continue with the setup.");

        getChildren().add(label);
    
        setVisible(false);
    }
}
