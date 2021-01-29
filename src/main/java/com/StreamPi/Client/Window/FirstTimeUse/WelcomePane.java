package com.StreamPi.Client.Window.FirstTimeUse;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WelcomePane extends VBox{
    public WelcomePane()
    {
        getStyleClass().add("first_time_use_pane_welcome");

        Label welcomeLabel = new Label("Welcome!");
        welcomeLabel.getStyleClass().add("first_time_use_welcome_pane_welcome_label");

        Label nextToContinue = new Label("Click on Next to continue with the setup.");
        nextToContinue.getStyleClass().add("first_time_use_welcome_pane_next_to_continue_label");


        setAlignment(Pos.CENTER);
        setSpacing(5.0);
        getChildren().addAll(welcomeLabel, nextToContinue);

        setVisible(false);
    }
}
