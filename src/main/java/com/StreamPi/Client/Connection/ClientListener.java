package com.StreamPi.Client.Connection;

import com.StreamPi.ActionAPI.Action.Action;
import com.StreamPi.ActionAPI.Action.Location;
import com.StreamPi.Client.Profile.ClientProfile;
import com.StreamPi.Client.Profile.ClientProfiles;
import com.StreamPi.Client.Window.Dashboard.ActionGridPane.ActionBox;
import com.StreamPi.Client.Window.FirstTimeUse.FirstTimeUse;
import com.StreamPi.ThemeAPI.Theme;
import com.StreamPi.ThemeAPI.Themes;
import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Exception.SevereException;
import com.StreamPi.Util.Exception.StreamPiException;

import javafx.stage.WindowEvent;

public interface ClientListener {
    void onActionFailed(String profileID, String actionID);
    void onNormalActionClicked(String profileID, String actionID);

    ClientProfiles getClientProfiles();

    Themes getThemes();
    String getDefaultThemeFullName();

    void renderRootDefaultProfile() throws SevereException;

    void setConnected(boolean isConnected);
    boolean isConnected();

    void renderProfile(ClientProfile clientProfile);

    void clearActionBox(int col, int row);
    void addBlankActionBox(int col, int row);
    void renderAction(String currentProfileID, Action action);
    void refreshGridIfCurrent(String currentProfileID);
    
    ActionBox getActionBox(int col, int row);

    ClientProfile getCurrentProfile();

    Theme getCurrentTheme();

    void init();

    void disconnect(String message) throws SevereException;

    void setupClientConnection();

    void updateSettingsConnectDisconnectButton();

    void onCloseRequest();

    void loadSettings();
}
