// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window.firsttimeuse;

import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WelcomePane extends VBox
{
    public WelcomePane() {
        this.getStyleClass().add((Object)"first_time_use_pane_welcome");
        final Label welcomeLabel = new Label("Welcome!");
        welcomeLabel.setWrapText(true);
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.getStyleClass().add((Object)"first_time_use_welcome_pane_welcome_label");
        final Label nextToContinue = new Label("Please click \"Next\" to start the Setup process");
        nextToContinue.setWrapText(true);
        nextToContinue.setAlignment(Pos.CENTER);
        nextToContinue.getStyleClass().add((Object)"first_time_use_welcome_pane_next_to_continue_label");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5.0);
        this.getChildren().addAll((Object[])new Node[] { (Node)welcomeLabel, (Node)nextToContinue });
        this.setVisible(false);
    }
}
