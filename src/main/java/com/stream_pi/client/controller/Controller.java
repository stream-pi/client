package com.stream_pi.client.controller;

import com.gluonhq.attach.browser.BrowserService;
import com.gluonhq.attach.orientation.OrientationService;
import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.connection.Client;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.window.Base;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionGridPaneListener;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertListener;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Services;

import com.stream_pi.util.platform.PlatformType;
import com.stream_pi.util.startatboot.StartAtBoot;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.*;
import java.util.logging.Level;


public class Controller extends Base
{
    private Client client;

    public Controller()
    {
        client = null;
    }


    private boolean firstRun = true;

    @Override
    public void init()
    {
        try 
        {
            if(firstRun)
                initBase();


            if(getClientInfo().getPlatform() != com.stream_pi.util.platform.Platform.ANDROID)
            {
                if(getConfig().isStartOnBoot())
                {
                    if(getClientInfo().isXMode() != getConfig().isStartupXMode())
                    {
                        StartAtBoot startAtBoot = new StartAtBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform());

                        boolean result = startAtBoot.delete();
                        if(!result)
                            new StreamPiAlert("Uh Oh!", "Unable to delete the previous starter file.\n" +
                                    "This was probably because you ran Stream-Pi as root before. Restart stream pi as root, " +
                                    "delete the old starter file, then exit and restart Stream-Pi as normal user.", StreamPiAlertType.ERROR).show();
                        else
                        {
                            startAtBoot.create(new File(ClientInfo.getInstance().getRunnerFileName()),
                                    ClientInfo.getInstance().isXMode());
                            getConfig().setStartupIsXMode(ClientInfo.getInstance().isXMode());
                        }
                    }
                }

                setupFlags();

                if(!getConfig().getIsFullScreenMode())
                {
                    getStage().setWidth(getConfig().getStartupWindowWidth());
                    getStage().setHeight(getConfig().getStartupWindowHeight());
                }
            }


            applyDefaultTheme();


            setupDashWindow();

            getStage().show();


            requestFocus();

            if(Config.getInstance().isFirstTimeUse())
                return;
        
            setupSettingsWindowsAnimations();



            getDashboardPane().getSettingsButton().setOnAction(event -> {
                openSettingsTimeLine.play();
            });
    
            getSettingsPane().getCloseButton().setOnAction(event -> {
                closeSettingsTimeLine.play();
            });

            setClientProfiles(new ClientProfiles(new File(getConfig().getProfilesPath()), getConfig().getStartupProfileID()));

            if(getClientProfiles().getLoadingErrors().size() > 0)
            {
                StringBuilder errors = new StringBuilder("Please rectify the following errors and try again");

                for(MinorException exception : getClientProfiles().getLoadingErrors())
                {
                    errors.append("\n * ")
                        .append(exception.getMessage());
                }

                throw new MinorException("Profiles", errors.toString());
            }

            renderRootDefaultProfile();
            loadSettings();

            if(firstRun)
            {
                if(getConfig().isConnectOnStartup())
                {
                    setupClientConnection();
                }
                firstRun = false;
            }

            if(!getClientInfo().isPhone())
            {
                getStage().widthProperty().addListener((observableValue, orientation, t1) -> syncClientSizeDetailsWithServer());
                getStage().heightProperty().addListener((observableValue, orientation, t1) -> syncClientSizeDetailsWithServer());
            }

        }
        catch (SevereException e)
        {
            handleSevereException(e);
        }
        catch (MinorException e)
        {
            handleMinorException(e);
        }
    }

    public void syncClientSizeDetailsWithServer()
    {
        if(isConnected())
        {
            try {
                client.sendClientScreenDetails();
            } catch (SevereException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setupClientConnection()
    {
        setupClientConnection(null);
    }

    @Override
    public void setupClientConnection(Runnable onConnect)
    {
        if(getSettingsPane().getGeneralTab().getConnectDisconnectButton().isDisabled()) //probably already connecting
            return;

        Platform.runLater(()->getSettingsPane().getGeneralTab().setDisableStatus(true));
        client = new Client(getConfig().getSavedServerHostNameOrIP(), getConfig().getSavedServerPort(), this, this, onConnect);
    }

    @Override
    public void updateSettingsConnectDisconnectButton() {
        getSettingsPane().getGeneralTab().setConnectDisconnectButtonStatus();
    }

    @Override
    public void disconnect(String message) throws SevereException {
        client.disconnect(message);
    }


    public void setupDashWindow()
    {
        getStage().setTitle("Stream-Pi Client");
        getStage().setOnCloseRequest(e->onCloseRequest());
    }


    @Override
    public void onCloseRequest()
    {
        try
        {
            if(isConnected())
                client.exit();


            if(!getClientInfo().isPhone() && !getConfig().getIsFullScreenMode())
            {
                getConfig().setStartupWindowSize(getStageWidth(), getStageHeight());
                getConfig().save();
            }

        }
        catch (SevereException e)
        {
            handleSevereException(e);
        }
        finally
        {

            getLogger().info("Shut down");
            closeLogger();

            if (ClientInfo.getInstance().getPlatform() == com.stream_pi.util.platform.Platform.ANDROID)
                Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
        }
    }

    @Override
    public void loadSettings() {
        try {
            getSettingsPane().getGeneralTab().loadData();
        } catch (SevereException e) {
            e.printStackTrace();
            handleSevereException(e);
        }
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void onDisconnect() {
        Platform.runLater(()->getDashboardPane().getActionGridPane().toggleOffAllToggleActions());
    }

    @Override
    public boolean getToggleStatus(String profileID, String actionID)
    {
        return getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID).getCurrentToggleStatus();
    }


    private Timeline openSettingsTimeLine;
    private Timeline closeSettingsTimeLine;


    private void setupSettingsWindowsAnimations()
    {
        Node settingsNode = getSettingsPane();
        Node dashboardNode = getDashboardPane();

        openSettingsTimeLine = new Timeline();
        openSettingsTimeLine.setCycleCount(1);


        openSettingsTimeLine.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(0.0D),
                        new KeyValue(settingsNode.opacityProperty(),
                                0.0D, Interpolator.EASE_IN)),
                new KeyFrame(Duration.millis(90.0D),
                        new KeyValue(settingsNode.opacityProperty(),
                                1.0D, Interpolator.LINEAR)),

                new KeyFrame(Duration.millis(0.0D),
                        new KeyValue(dashboardNode.opacityProperty(),
                                1.0D, Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(90.0D),
                        new KeyValue(dashboardNode.opacityProperty(),
                                0.0D, Interpolator.LINEAR))
        );

        openSettingsTimeLine.setOnFinished(event1 -> settingsNode.toFront());


        closeSettingsTimeLine = new Timeline();
        closeSettingsTimeLine.setCycleCount(1);

        closeSettingsTimeLine.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(0.0D),
                        new KeyValue(settingsNode.opacityProperty(),
                                1.0D, Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(90.0D),
                        new KeyValue(settingsNode.opacityProperty(),
                                0.0D, Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(0.0D),
                        new KeyValue(dashboardNode.opacityProperty(),
                                0.0D, Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(90.0D),
                        new KeyValue(dashboardNode.opacityProperty(),
                                1.0D, Interpolator.LINEAR))
        );

        closeSettingsTimeLine.setOnFinished(event1 -> {
            dashboardNode.toFront();
            Platform.runLater(()-> {
                try {
                    getSettingsPane().getGeneralTab().loadData();
                } catch (SevereException e) {
                    e.printStackTrace();

                    handleSevereException(e);
                }
            });
        });
    }





    @Override
    public void handleMinorException(MinorException e) 
    {
        getLogger().log(Level.SEVERE, e.getMessage(), e);
        e.printStackTrace();


        Platform.runLater(()-> genNewAlert(e.getTitle(), e.getShortMessage(), StreamPiAlertType.WARNING).show());
    }

    @Override
    public void handleSevereException(SevereException e)
    {
        getLogger().log(Level.SEVERE, e.getMessage(), e);
        e.printStackTrace();


        Platform.runLater(()->
        {
            StreamPiAlert alert = genNewAlert(e.getTitle(), e.getShortMessage(), StreamPiAlertType.ERROR);

            alert.setOnClicked(new StreamPiAlertListener()
            {
                @Override
                public void onClick(String txt)
                {
                    onCloseRequest();
                    Platform.exit();
                }
            });
            alert.show();
        });
    }

    @Override
    public void onAlert(String title, String body, StreamPiAlertType alertType) {
        Platform.runLater(()-> genNewAlert(title, body, alertType).show());
    }

    public StreamPiAlert genNewAlert(String title, String message, StreamPiAlertType alertType)
    {
        return new StreamPiAlert(title, message, alertType);
    }


    private boolean isConnected = false;

    @Override
    public void onActionFailed(String profileID, String actionID) {
        Platform.runLater(()-> getDashboardPane().getActionGridPane().actionFailed(profileID, actionID));
    }

    @Override
    public void onActionClicked(String profileID, String actionID, boolean toggleState)
    {
        try {

            vibratePhone();

            client.onActionClicked(profileID, actionID, toggleState);
        } catch (SevereException e) {
            e.printStackTrace();
            handleSevereException(e);
        }
    }

    public void vibratePhone()
    {
        if(getConfig().isVibrateOnActionClicked())
        {
            VibrationService.create().ifPresent(VibrationService::vibrate);
        }
    }

    @Override
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public ActionBox getActionBox(int col, int row)
    {
        return getDashboardPane().getActionGridPane().getActionBox(col, row);
    }

    @Override
    public boolean isConnected()
    {
        return isConnected;
    }

    @Override
    public void renderProfile(ClientProfile clientProfile, boolean freshRender)
    {
        getDashboardPane().renderProfile(clientProfile, freshRender);
    }

    @Override
    public void clearActionBox(int col, int row)
    {
        Platform.runLater(()->getDashboardPane().getActionGridPane().clearActionBox(col, row));
    }

    @Override
    public void addBlankActionBox(int col, int row)
    {
        Platform.runLater(()->getDashboardPane().getActionGridPane().addBlankActionBox(col, row));
    }

    @Override
    public void renderAction(String currentProfileID, Action action)
    {
        Platform.runLater(()->{
            try {
                if(getDashboardPane().getActionGridPane().getCurrentParent().equals(action.getParent()) &&
                        getCurrentProfile().getID().equals(currentProfileID))
                {
                    getDashboardPane().getActionGridPane().renderAction(action);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }



    @Override
    public void refreshGridIfCurrentProfile(String profileID) {
        if(getCurrentProfile().getID().equals(profileID))
        {
            Platform.runLater(()-> getDashboardPane().renderProfile(getClientProfiles().getProfileFromID(profileID), true));
        }
    }

    @Override
    public ClientProfile getCurrentProfile() {
        return getDashboardPane().getActionGridPane().getClientProfile();
    }

    @Override
    public String getCurrentParent()
    {
        return getDashboardPane().getActionGridPane().getCurrentParent();
    }


    @Override
    public ActionBox getActionBoxByProfileAndID(String profileID, String actionID)
    {
        Action action = getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);

        if(!getCurrentProfile().getID().equals(profileID) && !getCurrentParent().equals(action.getParent()))
            return null;

        return getDashboardPane().getActionGridPane().getActionBoxByLocation(action.getLocation());
    }

    @Override
    public void openURL(String url)
    {
        if(ClientInfo.getInstance().isPhone())
        {
            BrowserService.create().ifPresentOrElse(s->
            {
                try
                {
                    s.launchExternalBrowser(url);
                }
                catch (Exception e )
                {
                    handleMinorException(
                            new MinorException("Cant start browser!")
                    );
                }
            },()-> handleMinorException(
                    new MinorException("Sorry!","No browser detected.")
            ));
        }
        else
        {
            getHostServices().showDocument(url);
        }
    }
}
