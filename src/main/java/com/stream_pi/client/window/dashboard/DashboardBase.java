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

package com.stream_pi.client.window.dashboard;

import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionGridPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class DashboardBase extends AnchorPane
{
    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ActionGridPane actionGridPane;
    private Button settingsButton;

    public DashboardBase(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;

        actionGridPane = new ActionGridPane(exceptionAndAlertHandler, clientListener);
        AnchorPane.setTopAnchor(actionGridPane, 0.0);
        AnchorPane.setBottomAnchor(actionGridPane, 0.0);
        AnchorPane.setLeftAnchor(actionGridPane, 0.0);
        AnchorPane.setRightAnchor(actionGridPane, 0.0);

        FontIcon fontIcon = new FontIcon("fas-cog");
        fontIcon.getStyleClass().addAll("dashboard_settings_button_icon");

        settingsButton = new Button();
        settingsButton.getStyleClass().addAll("dashboard_settings_button");
        settingsButton.setGraphic(fontIcon);
        AnchorPane.setBottomAnchor(settingsButton, 10.0);
        AnchorPane.setRightAnchor(settingsButton, 10.0);

        if (actionGridPane.getSkin() == null)
        {
            ChangeListener<Skin<?>> skinChangeListener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                    actionGridPane.skinProperty().removeListener(this);
                    modifySettingsButtonToPreventOverlappingScrollBars();
                }
            };
            actionGridPane.skinProperty().addListener(skinChangeListener);
        }
        else
        {
            modifySettingsButtonToPreventOverlappingScrollBars();
        }

        getChildren().addAll(actionGridPane,settingsButton);

        getStyleClass().add("dashboard");

        setCache(true);
        setCacheHint(CacheHint.QUALITY);
    }

    private void modifySettingsButtonToPreventOverlappingScrollBars()
    {
        for (Node n : actionGridPane.lookupAll(".scroll-bar"))
        {
            if (n instanceof ScrollBar)
            {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL))
                {
                    bar.visibleProperty().addListener((observableValue, oldValue, newVal) ->
                    {
                        if (newVal)
                        {
                            AnchorPane.setRightAnchor(settingsButton, 20.0);
                        }
                        else
                        {
                            AnchorPane.setRightAnchor(settingsButton, 10.0);
                        }
                    });
                }
                else if (bar.getOrientation().equals(Orientation.HORIZONTAL))
                {
                    bar.visibleProperty().addListener((observableValue, oldValue, newVal) ->
                    {
                        if (newVal)
                        {
                            AnchorPane.setBottomAnchor(settingsButton, 20.0);
                        }
                        else
                        {
                            AnchorPane.setBottomAnchor(settingsButton, 10.0);
                        }
                    });
                }
            }
        }
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

    public ActionGridPane getActionGridPane() {
        return actionGridPane;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
}
