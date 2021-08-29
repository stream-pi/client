// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.firsttimeuse;

import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import com.stream_pi.util.uihelper.SpaceFiller;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FirstTimeUse extends VBox
{
    private Label headingLabel;
    private Button nextButton;
    private Button previousButton;
    private WelcomePane welcomePane;
    private LicensePane licensePane;
    private FinalConfigPane finalConfigPane;
    private WindowName windowName;
    
    public FirstTimeUse(final ExceptionAndAlertHandler exceptionAndAlertHandler, final ClientListener clientListener) {
        this.getStyleClass().add((Object)"first_time_use_pane");
        this.setSpacing(10.0);
        this.setPadding(new Insets(5.0));
        this.headingLabel = new Label();
        this.headingLabel.getStyleClass().add((Object)"first_time_use_pane_heading_label");
        final StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add((Object)"first_time_use_pane_stackpane");
        VBox.setVgrow((Node)stackPane, Priority.ALWAYS);
        (this.nextButton = new Button("Next")).setOnAction(event -> this.onNextButtonClicked());
        (this.previousButton = new Button("Previous")).setOnAction(event -> this.onPreviousButtonClicked());
        final HBox buttonBar = new HBox(new Node[] { (Node)this.previousButton, (Node)SpaceFiller.horizontal(), (Node)this.nextButton });
        buttonBar.getStyleClass().add((Object)"first_time_use_pane_button_bar");
        buttonBar.setSpacing(10.0);
        this.welcomePane = new WelcomePane();
        this.licensePane = new LicensePane();
        this.finalConfigPane = new FinalConfigPane(exceptionAndAlertHandler, clientListener, this.nextButton);
        stackPane.getChildren().addAll((Object[])new Node[] { (Node)this.welcomePane, (Node)this.licensePane, (Node)this.finalConfigPane });
        this.getChildren().addAll((Object[])new Node[] { (Node)this.headingLabel, (Node)stackPane, (Node)buttonBar });
        this.setWindow(WindowName.WELCOME);
    }
    
    private void onNextButtonClicked() {
        if (this.windowName == WindowName.WELCOME) {
            this.setWindow(WindowName.LICENSE);
        }
        else if (this.windowName == WindowName.LICENSE) {
            this.setWindow(WindowName.FINAL);
        }
    }
    
    private void onPreviousButtonClicked() {
        this.nextButton.setText("Next");
        if (this.windowName == WindowName.FINAL) {
            this.setWindow(WindowName.LICENSE);
        }
        else if (this.windowName == WindowName.LICENSE) {
            this.setWindow(WindowName.WELCOME);
        }
    }
    
    private void setWindow(final WindowName windowName) {
        if (windowName == WindowName.WELCOME) {
            this.windowName = WindowName.WELCOME;
            this.welcomePane.toFront();
            this.welcomePane.setVisible(true);
            this.licensePane.setVisible(false);
            this.finalConfigPane.setVisible(false);
            this.headingLabel.setText("");
            this.nextButton.setText("Next");
            this.nextButton.setOnAction(event -> this.onNextButtonClicked());
            this.previousButton.setVisible(false);
        }
        else if (windowName == WindowName.LICENSE) {
            this.windowName = WindowName.LICENSE;
            this.licensePane.toFront();
            this.welcomePane.setVisible(false);
            this.licensePane.setVisible(true);
            this.finalConfigPane.setVisible(false);
            this.headingLabel.setText("License Agreement");
            this.nextButton.setText("Agree and Continue");
            this.nextButton.setOnAction(event -> this.onNextButtonClicked());
            this.previousButton.setVisible(true);
        }
        else if (windowName == WindowName.FINAL) {
            this.windowName = WindowName.FINAL;
            this.finalConfigPane.toFront();
            this.welcomePane.setVisible(false);
            this.licensePane.setVisible(false);
            this.finalConfigPane.setVisible(true);
            this.headingLabel.setText("Finishing up ...");
            this.finalConfigPane.makeChangesToNextButton();
            this.previousButton.setVisible(true);
        }
    }
}
