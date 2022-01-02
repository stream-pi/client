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

package com.stream_pi.client.controller;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.connection.Client;
import com.stream_pi.client.profile.ClientAction;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.theme_api.Themes;
import com.stream_pi.util.comms.DisconnectReason;
import com.stream_pi.util.exception.SevereException;
import javafx.geometry.Orientation;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import java.util.concurrent.ExecutorService;

public interface ClientListener
{
    void onActionFailed(String profileID, String actionID);

    ClientProfiles getClientProfiles();

    Themes getThemes();

    Client getClient();

    void renderRootDefaultProfile();

    void setConnected(boolean isConnected);
    boolean isConnected();

    void setIsConnecting(boolean isConnecting);
    boolean isConnecting();

    void renderProfile(ClientProfile clientProfile, boolean freshRender);

    void clearActionBox(int col, int row, int colSpan, int rowSpan);
    void renderAction(String currentProfileID, ClientAction action);
    void refreshGridIfCurrentProfile(String currentProfileID);

    ClientProfile getCurrentProfile();

    String getCurrentParent();

    Theme getCurrentTheme();

    void initLogger() throws SevereException;
    void init();

    void disconnect();

    void setupClientConnection();

    void setupClientConnection(Runnable onConnect);

    void updateSettingsConnectDisconnectButton();

    void onQuitApp();

    void loadSettings();

    double getStageWidth();
    double getStageHeight();

    void onDisconnect();

    boolean getToggleStatus(String profileID, String actionID);

    ActionBox getActionBoxByProfileAndID(String profileID, String actionID);

    void openURL(String URL);

    void factoryReset();
    void exitApp();

    void setFirstRun(boolean firstRun);

    ScreenSaver getScreenSaver();

    void initThemes() throws SevereException;

    long getLastClientFailSystemMills();
    void setLastClientFailSystemMills();

    void restart();
}
