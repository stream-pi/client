// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.controller;

import javafx.beans.value.ObservableValue;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import com.stream_pi.util.iohelper.IOHelper;
import com.gluonhq.attach.browser.BrowserService;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.util.alert.StreamPiAlertListener;
import java.util.logging.Level;
import javafx.scene.Node;
import javafx.beans.value.WritableValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import java.util.function.Consumer;
import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import java.util.Iterator;
import com.stream_pi.util.exception.SevereException;
import com.gluonhq.attach.orientation.OrientationService;
import com.stream_pi.client.profile.ClientProfiles;
import java.io.File;
import com.stream_pi.client.io.Config;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.startatboot.StartAtBoot;
import com.stream_pi.client.Main;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.util.platform.PlatformType;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.util.platform.Platform;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import com.stream_pi.client.connection.Client;
import com.stream_pi.client.window.Base;

public class Controller extends Base
{
    private Client client;
    private boolean firstRun;
    private ScreenSaver screenSaver;
    private ScreenMover screenMover;
    private Orientation currentOrientation;
    private Timeline openSettingsTimeLine;
    private Timeline closeSettingsTimeLine;
    private boolean isConnected;
    
    public Controller() {
        this.firstRun = true;
        this.screenSaver = null;
        this.screenMover = null;
        this.currentOrientation = null;
        this.isConnected = false;
        this.client = null;
    }
    
    public ScreenSaver getScreenSaver() {
        return this.screenSaver;
    }
    
