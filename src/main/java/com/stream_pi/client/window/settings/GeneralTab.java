package com.stream_pi.client.window.settings;

import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.client.Main;
import com.stream_pi.client.combobox.LanguageChooserComboBox;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertButton;
import com.stream_pi.util.alert.StreamPiAlertListener;
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
import com.stream_pi.util.startonboot.StartOnBoot;
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

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Logger;

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

    private TextField nameTextField;

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

    private final LanguageChooserComboBox languageChooserComboBox;

    private final Button factoryResetButton;

    private Logger logger;


    private final Button checkForUpdatesButton;

    private HBoxInputBox screenTimeoutSecondsHBoxInputBox;

    public GeneralTab(ExceptionAndAlertHandler exceptionAndAlertHandler,
                      ClientListener clientListener, HostServices hostServices)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientListener = clientListener;
        this.hostServices = hostServices;

        logger = Logger.getLogger("");

        serverPortTextField = new TextField();
        screenTimeoutTextField = new TextField();


        serverHostNameOrIPTextField = new TextField();
        nameTextField = new TextField();

        clientProfileComboBox = new StreamPiComboBox<>();

        clientProfileComboBox.setStreamPiComboBoxFactory(new StreamPiComboBoxFactory<>()
        {
            @Override
            public String getOptionDisplayText(ClientProfile object)
            {
                return object.getName();
            }
        });

        clientProfileComboBox.setStreamPiComboBoxListener(new StreamPiComboBoxListener<>(){
            @Override
            public void onNewItemSelected(ClientProfile oldProfile, ClientProfile newProfile)
            {
                if(oldProfile != newProfile)
                {
                    clientListener.renderProfile(newProfile, true);
                }
            }
        });


        languageChooserComboBox = new LanguageChooserComboBox();

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

        screenMoverToggleSwitch = new ToggleSwitch();
        screenMoverHBox = new HBoxWithSpaceBetween("OLED Burn-In Protector", screenMoverToggleSwitch);
        screenMoverHBox.managedProperty().bind(screenMoverHBox.visibleProperty());

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

        invertRowsColsToggleSwitch = new ToggleSwitch();
        invertRowsColsHBox = new HBoxWithSpaceBetween("Invert Grid on Rotate", invertRowsColsToggleSwitch);
        invertRowsColsHBox.managedProperty().bind(invertRowsColsHBox.visibleProperty());

        int prefWidth = 200;

        HBoxInputBox themesPathInputBox = new HBoxInputBox("Themes", themesPathTextField, prefWidth);
        themesPathInputBox.managedProperty().bind(themesPathInputBox.visibleProperty());


        HBoxInputBox iconsPathInputBox = new HBoxInputBox("Icons", iconsPathTextField, prefWidth);
        iconsPathInputBox.managedProperty().bind(iconsPathInputBox.visibleProperty());


        HBoxInputBox profilesPathInputBox = new HBoxInputBox("Profiles", profilesPathTextField, prefWidth);
        profilesPathInputBox.managedProperty().bind(profilesPathInputBox.visibleProperty());

        checkForUpdatesButton = new Button("Check for updates");
        checkForUpdatesButton.setOnAction(event->checkForUpdates());

        factoryResetButton = new Button("Factory Reset");
        factoryResetButton.setOnAction(actionEvent -> onFactoryResetButtonClicked());


        screenTimeoutSecondsHBoxInputBox = new HBoxInputBox("Screen Timeout (seconds)", screenTimeoutTextField, prefWidth);
        screenTimeoutSecondsHBoxInputBox.managedProperty().bind(screenTimeoutSecondsHBoxInputBox.visibleProperty());
        screenTimeoutTextField.disableProperty().bind(screenSaverToggleSwitch.selectedProperty().not());

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


        VBox vBox = new VBox(
                generateSubHeading("Connection"),
                new HBoxInputBox("Name", nameTextField, prefWidth),
                new HBoxInputBox("Host Name/IP", serverHostNameOrIPTextField, prefWidth),
                new HBoxInputBox("Port", serverPortTextField, prefWidth),
                generateSubHeading("Client"),
                new HBoxWithSpaceBetween("Current Profile", clientProfileComboBox),
                new HBoxWithSpaceBetween("Theme", themeComboBox),
                generateSubHeading("Locations"),
                themesPathInputBox,
                iconsPathInputBox,
                profilesPathInputBox,
                generateSubHeading("Others"),
                new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralSettings.language"), languageChooserComboBox),
                screenTimeoutSecondsHBoxInputBox,
                invertRowsColsHBox,
                screenSaverHBox,
                screenMoverHBox,
                tryConnectingToServerIfActionClickedHBox,
                fullScreenModeHBox,
                connectOnStartupHBox,
                vibrateOnActionPressHBox,
                startOnBootHBox,
                showCursorHBox,
                checkForUpdatesButton,
                shutdownButton,
                factoryResetButton
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

        vBox.prefWidthProperty().bind(scrollPane.widthProperty().subtract(30));


        buttonBar.getStyleClass().add("settings_button_bar");


        buttonBar.setPadding(new Insets(0,5,5,0));
        buttonBar.setSpacing(5.0);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        setSpacing(10.0);

        getChildren().addAll(
                scrollPane,
                buttonBar
        );

        setCache(true);
        setCacheHint(CacheHint.SPEED);


        //Perform platform checks

        if(ClientInfo.getInstance().isPhone())
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
            invertRowsColsHBox.setVisible(false);
            vibrateOnActionPressHBox.setVisible(false);
            buttonBar.getChildren().add(exitButton);


            fullScreenModeHBox.setVisible(StartupFlags.SHOW_FULLSCREEN_TOGGLE_BUTTON);

            shutdownButton.setVisible(StartupFlags.IS_SHOW_SHUT_DOWN_BUTTON);
        }


        screenSaverHBox.setVisible(StartupFlags.SCREEN_SAVER_FEATURE);
        screenTimeoutSecondsHBoxInputBox.setVisible(StartupFlags.SCREEN_SAVER_FEATURE);
    }

    private Label generateSubHeading(String text)
    {
        Label label = new Label(text);
        label.getStyleClass().add("general_settings_sub_heading");
        return label;
    }

    private Logger getLogger()
    {
        return logger;
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

    private void onFactoryResetButtonClicked()
    {
        StreamPiAlert confirmation = new StreamPiAlert("Are you sure?\n" +
                "This will erase everything.",StreamPiAlertType.WARNING, StreamPiAlertButton.YES, StreamPiAlertButton.NO);


        confirmation.setOnClicked(new StreamPiAlertListener() {
            @Override
            public void onClick(StreamPiAlertButton s)
            {
                if(s.equals(StreamPiAlertButton.YES))
                {
                    clientListener.factoryReset();
                }
            }
        });

        confirmation.show();
    }


    public void onExitButtonClicked()
    {
        clientListener.onCloseRequest();
        clientListener.exitApp();
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

        try
        {
            Runtime.getRuntime().exec("sudo halt");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onConnectDisconnectButtonClicked()
    {
        if(clientListener.isConnected())
        {
            clientListener.disconnect();
        }
        else
        {
            clientListener.setupClientConnection();
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

        nameTextField.setText(config.getClientName());

        serverHostNameOrIPTextField.setText(config.getSavedServerHostNameOrIP());
        serverPortTextField.setText(config.getSavedServerPort()+"");

        screenTimeoutTextField.setText(config.getScreenSaverTimeout()+"");
        screenSaverToggleSwitch.setSelected(config.isScreenSaverEnabled());
        screenMoverToggleSwitch.setSelected(config.isScreenMoverEnabled());

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
        invertRowsColsToggleSwitch.setSelected(config.isInvertRowsColsOnDeviceRotate());

        languageChooserComboBox.setCurrentSelectedItem(I18N.getLanguage(config.getCurrentLanguageLocale()));
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
            screenSaverTimeout = Integer.parseInt(screenTimeoutTextField.getText());

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

        if(nameTextField.getText().isBlank())
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



        try
        {
            boolean toBeReloaded = false;
            boolean baseToBeReloaded = false;

            boolean syncWithServer = false;

            Config config = Config.getInstance();

            if(!config.getCurrentThemeFullName().equals(themeComboBox.getCurrentSelectedItem().getFullName()))
            {
                syncWithServer = true;
                
                try
                {
                    config.setCurrentThemeFullName(themeComboBox.getCurrentSelectedItem().getFullName());
                    config.save();
                    clientListener.initThemes();
                }
                catch(SevereException e)
                {
                    exceptionAndAlertHandler.handleSevereException(e);
                }
            }

            if(!config.getClientName().equals(nameTextField.getText()))
            {
                syncWithServer = true;
            }

            config.setName(nameTextField.getText());

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


            if (!languageChooserComboBox.getSelectedLocale().equals(config.getCurrentLanguageLocale()))
            {
                config.setCurrentLanguageLocale(languageChooserComboBox.getSelectedLocale());

                toBeReloaded = true;
                baseToBeReloaded = true;
            }



            config.setTryConnectingWhenActionClicked(tryConnectingToServerIfActionClickedToggleSwitch.isSelected());



            boolean startOnBoot = startOnBootToggleSwitch.isSelected();

            if(config.isStartOnBoot() != startOnBoot)
            {
                StartOnBoot startAtBoot = new StartOnBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform(),
                        Main.class.getProtectionDomain().getCodeSource().getLocation(),
                        StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION);

                if(startOnBoot)
                {
                    try
                    {
                        startAtBoot.create(StartupFlags.RUNNER_FILE_NAME, StartupFlags.IS_X_MODE);
                        config.setStartupIsXMode(StartupFlags.IS_X_MODE);
                    }
                    catch (MinorException e)
                    {
                        exceptionAndAlertHandler.handleMinorException(e);
                        startOnBoot = false;
                    }
                }
                else
                {
                    boolean result = startAtBoot.delete();
                    if(!result)
                        new StreamPiAlert("Uh Oh!", "Unable to delete starter file", StreamPiAlertType.ERROR).show();
                }
            }

            config.setStartOnBoot(startOnBoot);

            if(!config.isShowCursor() ==showCursorToggleSwitch.isSelected())
            {
                toBeReloaded = true;
            }

            config.setShowCursor(showCursorToggleSwitch.isSelected());


            if(!config.getThemesPath().equals(themesPathTextField.getText()))
            {
                toBeReloaded = true;
            }

            config.setThemesPath(themesPathTextField.getText());


            if(!config.getIconsPath().equals(iconsPathTextField.getText()))
            {
                toBeReloaded = true;
            }

            config.setIconsPath(iconsPathTextField.getText());

            if(!config.getProfilesPath().equals(profilesPathTextField.getText()))
            {
                toBeReloaded = true;
            }

            config.setProfilesPath(profilesPathTextField.getText());

            if(!(screenSaverTimeout+"").equals(screenTimeoutTextField.getText()))
            {
                toBeReloaded = true;
            }

            config.setScreenSaverTimeout(screenTimeoutTextField.getText());

            if(config.isScreenSaverEnabled() != screenSaverToggleSwitch.isSelected())
            {
                toBeReloaded = true;
            }

            config.setScreenSaverEnabled(screenSaverToggleSwitch.isSelected());

            if(config.isScreenMoverEnabled() != screenMoverToggleSwitch.isSelected())
            {
                toBeReloaded = true;
            }

            config.setScreenMoverEnabled(screenMoverToggleSwitch.isSelected());


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
            config.setInvertRowsColsOnDeviceRotate(invertRowsColsToggleSwitch.isSelected());

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
                if(!ClientInfo.getInstance().isPhone() && !config.getIsFullScreenMode())
                {
                    config.setStartupWindowSize(clientListener.getStageWidth(), clientListener.getStageHeight());
                    config.save();
                }

                if(baseToBeReloaded)
                {
                    clientListener.initBase();
                }

                clientListener.init();

                if (clientListener.isConnected())
                {
                    clientListener.getClient().refreshAllGauges();
                }

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
