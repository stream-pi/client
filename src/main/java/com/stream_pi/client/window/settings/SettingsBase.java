package com.stream_pi.client.window.settings;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import com.gluonhq.attach.browser.BrowserService;
import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.client.connection.ClientListener;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
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
import com.stream_pi.util.uihelper.HBoxInputBox;
import com.stream_pi.util.uihelper.SpaceFiller;
import com.stream_pi.util.startatboot.StartAtBoot;

import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SettingsBase extends VBox {
    private TextField serverPortTextField;
    private TextField serverHostNameOrIPTextField;

    private StreamPiComboBox<ClientProfile> clientProfileComboBox;
    private StreamPiComboBox<Theme> themeComboBox;

    private TextField nickNameTextField;

    private Button closeButton;
    private Button saveButton;
    private Button connectDisconnectButton;
    private Button shutdownButton;

    private ToggleButton startOnBootToggleButton;

    private ToggleButton connectOnStartupToggleButton;
    private ToggleButton vibrateOnActionPressToggleButton;

    private ToggleButton fullScreenModeToggleButton;

    private ToggleButton showCursorToggleButton;

    private ClientListener clientListener;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private TextField themesPathTextField;
    private TextField iconsPathTextField;
    private TextField profilesPathTextField;

    private final Button checkForUpdatesButton;
    private HostServices hostServices;

    public SettingsBase(ExceptionAndAlertHandler exceptionAndAlertHandler,
                        ClientListener clientListener, HostServices hostServices)
    {
        getStyleClass().add("settings_base");

        this.hostServices = hostServices;
        this.clientListener = clientListener;
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;

        serverPortTextField = new TextField();
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

        startOnBootToggleButton = new ToggleButton("Start On Boot");
        startOnBootToggleButton.managedProperty().bind(startOnBootToggleButton.visibleProperty());

        fullScreenModeToggleButton = new ToggleButton("Full Screen");
        fullScreenModeToggleButton.managedProperty().bind(fullScreenModeToggleButton.visibleProperty());

        vibrateOnActionPressToggleButton = new ToggleButton("Vibrate On Action Press");
        vibrateOnActionPressToggleButton.managedProperty().bind(vibrateOnActionPressToggleButton.visibleProperty());

        connectOnStartupToggleButton = new ToggleButton("Connect On Startup");
        connectOnStartupToggleButton.managedProperty().bind(connectOnStartupToggleButton.visibleProperty());

        showCursorToggleButton = new ToggleButton("Show Cursor");
        showCursorToggleButton.managedProperty().bind(showCursorToggleButton.visibleProperty());

        int prefWidth = 200;

        Label licenseLabel = new Label("This software is licensed to GNU GPLv3. Check StreamPi Server > settings > About to see full License Statement.");
        licenseLabel.setWrapText(true);

        Label versionLabel = new Label(ClientInfo.getInstance().getReleaseStatus().getUIName() +" "+ClientInfo.getInstance().getVersion().getText());


        HBoxInputBox themesPathInputBox = new HBoxInputBox("Themes Path", themesPathTextField, prefWidth);
        themesPathInputBox.managedProperty().bind(themesPathInputBox.visibleProperty());


        HBoxInputBox iconsPathInputBox = new HBoxInputBox("Icons Path", iconsPathTextField, prefWidth);
        iconsPathInputBox.managedProperty().bind(iconsPathInputBox.visibleProperty());


        HBoxInputBox profilesPathInputBox = new HBoxInputBox("Profiles Path", profilesPathTextField, prefWidth);
        profilesPathInputBox.managedProperty().bind(profilesPathInputBox.visibleProperty());

        checkForUpdatesButton = new Button("Check for updates");
        checkForUpdatesButton.setOnAction(event->checkForUpdates());

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
                profilesPathInputBox
        );


        vBox.getStyleClass().add("settings_base_vbox");

        vBox.setSpacing(5.0);
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

        closeButton = new Button("Close");

        connectDisconnectButton = new Button("Connect");
        connectDisconnectButton.setOnAction(event -> onConnectDisconnectButtonClicked());


        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> onExitButtonClicked());

        HBox buttonBar = new HBox(connectDisconnectButton, saveButton);

        shutdownButton = new Button("Shutdown");
        shutdownButton.managedProperty().bind(shutdownButton.visibleProperty());
        shutdownButton.setOnAction(event -> onShutdownButtonClicked());


        vBox.getChildren().addAll(
                shutdownButton,
                fullScreenModeToggleButton,
                connectOnStartupToggleButton,
                vibrateOnActionPressToggleButton,
                checkForUpdatesButton,
                startOnBootToggleButton,
                showCursorToggleButton,
                licenseLabel,
                versionLabel
        );






        buttonBar.getChildren().add(closeButton);
        buttonBar.getStyleClass().add("settings_button_bar");


        buttonBar.setPadding(new Insets(0,5,5,0));
        buttonBar.setSpacing(5.0);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        setSpacing(5.0);

        getChildren().addAll(
                settingsLabel,
                scrollPane,
                buttonBar
        );

        setCache(true);
        setCacheHint(CacheHint.SPEED);


        //Perform platform checks

        Platform platform = ClientInfo.getInstance().getPlatform();
        if(platform == Platform.ANDROID ||
                platform == Platform.IOS)
        {
            themesPathInputBox.setVisible(false);
            iconsPathInputBox.setVisible(false);
            profilesPathInputBox.setVisible(false);

            startOnBootToggleButton.setVisible(false);
            showCursorToggleButton.setVisible(false);
            fullScreenModeToggleButton.setVisible(false);
        }
        else
        {
            if(!ClientInfo.getInstance().isShowShutDownButton())
            {
                shutdownButton.setVisible(false);
            }

            vibrateOnActionPressToggleButton.setVisible(false);
            buttonBar.getChildren().add(exitButton);
        }

        if(!ClientInfo.getInstance().isShowFullScreenToggleButton())
        {
            fullScreenModeToggleButton.setVisible(false);
        }
    }

    private void checkForUpdates()
    {
        new CheckForUpdates(checkForUpdatesButton,
                PlatformType.CLIENT, ClientInfo.getInstance().getVersion(), new UpdateHyperlinkOnClick() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform platform = ClientInfo.getInstance().getPlatform();
                if(platform == Platform.ANDROID || platform == Platform.IOS)
                {
                    BrowserService.create().ifPresentOrElse(s->
                    {
                        try
                        {
                            s.launchExternalBrowser(getURL());
                        }
                        catch (Exception e )
                        {
                            exceptionAndAlertHandler.handleMinorException(
                                    new MinorException("Cant start browser!")
                            );
                        }
                    },()-> exceptionAndAlertHandler.handleMinorException(
                            new MinorException("Sorry!","No browser detected.")
                    ));
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

    public Button getCloseButton() {
        return closeButton;
    }

    public void loadData() throws SevereException
    {
        Config config = Config.getInstance();

        nickNameTextField.setText(config.getClientNickName());

        serverHostNameOrIPTextField.setText(config.getSavedServerHostNameOrIP());
        serverPortTextField.setText(config.getSavedServerPort()+"");

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

        startOnBootToggleButton.setSelected(config.isStartOnBoot());
        fullScreenModeToggleButton.setSelected(config.getIsFullScreenMode());

        showCursorToggleButton.setSelected(config.isShowCursor());

        connectOnStartupToggleButton.setSelected(config.isConnectOnStartup());
        vibrateOnActionPressToggleButton.setSelected(config.isVibrateOnActionClicked());
    }

    public void onSaveButtonClicked()
    {
        StringBuilder errors = new StringBuilder();

        int port = -1;
        try
        {
            port = Integer.parseInt(serverPortTextField.getText());

            if(port < 1024)
                errors.append("* Server IP should be above 1024.\n");
            else if(port > 65535)
                errors.append("* Server Port must be lesser than 65535\n");
        }
        catch (NumberFormatException exception)
        {
            errors.append("* Server IP should be a number.\n");
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

            boolean breakConnection = false;

            Config config = Config.getInstance();

            if(!config.getCurrentThemeFullName().equals(themeComboBox.getCurrentSelectedItem().getFullName()))
            {
                breakConnection = true;
                toBeReloaded = true;
            }

            config.setCurrentThemeFullName(themeComboBox.getCurrentSelectedItem().getFullName());

            if(!config.getClientNickName().equals(nickNameTextField.getText()))
                breakConnection = true;

            config.setNickName(nickNameTextField.getText());

            if(port != config.getSavedServerPort() || !serverHostNameOrIPTextField.getText().equals(config.getSavedServerHostNameOrIP()))
                breakConnection = true;

            config.setServerPort(port);
            config.setServerHostNameOrIP(serverHostNameOrIPTextField.getText());

            boolean isFullScreen = fullScreenModeToggleButton.isSelected();

            if(config.getIsFullScreenMode() != isFullScreen)
            {
                toBeReloaded = true;
            }

            config.setIsFullScreenMode(isFullScreen);




            boolean startOnBoot = startOnBootToggleButton.isSelected();

            if(config.isStartOnBoot() != startOnBoot)
            {
                if(ClientInfo.getInstance().getRunnerFileName() == null)
                {
                    new StreamPiAlert("Uh Oh", "No Runner File Name Specified as startup arguments. Cant set run at boot.", StreamPiAlertType.ERROR).show();
                    startOnBoot = false;
                }
                else
                {
                    StartAtBoot startAtBoot = new StartAtBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform());
                    if(startOnBoot)
                    {
                        startAtBoot.create(new File(ClientInfo.getInstance().getRunnerFileName()),
                                ClientInfo.getInstance().isXMode());
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

            config.setShowCursor(showCursorToggleButton.isSelected());



            if(!config.getThemesPath().equals(themesPathTextField.getText()))
                toBeReloaded = true;

            config.setThemesPath(themesPathTextField.getText());


            if(!config.getIconsPath().equals(iconsPathTextField.getText()))
                toBeReloaded = true;

            config.setIconsPath(iconsPathTextField.getText());

            if(!config.getProfilesPath().equals(profilesPathTextField.getText()))
                toBeReloaded = true;

            config.setProfilesPath(profilesPathTextField.getText());

            config.setConnectOnStartup(connectOnStartupToggleButton.isSelected());

            boolean isVibrateOnActionClicked = vibrateOnActionPressToggleButton.isSelected();

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


            if(breakConnection)
            {
                if(clientListener.isConnected())
                {
                    clientListener.disconnect("Client connection settings were changed. Client will reconnect again.");
                    clientListener.setupClientConnection();
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
