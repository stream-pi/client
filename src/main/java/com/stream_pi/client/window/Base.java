// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window;

import com.stream_pi.util.exception.MinorException;
import java.util.Iterator;
import java.util.Collection;
import javafx.scene.text.Font;
import com.stream_pi.util.platform.Platform;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import com.stream_pi.util.exception.SevereException;
import javafx.scene.Node;
import com.stream_pi.util.combobox.StreamPiComboBox;
import com.stream_pi.util.alert.StreamPiAlert;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import java.util.Objects;
import com.stream_pi.client.Main;
import java.io.InputStream;
import java.util.logging.Handler;
import java.io.File;
import java.util.concurrent.Executors;
import com.stream_pi.theme_api.Themes;
import com.stream_pi.theme_api.Theme;
import javafx.application.HostServices;
import com.stream_pi.util.loggerhelper.StreamPiLogFallbackHandler;
import com.stream_pi.util.loggerhelper.StreamPiLogFileHandler;
import java.util.logging.Logger;
import com.stream_pi.client.window.firsttimeuse.FirstTimeUse;
import com.stream_pi.client.window.settings.SettingsBase;
import com.stream_pi.client.window.dashboard.DashboardBase;
import javafx.stage.Stage;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientProfiles;
import com.stream_pi.client.io.Config;
import java.util.concurrent.ExecutorService;
import com.stream_pi.client.controller.ClientListener;
import javafx.scene.layout.StackPane;

public abstract class Base extends StackPane implements ExceptionAndAlertHandler, ClientListener
{
    private final ExecutorService executor;
    private Config config;
    private ClientProfiles clientProfiles;
    private ClientInfo clientInfo;
    private Stage stage;
    private DashboardBase dashboardBase;
    private SettingsBase settingsBase;
    private FirstTimeUse firstTimeUse;
    private StackPane alertStackPane;
    private Logger logger;
    private StreamPiLogFileHandler logFileHandler;
    private StreamPiLogFallbackHandler logFallbackHandler;
    private HostServices hostServices;
    private Theme currentTheme;
    Themes themes;
    
    public Base() {
        this.executor = Executors.newCachedThreadPool();
        this.logger = null;
        this.logFileHandler = null;
        this.logFallbackHandler = null;
    }
    
    public Stage getStage() {
        return this.stage;
    }
    
    public Logger getLogger() {
        return this.logger;
    }
    
    public ClientProfiles getClientProfiles() {
        return this.clientProfiles;
    }
    
    public void setClientProfiles(final ClientProfiles clientProfiles) {
        this.clientProfiles = clientProfiles;
    }
    
