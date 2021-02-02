package com.stream_pi.client.connection;

import com.stream_pi.actionapi.action.Action;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.themeapi.Theme;
import com.stream_pi.themeapi.Themes;
import com.stream_pi.util.exception.SevereException;

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
