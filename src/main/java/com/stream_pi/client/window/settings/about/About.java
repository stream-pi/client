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

package com.stream_pi.client.window.settings.about;

import com.stream_pi.action_api.ActionAPI;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.theme_api.ThemeAPI;
import com.stream_pi.util.Util;
import com.stream_pi.client.Main;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.info.ClientInfo;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class About extends ScrollPane
{
    private final ClientListener clientListener;

    private ContributorsTab contributorsTab;
    private VBox mainVBox;

    public About(ClientListener clientListener)
    {
        this.clientListener = clientListener;

        getStyleClass().add("settings_about_tab");


        mainVBox = new VBox();
        mainVBox.getStyleClass().add("settings_about_tab_vbox");


        mainVBox.setAlignment(Pos.TOP_CENTER);

        Image appIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/256x256.png")));
        ImageView appIconImageView = new ImageView(appIcon);
        VBox.setMargin(appIconImageView, new Insets(10, 0, 10, 0));
        appIconImageView.setFitHeight(128);
        appIconImageView.setFitWidth(128);

        Tab contributorsT = new Tab(I18N.getString("window.settings.about.About.contributorsTabHeading"));
        contributorsTab = new ContributorsTab(clientListener);
        contributorsT.setContent(contributorsTab);


        TabPane tabPane = new TabPane();
        tabPane.addEventFilter(SwipeEvent.ANY, Event::consume);

        tabPane.getStyleClass().add("settings_about_tab_internal");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMaxWidth(600);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab licenseTab = new Tab(I18N.getString("window.settings.about.About.licenseTabHeading"));
        licenseTab.setContent(new LicenseTab());



        Tab contactTab = new Tab(I18N.getString("window.settings.about.About.contactTabHeading"));
        contactTab.setContent(new ContactTab(clientListener));

        tabPane.getTabs().addAll(licenseTab, contributorsT, contactTab);


        Hyperlink donateButton = new Hyperlink(I18N.getString("window.settings.about.About.donate"));
        donateButton.setOnAction(event -> openWebpage("https://www.patreon.com/streampi"));
        donateButton.getStyleClass().add("about_donate_hyperlink");


        ClientInfo clientInfo = ClientInfo.getInstance();
        Label versionText = new Label(clientInfo.getVersion().getText() + " - "+ clientInfo.getPlatform().getUIName() + " - "+ clientInfo.getReleaseStatus().getUIName());
        versionText.getStyleClass().add("about_version_label");

        Label commStandardLabel = new Label(I18N.getString("window.settings.about.About.serverCommunicationProtocolVersion", clientInfo.getCommunicationProtocolVersion().getText()));
        commStandardLabel.getStyleClass().add("about_comm_standard_label");

        Label currentActionAPILabel = new Label("Action API " + ActionAPI.VERSION.getText());
        currentActionAPILabel.getStyleClass().add("about_current_action_api_label");

        Label currentUtilLabel = new Label("Util " + Util.VERSION.getText());
        currentUtilLabel.getStyleClass().add("about_current_util_label");


        Label javaVersionLabel = new Label("Java " + System.getProperty("java.version") + ", " + System.getProperty("java.vm.name"));
        javaVersionLabel.getStyleClass().add("about_java_version");

        Label javafxVersionLabel = new Label("JavaFX " + System.getProperty("javafx.version"));
        javafxVersionLabel.getStyleClass().add("about_javafx_version");

        Label javaGCLabel = new Label("GC: " + ManagementFactory.getGarbageCollectorMXBeans().get(0).getName());
        javaGCLabel.getStyleClass().add("about_java_gc");

        VBox vBox = new VBox(
                versionText,
                commStandardLabel,
                currentActionAPILabel,
                currentUtilLabel,
                javaVersionLabel,
                javafxVersionLabel,
                javaGCLabel
        );

        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);

        if(clientInfo.getBuildNumber() != null)
        {
            Label buildNumberLabel = new Label(I18N.getString("window.settings.about.About.buildNumber", clientInfo.getBuildNumber()));
            buildNumberLabel.getStyleClass().add("about_build_number_label");
            vBox.getChildren().addAll(buildNumberLabel);
        }


        mainVBox.getChildren().addAll(appIconImageView, tabPane,
                donateButton, vBox);

        setFitToWidth(true);

        setContent(mainVBox);


        setCache(true);
        setCacheHint(CacheHint.SPEED);
    }

    private Label getSep()
    {
        Label label = new Label("|");
        label.getStyleClass().add("separator_ui_label");
        return label;
    }

    public void openWebpage(String url)
    {
        clientListener.openURL(url);
    }
}