    public void initLogger() {
        try {
            if (this.logFileHandler != null) {
                return;
            }
            this.closeLogger();
            this.logger = Logger.getLogger("com.stream_pi");
            if (new File(ClientInfo.getInstance().getPrePath()).getAbsoluteFile().getParentFile().canWrite()) {
                String path = ClientInfo.getInstance().getPrePath() + "../stream-pi-client.log";
                if (this.getClientInfo().isPhone()) {
                    path = ClientInfo.getInstance().getPrePath() + "stream-pi-client.log";
                }
                this.logFileHandler = new StreamPiLogFileHandler(path);
                this.logger.addHandler((Handler)this.logFileHandler);
            }
            else {
                this.logFallbackHandler = new StreamPiLogFallbackHandler();
                this.logger.addHandler((Handler)this.logFallbackHandler);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.logFallbackHandler = new StreamPiLogFallbackHandler();
            this.logger.addHandler((Handler)this.logFallbackHandler);
        }
    }
    
    public void closeLogger() {
        if (this.logFileHandler != null) {
            this.logFileHandler.close();
        }
        else if (this.logFallbackHandler != null) {
            this.logFallbackHandler.close();
        }
    }
    
    public void setHostServices(final HostServices hostServices) {
        this.hostServices = hostServices;
    }
    
    public HostServices getHostServices() {
        return this.hostServices;
    }
    
    public void initBase() throws SevereException {
        this.stage = (Stage)this.getScene().getWindow();
        this.getStage().getIcons().add((Object)new Image((InputStream)Objects.requireNonNull(Main.class.getResourceAsStream("app_icon.png"))));
        this.clientInfo = ClientInfo.getInstance();
        this.dashboardBase = new DashboardBase(this, this);
        this.dashboardBase.prefWidthProperty().bind((ObservableValue)this.widthProperty());
        this.dashboardBase.prefHeightProperty().bind((ObservableValue)this.heightProperty());
        this.settingsBase = new SettingsBase(this.getHostServices(), this, this);
        (this.alertStackPane = new StackPane()).setCache(true);
        this.alertStackPane.setCacheHint(CacheHint.SPEED);
        this.alertStackPane.setPadding(new Insets(10.0));
        this.alertStackPane.setOpacity(0.0);
        StreamPiAlert.setParent(this.alertStackPane);
        StreamPiComboBox.setParent(this.alertStackPane);
        this.getChildren().clear();
        this.getChildren().addAll((Object[])new Node[] { (Node)this.alertStackPane });
        if (this.getClientInfo().isPhone()) {
            this.dashboardBase.setPadding(new Insets(10.0));
            this.settingsBase.setPadding(new Insets(10.0));
        }
        this.initLogger();
        this.checkPrePathDirectory();
        this.getChildren().addAll((Object[])new Node[] { (Node)this.settingsBase, (Node)this.dashboardBase });
        this.setStyle((String)null);
        this.config = Config.getInstance();
        this.initThemes();
        if (this.config.isFirstTimeUse()) {
            this.firstTimeUse = new FirstTimeUse(this, this);
            this.getChildren().add((Object)this.firstTimeUse);
            if (this.getClientInfo().isPhone()) {
                this.firstTimeUse.setPadding(new Insets(10.0));
            }
            this.firstTimeUse.toFront();
            this.resizeAccordingToResolution();
        }
        else {
            this.dashboardBase.toFront();
        }
    }
    
    public void initThemes() throws SevereException {
        this.clearStylesheets();
        if (this.themes == null) {
            this.registerThemes();
        }
        this.applyDefaultStylesheet();
        this.applyDefaultTheme();
        this.applyDefaultIconsStylesheet();
        this.applyGlobalDefaultStylesheet();
    }
    
    private void resizeAccordingToResolution() {
        if (!this.getClientInfo().isPhone()) {
            final double height = this.getScreenHeight();
            final double width = this.getScreenWidth();
            if (height < 500.0) {
                this.setPrefHeight(320.0);
            }
            if (width < 500.0) {
                this.setPrefWidth(240.0);
            }
        }
    }
    
    public ExecutorService getExecutor() {
        return this.executor;
    }
    
    public double getStageWidth() {
        if (this.getClientInfo().isPhone()) {
            return this.getScreenWidth();
        }
        return this.getStage().getWidth();
    }
    
    public double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }
    
    public double getStageHeight() {
        if (ClientInfo.getInstance().isPhone()) {
            return this.getScreenHeight();
        }
        return this.getStage().getHeight();
    }
    
