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

package com.stream_pi.client.window;

import com.stream_pi.action_api.ActionAPI;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.controller.ScreenSaver;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;

import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.stream_pi.client.Main;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.window.dashboard.DashboardBase;
import com.stream_pi.client.window.firsttimeuse.FirstTimeUse;
import com.stream_pi.client.window.settings.SettingsBase;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.theme_api.ThemeAPI;
import com.stream_pi.theme_api.Themes;
import com.stream_pi.util.Util;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.combobox.StreamPiComboBox;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.iohelper.IOHelper;
import com.stream_pi.util.loggerhelper.StreamPiLogFallbackHandler;
import com.stream_pi.util.loggerhelper.StreamPiLogFileHandler;
import com.stream_pi.util.platform.Platform;

import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public abstract class Base extends StackPane implements ExceptionAndAlertHandler, ClientListener
{
    private Config config;

    private ClientProfiles clientProfiles;

    private ClientInfo clientInfo;

    private Stage stage;

    public Stage getStage()
    {
        return stage;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public DashboardBase dashboardBase;
    public SettingsBase settingsBase;

    public FirstTimeUse firstTimeUse;

    private StackPane alertStackPane;

    @Override
    public ClientProfiles getClientProfiles() {
        return clientProfiles;
    }

    public void setClientProfiles(ClientProfiles clientProfiles) {
        this.clientProfiles = clientProfiles;
    }

    private Logger logger = null;
    private StreamPiLogFileHandler logFileHandler = null;
    private StreamPiLogFallbackHandler logFallbackHandler = null;

    @Override
    public void initLogger()
    {
        try
        {
            if(logFileHandler != null)
                return;

            closeLogger();
            logger = Logger.getLogger("com.stream_pi");

            if(new File(ClientInfo.getInstance().getPrePath()).getAbsoluteFile().getParentFile().canWrite())
            {
                String path = ClientInfo.getInstance().getPrePath()+"../stream-pi-client.log";

                if(getClientInfo().isPhone())
                    path = ClientInfo.getInstance().getPrePath()+"stream-pi-client.log";

                logFileHandler = new StreamPiLogFileHandler(path);
                logger.addHandler(logFileHandler);
            }
            else
            {
                logFallbackHandler = new StreamPiLogFallbackHandler();
                logger.addHandler(logFallbackHandler);
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();

            logFallbackHandler = new StreamPiLogFallbackHandler();
            logger.addHandler(logFallbackHandler);
        }
    }
    
    public void closeLogger()
    {
        if(logFileHandler != null)
            logFileHandler.close();
        else if(logFallbackHandler != null)
            logFallbackHandler.close();
    }

    private HostServices hostServices;

    public void setHostServices(HostServices hostServices)
    {
        this.hostServices = hostServices;
    }

    public HostServices getHostServices()
    {
        return hostServices;
    }

    public void initBase() throws SevereException
    {
        I18N.initAvailableLanguages();

        stage = (Stage) getScene().getWindow();

        getStage().getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/256x256.png"))));
        getStage().getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/48x48.png"))));
        getStage().getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/32x32.png"))));
        getStage().getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/24x24.png"))));
        getStage().getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/16x16.png"))));

        clientInfo = ClientInfo.getInstance();

        if ((clientInfo.getPlatform() == Platform.WINDOWS || clientInfo.getPlatform() == Platform.MAC || (clientInfo.getPlatform() == Platform.LINUX && StartupFlags.X_MODE))
                && StartupFlags.SET_FIXED_MIN_SIZE)
        {
            getStage().setMinWidth(350);
            getStage().setMinHeight(530);
        }


        alertStackPane = new StackPane();
        alertStackPane.setCache(true);
        alertStackPane.setCacheHint(CacheHint.QUALITY);

        StreamPiAlert.setParent(alertStackPane);
        StreamPiComboBox.setParent(alertStackPane);


        getChildren().clear();


        getChildren().addAll(alertStackPane);

        initLogger();

        checkPrePathDirectory();

        setStyle(null);

        config = Config.getInstance();


        initI18n();

        dashboardBase = new DashboardBase(this, this);
        dashboardBase.prefWidthProperty().bind(widthProperty());
        dashboardBase.prefHeightProperty().bind(heightProperty());

        settingsBase = new SettingsBase(getHostServices(), this, this);


        getChildren().addAll(settingsBase, dashboardBase);


        initThemes();


        if(getClientInfo().isPhone())
        {
            dashboardBase.setPadding(new Insets(15));
            settingsBase.setPadding(new Insets(15));
        }
    }

    private void initI18n() throws SevereException
    {
        if (I18N.isLanguageAvailable(config.getCurrentLanguageLocale()))
        {
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(I18N.BASE_LOCALE);
            // This sets the local to Locale en (fallback locale)
            // This is done because the proper way of removing fallback locales is not available on Java 9+
            // As ResourceBundle.Control is not supported on modular projects.


            Util.initI18n(config.getCurrentLanguageLocale());
            ActionAPI.initI18n(config.getCurrentLanguageLocale());
            ThemeAPI.initI18n(config.getCurrentLanguageLocale());
            I18N.init(config.getCurrentLanguageLocale());

            Locale.setDefault(defaultLocale); // Reset locale back to defaults ...
        }
        else
        {
            getLogger().warning("No translation available for locale : "+config.getCurrentLanguageLocale().toString());
            getLogger().warning("Setting it to base ...");
            getConfig().setCurrentLanguageLocale(I18N.BASE_LOCALE);
            getConfig().save();
            initI18n();
        }
    }

    public void initThemes() throws SevereException 
    {
        clearStylesheets();
        System.out.println("IS THEME NULL : "+(themes == null));
        if(themes==null)
            registerThemes();
        applyDefaultStylesheet();
        applyDefaultTheme();
        applyDefaultIconsStylesheet();
        applyGlobalDefaultStylesheet();
    }

    public void unregisterThemes()
    {
        themes = null;
    }

    protected void resizeAccordingToResolution()
    {
        if(!getClientInfo().isPhone())
        {
            double height = getScreenHeight();
            double width = getScreenWidth();

            if(height < 500)
                setPrefHeight(320);

            if(width < 500)
                setPrefWidth(240);
        }
    }

    @Override
    public double getStageWidth()
    {
        if(getClientInfo().isPhone())
        {
            return getScreenWidth();
        }
        else
        {
            return getStage().getWidth();
        }
    }

    public double getScreenWidth()
    {
        return Screen.getPrimary().getBounds().getWidth();
    }

    @Override
    public double getStageHeight()
    {
        if(ClientInfo.getInstance().isPhone())
        {
            return getScreenHeight();
        }
        else
        {
            return getStage().getHeight();
        }
    }

    public double getScreenHeight()
    {
        return Screen.getPrimary().getBounds().getHeight();
    }

    private void checkPrePathDirectory() throws SevereException
    {
        String path = getClientInfo().getPrePath();

        if(path == null)
        {
            throwStoragePermErrorAlert(I18N.getString("window.Base.failedToAccessFileSystem"));
            return;
        }

        File clientDataFolder = new File(path);

        if (clientDataFolder.exists())
        {
            if (new File(getClientInfo().getPrePath()+"config.xml").exists())
            {
                Config tempConfig = new Config();

                if (tempConfig.getVersion() == null || tempConfig.getVersion().getMajor() != getClientInfo().getVersion().getMajor())
                {
                    IOHelper.deleteFile(getClientInfo().getPrePath(), false);
                }
            }
            else
            {
                IOHelper.deleteFile(getClientInfo().getPrePath(), false);
            }
        }


        if (!clientDataFolder.exists())
        {
            try
            {
                Config.unzipToDefaultPrePath();
                initLogger();
            }
            catch (MinorException e)
            {
                throwStoragePermErrorAlert(e.getMessage());
            }
        }
    }

    private void throwStoragePermErrorAlert(String msg) throws SevereException
    {
        resizeAccordingToResolution();

        clearStylesheets();
        applyDefaultStylesheet();
        applyDefaultIconsStylesheet();
        applyGlobalDefaultStylesheet();
        getStage().show();
        throw new SevereException(msg);
    }

    public void setupFlags() throws SevereException
    {
        //Full Screen
        if(Config.getInstance().getIsFullScreenMode())
        {
            getStage().setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            getStage().setFullScreen(true);
        }
        else
        {
            getStage().setFullScreenExitKeyCombination(KeyCombination.keyCombination("ESC"));
            getStage().setFullScreen(false);
        }

        //Cursor
        if(Config.getInstance().isShowCursor())
        {
            setCursor(Cursor.DEFAULT);
        }
        else
        {
            setCursor(Cursor.NONE);
        }
    }


    public SettingsBase getSettingsPane() {
        return settingsBase;
    }

    public DashboardBase getDashboardPane() {
        return dashboardBase;
    }

    public void renderRootDefaultProfile()
    {
        getDashboardPane().renderProfile(getClientProfiles().getProfileFromID(
                getConfig().getStartupProfileID()
        ), true);
    }



    public void clearStylesheets()
    {
        getStylesheets().clear();
    }



    public void applyDefaultStylesheet()
    {
        getStylesheets().add(Objects.requireNonNull(Main.class.getResource("style.css")).toExternalForm());
    }

    public void applyDefaultIconsStylesheet()
    {
        getStylesheets().add(Objects.requireNonNull(Main.class.getResource("default_icons.css")).toExternalForm());
    }


    public Config getConfig()
    {
        return config;
    }

    public ClientInfo getClientInfo()
    {
        return clientInfo;
    }

    private Theme currentTheme;

    @Override
    public Theme getCurrentTheme()
    {
        return currentTheme;
    }


    public void applyTheme(Theme t)
    {
        logger.info("Applying theme '"+t.getFullName()+"' ...");

        currentTheme = t;
        getStylesheets().addAll(t.getStylesheets());
      
        logger.info("... Done!");
    }

    public void applyGlobalDefaultStylesheet()
    {
        File globalCSSFile = new File(getConfig().getDefaultThemesPath()+"/global.css");
        if(globalCSSFile.exists())
        {
            getLogger().info("Found global default style sheet. Adding ...");
            getStylesheets().add(globalCSSFile.toURI().toString());
        }
    }

    Themes themes;
    public void registerThemes() throws SevereException
    {
        logger.info("Loading themes ...");

        themes = new Themes(Config.getDefaultThemesPath(), getConfig().getThemesPath(), getConfig().getCurrentThemeFullName());
        
        if(!themes.getErrors().isEmpty())
        {
            StringBuilder themeErrors = new StringBuilder();

            for(MinorException eachException : themes.getErrors())
            {
                themeErrors.append("\n * ").append(eachException.getMessage());
            }

            if(themes.getIsBadThemeTheCurrentOne())
            {
                if(getConfig().getCurrentThemeFullName().equals(getConfig().getDefaultCurrentThemeFullName()))
                {
                    throw new SevereException(I18N.getString("window.Base.defaultThemeCorrupt", getConfig().getDefaultCurrentThemeFullName()));
                }

                themeErrors.append("\n\n").append(I18N.getString("window.Base.revertedToDefaultTheme" , getConfig().getDefaultCurrentThemeFullName()));

                getConfig().setCurrentThemeFullName(getConfig().getDefaultCurrentThemeFullName());
                getConfig().save();
            }

            handleMinorException(new MinorException(I18N.getString("window.Base.failedToLoadThemes", themeErrors.toString())));
        }
        logger.info("...Themes loaded successfully !");
    }

    @Override
    public Themes getThemes()
    {
        return themes;
    }


    public void applyDefaultTheme()
    {
        logger.info("Applying default theme ...");

        boolean foundTheme = false;
        for(Theme t: themes.getThemeList())
        {
            if(t.getFullName().equals(config.getCurrentThemeFullName()))
            {
                foundTheme = true;
                applyTheme(t);
                break;
            }
        }

        if(foundTheme)
        {
            logger.info("... Done!");
        }
        else
        {
            logger.info("Theme not found. reverting to light theme ...");
            try
            {
                Config.getInstance().setCurrentThemeFullName("com.stream_pi.defaultlight");
                Config.getInstance().save();

                applyDefaultTheme();
            }
            catch (SevereException e)
            {
                handleSevereException(e);
            }
        }
    }
}