    public void init() {
        try {
            if (this.firstRun) {
                this.initBase();
            }
            if (this.getConfig().isScreenSaverEnabled()) {
                if (this.screenSaver == null) {
                    this.screenSaver = new ScreenSaver(this, this.getConfig().getScreenSaverTimeout());
                    this.getChildren().add((Object)this.screenSaver);
                    this.screenSaver.toBack();
                }
                else {
                    this.screenSaver.setTimeout(this.getConfig().getScreenSaverTimeout());
                    this.screenSaver.restart();
                }
            }
            else if (this.screenSaver != null) {
                this.screenSaver.stop();
                this.getChildren().remove((Object)this.screenSaver);
                this.screenSaver = null;
            }
            if (this.getClientInfo().getPlatform() != Platform.ANDROID) {
                if (this.getConfig().isStartOnBoot() && StartupFlags.IS_X_MODE != this.getConfig().isStartupXMode()) {
                    final StartAtBoot startAtBoot = new StartAtBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform(), Main.class.getProtectionDomain().getCodeSource().getLocation(), StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION);
                    final boolean result = startAtBoot.delete();
                    if (!result) {
                        new StreamPiAlert("Uh Oh!", "Unable to delete the previous starter file.\nThis was probably because you ran Stream-Pi as root before. Restart stream pi as root, delete the old starter file, then exit and restart Stream-Pi as normal user.", StreamPiAlertType.ERROR).show();
                    }
                    else {
                        try {
                            startAtBoot.create(StartupFlags.RUNNER_FILE_NAME, StartupFlags.IS_X_MODE);
                            this.getConfig().setStartupIsXMode(StartupFlags.IS_X_MODE);
                        }
                        catch (MinorException e) {
                            this.getConfig().setStartOnBoot(false);
                            this.handleMinorException(e);
                        }
                    }
                }
                this.setupFlags();
                if (!this.getConfig().getIsFullScreenMode()) {
                    this.getStage().setWidth(this.getConfig().getStartupWindowWidth());
                    this.getStage().setHeight(this.getConfig().getStartupWindowHeight());
                }
            }
            this.setupDashWindow();
            this.getStage().show();
            if (this.getConfig().isScreenMoverEnabled()) {
                if (this.screenMover == null) {
                    this.screenMover = new ScreenMover(this.getStage(), this.getConfig().getScreenMoverInterval(), this.getConfig().getScreenMoverXChange(), this.getConfig().getScreenMoverYChange());
                }
                else {
                    this.screenMover.setInterval(this.getConfig().getScreenMoverInterval());
                    this.screenMover.restart();
                }
            }
            else if (this.screenMover != null) {
                this.screenMover.stop();
                this.screenMover = null;
            }
            if (Config.getInstance().isFirstTimeUse()) {
                return;
            }
            this.setupSettingsWindowsAnimations();
            this.getDashboardPane().getSettingsButton().setOnAction(event -> this.openSettingsTimeLine.play());
            this.getSettingsPane().getCloseButton().setOnAction(event -> this.closeSettingsTimeLine.play());
            this.setClientProfiles(new ClientProfiles(new File(this.getConfig().getProfilesPath()), this.getConfig().getStartupProfileID()));
            if (this.getClientProfiles().getLoadingErrors().size() > 0) {
                final StringBuilder errors = new StringBuilder("Please rectify the following errors and try again");
                for (final MinorException exception : this.getClientProfiles().getLoadingErrors()) {
                    errors.append("\n * ").append(exception.getMessage());
                }
                throw new MinorException("Profiles", errors.toString());
            }
            if (this.getClientInfo().isPhone() && this.getConfig().isInvertRowsColsOnDeviceRotate()) {
                OrientationService.create().ifPresent(orientationService -> {
                    if (orientationService.getOrientation().isPresent()) {
                        this.setCurrentOrientation(orientationService.getOrientation().get());
                        orientationService.orientationProperty().addListener((observableValue, oldOrientation, newOrientation) -> {
                            this.setCurrentOrientation(newOrientation);
                            this.getDashboardPane().renderProfile(this.getCurrentProfile(), this.getCurrentParent(), true);
                            this.getExecutor().submit(() -> {
                                try {
                                    if (this.isConnected()) {
                                        this.getClient().updateOrientationOnClient(this.getCurrentOrientation());
                                    }
                                }
                                catch (SevereException e) {
                                    this.handleSevereException(e);
                                }
                            });
                        });
                    }
                    return;
                });
            }
            this.renderRootDefaultProfile();
            this.loadSettings();
            if (this.firstRun) {
                if (this.getConfig().isConnectOnStartup()) {
                    this.setupClientConnection();
                }
                this.firstRun = false;
            }
            if (!this.getClientInfo().isPhone()) {
                this.getStage().widthProperty().addListener((observableValue, orientation, t1) -> this.syncClientSizeDetailsWithServer());
                this.getStage().heightProperty().addListener((observableValue, orientation, t1) -> this.syncClientSizeDetailsWithServer());
            }
        }
        catch (SevereException e2) {
            this.handleSevereException(e2);
        }
        catch (MinorException e3) {
            this.handleMinorException(e3);
        }
    }
    
    public Orientation getCurrentOrientation() {
        return this.currentOrientation;
    }
    
    private void setCurrentOrientation(final Orientation currentOrientation) {
        this.currentOrientation = currentOrientation;
    }
    
    public void syncClientSizeDetailsWithServer() {
        if (this.isConnected()) {
            try {
                this.client.sendClientScreenDetails();
            }
            catch (SevereException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setupClientConnection() {
        this.setupClientConnection(null);
    }
    
    public void setupClientConnection(final Runnable onConnect) {
        if (this.getSettingsPane().getGeneralTab().getConnectDisconnectButton().isDisabled()) {
            return;
        }
        javafx.application.Platform.runLater(() -> this.getSettingsPane().getGeneralTab().setDisableStatus(true));
        this.client = new Client(this.getConfig().getSavedServerHostNameOrIP(), this.getConfig().getSavedServerPort(), this, this, onConnect);
    }
    
    public void updateSettingsConnectDisconnectButton() {
        this.getSettingsPane().getGeneralTab().setConnectDisconnectButtonStatus();
    }
    
    public void disconnect(final String message) throws SevereException {
        this.client.disconnect(message);
    }
    
    public void setupDashWindow() {
        this.getStage().setTitle("Stream-Pi Client");
        this.getStage().setOnCloseRequest(e -> {
            this.onCloseRequest();
            this.exitApp();
        });
    }
    
    public void onCloseRequest() {
        try {
            if (this.isConnected()) {
                this.client.exit();
            }
            if (this.screenSaver != null) {
                this.screenSaver.stop();
            }
            if (this.screenMover != null) {
                this.screenMover.stop();
            }
            if (this.getConfig() != null && !this.getClientInfo().isPhone() && !this.getConfig().getIsFullScreenMode()) {
                this.getConfig().setStartupWindowSize(this.getStageWidth(), this.getStageHeight());
                this.getConfig().save();
            }
        }
        catch (SevereException e) {
            this.handleSevereException(e);
        }
        finally {
            this.getLogger().info("Shut down");
            this.closeLogger();
            Config.nullify();
        }
    }
    
    public void exitApp() {
        this.getExecutor().shutdown();
        if (ClientInfo.getInstance().getPlatform() == Platform.ANDROID) {
            Services.get((Class)LifecycleService.class).ifPresent(LifecycleService::shutdown);
        }
        else {
            javafx.application.Platform.exit();
        }
    }
    
    public void loadSettings() {
        try {
            this.getSettingsPane().getGeneralTab().loadData();
        }
        catch (SevereException e) {
            e.printStackTrace();
            this.handleSevereException(e);
        }
    }
    
    public Client getClient() {
        return this.client;
    }
    
    public void onDisconnect() {
        javafx.application.Platform.runLater(() -> this.getDashboardPane().getActionGridPane().toggleOffAllToggleActions());
    }
    
    public boolean getToggleStatus(final String profileID, final String actionID) {
        return this.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID).getCurrentToggleStatus();
    }
    
    private void setupSettingsWindowsAnimations() {
        final Node settingsNode = (Node)this.getSettingsPane();
        final Node dashboardNode = (Node)this.getDashboardPane();
        (this.openSettingsTimeLine = new Timeline()).setCycleCount(1);
        this.openSettingsTimeLine.getKeyFrames().addAll((Object[])new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)settingsNode.opacityProperty(), (Object)0.0, Interpolator.EASE_IN) }), new KeyFrame(Duration.millis(90.0), new KeyValue[] { new KeyValue((WritableValue)settingsNode.opacityProperty(), (Object)1.0, Interpolator.LINEAR) }), new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)dashboardNode.opacityProperty(), (Object)1.0, Interpolator.LINEAR) }), new KeyFrame(Duration.millis(90.0), new KeyValue[] { new KeyValue((WritableValue)dashboardNode.opacityProperty(), (Object)0.0, Interpolator.LINEAR) }) });
        this.openSettingsTimeLine.setOnFinished(event1 -> settingsNode.toFront());
        (this.closeSettingsTimeLine = new Timeline()).setCycleCount(1);
        this.closeSettingsTimeLine.getKeyFrames().addAll((Object[])new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)settingsNode.opacityProperty(), (Object)1.0, Interpolator.LINEAR) }), new KeyFrame(Duration.millis(90.0), new KeyValue[] { new KeyValue((WritableValue)settingsNode.opacityProperty(), (Object)0.0, Interpolator.LINEAR) }), new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)dashboardNode.opacityProperty(), (Object)0.0, Interpolator.LINEAR) }), new KeyFrame(Duration.millis(90.0), new KeyValue[] { new KeyValue((WritableValue)dashboardNode.opacityProperty(), (Object)1.0, Interpolator.LINEAR) }) });
        this.closeSettingsTimeLine.setOnFinished(event1 -> {
            dashboardNode.toFront();
            javafx.application.Platform.runLater(() -> {
                try {
                    this.getSettingsPane().getGeneralTab().loadData();
                }
                catch (SevereException e) {
                    e.printStackTrace();
                    this.handleSevereException(e);
                }
            });
        });
    }
    
    public void handleMinorException(final MinorException e) {
        this.handleMinorException(e.getMessage(), e);
    }
    
    public void handleMinorException(final String message, final MinorException e) {
        this.getLogger().log(Level.SEVERE, message, (Throwable)e);
        e.printStackTrace();
        javafx.application.Platform.runLater(() -> {
            if (this.getScreenSaver() != null) {
                this.getScreenSaver().restart();
            }
            this.genNewAlert(e.getTitle(), message, StreamPiAlertType.WARNING).show();
        });
    }
    
    public void handleSevereException(final SevereException e) {
        this.handleSevereException(e.getMessage(), e);
    }
    
    public void handleSevereException(final String message, final SevereException e) {
        this.getLogger().log(Level.SEVERE, message, (Throwable)e);
        e.printStackTrace();
        javafx.application.Platform.runLater(() -> {
            if (this.getScreenSaver() != null) {
                this.getScreenSaver().restart();
            }
            final StreamPiAlert alert = this.genNewAlert(e.getTitle(), message, StreamPiAlertType.ERROR);
            alert.setOnClicked((StreamPiAlertListener)new StreamPiAlertListener() {
                public void onClick(final String txt) {
                    Controller.this.onCloseRequest();
                    Controller.this.exitApp();
                }
            });
            alert.show();
        });
    }
    
    public void onAlert(final String title, final String body, final StreamPiAlertType alertType) {
        javafx.application.Platform.runLater(() -> this.genNewAlert(title, body, alertType).show());
    }
    
    public StreamPiAlert genNewAlert(final String title, final String message, final StreamPiAlertType alertType) {
        return new StreamPiAlert(title, message, alertType);
    }
    
    public void onActionFailed(final String profileID, final String actionID) {
        javafx.application.Platform.runLater(() -> this.getDashboardPane().getActionGridPane().actionFailed(profileID, actionID));
    }
    
    public void onActionClicked(final String profileID, final String actionID, final boolean toggleState) {
        try {
            this.vibratePhone();
            this.client.onActionClicked(profileID, actionID, toggleState);
        }
        catch (SevereException e) {
            e.printStackTrace();
            this.handleSevereException(e);
        }
    }
    
    public void vibratePhone() {
        if (this.getConfig().isVibrateOnActionClicked()) {
            VibrationService.create().ifPresent(VibrationService::vibrate);
        }
    }
    
    public void setConnected(final boolean isConnected) {
        this.isConnected = isConnected;
    }
    
    public ActionBox getActionBox(final int col, final int row) {
        return this.getDashboardPane().getActionGridPane().getActionBox(col, row);
    }
    
    public boolean isConnected() {
        return this.isConnected;
    }
    
    public void renderProfile(final ClientProfile clientProfile, final boolean freshRender) {
        this.getDashboardPane().renderProfile(clientProfile, freshRender);
    }
    
    public void clearActionBox(final int col, final int row) {
        javafx.application.Platform.runLater(() -> this.getDashboardPane().getActionGridPane().clearActionBox(col, row));
    }
    
    public void addBlankActionBox(final int col, final int row) {
        javafx.application.Platform.runLater(() -> this.getDashboardPane().getActionGridPane().addBlankActionBox(col, row));
    }
    
    public void renderAction(final String currentProfileID, final Action action) {
        javafx.application.Platform.runLater(() -> {
            try {
                if (this.getDashboardPane().getActionGridPane().getCurrentParent().equals(action.getParent()) && this.getCurrentProfile().getID().equals(currentProfileID)) {
                    this.getDashboardPane().getActionGridPane().renderAction(action);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public void refreshGridIfCurrentProfile(final String profileID) {
        if (this.getCurrentProfile().getID().equals(profileID)) {
            javafx.application.Platform.runLater(() -> this.getDashboardPane().renderProfile(this.getClientProfiles().getProfileFromID(profileID), true));
        }
    }
    
    public ClientProfile getCurrentProfile() {
        return this.getDashboardPane().getActionGridPane().getClientProfile();
    }
    
    public String getCurrentParent() {
        return this.getDashboardPane().getActionGridPane().getCurrentParent();
    }
    
    public ActionBox getActionBoxByProfileAndID(final String profileID, final String actionID) {
        final Action action = this.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);
        if (!this.getCurrentProfile().getID().equals(profileID) && !this.getCurrentParent().equals(action.getParent())) {
            return null;
        }
        return this.getDashboardPane().getActionGridPane().getActionBoxByLocation(action.getLocation());
    }
    
    public void openURL(final String url) {
        if (ClientInfo.getInstance().isPhone()) {
            BrowserService.create().ifPresentOrElse(s -> {
                try {
                    s.launchExternalBrowser(url);
                }
                catch (Exception e) {
                    this.handleMinorException(new MinorException("Cant start browser! You can go to Server Settings > About > Contact and open the links from there."));
                }
            }, () -> this.handleMinorException(new MinorException("Sorry!", "No browser detected. You can go to Server Settings > About > Contact and open the links from there.")));
        }
        else if (this.getClientInfo().getPlatform() == Platform.LINUX && !StartupFlags.IS_X_MODE) {
            this.handleMinorException(new MinorException("Sorry!", "Your system is running directly on framebuffer and does not support opening a browser. You can go to Server Settings > About > Contact and open the links from there."));
        }
        else {
            this.getHostServices().showDocument(url);
        }
    }
    
    public void factoryReset() {
        this.getLogger().info("Reset to factory ...");
        this.onCloseRequest();
        try {
            IOHelper.deleteFile(this.getClientInfo().getPrePath());
            this.setFirstRun(true);
            this.init();
        }
        catch (SevereException e) {
            this.handleSevereException("Unable to successfully factory reset. Delete directory \n'" + this.getClientInfo().getPrePath() + "/home/rnayabed/HDD_1/projects/stream-pi/server'\nMessage:\n" + e.getMessage(), e);
        }
    }
    
    public void setFirstRun(final boolean firstRun) {
        this.firstRun = firstRun;
    }
}
