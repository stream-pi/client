package com.stream_pi.client.window.settings;

import com.gluonhq.attach.browser.BrowserService;
import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.checkforupdates.CheckForUpdates;
import com.stream_pi.util.checkforupdates.UpdateHyperlinkOnClick;
import com.stream_pi.util.combobox.StreamPiComboBox;
import com.stream_pi.util.combobox.StreamPiComboBoxFactory;
import com.stream_pi.util.combobox.StreamPiComboBoxListener;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.platform.PlatformType;
import com.stream_pi.util.startatboot.StartAtBoot;
import com.stream_pi.util.uihelper.HBoxInputBox;
import com.stream_pi.util.uihelper.HBoxWithSpaceBetween;
import com.stream_pi.util.uihelper.SpaceFiller;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.w3c.dom.Text;

import java.io.File;

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

    private TextField nickNameTextField;

    private Button saveButton;
    private Button connectDisconnectButton;
    private Button shutdownButton;

    private HBoxWithSpaceBetween startOnBootHBox;
    private ToggleSwitch startOnBootToggleSwitch;

    private HBoxWithSpaceBetween screenSaverHBox;
    private ToggleSwitch screenSaverToggleSwitch;

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

    private TextField themesPathTextField;
    private TextField iconsPathTextField;
    private TextField profilesPathTextField;


    private final Button checkForUpdatesButton;

    private HBoxInputBox screenTimeoutSecondsHBoxInputBox;

    public GeneralTab(ExceptionAndAlertHandler exceptionAndAlertHandler,
                      ClientListener clientListener, HostServices hostServices)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientListener = clientListener;
        this.hostServices = hostServices;

        serverPortTextField = new TextField();
        screenTimeoutTextField = new TextField();


        serverHostNameOrIPTextField = new TextField();
        nickNameTextField = new TextField();

        clientProfileComboBox = new StreamPiComboBox<>();

        clientProfileComboBox.setStreamPiComboBoxFactory(new StreamPiComboBoxFactory<ClientProfile>()
        {
            @Override
            public String getOptionDisplayText(ClientProfile object)
            {
                return object.getName();
            }
        });

        clientProfileComboBox.setStreamPiComboBoxListener(new StreamPiComboBoxListener<ClientProfile>(){
            @Override
            public void onNewItemSelected(ClientProfile selectedItem)
            {
                clientListener.renderProfile(selectedItem, true);
            }
        });


        themeComboBox = new StreamPiComboBox<>();
        themeComboBox.setStreamPiComboBoxFactory(new StreamPiComboBoxFactory<Theme>()
        {
            @Override
            public String getOptionDisplayText(Theme object)
            {
                return object.getShortName();
            }
        });

        themesPathTextField = new TextField();
        iconsPathTextField = new TextField();
        profilesPathTextField = new TextField();

        startOnBootToggleSwitch = new ToggleSwitch();
        startOnBootHBox = new HBoxWithSpaceBetween("Start On Boot", startOnBootToggleSwitch);
        startOnBootHBox.managedProperty().bind(startOnBootHBox.visibleProperty());

        screenSaverToggleSwitch = new ToggleSwitch();
        screenSaverHBox = new HBoxWithSpaceBetween("Screen Saver", screenSaverToggleSwitch);
        screenSaverHBox.managedProperty().bind(screenSaverHBox.visibleProperty());

        tryConnectingToServerIfActionClickedToggleSwitch  = new ToggleSwitch();
        tryConnectingToServerIfActionClickedHBox = new HBoxWithSpaceBetween("Try connect to server on action click", tryConnectingToServerIfActionClickedToggleSwitch);
        tryConnectingToServerIfActionClickedHBox.managedProperty().bind(tryConnectingToServerIfActionClickedHBox.visibleProperty());

        fullScreenModeToggleSwitch = new ToggleSwitch();
        fullScreenModeHBox = new HBoxWithSpaceBetween("Full Screen", fullScreenModeToggleSwitch);
        fullScreenModeHBox.managedProperty().bind(fullScreenModeHBox.visibleProperty());

        vibrateOnActionPressToggleSwitch = new ToggleSwitch();
        vibrateOnActionPressHBox = new HBoxWithSpaceBetween("Vibrate On Action Press", vibrateOnActionPressToggleSwitch);
        vibrateOnActionPressHBox.managedProperty().bind(vibrateOnActionPressHBox.visibleProperty());

        connectOnStartupToggleSwitch = new ToggleSwitch();
        connectOnStartupHBox = new HBoxWithSpaceBetween("Connect On Startup", connectOnStartupToggleSwitch);
        connectOnStartupHBox.managedProperty().bind(connectOnStartupHBox.visibleProperty());

        showCursorToggleSwitch = new ToggleSwitch();
        showCursorHBox = new HBoxWithSpaceBetween("Show Cursor", showCursorToggleSwitch);
        showCursorHBox.managedProperty().bind(showCursorHBox.visibleProperty());

        int prefWidth = 200;

        HBoxInputBox themesPathInputBox = new HBoxInputBox("Themes Path", themesPathTextField, prefWidth);
        themesPathInputBox.managedProperty().bind(themesPathInputBox.visibleProperty());


        HBoxInputBox iconsPathInputBox = new HBoxInputBox("Icons Path", iconsPathTextField, prefWidth);
        iconsPathInputBox.managedProperty().bind(iconsPathInputBox.visibleProperty());


        HBoxInputBox profilesPathInputBox = new HBoxInputBox("Profiles Path", profilesPathTextField, prefWidth);
        profilesPathInputBox.managedProperty().bind(profilesPathInputBox.visibleProperty());

        checkForUpdatesButton = new Button("Check for updates");
        checkForUpdatesButton.setOnAction(event->checkForUpdates());


        screenTimeoutSecondsHBoxInputBox = new HBoxInputBox("Screen Timeout (seconds)", screenTimeoutTextField, prefWidth);
        screenTimeoutSecondsHBoxInputBox.managedProperty().bind(screenTimeoutSecondsHBoxInputBox.visibleProperty());

        VBox vBox = new VBox(
                new HBoxInputBox("Device Name", nickNameTextField, prefWidth),
                new HBoxInputBox("Host Name/IP", serverHostNameOrIPTextField, prefWidth),
                new HBoxInputBox("Port", serverPortTextField, prefWidth),
                new HBox(
                        new Label("Current profile"),
                        SpaceFiller.horizontal(),
                        clientProfileComboBox
                ),
                new HBox(
                        new Label("Theme"),
                        SpaceFiller.horizontal(),
                        themeComboBox
                ),
                themesPathInputBox,
                iconsPathInputBox,
                profilesPathInputBox,
                screenTimeoutSecondsHBoxInputBox
        );


        vBox.getStyleClass().add("settings_base_vbox");

        vBox.setSpacing(10.0);
        vBox.setPadding(new Insets(5));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("settings_base_scroll_pane");
        scrollPane.setContent(vBox);

        vBox.setMinWidth(300);

        vBox.prefWidthProperty().bind(scrollPane.widthProperty().subtract(25));


        Label settingsLabel = new Label("Settings");
        settingsLabel.setPadding(new Insets(5,0,0,5));
        settingsLabel.getStyleClass().add("settings_heading_label");

        saveButton = new Button("Save");
        saveButton.setOnAction(event->onSaveButtonClicked());

        connectDisconnectButton = new Button("Connect");
        connectDisconnectButton.setOnAction(event -> onConnectDisconnectButtonClicked());


        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> onExitButtonClicked());

        HBox buttonBar = new HBox(connectDisconnectButton, saveButton);

        shutdownButton = new Button("Shutdown");
        shutdownButton.managedProperty().bind(shutdownButton.visibleProperty());
        shutdownButton.setOnAction(event -> onShutdownButtonClicked());


        vBox.getChildren().addAll(
                tryConnectingToServerIfActionClickedHBox,
                fullScreenModeHBox,
                connectOnStartupHBox,
                vibrateOnActionPressHBox,
                screenSaverHBox,
                startOnBootHBox,
                showCursorHBox,
                checkForUpdatesButton,
                shutdownButton
        );



        buttonBar.getStyleClass().add("settings_button_bar");


        buttonBar.setPadding(new Insets(0,5,5,0));
        buttonBar.setSpacing(5.0);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        setSpacing(10.0);

        getChildren().addAll(
                settingsLabel,
                scrollPane,
                buttonBar
        );

        setCache(true);
        setCacheHint(CacheHint.SPEED);


        //Perform platform checks

        Platform platform = ClientInfo.getInstance().getPlatform();

        if(platform == Platform.LINUX)
        {
            fullScreenModeHBox.setVisible(StartupFlags.IS_X_MODE);
        }

        if(platform == Platform.ANDROID ||
                platform == Platform.IOS)
        {
            themesPathInputBox.setVisible(false);
            iconsPathInputBox.setVisible(false);
            profilesPathInputBox.setVisible(false);

            startOnBootHBox.setVisible(false);
            showCursorHBox.setVisible(false);
            fullScreenModeHBox.setVisible(false);
            shutdownButton.setVisible(false);
        }
        else
        {
            if(!StartupFlags.IS_SHOW_SHUT_DOWN_BUTTON)
            {
                shutdownButton.setVisible(false);
            }

            vibrateOnActionPressHBox.setVisible(false);

            fullScreenModeHBox.setVisible(ClientInfo.getInstance().isShowFullScreenToggleButton());

            buttonBar.getChildren().add(exitButton);
        }


        screenTimeoutSecondsHBoxInputBox.setVisible(ClientInfo.getInstance().isShowFullScreenToggleButton());
        screenSaverHBox.setVisible(StartupFlags.SCREEN_SAVER_FEATURE);

    }

    private void checkForUpdates()
    {
        new CheckForUpdates(checkForUpdatesButton,
                PlatformType.CLIENT, ClientInfo.getInstance().getVersion(), new UpdateHyperlinkOnClick() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(ClientInfo.getInstance().isPhone())
                {
                    clientListener.openURL(getURL());
                }
                else
                {
                    hostServices.showDocument(getURL());
                }
            }
        });
    }


    public void onExitButtonClicked()
    {
        clientListener.onCloseRequest();
        javafx.application.Platform.exit();
    }

    public void setDisableStatus(boolean status)
    {
        saveButton.setDisable(status);
        connectDisconnectButton.setDisable(status);
    }

    public Button getConnectDisconnectButton()
    {
        return connectDisconnectButton;
    }

    public void onShutdownButtonClicked()
    {
        clientListener.onCloseRequest();
        try {
            Runtime.getRuntime().exec("sudo halt");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onConnectDisconnectButtonClicked()
    {
        try
        {
            if(clientListener.isConnected())
                clientListener.disconnect("Client disconnected from settings");
            else
                clientListener.setupClientConnection();
        }
        catch (SevereException e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleSevereException(e);
        }
    }

    public void setConnectDisconnectButtonStatus()
    {
        javafx.application.Platform.runLater(()->{
            setDisableStatus(false);

            if(clientListener.isConnected())
                connectDisconnectButton.setText("Disconnect");
            else
                connectDisconnectButton.setText("Connect");
        });

    }

    public void loadData() throws SevereException
    {
        Config config = Config.getInstance();

        nickNameTextField.setText(config.getClientNickName());

        serverHostNameOrIPTextField.setText(config.getSavedServerHostNameOrIP());
        serverPortTextField.setText(config.getSavedServerPort()+"");

        screenTimeoutTextField.setText(config.getScreenSaverTimeout()+"");
        screenSaverToggleSwitch.setSelected(config.isScreenSaverEnabled());

        screenTimeoutSecondsHBoxInputBox.setVisible(config.isScreenSaverEnabled());

        clientProfileComboBox.setOptions(clientListener.getClientProfiles().getClientProfiles());

        int ind = 0;
        for(int i = 0;i<clientProfileComboBox.getOptions().size();i++)
        {
            if(clientProfileComboBox.getOptions().get(i).getID().equals(clientListener.getCurrentProfile().getID()))
            {
                ind = i;
                break;
            }
        }

        clientProfileComboBox.setCurrentSelectedItemIndex(ind);

        themeComboBox.setOptions(clientListener.getThemes().getThemeList());

        int ind2 = 0;
        for(int i = 0;i<themeComboBox.getOptions().size();i++)
        {
            if(themeComboBox.getOptions().get(i).getFullName().equals(clientListener.getCurrentTheme().getFullName()))
            {
                ind2 = i;
                break;
            }
        }

        themeComboBox.setCurrentSelectedItemIndex(ind2);

        themesPathTextField.setText(config.getThemesPath());
        iconsPathTextField.setText(config.getIconsPath());
        profilesPathTextField.setText(config.getProfilesPath());

        startOnBootToggleSwitch.setSelected(config.isStartOnBoot());
        fullScreenModeToggleSwitch.setSelected(config.getIsFullScreenMode());

        showCursorToggleSwitch.setSelected(config.isShowCursor());

        connectOnStartupToggleSwitch.setSelected(config.isConnectOnStartup());
        vibrateOnActionPressToggleSwitch.setSelected(config.isVibrateOnActionClicked());
        tryConnectingToServerIfActionClickedToggleSwitch.setSelected(config.isTryConnectingWhenActionClicked());
    }

    public void onSaveButtonClicked()
    {
        StringBuilder errors = new StringBuilder();

        int port = -1;
        try
        {
            port = Integer.parseInt(serverPortTextField.getText());

            if(port < 1024)
                errors.append("* Server Port should be above 1024.\n");
            else if(port > 65535)
                errors.append("* Server Port must be lesser than 65535\n");
        }
        catch (NumberFormatException exception)
        {
            errors.append("* Server Port should be a number.\n");
        }


        int screenSaverTimeout = -1;
        try
        {
            screenSaverTimeout = Integer.parseInt(serverPortTextField.getText());

            if(screenSaverTimeout < 15)
                errors.append("* Screen Timeout cannot be below 15 seconds.\n");
        }
        catch (NumberFormatException exception)
        {
            errors.append("* Screen Timeout should be a number.\n");
        }


        if(serverHostNameOrIPTextField.getText().isBlank())
        {
            errors.append("* Server IP cannot be empty.\n");
        }

        if(nickNameTextField.getText().isBlank())
        {
            errors.append("* Nick name cannot be blank.\n");
        }


        if(!errors.toString().isEmpty())
        {
            exceptionAndAlertHandler.handleMinorException(new MinorException(
                    "You made mistakes",
                    "Please fix the errors and try again :\n"+errors.toString()
            ));
            return;
        }



        try {
            boolean toBeReloaded = false;

            boolean syncWithServer = false;

            Config config = Config.getInstance();

            if(!config.getCurrentThemeFullName().equals(themeComboBox.getCurrentSelectedItem().getFullName()))
            {
                syncWithServer = true;
                toBeReloaded = true;
            }

            config.setCurrentThemeFullName(themeComboBox.getCurrentSelectedItem().getFullName());

            if(!config.getClientNickName().equals(nickNameTextField.getText()))
            {
                syncWithServer = true;
            }

            config.setNickName(nickNameTextField.getText());

            if(port != config.getSavedServerPort() || !serverHostNameOrIPTextField.getText().equals(config.getSavedServerHostNameOrIP()))
            {
                syncWithServer = true;
            }

            config.setServerPort(port);
            config.setServerHostNameOrIP(serverHostNameOrIPTextField.getText());

            boolean isFullScreen = fullScreenModeToggleSwitch.isSelected();

            if(config.getIsFullScreenMode() != isFullScreen)
            {
                toBeReloaded = true;
            }

            config.setIsFullScreenMode(isFullScreen);



            config.setTryConnectingWhenActionClicked(tryConnectingToServerIfActionClickedToggleSwitch.isSelected());



            boolean startOnBoot = startOnBootToggleSwitch.isSelected();

            if(config.isStartOnBoot() != startOnBoot)
            {
                if(StartupFlags.RUNNER_FILE_NAME == null)
                {
                    new StreamPiAlert("Uh Oh", "No Runner File Name Specified as startup arguments. Cant set run at boot.", StreamPiAlertType.ERROR).show();
                    startOnBoot = false;
                }
                else
                {
                    StartAtBoot startAtBoot = new StartAtBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform());
                    if(startOnBoot)
                    {
                        startAtBoot.create(new File(StartupFlags.RUNNER_FILE_NAME),
                                StartupFlags.IS_X_MODE);

                        config.setStartupIsXMode(StartupFlags.IS_X_MODE);
                    }
                    else
                    {
                        boolean result = startAtBoot.delete();
                        if(!result)
                            new StreamPiAlert("Uh Oh!", "Unable to delete starter file", StreamPiAlertType.ERROR).show();
                    }
                }
            }

            config.setStartOnBoot(startOnBoot);

            config.setShowCursor(showCursorToggleSwitch.isSelected());



            if(!config.getThemesPath().equals(themesPathTextField.getText()))
                toBeReloaded = true;

            config.setThemesPath(themesPathTextField.getText());


            if(!config.getIconsPath().equals(iconsPathTextField.getText()))
                toBeReloaded = true;

            config.setIconsPath(iconsPathTextField.getText());

            if(!config.getProfilesPath().equals(profilesPathTextField.getText()))
                toBeReloaded = true;

            config.setProfilesPath(profilesPathTextField.getText());

            if(config.isScreenSaverEnabled() != screenSaverToggleSwitch.isSelected())
                toBeReloaded = true;

            config.setScreenSaverEnabled(screenSaverToggleSwitch.isSelected());

            if(!(screenSaverTimeout+"").equals(screenTimeoutTextField.getText()))
                toBeReloaded = true;

            config.setScreenSaverTimeout(screenTimeoutTextField.getText());

            config.setConnectOnStartup(connectOnStartupToggleSwitch.isSelected());

            boolean isVibrateOnActionClicked = vibrateOnActionPressToggleSwitch.isSelected();

            if(config.isVibrateOnActionClicked() != isVibrateOnActionClicked && isVibrateOnActionClicked)
            {
                if(VibrationService.create().isEmpty())
                {
                    isVibrateOnActionClicked = false;
                    new StreamPiAlert("Uh Oh!", "Vibration not supported", StreamPiAlertType.ERROR).show();
                }
            }

            config.setVibrateOnActionClicked(isVibrateOnActionClicked);


            config.save();

            loadData();


            if(syncWithServer)
            {
                if(clientListener.isConnected())
                {
                    clientListener.getClient().updateClientDetails();
                }
            }

            if(toBeReloaded)
            {
                clientListener.init();
                clientListener.renderRootDefaultProfile();
            }
        }
        catch (SevereException e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleSevereException(e);
        }
        catch (MinorException e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleMinorException(e);
        }
    }

}
