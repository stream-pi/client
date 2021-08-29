// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.settings;

import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.util.startatboot.StartAtBoot;
import com.stream_pi.client.Main;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.client.io.Config;
import javafx.application.Platform;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.alert.StreamPiAlertListener;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertType;
import javafx.event.Event;
import javafx.event.ActionEvent;
import com.stream_pi.util.checkforupdates.UpdateHyperlinkOnClick;
import com.stream_pi.util.platform.PlatformType;
import com.stream_pi.util.checkforupdates.CheckForUpdates;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.info.ClientInfo;
import javafx.scene.CacheHint;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import com.stream_pi.util.uihelper.SpaceFiller;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import com.stream_pi.util.combobox.StreamPiComboBoxListener;
import com.stream_pi.util.combobox.StreamPiComboBoxFactory;
import java.util.Arrays;
import java.util.List;
import com.stream_pi.util.uihelper.HBoxInputBox;
import java.util.logging.Logger;
import org.controlsfx.control.ToggleSwitch;
import com.stream_pi.util.uihelper.HBoxWithSpaceBetween;
import javafx.scene.control.Button;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.util.combobox.StreamPiComboBox;
import javafx.scene.control.TextField;
import javafx.application.HostServices;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import javafx.scene.layout.VBox;

public class GeneralTab extends VBox
{
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ClientListener clientListener;
    private HostServices hostServices;
    private TextField serverPortTextField;
    private TextField serverHostNameOrIPTextField;
    private TextField screenTimeoutTextField;
    private StreamPiComboBox<ClientProfile> clientProfileComboBox;
    private StreamPiComboBox<Theme> themeComboBox;
    private StreamPiComboBox<String> animationComboBox;
    private TextField nickNameTextField;
    private Button saveButton;
    private Button connectDisconnectButton;
    private Button shutdownButton;
    private HBoxWithSpaceBetween startOnBootHBox;
    private ToggleSwitch startOnBootToggleSwitch;
    private HBoxWithSpaceBetween screenSaverHBox;
    private ToggleSwitch screenSaverToggleSwitch;
    private HBoxWithSpaceBetween screenMoverHBox;
    private ToggleSwitch screenMoverToggleSwitch;
    private HBoxWithSpaceBetween tryConnectingToServerIfActionClickedHBox;
    private ToggleSwitch tryConnectingToServerIfActionClickedToggleSwitch;
    private HBoxWithSpaceBetween connectOnStartupHBox;
    private ToggleSwitch connectOnStartupToggleSwitch;
    private HBoxWithSpaceBetween vibrateOnActionPressHBox;
    private ToggleSwitch vibrateOnActionPressToggleSwitch;
    private HBoxWithSpaceBetween fullScreenModeHBox;
    private ToggleSwitch fullScreenModeToggleSwitch;
    private HBoxWithSpaceBetween showCursorHBox;
    private ToggleSwitch showCursorToggleSwitch;
    private HBoxWithSpaceBetween invertRowsColsHBox;
    private ToggleSwitch invertRowsColsToggleSwitch;
    private TextField themesPathTextField;
    private TextField iconsPathTextField;
    private TextField profilesPathTextField;
    private final Button factoryResetButton;
    private Logger logger;
    private final Button checkForUpdatesButton;
    private HBoxInputBox screenTimeoutSecondsHBoxInputBox;
    private List<String> animationList;
    
