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

import com.gluonhq.attach.orientation.OrientationService;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.rootchecker.RootChecker;
import com.stream_pi.util.uihelper.ActionGridRowsAndColsCalculator;
import com.stream_pi.util.uihelper.HBoxInputBox;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class FinalConfigPane extends ScrollPane
{
    private TextField clientNameTextField;
    private TextField serverIPHostNameTextField;
    private TextField serverPortTextField;
    private Button nextButton;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ClientListener clientListener;

    public FinalConfigPane(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener,
                           Button nextButton)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientListener = clientListener;
        this.nextButton = nextButton;

        getStyleClass().add("first_time_use_pane_final_config");

        Label label = new Label(I18N.getString("firsttimeuse.FinalConfigPane.subHeading"));
        label.setWrapText(true);
        VBox.setVgrow(label, Priority.ALWAYS);
        label.getStyleClass().add("first_time_use_pane_final_config_label");

        clientNameTextField = new TextField();
        serverIPHostNameTextField = new TextField();
        serverPortTextField = new TextField();

        Label securityWarningLabel = new Label(I18N.getString("firsttimeuse.FinalConfigPane.securityWarning"));
        securityWarningLabel.setWrapText(true);
        securityWarningLabel.prefWidthProperty().bind(widthProperty());
        securityWarningLabel.getStyleClass().add("first_time_use_pane_final_config_security_warning_label");



        HBoxInputBox clientNameInputBox = new HBoxInputBox(I18N.getString("name"), clientNameTextField, 150);
        HBoxInputBox serverIPHostNameInputBox = new HBoxInputBox(I18N.getString("serverHostNameOrIP"), serverIPHostNameTextField, 150);
        HBoxInputBox serverIPPortInputBox = new HBoxInputBox(I18N.getString("serverPort"), serverPortTextField, 150);

        VBox vBox = new VBox(label, clientNameInputBox, serverIPHostNameInputBox, serverIPPortInputBox, securityWarningLabel);
        vBox.getStyleClass().add("first_time_use_pane_final_config_vbox");
        vBox.setSpacing(10.0);

        setContent(vBox);
        setFitToWidth(true);

        setVisible(false);

        try
        {
            clientNameTextField.setText(InetAddress.getLocalHost().getHostName());
        }
        catch (UnknownHostException e)
        {
            Logger.getLogger(getClass().getName()).warning("Hostname lookup failed! Not setting any placeholder for clientNameTextField.");
        }
    }

    public void makeChangesToNextButton()
    {
        nextButton.setText(I18N.getString("firsttimeuse.FinalConfigPane.confirm"));
        nextButton.setOnAction(actionEvent -> new Thread(new Task<Void>() {
            @Override
            protected Void call()
            {
                onConfirmButtonClicked();
                return null;
            }
        }).start());
    }

    private void onConfirmButtonClicked()
    {
        Platform.runLater(()->nextButton.setDisable(true));

        StringBuilder errors = new StringBuilder();

        if(clientNameTextField.getText().isBlank())
        {
            errors.append("* ").append(I18N.getString("nameCannotBeBlank")).append("\n");
        }

        if(serverIPHostNameTextField.getText().isBlank())
        {
            errors.append("* ").append(I18N.getString("serverHostNameOrIPCannotBeBlank")).append("\n");
        }

        int port = -1;
        try
        {
            port = Integer.parseInt(serverPortTextField.getText());

            if(port < 1024 && !RootChecker.isRoot(ClientInfo.getInstance().getPlatform()))
            {
                errors.append("* ").append(I18N.getString("serverPortMustBeGreaterThan1024")).append("\n");
            }
            else if(port > 65535)
            {
                errors.append("* ").append(I18N.getString("serverPortMustBeLesserThan65535")).append("\n");
            }
        }
        catch (NumberFormatException exception)
        {
            errors.append("* ").append(I18N.getString("serverPortMustBeInteger")).append("\n");
        }

        if(errors.toString().isEmpty())
        {
            try
            {
                Config.getInstance().setName(clientNameTextField.getText());
                Config.getInstance().setServerHostNameOrIP(serverIPHostNameTextField.getText());
                Config.getInstance().setServerPort(port);
                Config.getInstance().setFirstTimeUse(false);

                if(ClientInfo.getInstance().isPhone())
                {
                    Config.getInstance().setScreenMoverEnabled(true);
                }

                Config.getInstance().save();

                ClientProfile clientProfile = new ClientProfile(new File(Config.getInstance().getProfilesPath()+ File.separator +
                        Config.getInstance().getStartupProfileID()+".xml"), Config.getInstance().getIconsPath());

                ActionGridRowsAndColsCalculator actionGridRowsAndColsCalculator = new ActionGridRowsAndColsCalculator(ClientInfo.getInstance().getOrientation(),
                        clientProfile.getActionSize(), clientProfile.getActionGap(), clientListener.getStageWidth(), clientListener.getStageHeight());

                clientProfile.setCols(actionGridRowsAndColsCalculator.getCols());
                clientProfile.setRows(actionGridRowsAndColsCalculator.getRows());

                clientProfile.saveProfileDetails();

                Platform.runLater(()-> {
                    clientListener.init();
                    clientListener.setupClientConnection();
                });
            }
            catch(Exception e)
            {
                e.printStackTrace();
                exceptionAndAlertHandler.handleSevereException(new SevereException(e.getMessage()));
            }
        }
        else
        {
            Platform.runLater(()->nextButton.setDisable(false));
            new StreamPiAlert(I18N.getString("validationError", errors), StreamPiAlertType.ERROR).show();
        }
    }
}
