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
import com.stream_pi.util.platform.PlatformType;
import com.stream_pi.util.rootchecker.RootChecker;
import com.stream_pi.util.startonboot.StartOnBoot;
import com.stream_pi.util.uihelper.HBoxInputBox;
import com.stream_pi.util.uihelper.HBoxWithSpaceBetween;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

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
    private final Button restartButton;

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
        startOnBootHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.startOnBoot"), startOnBootToggleSwitch);
        startOnBootHBox.managedProperty().bind(startOnBootHBox.visibleProperty());

        screenSaverToggleSwitch = new ToggleSwitch();
        screenSaverHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.screenSaver"), screenSaverToggleSwitch);
        screenSaverHBox.managedProperty().bind(screenSaverHBox.visibleProperty());

        screenMoverToggleSwitch = new ToggleSwitch();
        screenMoverHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.oledBurnInProtector"), screenMoverToggleSwitch);
        screenMoverHBox.managedProperty().bind(screenMoverHBox.visibleProperty());

        tryConnectingToServerIfActionClickedToggleSwitch  = new ToggleSwitch();
        tryConnectingToServerIfActionClickedHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.tryConnectToServerOnActionClick"), tryConnectingToServerIfActionClickedToggleSwitch);
        tryConnectingToServerIfActionClickedHBox.managedProperty().bind(tryConnectingToServerIfActionClickedHBox.visibleProperty());

        fullScreenModeToggleSwitch = new ToggleSwitch();
        fullScreenModeHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.fullScreen"), fullScreenModeToggleSwitch);
        fullScreenModeHBox.managedProperty().bind(fullScreenModeHBox.visibleProperty());

        vibrateOnActionPressToggleSwitch = new ToggleSwitch();
        vibrateOnActionPressHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.vibrateOnActionClick"), vibrateOnActionPressToggleSwitch);
        vibrateOnActionPressHBox.managedProperty().bind(vibrateOnActionPressHBox.visibleProperty());

        connectOnStartupToggleSwitch = new ToggleSwitch();
        connectOnStartupHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.connectOnStartup"), connectOnStartupToggleSwitch);
        connectOnStartupHBox.managedProperty().bind(connectOnStartupHBox.visibleProperty());

        showCursorToggleSwitch = new ToggleSwitch();
        showCursorHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.showCursor"), showCursorToggleSwitch);
        showCursorHBox.managedProperty().bind(showCursorHBox.visibleProperty());

        invertRowsColsToggleSwitch = new ToggleSwitch();
        invertRowsColsHBox = new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.invertGridOnRotate"), invertRowsColsToggleSwitch);
        invertRowsColsHBox.managedProperty().bind(invertRowsColsHBox.visibleProperty());

        int prefWidth = 200;

        HBoxInputBox themesPathInputBox = new HBoxInputBox(I18N.getString("window.settings.GeneralTab.themes"), themesPathTextField, prefWidth);
        themesPathInputBox.managedProperty().bind(themesPathInputBox.visibleProperty());


        HBoxInputBox iconsPathInputBox = new HBoxInputBox(I18N.getString("window.settings.GeneralTab.icons"), iconsPathTextField, prefWidth);
        iconsPathInputBox.managedProperty().bind(iconsPathInputBox.visibleProperty());


        HBoxInputBox profilesPathInputBox = new HBoxInputBox(I18N.getString("window.settings.GeneralTab.profiles"), profilesPathTextField, prefWidth);
        profilesPathInputBox.managedProperty().bind(profilesPathInputBox.visibleProperty());

        checkForUpdatesButton = new Button(I18N.getString("window.settings.GeneralTab.checkForUpdates"));
        checkForUpdatesButton.setOnAction(event->checkForUpdates());

        factoryResetButton = new Button(I18N.getString("window.settings.GeneralTab.factoryReset"));
        factoryResetButton.setOnAction(actionEvent -> onFactoryResetButtonClicked());


        restartButton = new Button(I18N.getString("window.settings.GeneralTab.restart"));
        restartButton.setOnAction(event->{
            if (clientListener.isConnected())
            {
                showRestartPrompt(I18N.getString("window.settings.GeneralTab.restartPromptWarning"));
            }
            else
            {
                clientListener.restart();
            }
        });

        screenTimeoutSecondsHBoxInputBox = new HBoxInputBox(I18N.getString("window.settings.GeneralTab.screenTimeoutInSeconds"), screenTimeoutTextField, prefWidth);
        screenTimeoutSecondsHBoxInputBox.managedProperty().bind(screenTimeoutSecondsHBoxInputBox.visibleProperty());
        screenTimeoutTextField.disableProperty().bind(screenSaverToggleSwitch.selectedProperty().not());

        saveButton = new Button(I18N.getString("window.settings.GeneralTab.save"));
        saveButton.setOnAction(event->onSaveButtonClicked());

        connectDisconnectButton = new Button(I18N.getString("window.settings.GeneralTab.connect"));
        connectDisconnectButton.setOnAction(event -> onConnectDisconnectButtonClicked());


        Button exitButton = new Button(I18N.getString("window.settings.GeneralTab.exit"));
        exitButton.setOnAction(event -> onExitButtonClicked());

        HBox buttonBar = new HBox(connectDisconnectButton, saveButton);

        shutdownButton = new Button(I18N.getString("window.settings.GeneralTab.shutdown"));
        shutdownButton.managedProperty().bind(shutdownButton.visibleProperty());
        shutdownButton.setOnAction(event -> onShutdownButtonClicked());


        VBox vBox = new VBox(
                generateSubHeading(I18N.getString("window.settings.GeneralTab.connection")),
                new HBoxInputBox(I18N.getString("serverHostNameOrIP"), serverHostNameOrIPTextField, prefWidth),
                new HBoxInputBox(I18N.getString("serverPort"), serverPortTextField, prefWidth),
                generateSubHeading(I18N.getString("window.settings.GeneralTab.client")),
                new HBoxInputBox(I18N.getString("name"), nameTextField, prefWidth),
                new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.currentProfile"), clientProfileComboBox),
                new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.theme"), themeComboBox),
                generateSubHeading(I18N.getString("window.settings.GeneralTab.locations")),
                themesPathInputBox,
                iconsPathInputBox,
                profilesPathInputBox,
                generateSubHeading(I18N.getString("window.settings.GeneralTab.others")),
                new HBoxWithSpaceBetween(I18N.getString("window.settings.GeneralTab.language"), languageChooserComboBox),
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
                shutdownButton,
                factoryResetButton,
                restartButton
        );

        // checkForUpdatesButton removed until Update API is finalised


        getStyleClass().add("settings_general_tab");
        vBox.getStyleClass().add("settings_general_tab_vbox");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("settings_general_tab_scroll_pane");
        scrollPane.setContent(vBox);
        scrollPane.setFitToWidth(true);

        vBox.setMinWidth(300);


        buttonBar.getStyleClass().add("settings_general_tab_button_bar");
        buttonBar.setSpacing(5.0);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        setSpacing(10.0);

        getChildren().addAll(
                scrollPane,
                buttonBar
        );

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

            shutdownButton.setVisible(StartupFlags.SHOW_SHUT_DOWN_BUTTON);
        }


        screenSaverHBox.setVisible(StartupFlags.SCREEN_SAVER_FEATURE);
        screenTimeoutSecondsHBoxInputBox.setVisible(StartupFlags.SCREEN_SAVER_FEATURE);
    }

    private void showRestartPrompt(String promptText)
    {
        StreamPiAlert restartPrompt = new StreamPiAlert(promptText,
                StreamPiAlertType.WARNING, StreamPiAlertButton.YES, StreamPiAlertButton.NO
        );

        restartPrompt.setOnClicked(new StreamPiAlertListener() {
            @Override
            public void onClick(StreamPiAlertButton s) {
                if(s.equals(StreamPiAlertButton.YES))
                {
                    clientListener.restart();
                }
            }
        });

        restartPrompt.show();
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
        StreamPiAlert confirmation = new StreamPiAlert(I18N.getString("window.settings.GeneralTab.resetAreYouSure"),StreamPiAlertType.WARNING, StreamPiAlertButton.YES, StreamPiAlertButton.NO);

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
        clientListener.onQuitApp();
        clientListener.exitApp();
    }

    public Button getConnectDisconnectButton()
    {
        return connectDisconnectButton;
    }

    public void onShutdownButtonClicked()
    {
        clientListener.onQuitApp();
        shutdownButton.setDisable(true);

        try
        {
            Runtime.getRuntime().exec("sudo halt");
        }
        catch (Exception e)
        {
            shutdownButton.setDisable(false);
            exceptionAndAlertHandler.handleMinorException(new MinorException(I18N.getString("window.settings.GeneralTab.failedToShutdown", e.getLocalizedMessage())));
            e.printStackTrace();
        }
    }

    public StringBuilder connectionInputValidation()
    {
        StringBuilder errors = new StringBuilder();

        int port;
        try
        {
            port = Integer.parseInt(serverPortTextField.getText());

            if(port < 1024 && !RootChecker.isRoot(ClientInfo.getInstance().getPlatform()))
            {
                errors.append("* ").append(I18N.getString("serverPortMustBeGreaterThan1024")).append("\n");
            }
            else if(port > 65535)
            {
                errors.append("* ").append(I18N.getString("serverPortMustBeLesserThan65535")).append("\n");
            }
        }
        catch (NumberFormatException exception)
        {
            errors.append("* ").append(I18N.getString("serverPortMustBeInteger")).append("\n");
        }

        if(serverHostNameOrIPTextField.getText().isBlank())
        {
            errors.append("* ").append(I18N.getString("serverHostNameOrIPCannotBeBlank")).append("\n");
        }

        if(nameTextField.getText().isBlank())
        {
            errors.append("* ").append(I18N.getString("nameCannotBeBlank")).append("\n");
        }

        return errors;
    }

    public void onConnectDisconnectButtonClicked()
    {
        if(clientListener.isConnected())
        {
            clientListener.disconnect();
        }
        else
        {
            StringBuilder errors = connectionInputValidation();

            if(!errors.toString().isEmpty())
            {
                exceptionAndAlertHandler.handleMinorException(new MinorException(I18N.getString("validationError", errors)));
                return;
            }

            clientListener.setupClientConnection();
        }
    }

    public void setConnectDisconnectButtonStatus()
    {
        javafx.application.Platform.runLater(()->
        {
            if(clientListener.isConnected())
            {
                connectDisconnectButton.setText(I18N.getString("window.settings.GeneralTab.disconnect"));
            }
            else
            {
                connectDisconnectButton.setText(I18N.getString("window.settings.GeneralTab.connect"));
            }
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


        int screenSaverTimeout = -1;
        try
        {
            screenSaverTimeout = Integer.parseInt(screenTimeoutTextField.getText());

            if(screenSaverTimeout < 15)
            {
                errors.append("* ").append(I18N.getString("window.settings.GeneralTab.screenTimeoutCannotBeBelow15Seconds")).append("\n");
            }
        }
        catch (NumberFormatException exception)
        {
            errors.append("* ").append(I18N.getString("window.settings.GeneralTab.screenTimeoutMustBeInteger")).append("\n");
        }

        errors.append(connectionInputValidation());

        if(!errors.toString().isEmpty())
        {
            exceptionAndAlertHandler.handleMinorException(new MinorException(I18N.getString("validationError", errors)));
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

            int port = Integer.parseInt(serverPortTextField.getText());
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



            boolean isStartOnBoot = startOnBootToggleSwitch.isSelected();

            if(config.isStartOnBoot() != isStartOnBoot)
            {
                StartOnBoot startOnBoot = new StartOnBoot(PlatformType.CLIENT, ClientInfo.getInstance().getPlatform(),
                        Main.class.getProtectionDomain().getCodeSource().getLocation(),
                        StartupFlags.APPEND_PATH_BEFORE_RUNNER_FILE_TO_OVERCOME_JPACKAGE_LIMITATION);

                if(isStartOnBoot)
                {
                    try
                    {
                        startOnBoot.create(StartupFlags.RUNNER_FILE_NAME, StartupFlags.X_MODE, StartupFlags.generateRuntimeArgumentsForStartOnBoot());
                        config.setStartupIsXMode(StartupFlags.X_MODE);
                    }
                    catch (MinorException e)
                    {
                        exceptionAndAlertHandler.handleMinorException(e);
                        isStartOnBoot = false;
                    }
                }
                else
                {
                    boolean result = startOnBoot.delete();
                    if(!result)
                        new StreamPiAlert(I18N.getString("window.settings.GeneralTab.unableToDeleteStarterFile"), StreamPiAlertType.ERROR).show();
                }
            }

            config.setStartOnBoot(isStartOnBoot);

            if(!config.isShowCursor() ==showCursorToggleSwitch.isSelected())
            {
                toBeReloaded = true;
            }

            config.setShowCursor(showCursorToggleSwitch.isSelected());


            if(!config.getThemesPath().equals(themesPathTextField.getText()))
            {
                toBeReloaded = true;
                baseToBeReloaded = true;
            }

            config.setThemesPath(themesPathTextField.getText());


            if(!config.getIconsPath().equals(iconsPathTextField.getText()))
            {
                toBeReloaded = true;
                baseToBeReloaded = true;
            }

            config.setIconsPath(iconsPathTextField.getText());

            if(!config.getProfilesPath().equals(profilesPathTextField.getText()))
            {
                toBeReloaded = true;
                baseToBeReloaded = true;
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
                    new StreamPiAlert(I18N.getString("window.settings.GeneralTab.vibrationNotSupported"), StreamPiAlertType.ERROR).show();
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
                    clientListener.setFirstRun(true);
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