    public GeneralTab(final ExceptionAndAlertHandler exceptionAndAlertHandler, final ClientListener clientListener, final HostServices hostServices) {
        this.animationList = Arrays.asList("None", "Bounce", "Flip", "Jack In The Box", "Jello", "Pulse", "RubberBand", "Shake", "Swing", "Tada", "Wobble");
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientListener = clientListener;
        this.hostServices = hostServices;
        this.logger = Logger.getLogger("");
        this.serverPortTextField = new TextField();
        this.screenTimeoutTextField = new TextField();
        this.serverHostNameOrIPTextField = new TextField();
        this.nickNameTextField = new TextField();
        (this.clientProfileComboBox = (StreamPiComboBox<ClientProfile>)new StreamPiComboBox()).setStreamPiComboBoxFactory((StreamPiComboBoxFactory)new StreamPiComboBoxFactory<ClientProfile>() {
            public String getOptionDisplayText(final ClientProfile object) {
                return object.getName();
            }
        });
        (this.animationComboBox = (StreamPiComboBox<String>)new StreamPiComboBox()).setStreamPiComboBoxFactory((StreamPiComboBoxFactory)new StreamPiComboBoxFactory<String>() {
            public String getOptionDisplayText(final String object) {
                return object;
            }
        });
        this.clientProfileComboBox.setStreamPiComboBoxListener((StreamPiComboBoxListener)new StreamPiComboBoxListener<ClientProfile>() {
            public void onNewItemSelected(final ClientProfile selectedItem) {
                clientListener.renderProfile(selectedItem, true);
            }
        });
        (this.themeComboBox = (StreamPiComboBox<Theme>)new StreamPiComboBox()).setStreamPiComboBoxFactory((StreamPiComboBoxFactory)new StreamPiComboBoxFactory<Theme>() {
            public String getOptionDisplayText(final Theme object) {
                return object.getShortName();
            }
        });
        this.themesPathTextField = new TextField();
        this.iconsPathTextField = new TextField();
        this.profilesPathTextField = new TextField();
        this.startOnBootToggleSwitch = new ToggleSwitch();
        this.startOnBootHBox = new HBoxWithSpaceBetween("Start On Boot", (Node)this.startOnBootToggleSwitch);
        this.startOnBootHBox.managedProperty().bind((ObservableValue)this.startOnBootHBox.visibleProperty());
        this.screenSaverToggleSwitch = new ToggleSwitch();
        this.screenSaverHBox = new HBoxWithSpaceBetween("Screen Saver", (Node)this.screenSaverToggleSwitch);
        this.screenSaverHBox.managedProperty().bind((ObservableValue)this.screenSaverHBox.visibleProperty());
        this.screenMoverToggleSwitch = new ToggleSwitch();
        this.screenMoverHBox = new HBoxWithSpaceBetween("OLED Burn-In Protector", (Node)this.screenMoverToggleSwitch);
        this.screenMoverHBox.managedProperty().bind((ObservableValue)this.screenMoverHBox.visibleProperty());
        this.tryConnectingToServerIfActionClickedToggleSwitch = new ToggleSwitch();
        this.tryConnectingToServerIfActionClickedHBox = new HBoxWithSpaceBetween("Try connect to server on action click", (Node)this.tryConnectingToServerIfActionClickedToggleSwitch);
        this.tryConnectingToServerIfActionClickedHBox.managedProperty().bind((ObservableValue)this.tryConnectingToServerIfActionClickedHBox.visibleProperty());
        this.fullScreenModeToggleSwitch = new ToggleSwitch();
        this.fullScreenModeHBox = new HBoxWithSpaceBetween("Full Screen", (Node)this.fullScreenModeToggleSwitch);
        this.fullScreenModeHBox.managedProperty().bind((ObservableValue)this.fullScreenModeHBox.visibleProperty());
        this.vibrateOnActionPressToggleSwitch = new ToggleSwitch();
        this.vibrateOnActionPressHBox = new HBoxWithSpaceBetween("Vibrate On Action Press", (Node)this.vibrateOnActionPressToggleSwitch);
        this.vibrateOnActionPressHBox.managedProperty().bind((ObservableValue)this.vibrateOnActionPressHBox.visibleProperty());
        this.connectOnStartupToggleSwitch = new ToggleSwitch();
        this.connectOnStartupHBox = new HBoxWithSpaceBetween("Connect On Startup", (Node)this.connectOnStartupToggleSwitch);
        this.connectOnStartupHBox.managedProperty().bind((ObservableValue)this.connectOnStartupHBox.visibleProperty());
        this.showCursorToggleSwitch = new ToggleSwitch();
        this.showCursorHBox = new HBoxWithSpaceBetween("Show Cursor", (Node)this.showCursorToggleSwitch);
        this.showCursorHBox.managedProperty().bind((ObservableValue)this.showCursorHBox.visibleProperty());
        this.invertRowsColsToggleSwitch = new ToggleSwitch();
        this.invertRowsColsHBox = new HBoxWithSpaceBetween("Invert Grid on Rotate", (Node)this.invertRowsColsToggleSwitch);
        this.invertRowsColsHBox.managedProperty().bind((ObservableValue)this.invertRowsColsHBox.visibleProperty());
        final int prefWidth = 200;
        final HBoxInputBox themesPathInputBox = new HBoxInputBox("Themes Path", this.themesPathTextField, prefWidth);
        themesPathInputBox.managedProperty().bind((ObservableValue)themesPathInputBox.visibleProperty());
        final HBoxInputBox iconsPathInputBox = new HBoxInputBox("Icons Path", this.iconsPathTextField, prefWidth);
        iconsPathInputBox.managedProperty().bind((ObservableValue)iconsPathInputBox.visibleProperty());
        final HBoxInputBox profilesPathInputBox = new HBoxInputBox("Profiles Path", this.profilesPathTextField, prefWidth);
        profilesPathInputBox.managedProperty().bind((ObservableValue)profilesPathInputBox.visibleProperty());
        (this.checkForUpdatesButton = new Button("Check for updates")).setOnAction(event -> this.checkForUpdates());
        (this.factoryResetButton = new Button("Factory Reset")).setOnAction(actionEvent -> this.onFactoryResetButtonClicked());
        this.screenTimeoutSecondsHBoxInputBox = new HBoxInputBox("Screen Timeout (seconds)", this.screenTimeoutTextField, prefWidth);
        this.screenTimeoutSecondsHBoxInputBox.managedProperty().bind((ObservableValue)this.screenTimeoutSecondsHBoxInputBox.visibleProperty());
        this.screenTimeoutTextField.disableProperty().bind((ObservableValue)this.screenSaverToggleSwitch.selectedProperty().not());
        (this.saveButton = new Button("Save")).setOnAction(event -> this.onSaveButtonClicked());
        (this.connectDisconnectButton = new Button("Connect")).setOnAction(event -> this.onConnectDisconnectButtonClicked());
        final Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> this.onExitButtonClicked());
        final HBox buttonBar = new HBox(new Node[] { (Node)this.connectDisconnectButton, (Node)this.saveButton });
        this.shutdownButton = new Button("Shutdown");
        this.shutdownButton.managedProperty().bind((ObservableValue)this.shutdownButton.visibleProperty());
        this.shutdownButton.setOnAction(event -> this.onShutdownButtonClicked());
        final VBox vBox = new VBox(new Node[] { (Node)this.generateSubHeading("Connection"), (Node)new HBoxInputBox("Device Name", this.nickNameTextField, prefWidth), (Node)new HBoxInputBox("Host Name/IP", this.serverHostNameOrIPTextField, prefWidth), (Node)new HBoxInputBox("Port", this.serverPortTextField, prefWidth), (Node)this.generateSubHeading("Client"), (Node)new HBox(new Node[] { (Node)new Label("Current profile"), (Node)SpaceFiller.horizontal(), (Node)this.clientProfileComboBox }), (Node)new HBox(new Node[] { (Node)new Label("Theme"), (Node)SpaceFiller.horizontal(), (Node)this.themeComboBox }), (Node)new HBox(new Node[] { (Node)new Label("Action Animation"), (Node)SpaceFiller.horizontal(), (Node)this.animationComboBox }), (Node)this.generateSubHeading("Others"), (Node)themesPathInputBox, (Node)iconsPathInputBox, (Node)profilesPathInputBox, (Node)this.screenTimeoutSecondsHBoxInputBox, (Node)this.invertRowsColsHBox, (Node)this.screenSaverHBox, (Node)this.screenMoverHBox, (Node)this.tryConnectingToServerIfActionClickedHBox, (Node)this.fullScreenModeHBox, (Node)this.connectOnStartupHBox, (Node)this.vibrateOnActionPressHBox, (Node)this.startOnBootHBox, (Node)this.showCursorHBox, (Node)this.checkForUpdatesButton, (Node)this.shutdownButton, (Node)this.factoryResetButton });
        vBox.getStyleClass().add((Object)"settings_base_vbox");
        vBox.setSpacing(10.0);
        vBox.setPadding(new Insets(5.0));
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow((Node)scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add((Object)"settings_base_scroll_pane");
        scrollPane.setContent((Node)vBox);
        vBox.setMinWidth(300.0);
        vBox.prefWidthProperty().bind((ObservableValue)scrollPane.widthProperty().subtract(30));
        buttonBar.getStyleClass().add((Object)"settings_button_bar");
        buttonBar.setPadding(new Insets(0.0, 5.0, 5.0, 0.0));
        buttonBar.setSpacing(5.0);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(10.0);
        this.getChildren().addAll((Object[])new Node[] { (Node)scrollPane, (Node)buttonBar });
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
        if (ClientInfo.getInstance().isPhone()) {
            themesPathInputBox.setVisible(false);
            iconsPathInputBox.setVisible(false);
            profilesPathInputBox.setVisible(false);
            this.startOnBootHBox.setVisible(false);
            this.showCursorHBox.setVisible(false);
            this.fullScreenModeHBox.setVisible(false);
            this.shutdownButton.setVisible(false);
        }
        else {
            this.invertRowsColsHBox.setVisible(false);
            this.vibrateOnActionPressHBox.setVisible(false);
            buttonBar.getChildren().add((Object)exitButton);
            this.fullScreenModeHBox.setVisible(StartupFlags.SHOW_FULLSCREEN_TOGGLE_BUTTON);
            this.shutdownButton.setVisible(StartupFlags.IS_SHOW_SHUT_DOWN_BUTTON);
        }
        this.screenSaverHBox.setVisible(StartupFlags.SCREEN_SAVER_FEATURE);
        this.screenTimeoutSecondsHBoxInputBox.setVisible(StartupFlags.SCREEN_SAVER_FEATURE);
    }
    
