package com.stream_pi.client.controller;

import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.connection.Client;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.window.Base;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertListener;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Services;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
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
                getStage().centerOnScreen();
                setupFlags();
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

   

    @Override
    public void setupClientConnection()
    {
        Platform.runLater(()->getSettingsPane().setDisableStatus(true));
        client = new Client(getConfig().getSavedServerHostNameOrIP(), getConfig().getSavedServerPort(), this, this);
    }

    @Override
    public void updateSettingsConnectDisconnectButton() {
        getSettingsPane().setConnectDisconnectButtonStatus();
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
        if(isConnected())
            client.exit();


        getLogger().info("Shut down");
        closeLogger();

        if (ClientInfo.getInstance().getPlatform() == com.stream_pi.util.platform.Platform.ANDROID)
            Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
    }

    @Override
    public void loadSettings() {
        try {
            getSettingsPane().loadData();
        } catch (SevereException e) {
            e.printStackTrace();
            handleSevereException(e);
        }
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
                    getSettingsPane().loadData();
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
        StreamPiAlert alert = new StreamPiAlert(title, message, alertType);
        return alert;
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

}
