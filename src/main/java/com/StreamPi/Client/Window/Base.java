package com.StreamPi.Client.Window;

import com.StreamPi.Client.Connection.ClientListener;
import com.StreamPi.Client.IO.Config;
import com.StreamPi.Client.Info.ClientInfo;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.StreamPi.Client.Main;
import com.StreamPi.Client.Profile.ClientProfiles;
import com.StreamPi.Client.Window.Dashboard.DashboardBase;
import com.StreamPi.Client.Window.FirstTimeUse.FirstTimeUse;
import com.StreamPi.Client.Window.Settings.SettingsBase;
import com.StreamPi.ThemeAPI.Theme;
import com.StreamPi.ThemeAPI.Themes;
import com.StreamPi.Util.Alert.StreamPiAlert;
import com.StreamPi.Util.ComboBox.StreamPiComboBox;
import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Exception.SevereException;
import com.StreamPi.Util.IOHelper.IOHelper;
import com.StreamPi.Util.LoggerHelper.StreamPiLogFallbackHandler;
import com.StreamPi.Util.LoggerHelper.StreamPiLogFileHandler;
import com.StreamPi.Util.Platform.Platform;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Services;

import javafx.geometry.Dimension2D;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public abstract class Base extends StackPane implements ExceptionAndAlertHandler, ClientListener {

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

    private DashboardBase dashboardBase;
    private SettingsBase settingsBase;

    private FirstTimeUse firstTimeUse;

    public FirstTimeUse getFirstTimeUse() {
        return firstTimeUse;
    }
    

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

    public void initLogger()
    {
        try
        {
            if(logger != null || logFileHandler != null)
                return;

            closeLogger();
            logger = Logger.getLogger("");

            if(new File(ClientInfo.getInstance().getPrePath()).getAbsoluteFile().getParentFile().canWrite())
            {

                String path = ClientInfo.getInstance().getPrePath()+"../streampi.log";

                if(ClientInfo.getInstance().getPlatformType() == Platform.ANDROID)
                    path = ClientInfo.getInstance().getPrePath()+"streampi.log";
    
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
            //throw new SevereException("Cant get logger started!");
        }
    }
    
    public void closeLogger()
    {
        if(logFileHandler != null)
            logFileHandler.close();
        else if(logFallbackHandler != null)
            logFallbackHandler.close();
    }

    public void initBase() throws SevereException
    {        
        
        stage = (Stage) getScene().getWindow();
        
        initLogger();

        clientInfo = ClientInfo.getInstance();
        dashboardBase = new DashboardBase(this, this);
        dashboardBase.setCache(true);
        dashboardBase.setCacheHint(CacheHint.SPEED);
        dashboardBase.prefWidthProperty().bind(widthProperty());
        dashboardBase.prefHeightProperty().bind(heightProperty());

        settingsBase = new SettingsBase(this, this);
        settingsBase.setCache(true);
        settingsBase.setCacheHint(CacheHint.SPEED);

        alertStackPane = new StackPane();
        alertStackPane.setVisible(false);

        StreamPiAlert.setParent(alertStackPane);
        StreamPiComboBox.setParent(alertStackPane);

        firstTimeUse = new FirstTimeUse(this, this);

        getChildren().clear();
        getChildren().addAll(settingsBase, dashboardBase, alertStackPane);
        
        setStyle(null);
        clearStylesheets();
        applyDefaultStylesheet();

        checkPrePathDirectory();

        config = Config.getInstance();

        if(config.isFirstTimeUse())
        {
            getChildren().add(firstTimeUse);
            firstTimeUse.toFront();
        }
        else
        {
            dashboardBase.toFront();
        }

        initThemes();


        logger.info("HEIGHT"+getHeight());
        
        if(clientInfo.getPlatformType()!= Platform.ANDROID && clientInfo.getPlatformType() != Platform.IOS)
        {
            stage.setWidth(config.getStartupWindowWidth());
            stage.setHeight(config.getStartupWindowHeight());
            stage.centerOnScreen();
            setupFlags();
        }
        else
        {
            /*Services.get(DisplayService.class).ifPresentOrElse(service->{
                Dimension2D resolution = service.getScreenResolution();
                float uiScale = service.getScreenScale();
                logger.info("resolution"+resolution.getHeight()+","+resolution.getWidth()+","+uiScale);
            },()->{
                logger.info("Display service not available");
            });
            


            getConfig().setStartupWindowSize(getWidth(), getHeight());
            getConfig().save();*/
        }
    }

    private void checkPrePathDirectory() throws SevereException
    {
        try 
        {
            File filex = new File(ClientInfo.getInstance().getPrePath());

            if(filex.getAbsoluteFile().getParentFile().canWrite())
            {
                if(!filex.exists())
                { 
                    filex.mkdirs();
                    IOHelper.unzip(Main.class.getResourceAsStream("Default.obj"), ClientInfo.getInstance().getPrePath());
                }
            }
            else
            {
                throw new SevereException("No storage permission. Give it!");
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SevereException(e.getMessage());
        }
    }

    public void setupFlags()
    {
        //Full Screen
        if(getConfig().isFullscreen())
        {
            getStage().setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            getStage().setFullScreen(true);
        }
        else
        {
            getStage().setFullScreenExitKeyCombination(KeyCombination.keyCombination("Esc"));
            getStage().setFullScreen(false);
        }

        //Cursor
        if(getConfig().isShowCursor())
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

    public void renderRootDefaultProfile() throws SevereException {
        getDashboardPane().renderProfile(getClientProfiles().getProfileFromID(
                Config.getInstance().getStartupProfileID()
        ));
    }



    public void clearStylesheets()
    {
        getStylesheets().clear();
    }

    public void initThemes() throws SevereException
    {
        registerThemes();
       
        applyDefaultTheme();
    }



    public void applyDefaultStylesheet()
    {
        Font.loadFont(Main.class.getResourceAsStream("Roboto.ttf"), 13);
        getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
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

        if(t.getFonts() != null)
        {
            for(String fontFile : t.getFonts())
            {
                Font.loadFont(fontFile.replace("%20",""), 13);
            }
        }
        currentTheme = t;
        getStylesheets().addAll(t.getStylesheets());

        logger.info("... Done!");
    }

    Themes themes;
    public void registerThemes() throws SevereException
    {
        logger.info("Loading themes ...");
        themes = new Themes(getConfig().getThemesPath(), getConfig().getCurrentThemeFullName(), clientInfo.getMinThemeSupportVersion());


        if(themes.getErrors().size()>0)
        {
            StringBuilder themeErrors = new StringBuilder();

            for(MinorException eachException : themes.getErrors())
            {
                themeErrors.append("\n * ").append(eachException.getShortMessage());
            }

            if(themes.getIsBadThemeTheCurrentOne())
            {
                themeErrors.append("\n\nReverted to default theme! (").append(getConfig().getDefaultCurrentThemeFullName()).append(")");

                getConfig().setCurrentThemeFullName(getConfig().getDefaultCurrentThemeFullName());
                getConfig().save();
            }

            handleMinorException(new MinorException("Theme Loading issues", themeErrors.toString()));
        }

        logger.info("... Done!");
    }

    @Override
    public Themes getThemes() {
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
            try {
                Config.getInstance().setCurrentThemeFullName("com.StreamPi.DefaultLight");
                Config.getInstance().save();

                applyDefaultTheme();
            }
            catch (SevereException e)
            {
                handleSevereException(e);
            }
        }


    }

    @Override
    public String getDefaultThemeFullName()
    {
        return config.getCurrentThemeFullName();
    }


}
