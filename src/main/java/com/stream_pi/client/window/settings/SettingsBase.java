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

package com.stream_pi.client.window.settings;

import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.window.settings.about.About;

import javafx.application.HostServices;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SettingsBase extends VBox
{
    private TabPane tabPane;

    private GeneralTab generalTab;

    private Button closeButton;

    private HostServices hostServices;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    public SettingsBase(HostServices hostServices, ExceptionAndAlertHandler exceptionAndAlertHandler,
                        ClientListener clientListener)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.hostServices = hostServices;

        tabPane = new TabPane();
        tabPane.addEventFilter(SwipeEvent.ANY, Event::consume);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab generalSettingsTab = new Tab(I18N.getString("window.settings.SettingsBase.settings"));
        generalTab = new GeneralTab(exceptionAndAlertHandler, clientListener, hostServices);
        generalSettingsTab.setContent(generalTab);

        Tab aboutTab = new Tab(I18N.getString("window.settings.SettingsBase.about"));
        aboutTab.setContent(new About(clientListener));

        tabPane.getTabs().addAll(generalSettingsTab, aboutTab);

        setAlignment(Pos.TOP_RIGHT);

        closeButton = new Button(I18N.getString("window.settings.SettingsBase.close"));
        VBox.setMargin(closeButton, new Insets(10.0));

        getChildren().addAll(tabPane, closeButton);

        getStyleClass().add("settings_base");

        setCache(true);
        setCacheHint(CacheHint.QUALITY);
    }

    public void setDefaultTabToGeneral()
    {
        tabPane.getSelectionModel().selectFirst();
    }

    public Button getCloseButton()
    {
        return closeButton;
    }

    public GeneralTab getGeneralTab()
    {
        return generalTab;
    }
}
