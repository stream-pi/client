/*
 * Stream-Pi - Free, Open-Source, Modular, Cross-Platform and Programmable Macro Pad
 * Copyright (C) 2019-2022 Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.stream_pi.client.window.firsttimeuse;

import com.stream_pi.client.Main;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.uihelper.SpaceFiller;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FirstTimeUse extends VBox
{
    public FirstTimeUse(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        getStyleClass().add("first_time_use_pane");

        setSpacing(10.0);

        headingLabel = new Label();
        headingLabel.getStyleClass().add("first_time_use_pane_heading_label");

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("first_time_use_pane_stackpane");

        VBox.setVgrow(stackPane, Priority.ALWAYS);

        nextButton = new Button(I18N.getString("firsttimeuse.FirstTimeUse.next"));
        nextButton.setOnAction(event-> onNextButtonClicked());

        previousButton = new Button(I18N.getString("firsttimeuse.FirstTimeUse.previous"));
        previousButton.setOnAction(event-> onPreviousButtonClicked());

        HBox buttonBar = new HBox(previousButton, SpaceFiller.horizontal(), nextButton);
        buttonBar.getStyleClass().add("first_time_use_pane_button_bar");
        buttonBar.setSpacing(10.0);

        welcomePane = new WelcomePane(exceptionAndAlertHandler, clientListener);
        licensePane = new LicensePane();
        finalConfigPane = new FinalConfigPane(exceptionAndAlertHandler, clientListener, nextButton);

        stackPane.getChildren().addAll(
            welcomePane,
            licensePane,
            finalConfigPane
        );

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
        nextButton.setText("Next");

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

            nextButton.setText(I18N.getString("firsttimeuse.FirstTimeUse.next"));
            nextButton.setOnAction(event-> onNextButtonClicked());
            previousButton.setVisible(false);
        }
        else if (windowName == WindowName.LICENSE)
        {
            this.windowName = WindowName.LICENSE;
            licensePane.toFront();
            welcomePane.setVisible(false);
            licensePane.setVisible(true);
            finalConfigPane.setVisible(false);

            headingLabel.setText(I18N.getString("firsttimeuse.FirstTimeUse.licenseAgreement"));

            nextButton.setText(I18N.getString("firsttimeuse.FirstTimeUse.agreeAndContinue"));
            nextButton.setOnAction(event-> onNextButtonClicked());
            previousButton.setVisible(true);
        }
        else if (windowName == WindowName.FINAL)
        {
            this.windowName = WindowName.FINAL;
            finalConfigPane.toFront();
            welcomePane.setVisible(false);
            licensePane.setVisible(false);
            finalConfigPane.setVisible(true);

            headingLabel.setText(I18N.getString("firsttimeuse.FirstTimeUse.finishingUp"));

            finalConfigPane.makeChangesToNextButton();
            previousButton.setVisible(true);
        }
    }


    
}