    private Label generateSubHeading(final String text) {
        final Label label = new Label(text);
        label.getStyleClass().add((Object)"general_settings_sub_heading");
        return label;
    }
    
    private Logger getLogger() {
        return this.logger;
    }
    
    private void checkForUpdates() {
        new CheckForUpdates(this.checkForUpdatesButton, PlatformType.CLIENT, ClientInfo.getInstance().getVersion(), (UpdateHyperlinkOnClick)new UpdateHyperlinkOnClick() {
            public void handle(final ActionEvent actionEvent) {
                if (ClientInfo.getInstance().isPhone()) {
                    GeneralTab.this.clientListener.openURL(this.getURL());
                }
                else {
                    GeneralTab.this.hostServices.showDocument(this.getURL());
                }
            }
        });
    }
    
    private void onFactoryResetButtonClicked() {
        final StreamPiAlert confirmation = new StreamPiAlert("Warning", "Are you sure?\nThis will erase everything.", StreamPiAlertType.WARNING);
        final String yesButton = "Yes";
        final String noButton = "No";
        confirmation.setButtons(new String[] { yesButton, noButton });
        confirmation.setOnClicked((StreamPiAlertListener)new StreamPiAlertListener() {
            public void onClick(final String s) {
                if (s.equals(yesButton)) {
                    GeneralTab.this.clientListener.factoryReset();
                }
            }
        });
        confirmation.show();
    }
    
