package com.StreamPi.Client.Controller;

import com.StreamPi.ActionAPI.Action.Action;
import com.StreamPi.ActionAPI.Action.Location;
import com.StreamPi.Client.Connection.Client;
import com.StreamPi.Client.IO.Config;
import com.StreamPi.Client.Info.ClientInfo;
import com.StreamPi.Client.Main;
import com.StreamPi.Client.Profile.ClientProfile;
import com.StreamPi.Client.Profile.ClientProfiles;
import com.StreamPi.Client.Window.Base;
import com.StreamPi.Client.Window.Dashboard.ActionGridPane.ActionBox;
import com.StreamPi.Client.Window.FirstTimeUse.FirstTimeUse;
import com.StreamPi.Client.Window.Settings.SettingsBase;
import com.StreamPi.Util.Alert.StreamPiAlert;
import com.StreamPi.Util.Alert.StreamPiAlertListener;
import com.StreamPi.Util.Alert.StreamPiAlertType;
import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Exception.SevereException;
import com.StreamPi.Util.Exception.StreamPiException;
import com.StreamPi.Util.IOHelper.IOHelper;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Services;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


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
            initBase(); 

            requestFocus();

            if(Config.getInstance().isFirstTimeUse())
                return;
            
            setupDashWindow();
    
        
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
                setupClientConnection();
                firstRun = false;
            }

        }
        catch (SevereException e)
        {
            handleSevereException(e);
            return;
        }
        catch (MinorException e)
        {
            handleMinorException(e);
            return;
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
        getStage().setTitle("StreamPi Client");
        getStage().setOnCloseRequest(e->onCloseRequest());
    }


    @Override
    public void onCloseRequest()
    {
        if(isConnected())
            client.exit();


        getLogger().info("Shut down");
        closeLogger();

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
        Platform.runLater(()-> genNewAlert(e.getTitle(), e.getShortMessage(), StreamPiAlertType.WARNING).show());
    }

    @Override
    public void handleSevereException(SevereException e)
    {
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
    public void onNormalActionClicked(String profileID, String actionID) {
        try {
            client.onActionClicked(profileID, actionID);
        } catch (SevereException e) {
            e.printStackTrace();
            handleSevereException(e);
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
    public void renderProfile(ClientProfile clientProfile) {
        try {
            getDashboardPane().renderProfile(clientProfile);
        } catch (SevereException e) {
            e.printStackTrace();
            handleSevereException(e);
        }
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
                        getDashboardPane().getActionGridPane().getClientProfile().getID().equals(currentProfileID))
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
    public void refreshGridIfCurrent(String profileID) {
        ClientProfile clientProfile = getDashboardPane().getActionGridPane().getClientProfile();

        if(clientProfile.getID().equals(profileID))
        {
            Platform.runLater(()->{
                try {
                    getDashboardPane().renderProfile(getClientProfiles().getProfileFromID(profileID));
                } catch (SevereException e) {
                    e.printStackTrace();
                    handleSevereException(e);
                }
            });
        }
    }

    @Override
    public ClientProfile getCurrentProfile() {
        return getDashboardPane().getActionGridPane().getClientProfile();
    }

}