    public double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }
    
    private void checkPrePathDirectory() throws SevereException {
        try {
            final String path = this.getClientInfo().getPrePath();
            if (path == null) {
                this.throwStoragePermErrorAlert("Unable to access file system!");
                return;
            }
            final File file = new File(path);
            if (!file.exists()) {
                final boolean result = file.mkdirs();
                if (result) {
                    Config.unzipToDefaultPrePath();
                    this.initLogger();
                }
                else {
                    this.throwStoragePermErrorAlert("No storage permission. Give it!");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SevereException(e.getMessage());
        }
    }
    
    private void throwStoragePermErrorAlert(final String msg) throws SevereException {
        this.resizeAccordingToResolution();
        this.clearStylesheets();
        this.applyDefaultStylesheet();
        this.applyDefaultIconsStylesheet();
        this.applyGlobalDefaultStylesheet();
        this.getStage().show();
        throw new SevereException(msg);
    }
    
    public void setupFlags() throws SevereException {
        if (Config.getInstance().getIsFullScreenMode()) {
            this.getStage().setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            this.getStage().setFullScreen(true);
        }
        else {
            this.getStage().setFullScreenExitKeyCombination(KeyCombination.keyCombination("ESC"));
            this.getStage().setFullScreen(false);
        }
        if (Config.getInstance().isShowCursor()) {
            this.setCursor(Cursor.DEFAULT);
        }
        else {
            this.setCursor(Cursor.NONE);
        }
    }
    
    public SettingsBase getSettingsPane() {
        return this.settingsBase;
    }
    
    public DashboardBase getDashboardPane() {
        return this.dashboardBase;
    }
    
    public void renderRootDefaultProfile() {
        this.getDashboardPane().renderProfile(this.getClientProfiles().getProfileFromID(this.getConfig().getStartupProfileID()), true);
    }
    
    public void clearStylesheets() {
        this.getStylesheets().clear();
    }
    
    public void applyDefaultStylesheet() {
        if (this.clientInfo.getPlatform() != Platform.IOS) {
            Font.loadFont(Main.class.getResourceAsStream("Roboto.ttf"), 13.0);
        }
        this.getStylesheets().add((Object)Main.class.getResource("style.css").toExternalForm());
    }
    
    public void applyDefaultIconsStylesheet() {
        this.getStylesheets().add((Object)Main.class.getResource("default_icons.css").toExternalForm());
    }
    
    public Config getConfig() {
        return this.config;
    }
    
    public ClientInfo getClientInfo() {
        return this.clientInfo;
    }
    
    public Theme getCurrentTheme() {
        return this.currentTheme;
    }
    
    public void applyTheme(final Theme t) {
        this.logger.info("Applying theme '" + t.getFullName() + "' ...");
        if (t.getFonts() != null) {
            for (final String fontFile : t.getFonts()) {
                Font.loadFont(fontFile.replace("%20", ""), 13.0);
            }
        }
        this.currentTheme = t;
        this.getStylesheets().addAll((Collection)t.getStylesheets());
        this.logger.info("... Done!");
    }
    
    public void applyGlobalDefaultStylesheet() {
        final File globalCSSFile = new File(this.getConfig().getDefaultThemesPath() + "/global.css");
        if (globalCSSFile.exists()) {
            this.getLogger().info("Found global default style sheet. Adding ...");
            this.getStylesheets().add((Object)globalCSSFile.toURI().toString());
        }
    }
    
    public void registerThemes() throws SevereException {
        this.logger.info("Loading themes ...");
        this.themes = new Themes(this.getConfig().getDefaultThemesPath(), this.getConfig().getThemesPath(), this.getConfig().getCurrentThemeFullName(), this.clientInfo.getMinThemeSupportVersion());
        if (!this.themes.getErrors().isEmpty()) {
            final StringBuilder themeErrors = new StringBuilder();
            for (final MinorException eachException : this.themes.getErrors()) {
                themeErrors.append("\n * ").append(eachException.getMessage());
            }
            if (this.themes.getIsBadThemeTheCurrentOne()) {
                if (this.getConfig().getCurrentThemeFullName().equals(this.getConfig().getDefaultCurrentThemeFullName())) {
                    throw new SevereException("Unable to get default theme (" + this.getConfig().getDefaultCurrentThemeFullName() + ")\nPlease restore the theme or reinstall.");
                }
                themeErrors.append("\n\nReverted to default theme! (").append(this.getConfig().getDefaultCurrentThemeFullName()).append(")");
                this.getConfig().setCurrentThemeFullName(this.getConfig().getDefaultCurrentThemeFullName());
                this.getConfig().save();
            }
            this.handleMinorException(new MinorException("Theme Loading issues", themeErrors.toString()));
        }
        this.logger.info("...Themes loaded successfully !");
    }
    
    public Themes getThemes() {
        return this.themes;
    }
    
    public void applyDefaultTheme() {
        this.logger.info("Applying default theme ...");
        boolean foundTheme = false;
        for (final Theme t : this.themes.getThemeList()) {
            if (t.getFullName().equals(this.config.getCurrentThemeFullName())) {
                foundTheme = true;
                this.applyTheme(t);
                break;
            }
        }
        if (foundTheme) {
            this.logger.info("... Done!");
        }
        else {
            this.logger.info("Theme not found. reverting to light theme ...");
            try {
                Config.getInstance().setCurrentThemeFullName("com.stream_pi.defaultlight");
                Config.getInstance().save();
                this.applyDefaultTheme();
            }
            catch (SevereException e) {
                this.handleSevereException(e);
            }
        }
    }
    
    public String getDefaultThemeFullName() {
        return this.config.getCurrentThemeFullName();
    }
}
