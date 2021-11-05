/*
 * Stream-Pi - Free & Open-Source Modular Cross-Platform Programmable Macro Pad
 * Copyright (C) 2019-2021  Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)
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

package com.stream_pi.client.window.dashboard;

import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionGridPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class DashboardBase extends VBox
{
    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ActionGridPane actionGridPane;
    private Button settingsButton;

    public DashboardBase(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;

        actionGridPane = new ActionGridPane(exceptionAndAlertHandler, clientListener);

        FontIcon fontIcon = new FontIcon("fas-cog");
        fontIcon.getStyleClass().addAll("dashboard_settings_button_icon");

        settingsButton = new Button();
        settingsButton.getStyleClass().addAll("dashboard_settings_button");
        settingsButton.setGraphic(fontIcon);

        HBox hBox = new HBox(settingsButton);
        hBox.getStyleClass().add("dashboard_settings_button_parent");
        hBox.setAlignment(Pos.CENTER_RIGHT);


        getChildren().addAll(actionGridPane,hBox);

        getStyleClass().add("dashboard");
    }

    public void renderProfile(ClientProfile clientProfile, boolean freshRender)
    {
        renderProfile(clientProfile, "root", freshRender);
    }

    public void renderProfile(ClientProfile clientProfile, String currentParent, boolean freshRender)
    {
        actionGridPane.setClientProfile(clientProfile);
        actionGridPane.setCurrentParent(currentParent);
        actionGridPane.setFreshRender(freshRender);

        actionGridPane.renderGrid();
        actionGridPane.renderActions();
    }

    public void addBlankActionBox(int col, int row)
    {
        actionGridPane.addBlankActionBox(col, row);
    }

    public ActionGridPane getActionGridPane() {
        return actionGridPane;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
}
