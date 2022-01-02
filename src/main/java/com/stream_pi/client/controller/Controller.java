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

import com.gluonhq.attach.browser.BrowserService;
import com.gluonhq.attach.orientation.OrientationService;
import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.Main;
import com.stream_pi.client.connection.Client;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientAction;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.window.Base;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.client.window.firsttimeuse.FirstTimeUse;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertButton;
import com.stream_pi.util.alert.StreamPiAlertListener;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.comms.DisconnectReason;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Services;

import com.stream_pi.util.iohelper.IOHelper;
import com.stream_pi.util.platform.PlatformType;
import com.stream_pi.util.rootchecker.RootChecker;
import com.stream_pi.util.startonboot.StartOnBoot;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.util.Duration;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;


public class Controller extends Base
{
    private Client client;

    public Controller()
    {
        client = null;
    }


    private boolean firstRun = true;
    private ScreenSaver screenSaver = null;
    private ScreenMover screenMover = null;

    @Override
    public ScreenSaver getScreenSaver()
    {
        return screenSaver;
    }

    @Override
    public void init()
    {
        try 
        {
            if(firstRun)
            {
                initBase();
            }


            if(getConfig().isScreenSaverEnabled())
            {
                if(screenSaver == null)
                {
                    screenSaver = new ScreenSaver(this, getConfig().getScreenSaverTimeout(), getLogger());
                    getChildren().add(screenSaver);
                    screenSaver.toBack();
                }
                else
                {
                    screenSaver.setTimeout(getConfig().getScreenSaverTimeout());
                    screenSaver.restart();
                }
            }
            else
            {
                if(screenSaver != null)
                {
                    screenSaver.stop();
                    getChildren().remove(screenSaver);
                    screenSaver = null;
                }
            }


            if(!getClientInfo().isPhone())
            {
                if(getConfig().isStartOnBoot())
                {
                    if(StartupFlags.X_MODE != getConfig().isStartupXMode())
                    {
                        StartOnBoot startAtBoot = new StartOnBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform(),
                                Main.class.getProtectionDomain().getCodeSource().getLocation(),
                                StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION);

                        boolean result = startAtBoot.delete();
                        if(!result)
                        {
                            new StreamPiAlert(I18N.getString("controller.Controller.unableToDeleteStarterFile"), StreamPiAlertType.ERROR).show();
                        }
                        else
                        {
                            try
                            {
                                startAtBoot.create(StartupFlags.RUNNER_FILE_NAME, StartupFlags.X_MODE, StartupFlags.generateRuntimeArgumentsForStartOnBoot());
                                getConfig().setStartupIsXMode(StartupFlags.X_MODE);
                            }
                            catch (MinorException e)
                            {
                                getConfig().setStartOnBoot(false);
                                handleMinorException(e);
                            }
                        }
                    }
                }

                setupFlags();

                if(!getConfig().getIsFullScreenMode())
                {
                    if (firstRun)
                    {
                        getStage().setWidth(getConfig().getStartupWindowWidth());
                        getStage().setHeight(getConfig().getStartupWindowHeight());
                    }
                }
            }


            setupDashWindow();

            getStage().show();


            if (RootChecker.isRoot(getClientInfo().getPlatform()))
            {
                if(StartupFlags.ALLOW_ROOT)
                {
                    getLogger().warning("Stream-Pi has been started as root due to allowRoot flag. This may be unsafe and is strictly not recommended!");
                }
                else
                {
                    throw new SevereException(RootChecker.getRootNotAllowedI18NString());
                }
            }


            if(getConfig().isScreenMoverEnabled())
            {
                if(screenMover == null)
                {
                    screenMover = new ScreenMover(getStage(), getConfig().getScreenMoverInterval(),
                            getConfig().getScreenMoverXChange(),
                            getConfig().getScreenMoverYChange());
                }
                else
                {
                    screenMover.setInterval(getConfig().getScreenMoverInterval());
                    screenMover.restart();
                }
            }
            else
            {
                if(screenMover != null)
                {
                    screenMover.stop();
                    screenMover = null;
                }
            }


            if(Config.getInstance().isFirstTimeUse())
            {
                firstRun = false;

                resizeAccordingToResolution();

                firstTimeUse = new FirstTimeUse(this, this);

                if(getClientInfo().isPhone())
                {
                    firstTimeUse.setPadding(new Insets(15));
                }

                getChildren().add(firstTimeUse);
                firstTimeUse.toFront();

                //resolution check
                return;
            }
            else
            {
                getDashboardPane().toFront();

                if (firstTimeUse!=null)
                {
                    getChildren().remove(firstTimeUse);
                    firstTimeUse = null;
                }
            }
            
            setupSettingsWindowsAnimations();
            getDashboardPane().getSettingsButton().setOnAction(event -> openSettingsAnimation.play());
            getSettingsPane().getCloseButton().setOnAction(event -> closeSettingsAnimation.play());

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


            if(getClientInfo().isPhone() && getConfig().isInvertRowsColsOnDeviceRotate())
            {
                OrientationService.create().ifPresent(orientationService -> {
                    if(orientationService.getOrientation().isPresent())
                    {
                        orientationService.orientationProperty().addListener((observableValue, oldOrientation, newOrientation) ->
                        {
                            getDashboardPane().renderProfile(
                                    getCurrentProfile(),
                                    getCurrentParent(),
                                    true
                            );

                            ClientExecutorService.getExecutorService().submit(()->{
                                try
                                {
                                    if(isConnected())
                                    {
                                        getClient().updateOrientationOnClient(getClientInfo().getOrientation());
                                    }
                                }
                                catch (SevereException e)
                                {
                                    handleSevereException(e);
                                }
                            });
                        });
                    }
                });
            }

            renderRootDefaultProfile();
            loadSettings();

            if(firstRun)
            {
                if(getConfig().isConnectOnStartup())
                {
                    setupClientConnection();
                }
            }

            if(!getClientInfo().isPhone())
            {
                getStage().widthProperty().addListener((observableValue, orientation, t1) -> syncClientSizeDetailsWithServer());
                getStage().heightProperty().addListener((observableValue, orientation, t1) -> syncClientSizeDetailsWithServer());
            }


            firstRun = false;
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
    public synchronized void setupClientConnection()
    {
        setupClientConnection(null);
    }

    @Override
    public synchronized void setupClientConnection(Runnable onConnect)
    {
        if(isConnecting()) //probably already connecting
            return;

        client = new Client(getConfig().getSavedServerHostNameOrIP(), getConfig().getSavedServerPort(), this, this, onConnect);
    }

    private long lastClientFailSystemMills = -1;

    @Override
    public long getLastClientFailSystemMills()
    {
        return lastClientFailSystemMills;
    }

    @Override
    public void setLastClientFailSystemMills()
    {
        this.lastClientFailSystemMills = System.currentTimeMillis();
    }

    @Override
    public void updateSettingsConnectDisconnectButton()
    {
        getSettingsPane().getGeneralTab().setConnectDisconnectButtonStatus();
    }

    @Override
    public void disconnect()
    {
        try
        {
            client.disconnect();
        }
        catch (SevereException e)
        {
            handleSevereException(e);
        }
    }


    public void setupDashWindow()
    {
        getStage().setTitle(I18N.getString("windowTitle"));
        getStage().setOnCloseRequest(e->{
            onQuitApp();
            exitApp();
        });
    }


    @Override
    public void onQuitApp()
    {
        try
        {
            if(isConnected())
                client.exit();

            if(screenSaver != null)
            {
                screenSaver.stop();
            }

            if(screenMover != null)
            {
                screenMover.stop();
            }

            if(getConfig() != null)
            {
                if(!getClientInfo().isPhone() && !getConfig().getIsFullScreenMode())
                {
                    getConfig().setStartupWindowSize(getStageWidth(), getStageHeight());
                    getConfig().save();
                }
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
            Config.nullify();
        }
    }

    @Override
    public void exitApp()
    {
        ClientExecutorService.getExecutorService().shutdown();
        if (ClientInfo.getInstance().getPlatform() == com.stream_pi.util.platform.Platform.ANDROID)
        {
            Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
        }
        else
        {
            Platform.exit();
        }
    }

    @Override
    public void loadSettings()
    {
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
    public void onDisconnect()
    {
        Platform.runLater(()-> getDashboardPane().getActionGridPane().toggleOffAllToggleActionsAndHideAllGaugeActionsAndResetTheActionsDisplayText());
    }

    @Override
    public boolean getToggleStatus(String profileID, String actionID)
    {
        return getClientProfiles().getProfileFromID(profileID).getActionByID(actionID).getCurrentToggleStatus();
    }


    private Animation openSettingsAnimation;
    private Animation closeSettingsAnimation;


    private void setupSettingsWindowsAnimations()
    {
        Node settingsNode = getSettingsPane();
        Node dashboardNode = getDashboardPane();

        openSettingsAnimation = createOpenSettingsAnimation(settingsNode, dashboardNode);
        closeSettingsAnimation = createCloseSettingsAnimation(settingsNode, dashboardNode);
    }


    @Override
    public StreamPiAlert handleMinorException(MinorException e)
    {
        return handleMinorException(e.getMessage(), e);
    }

    @Override
    public StreamPiAlert handleMinorException(String message, MinorException e)
    {
        getLogger().log(Level.SEVERE, message, e);
        e.printStackTrace();

        if(getScreenSaver() != null)
        {
            getScreenSaver().restart();
        }

        StreamPiAlert alert = new StreamPiAlert(e.getTitle(), message, StreamPiAlertType.WARNING);
        alert.show();
        return alert;
    }

    @Override
    public StreamPiAlert handleSevereException(SevereException e)
    {
        return handleSevereException(e.getMessage(), e);
    }

    @Override
    public StreamPiAlert handleSevereException(String message, SevereException e)
    {
        getLogger().log(Level.SEVERE, message, e);
        e.printStackTrace();

        if(getScreenSaver() != null)
        {
            getScreenSaver().restart();
        }

        StreamPiAlert alert = new StreamPiAlert(e.getTitle(), message, StreamPiAlertType.ERROR);

        alert.setOnClicked(new StreamPiAlertListener()
        {
            @Override
            public void onClick(StreamPiAlertButton s)
            {
                onQuitApp();
                exitApp();
            }
        });

        alert.show();

        return alert;
    }

    @Override
    public void onActionFailed(String profileID, String actionID) {
        Platform.runLater(()-> getDashboardPane().getActionGridPane().actionFailed(profileID, actionID));
    }

    private final AtomicBoolean isConnected = new AtomicBoolean(false);

    @Override
    public void setConnected(boolean isConnected)
    {
        this.isConnected.set(isConnected);
    }

    @Override
    public boolean isConnected()
    {
        return isConnected.get();
    }

    private final AtomicBoolean isConnecting = new AtomicBoolean(false);

    @Override
    public void setIsConnecting(boolean isConnecting)
    {
        this.isConnecting.set(isConnecting);
    }

    @Override
    public boolean isConnecting()
    {
        return isConnecting.get();
    }


    @Override
    public void renderProfile(ClientProfile clientProfile, boolean freshRender)
    {
        getDashboardPane().renderProfile(clientProfile, freshRender);
    }

    @Override
    public void clearActionBox(int col, int row, int colSpan, int rowSpan)
    {
        Platform.runLater(()-> getDashboardPane().getActionGridPane().clearActionBox(col, row, colSpan, rowSpan));
    }

    @Override
    public void renderAction(String currentProfileID, ClientAction action)
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
        Action action = getClientProfiles().getProfileFromID(profileID).getActionByID(actionID);

        if(!getCurrentProfile().getID().equals(profileID) && !getCurrentParent().equals(action.getParent()))
        {
            return null;
        }

        if (action == null)
        {
            getLogger().warning("Action is null. Probably because deleted.");
            return null;
        }

        if (action.getLocation() == null)
        {
            getLogger().warning("Action has no location. Probably combine action child.");
            return null;
        }

        return getDashboardPane().getActionGridPane().getActionBox(action.getLocation().getCol(), action.getLocation().getRow());
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
                catch (Exception e)
                {
                    e.printStackTrace();
                    handleMinorException(
                            new MinorException(I18N.getString("controller.Controller.failedToStartBrowser", e.getLocalizedMessage()))
                    );
                }
            },()-> handleMinorException(
                    new MinorException(I18N.getString("controller.Controller.noBrowserDetected"))
            ));
        }
        else
        {
            if(getClientInfo().getPlatform() == com.stream_pi.util.platform.Platform.LINUX &&
                !StartupFlags.X_MODE)
            {
               handleMinorException(new MinorException(I18N.getString("controller.Controller.browserNotSupportedInFBMode")));
            }
            else
            {
                getHostServices().showDocument(url);
            }
        }
    }

    @Override
    public void factoryReset()
    {
        getLogger().info("Reset to factory ...");

        onQuitApp();

        if (IOHelper.deleteFile(getClientInfo().getPrePath(), false))
        {
            setFirstRun(true);
            init();
        }
        else
        {
            handleSevereException(new SevereException(I18N.getString("controller.Controller.factoryResetUnsuccessful", getClientInfo().getPrePath())));
        }
    }

    @Override
    public void setFirstRun(boolean firstRun)
    {
        this.firstRun = firstRun;
    }

    private Animation createOpenSettingsAnimation(Node settingsNode, Node dashboardNode)
    {
        Timeline openSettingsTimeline = new Timeline();
        openSettingsTimeline.setCycleCount(1);


        openSettingsTimeline.getKeyFrames().addAll(
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

        openSettingsTimeline.setOnFinished(e -> settingsNode.toFront());

        return openSettingsTimeline;
    }

    private Animation createCloseSettingsAnimation(Node settingsNode, Node dashboardNode)
    {
        Timeline closeSettingsTimeline = new Timeline();
        closeSettingsTimeline.setCycleCount(1);

        closeSettingsTimeline.getKeyFrames().addAll(
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

        closeSettingsTimeline.setOnFinished(event1 -> {
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

        return closeSettingsTimeline;
    }

    @Override
    public void restart()
    {
        getLogger().info("Restarting ...");

        onQuitApp();
        setFirstRun(true);
        Platform.runLater(()->{
            unregisterThemes();
            init();
        });
    }
}