    public void onExitButtonClicked() {
        this.clientListener.onCloseRequest();
        this.clientListener.exitApp();
    }
    
    public void setDisableStatus(final boolean status) {
        this.saveButton.setDisable(status);
        this.connectDisconnectButton.setDisable(status);
    }
    
    public Button getConnectDisconnectButton() {
        return this.connectDisconnectButton;
    }
    
    public void onShutdownButtonClicked() {
        this.clientListener.onCloseRequest();
        try {
            Runtime.getRuntime().exec("sudo halt");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void onConnectDisconnectButtonClicked() {
        try {
            if (this.clientListener.isConnected()) {
                this.clientListener.disconnect("Client disconnected from settings");
            }
            else {
                this.clientListener.setupClientConnection();
            }
        }
        catch (SevereException e) {
            e.printStackTrace();
            this.exceptionAndAlertHandler.handleSevereException(e);
        }
    }
    
    public void setConnectDisconnectButtonStatus() {
        Platform.runLater(() -> {
            this.setDisableStatus(false);
            if (this.clientListener.isConnected()) {
                this.connectDisconnectButton.setText("Disconnect");
            }
            else {
                this.connectDisconnectButton.setText("Connect");
            }
        });
    }
    
    public void loadData() throws SevereException {
        final Config config = Config.getInstance();
        this.nickNameTextField.setText(config.getClientNickName());
        this.serverHostNameOrIPTextField.setText(config.getSavedServerHostNameOrIP());
        this.serverPortTextField.setText("" + config.getSavedServerPort());
        this.screenTimeoutTextField.setText("" + config.getScreenSaverTimeout());
        this.screenSaverToggleSwitch.setSelected(config.isScreenSaverEnabled());
        this.screenMoverToggleSwitch.setSelected(config.isScreenMoverEnabled());
        this.clientProfileComboBox.setOptions((List)this.clientListener.getClientProfiles().getClientProfiles());
        this.animationComboBox.setOptions((List)this.animationList);
        int ind = 0;
        for (int i = 0; i < this.clientProfileComboBox.getOptions().size(); ++i) {
            if (this.clientProfileComboBox.getOptions().get(i).getID().equals(this.clientListener.getCurrentProfile().getID())) {
                ind = i;
                break;
            }
        }
        this.clientProfileComboBox.setCurrentSelectedItemIndex(ind);
        this.themeComboBox.setOptions(this.clientListener.getThemes().getThemeList());
        int ind2 = 0;
        for (int j = 0; j < this.themeComboBox.getOptions().size(); ++j) {
            if (this.themeComboBox.getOptions().get(j).getFullName().equals(this.clientListener.getCurrentTheme().getFullName())) {
                ind2 = j;
                break;
            }
        }
        int ind3 = 0;
        for (int k = 0; k < this.animationComboBox.getOptions().size(); ++k) {
            if (this.animationComboBox.getOptions().get(k).equals(config.getCurrentAnimationName())) {
                ind3 = k;
                break;
            }
        }
        this.themeComboBox.setCurrentSelectedItemIndex(ind2);
        this.animationComboBox.setCurrentSelectedItemIndex(ind3);
        this.themesPathTextField.setText(config.getThemesPath());
        this.iconsPathTextField.setText(config.getIconsPath());
        this.profilesPathTextField.setText(config.getProfilesPath());
        this.startOnBootToggleSwitch.setSelected(config.isStartOnBoot());
        this.fullScreenModeToggleSwitch.setSelected(config.getIsFullScreenMode());
        this.showCursorToggleSwitch.setSelected(config.isShowCursor());
        this.connectOnStartupToggleSwitch.setSelected(config.isConnectOnStartup());
        this.vibrateOnActionPressToggleSwitch.setSelected(config.isVibrateOnActionClicked());
        this.tryConnectingToServerIfActionClickedToggleSwitch.setSelected(config.isTryConnectingWhenActionClicked());
        this.invertRowsColsToggleSwitch.setSelected(config.isInvertRowsColsOnDeviceRotate());
    }
    
    public void onSaveButtonClicked() {
        final StringBuilder errors = new StringBuilder();
        int port = -1;
        try {
            port = Integer.parseInt(this.serverPortTextField.getText());
            if (port < 1024) {
                errors.append("* Server Port should be above 1024.\n");
            }
            else if (port > 65535) {
                errors.append("* Server Port must be lesser than 65535\n");
            }
        }
        catch (NumberFormatException exception) {
            errors.append("* Server Port should be a number.\n");
        }
        int screenSaverTimeout = -1;
        try {
            screenSaverTimeout = Integer.parseInt(this.serverPortTextField.getText());
            if (screenSaverTimeout < 15) {
                errors.append("* Screen Timeout cannot be below 15 seconds.\n");
            }
        }
        catch (NumberFormatException exception2) {
            errors.append("* Screen Timeout should be a number.\n");
        }
        if (this.serverHostNameOrIPTextField.getText().isBlank()) {
            errors.append("* Server IP cannot be empty.\n");
        }
        if (this.nickNameTextField.getText().isBlank()) {
            errors.append("* Nick name cannot be blank.\n");
        }
        else if (this.nickNameTextField.getText().equals("about maker")) {
            new StreamPiAlert("\u0915\u093f\u0938\u0928\u0947 \u092c\u0928\u093e\u092f\u093e ? / \u0995\u09c7 \u09ac\u09be\u09a8\u09bf\u09df\u09c7\u099b\u09c7 ?", "ZGViYXlhbiAtIGluZGlh\nboka XD").show();
        }
        else if (this.nickNameTextField.getText().equals("imachonk")) {
            new StreamPiAlert("bigquimo is mega chonk", "i cant stop sweating lol").show();
        }
        if (!errors.toString().isEmpty()) {
            this.exceptionAndAlertHandler.handleMinorException(new MinorException("You made mistakes", "Please fix the errors and try again :\n" + errors.toString()));
            return;
        }
        try {
            boolean toBeReloaded = false;
            boolean syncWithServer = false;
            final Config config = Config.getInstance();
            if (!config.getCurrentThemeFullName().equals(((Theme)this.themeComboBox.getCurrentSelectedItem()).getFullName())) {
                syncWithServer = true;
                try {
                    config.setCurrentThemeFullName(((Theme)this.themeComboBox.getCurrentSelectedItem()).getFullName());
                    config.save();
                    this.clientListener.initThemes();
                }
                catch (SevereException e) {
                    this.exceptionAndAlertHandler.handleSevereException(e);
                }
            }
            if (!config.getCurrentAnimationName().equals(this.animationComboBox.getCurrentSelectedItem())) {
                syncWithServer = true;
                try {
                    config.setCurrentAnimationFullName((String)this.animationComboBox.getCurrentSelectedItem());
                    config.save();
                }
                catch (SevereException e) {
                    this.exceptionAndAlertHandler.handleSevereException(e);
                }
            }
            if (!config.getClientNickName().equals(this.nickNameTextField.getText())) {
                syncWithServer = true;
            }
            config.setNickName(this.nickNameTextField.getText());
            if (port != config.getSavedServerPort() || !this.serverHostNameOrIPTextField.getText().equals(config.getSavedServerHostNameOrIP())) {
                syncWithServer = true;
            }
            config.setServerPort(port);
            config.setServerHostNameOrIP(this.serverHostNameOrIPTextField.getText());
            final boolean isFullScreen = this.fullScreenModeToggleSwitch.isSelected();
            if (config.getIsFullScreenMode() != isFullScreen) {
                toBeReloaded = true;
            }
            config.setIsFullScreenMode(isFullScreen);
            config.setTryConnectingWhenActionClicked(this.tryConnectingToServerIfActionClickedToggleSwitch.isSelected());
            boolean startOnBoot = this.startOnBootToggleSwitch.isSelected();
            if (config.isStartOnBoot() != startOnBoot) {
                final StartAtBoot startAtBoot = new StartAtBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform(), Main.class.getProtectionDomain().getCodeSource().getLocation(), StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION);
                if (startOnBoot) {
                    try {
                        startAtBoot.create(StartupFlags.RUNNER_FILE_NAME, StartupFlags.IS_X_MODE);
                        config.setStartupIsXMode(StartupFlags.IS_X_MODE);
                    }
                    catch (MinorException e2) {
                        this.exceptionAndAlertHandler.handleMinorException(e2);
                        startOnBoot = false;
                    }
                }
                else {
                    final boolean result = startAtBoot.delete();
                    if (!result) {
                        new StreamPiAlert("Uh Oh!", "Unable to delete starter file", StreamPiAlertType.ERROR).show();
                    }
                }
            }
            config.setStartOnBoot(startOnBoot);
            if (!config.isShowCursor() == this.showCursorToggleSwitch.isSelected()) {
                toBeReloaded = true;
            }
            config.setShowCursor(this.showCursorToggleSwitch.isSelected());
            if (!config.getThemesPath().equals(this.themesPathTextField.getText())) {
                toBeReloaded = true;
            }
            config.setThemesPath(this.themesPathTextField.getText());
            if (!config.getIconsPath().equals(this.iconsPathTextField.getText())) {
                toBeReloaded = true;
            }
            config.setIconsPath(this.iconsPathTextField.getText());
            if (!config.getProfilesPath().equals(this.profilesPathTextField.getText())) {
                toBeReloaded = true;
            }
            config.setProfilesPath(this.profilesPathTextField.getText());
            if (config.isScreenSaverEnabled() != this.screenSaverToggleSwitch.isSelected()) {
                toBeReloaded = true;
            }
            config.setScreenSaverEnabled(this.screenSaverToggleSwitch.isSelected());
            if (config.isScreenMoverEnabled() != this.screenMoverToggleSwitch.isSelected()) {
                toBeReloaded = true;
            }
            config.setScreenMoverEnabled(this.screenMoverToggleSwitch.isSelected());
            if (!("" + screenSaverTimeout).equals(this.screenTimeoutTextField.getText()) && config.isScreenSaverEnabled()) {
                config.setScreenSaverTimeout(this.screenTimeoutTextField.getText());
                this.clientListener.getScreenSaver().setTimeout(config.getScreenSaverTimeout());
                this.clientListener.getScreenSaver().restartTimer();
            }
            config.setConnectOnStartup(this.connectOnStartupToggleSwitch.isSelected());
            boolean isVibrateOnActionClicked = this.vibrateOnActionPressToggleSwitch.isSelected();
            if (config.isVibrateOnActionClicked() != isVibrateOnActionClicked && isVibrateOnActionClicked && VibrationService.create().isEmpty()) {
                isVibrateOnActionClicked = false;
                new StreamPiAlert("Uh Oh!", "Vibration not supported", StreamPiAlertType.ERROR).show();
            }
            config.setVibrateOnActionClicked(isVibrateOnActionClicked);
            config.setInvertRowsColsOnDeviceRotate(this.invertRowsColsToggleSwitch.isSelected());
            config.save();
            this.loadData();
            if (syncWithServer && this.clientListener.isConnected()) {
                this.clientListener.getClient().updateClientDetails();
            }
            if (toBeReloaded) {
                if (!ClientInfo.getInstance().isPhone() && !config.getIsFullScreenMode()) {
                    config.setStartupWindowSize(this.clientListener.getStageWidth(), this.clientListener.getStageHeight());
                    config.save();
                }
                this.clientListener.init();
                this.clientListener.renderRootDefaultProfile();
            }
        }
        catch (SevereException e3) {
            e3.printStackTrace();
            this.exceptionAndAlertHandler.handleSevereException(e3);
        }
        catch (MinorException e4) {
            e4.printStackTrace();
            this.exceptionAndAlertHandler.handleMinorException(e4);
        }
    }
}
