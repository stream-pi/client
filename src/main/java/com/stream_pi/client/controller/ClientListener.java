package com.stream_pi.client.controller;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.connection.Client;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionGridPaneListener;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.theme_api.Themes;
import com.stream_pi.util.exception.SevereException;
import javafx.geometry.Orientation;

import java.util.concurrent.ExecutorService;

public interface ClientListener
{
    void onActionFailed(String profileID, String actionID);
    void onActionClicked(String profileID, String actionID, boolean toggleState);

    ClientProfiles getClientProfiles();

    Themes getThemes();
    String getDefaultThemeFullName();

    Client getClient();

    void renderRootDefaultProfile();

    void setConnected(boolean isConnected);
    boolean isConnected();

    void renderProfile(ClientProfile clientProfile, boolean freshRender);

    void clearActionBox(int col, int row);
    void addBlankActionBox(int col, int row);
    void renderAction(String currentProfileID, Action action);
    void refreshGridIfCurrentProfile(String currentProfileID);
    
    ActionBox getActionBox(int col, int row);

    ClientProfile getCurrentProfile();

    String getCurrentParent();

    Theme getCurrentTheme();

    void initLogger() throws SevereException;
    void init();

    void disconnect(String message) throws SevereException;

    void setupClientConnection();

    void setupClientConnection(Runnable onConnect);

    void updateSettingsConnectDisconnectButton();

    void onCloseRequest();

    void loadSettings();

    double getStageWidth();
    double getStageHeight();

    void onDisconnect();

    boolean getToggleStatus(String profileID, String actionID);

    ActionBox getActionBoxByProfileAndID(String profileID, String actionID);

    void openURL(String URL);

    void factoryReset();
    void exitApp();

    ExecutorService getExecutor();

    Orientation getCurrentOrientation();

    void setFirstRun(boolean firstRun);

    ScreenSaver getScreenSaver();

    void initThemes() throws SevereException;
}
