package com.stream_pi.client.window.firsttimeuse;

import com.stream_pi.client.Main;
import com.stream_pi.client.connection.ClientListener;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.uihelper.SpaceFiller;
import com.stream_pi.util.uihelper.SpaceFiller.FillerType;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FirstTimeUse extends VBox{

    
    public FirstTimeUse(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        Font.loadFont(Main.class.getResourceAsStream("Roboto.ttf"), 13);
        getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

        getStyleClass().add("first_time_use_pane");

        setSpacing(10.0);
        setPadding(new Insets(5));

        headingLabel = new Label();
        headingLabel.getStyleClass().add("first_time_use_pane_heading_label");

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("first_time_use_pane_stackpane");

        VBox.setVgrow(stackPane, Priority.ALWAYS);

        welcomePane = new WelcomePane();
        licensePane = new LicensePane();
        finalConfigPane = new FinalConfigPane(exceptionAndAlertHandler, clientListener);

        stackPane.getChildren().addAll(
            welcomePane,
            licensePane,
            finalConfigPane
        );


        nextButton = new Button("Next");
        nextButton.setOnAction(event-> onNextButtonClicked());

        previousButton = new Button("Previous");
        previousButton.setOnAction(event-> onPreviousButtonClicked());


        HBox buttonBar = new HBox(previousButton, new SpaceFiller(FillerType.HBox), nextButton);
        buttonBar.getStyleClass().add("first_time_use_pane_button_bar");
        buttonBar.setSpacing(10.0);

        getChildren().addAll(headingLabel, stackPane, buttonBar);

        setWindow(WindowName.WELCOME);
    }

    private Label headingLabel;
    private Button nextButton;
    private Button previousButton;
    private WelcomePane welcomePane;
    private LicensePane licensePane;
    private FinalConfigPane finalConfigPane;

    private WindowName windowName;

    private void onNextButtonClicked()
    {
        if(windowName == WindowName.WELCOME)
        {
            setWindow(WindowName.LICENSE);
        }
        else if(windowName == WindowName.LICENSE)
        {
            setWindow(WindowName.FINAL);
        }
    }

    private void onPreviousButtonClicked()
    {
        if(windowName == WindowName.FINAL)
        {
            setWindow(WindowName.LICENSE);
        }
        else if(windowName == WindowName.LICENSE)
        {
            setWindow(WindowName.WELCOME);
        }
    }

    private void setWindow(WindowName windowName)
    {
        if (windowName == WindowName.WELCOME)
        {
            this.windowName = WindowName.WELCOME;
            welcomePane.toFront();
            welcomePane.setVisible(true);
            licensePane.setVisible(false);
            finalConfigPane.setVisible(false);

            headingLabel.setText("");

            nextButton.setDisable(false);
            previousButton.setDisable(true);
        }
        else if (windowName == WindowName.LICENSE)
        {
            this.windowName = WindowName.LICENSE;
            licensePane.toFront();
            welcomePane.setVisible(false);
            licensePane.setVisible(true);
            finalConfigPane.setVisible(false);

            headingLabel.setText("License Agreement");

            nextButton.setDisable(false);
            previousButton.setDisable(false);
        }
        else if (windowName == WindowName.FINAL)
        {
            this.windowName = WindowName.FINAL;
            finalConfigPane.toFront();
            welcomePane.setVisible(false);
            licensePane.setVisible(false);
            finalConfigPane.setVisible(true);
            
            headingLabel.setText("Finishing up ...");

            nextButton.setDisable(true);
            previousButton.setDisable(false);
        }
    }


    
}
