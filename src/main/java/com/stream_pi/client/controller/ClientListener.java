// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.controller;

import javafx.geometry.Orientation;
import java.util.concurrent.ExecutorService;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.connection.Client;
import com.stream_pi.theme_api.Themes;
import com.stream_pi.client.profile.ClientProfiles;

public interface ClientListener
{
    void onActionFailed(final String p0, final String p1);
    
    void onActionClicked(final String p0, final String p1, final boolean p2);
    
    ClientProfiles getClientProfiles();
    
    Themes getThemes();
    
    String getDefaultThemeFullName();
    
    Client getClient();
    
    void renderRootDefaultProfile();
    
    void setConnected(final boolean p0);
    
    boolean isConnected();
    
    void renderProfile(final ClientProfile p0, final boolean p1);
    
    void clearActionBox(final int p0, final int p1);
    
    void addBlankActionBox(final int p0, final int p1);
    
    void renderAction(final String p0, final Action p1);
    
    void refreshGridIfCurrentProfile(final String p0);
    
    ActionBox getActionBox(final int p0, final int p1);
    
    ClientProfile getCurrentProfile();
    
    String getCurrentParent();
    
    Theme getCurrentTheme();
    
    void initLogger() throws SevereException;
    
    void init();
    
    void disconnect(final String p0) throws SevereException;
    
    void setupClientConnection();
    
    void setupClientConnection(final Runnable p0);
    
    void updateSettingsConnectDisconnectButton();
    
    void onCloseRequest();
    
    void loadSettings();
    
    double getStageWidth();
    
    double getStageHeight();
    
    void onDisconnect();
    
    boolean getToggleStatus(final String p0, final String p1);
    
    ActionBox getActionBoxByProfileAndID(final String p0, final String p1);
    
    void openURL(final String p0);
    
    void factoryReset();
    
    void exitApp();
    
    ExecutorService getExecutor();
    
    Orientation getCurrentOrientation();
    
    void setFirstRun(final boolean p0);
    
    ScreenSaver getScreenSaver();
    
    void initThemes() throws SevereException;
}
