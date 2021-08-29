// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.io;

import org.w3c.dom.Element;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.xmlconfighelper.XMLConfigHelper;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.util.iohelper.IOHelper;
import java.util.Objects;
import com.stream_pi.client.Main;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import com.stream_pi.util.exception.SevereException;
import javax.xml.parsers.DocumentBuilderFactory;
import com.stream_pi.client.info.ClientInfo;
import org.w3c.dom.Document;
import java.io.File;

public class Config
{
    private static Config instance;
    private final File configFile;
    private Document document;
    
    private Config() throws SevereException {
        try {
            this.configFile = new File(ClientInfo.getInstance().getPrePath() + "config.xml");
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.document = docBuilder.parse(this.configFile);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SevereException("Config", "unable to read config.xml\n" + e.getMessage());
        }
    }
    
    public static synchronized Config getInstance() throws SevereException {
        if (Config.instance == null) {
            Config.instance = new Config();
        }
        return Config.instance;
    }
    
    public static void nullify() {
        Config.instance = null;
    }
    
    public static void unzipToDefaultPrePath() throws Exception {
        IOHelper.unzip((InputStream)Objects.requireNonNull(Main.class.getResourceAsStream("Default.zip")), ClientInfo.getInstance().getPrePath());
        getInstance().setThemesPath(ClientInfo.getInstance().getPrePath() + "Themes/");
        getInstance().setIconsPath(ClientInfo.getInstance().getPrePath() + "Icons/");
        getInstance().setProfilesPath(ClientInfo.getInstance().getPrePath() + "Profiles/");
        getInstance().setIsFullScreenMode(StartupFlags.DEFAULT_FULLSCREEN_MODE);
        getInstance().save();
    }
    
    public void save() throws SevereException {
        try {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final Result output = new StreamResult(this.configFile);
            final Source input = new DOMSource(this.document);
            transformer.transform(input, output);
        }
        catch (Exception e) {
            throw new SevereException("Config", "unable to save config.xml");
        }
    }
    
    public String getDefaultClientNickName() {
        return "Stream-Pi Client";
    }
    
    public String getDefaultStartupProfileID() {
        return "default";
    }
    
    public String getDefaultCurrentThemeFullName() {
        return "com.stream_pi.defaultlight";
    }
    
    public String getDefaultCurrentAnimationName() {
        return "None";
    }
    
    public String getDefaultThemesPath() {
        return ClientInfo.getInstance().getPrePath() + "Themes/";
    }
    
    public String getDefaultProfilesPath() {
        return ClientInfo.getInstance().getPrePath() + "Profiles/";
    }
    
    public String getDefaultIconsPath() {
        return ClientInfo.getInstance().getPrePath() + "Icons/";
    }
    
    public String getClientNickName() {
        return XMLConfigHelper.getStringProperty((Node)this.document, "nickname", this.getDefaultClientNickName(), false, true, this.document, this.configFile);
    }
    
    public String getStartupProfileID() {
        return XMLConfigHelper.getStringProperty((Node)this.document, "startup-profile", this.getDefaultStartupProfileID(), false, true, this.document, this.configFile);
    }
    
    public String getCurrentThemeFullName() {
        return XMLConfigHelper.getStringProperty((Node)this.document, "current-theme-full-name", this.getDefaultCurrentThemeFullName(), false, true, this.document, this.configFile);
    }
    
    public String getCurrentAnimationName() {
        return XMLConfigHelper.getStringProperty((Node)this.document, "current-animation-name", this.getDefaultCurrentAnimationName(), false, true, this.document, this.configFile);
    }
    
    public String getThemesPath() {
        final Platform platform = ClientInfo.getInstance().getPlatform();
        if (platform != Platform.ANDROID && platform != Platform.IOS) {
            return ClientInfo.getInstance().getPrePath() + "Themes/";
        }
        return XMLConfigHelper.getStringProperty((Node)this.document, "themes-path", this.getDefaultThemesPath(), false, true, this.document, this.configFile);
    }
    
    public String getProfilesPath() {
        final Platform platform = ClientInfo.getInstance().getPlatform();
        if (platform != Platform.ANDROID && platform != Platform.IOS) {
            return ClientInfo.getInstance().getPrePath() + "Profiles/";
        }
        return XMLConfigHelper.getStringProperty((Node)this.document, "profiles-path", this.getDefaultProfilesPath(), false, true, this.document, this.configFile);
    }
    
    public String getIconsPath() {
        final Platform platform = ClientInfo.getInstance().getPlatform();
        if (platform != Platform.ANDROID && platform != Platform.IOS) {
            return ClientInfo.getInstance().getPrePath() + "Icons/";
        }
        return XMLConfigHelper.getStringProperty((Node)this.document, "icons-path", this.getDefaultIconsPath(), false, true, this.document, this.configFile);
    }
    
    public void setNickName(final String nickName) {
        this.document.getElementsByTagName("nickname").item(0).setTextContent(nickName);
    }
    
    public void setStartupProfileID(final String id) {
        this.document.getElementsByTagName("startup-profile").item(0).setTextContent(id);
    }
    
    public void setCurrentThemeFullName(final String name) {
        this.document.getElementsByTagName("current-theme-full-name").item(0).setTextContent(name);
    }
    
    public void setCurrentAnimationFullName(final String name) {
        this.document.getElementsByTagName("current-animation-name").item(0).setTextContent(name);
    }
    
    public void setProfilesPath(final String profilesPath) {
        this.document.getElementsByTagName("profiles-path").item(0).setTextContent(profilesPath);
    }
    
    public void setIconsPath(final String iconsPath) {
        this.document.getElementsByTagName("icons-path").item(0).setTextContent(iconsPath);
    }
    
    public void setThemesPath(final String themesPath) {
        this.document.getElementsByTagName("themes-path").item(0).setTextContent(themesPath);
    }
    
    public Element getCommsServerElement() {
        return (Element)this.document.getElementsByTagName("comms-server").item(0);
    }
    
    public String getDefaultSavedServerHostNameOrIP() {
        return "127.0.0.1";
    }
    
    public int getDefaultSavedServerPort() {
        return -1;
    }
    
    public String getSavedServerHostNameOrIP() {
        return XMLConfigHelper.getStringProperty((Node)this.getCommsServerElement(), "hostname-ip", this.getDefaultSavedServerHostNameOrIP(), false, true, this.document, this.configFile);
    }
    
    public int getSavedServerPort() {
        return XMLConfigHelper.getIntProperty((Node)this.getCommsServerElement(), "port", this.getDefaultSavedServerPort(), false, true, this.document, this.configFile);
    }
    
    public void setServerHostNameOrIP(final String hostNameOrIP) {
        this.getCommsServerElement().getElementsByTagName("hostname-ip").item(0).setTextContent(hostNameOrIP);
    }
    
    public void setServerPort(final int port) {
        this.getCommsServerElement().getElementsByTagName("port").item(0).setTextContent("" + port);
    }
    
    public Element getScreenMoverElement() {
        return (Element)this.document.getElementsByTagName("screen-mover").item(0);
    }
    
    public Element getOthersElement() {
        return (Element)this.document.getElementsByTagName("others").item(0);
    }
    
    public boolean getDefaultStartOnBoot() {
        return false;
    }
    
    public boolean getDefaultIsShowCursor() {
        return true;
    }
    
    public boolean getDefaultFirstTimeUse() {
        return true;
    }
    
    public boolean isShowCursor() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "show-cursor", this.getDefaultIsShowCursor(), false, true, this.document, this.configFile);
    }
    
    public boolean isStartOnBoot() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "start-on-boot", this.getDefaultStartOnBoot(), false, true, this.document, this.configFile);
    }
    
    public boolean isFirstTimeUse() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "first-time-use", true, false, true, this.document, this.configFile);
    }
    
    public boolean isVibrateOnActionClicked() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "vibrate-on-action-clicked", false, false, true, this.document, this.configFile);
    }
    
    public boolean isConnectOnStartup() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "connect-on-startup", false, false, true, this.document, this.configFile);
    }
    
    public boolean getIsFullScreenMode() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "full-screen-mode", false, false, true, this.document, this.configFile);
    }
    
    public void setStartOnBoot(final boolean value) {
        this.getOthersElement().getElementsByTagName("start-on-boot").item(0).setTextContent("" + value);
    }
    
    public void setShowCursor(final boolean value) {
        this.getOthersElement().getElementsByTagName("show-cursor").item(0).setTextContent("" + value);
    }
    
    public void setFullscreen(final boolean value) {
        this.getOthersElement().getElementsByTagName("fullscreen").item(0).setTextContent("" + value);
    }
    
    public void setFirstTimeUse(final boolean value) {
        this.getOthersElement().getElementsByTagName("first-time-use").item(0).setTextContent("" + value);
    }
    
    public void setVibrateOnActionClicked(final boolean value) {
        this.getOthersElement().getElementsByTagName("vibrate-on-action-clicked").item(0).setTextContent("" + value);
    }
    
    public void setConnectOnStartup(final boolean value) {
        this.getOthersElement().getElementsByTagName("connect-on-startup").item(0).setTextContent("" + value);
    }
    
    public void setIsFullScreenMode(final boolean value) {
        this.getOthersElement().getElementsByTagName("full-screen-mode").item(0).setTextContent("" + value);
    }
    
    private Element getStartupWindowSizeElement() {
        return (Element)this.document.getElementsByTagName("startup-window-size").item(0);
    }
    
    public double getStartupWindowWidth() {
        return XMLConfigHelper.getDoubleProperty((Node)this.getStartupWindowSizeElement(), "width", (double)this.getDefaultStartupWindowWidth(), false, true, this.document, this.configFile);
    }
    
    public double getStartupWindowHeight() {
        return XMLConfigHelper.getDoubleProperty((Node)this.getStartupWindowSizeElement(), "height", (double)this.getDefaultStartupWindowHeight(), false, true, this.document, this.configFile);
    }
    
    public int getDefaultStartupWindowWidth() {
        return 800;
    }
    
    public int getDefaultStartupWindowHeight() {
        return 400;
    }
    
    public void setStartupWindowSize(final double width, final double height) {
        this.setStartupWindowWidth(width);
        this.setStartupWindowHeight(height);
    }
    
    public void setStartupWindowWidth(final double width) {
        this.getStartupWindowSizeElement().getElementsByTagName("width").item(0).setTextContent("" + width);
    }
    
    public void setStartupWindowHeight(final double height) {
        this.getStartupWindowSizeElement().getElementsByTagName("height").item(0).setTextContent("" + height);
    }
    
    public void setStartupIsXMode(final boolean value) {
        this.getOthersElement().getElementsByTagName("start-on-boot-x-mode").item(0).setTextContent("" + value);
    }
    
    public boolean getDefaultIsStartupXMode() {
        return false;
    }
    
    public boolean isStartupXMode() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "start-on-boot-x-mode", this.getDefaultIsStartupXMode(), false, true, this.document, this.configFile);
    }
    
    public boolean getDefaultIsTryConnectingWhenActionClicked() {
        return false;
    }
    
    public boolean isTryConnectingWhenActionClicked() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "try-connecting-when-action-clicked", this.getDefaultIsTryConnectingWhenActionClicked(), false, true, this.document, this.configFile);
    }
    
    public void setTryConnectingWhenActionClicked(final boolean value) {
        this.getOthersElement().getElementsByTagName("try-connecting-when-action-clicked").item(0).setTextContent("" + value);
    }
    
    public boolean getDefaultScreenSaverEnabled() {
        return false;
    }
    
    public boolean isScreenSaverEnabled() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "screen-saver", this.getDefaultScreenSaverEnabled(), false, true, this.document, this.configFile);
    }
    
    public void setScreenSaverEnabled(final boolean value) {
        this.getOthersElement().getElementsByTagName("screen-saver").item(0).setTextContent("" + value);
    }
    
    public int getDefaultScreenSaverTimeout() {
        return 60;
    }
    
    public int getScreenSaverTimeout() {
        return XMLConfigHelper.getIntProperty((Node)this.getOthersElement(), "screen-saver-timeout-seconds", this.getDefaultScreenSaverTimeout(), false, true, this.document, this.configFile);
    }
    
    public void setScreenSaverTimeout(final String value) {
        this.getOthersElement().getElementsByTagName("screen-saver-timeout-seconds").item(0).setTextContent(value);
    }
    
    public int getDefaultScreenMoverInterval() {
        return 120000;
    }
    
    public int getScreenMoverInterval() {
        return XMLConfigHelper.getIntProperty((Node)this.getScreenMoverElement(), "interval", this.getDefaultScreenMoverInterval(), false, true, this.document, this.configFile);
    }
    
    public void setScreenMoverInterval(final String value) {
        this.getScreenMoverElement().getElementsByTagName("interval").item(0).setTextContent(value);
    }
    
    public int getDefaultScreenMoverXChange() {
        return 5;
    }
    
    public int getScreenMoverXChange() {
        return XMLConfigHelper.getIntProperty((Node)this.getScreenMoverElement(), "x-change", this.getDefaultScreenMoverXChange(), false, true, this.document, this.configFile);
    }
    
    public void setScreenMoverXChange(final String value) {
        this.getScreenMoverElement().getElementsByTagName("x-change").item(0).setTextContent(value);
    }
    
    public int getDefaultScreenMoverYChange() {
        return 5;
    }
    
    public int getScreenMoverYChange() {
        return XMLConfigHelper.getIntProperty((Node)this.getScreenMoverElement(), "y-change", this.getDefaultScreenMoverYChange(), false, true, this.document, this.configFile);
    }
    
    public void setScreenMoverYChange(final String value) {
        this.getScreenMoverElement().getElementsByTagName("y-change").item(0).setTextContent(value);
    }
    
    public boolean getDefaultScreenMoverEnabled() {
        return false;
    }
    
    public boolean isScreenMoverEnabled() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getScreenMoverElement(), "status", this.getDefaultScreenMoverEnabled(), false, true, this.document, this.configFile);
    }
    
    public void setScreenMoverEnabled(final boolean value) {
        this.getScreenMoverElement().getElementsByTagName("status").item(0).setTextContent("" + value);
    }
    
    public boolean getDefaultInvertRowsColsOnDeviceRotate() {
        return true;
    }
    
    public boolean isInvertRowsColsOnDeviceRotate() {
        return XMLConfigHelper.getBooleanProperty((Node)this.getOthersElement(), "invert-rows-cols-on-device-rotate", this.getDefaultInvertRowsColsOnDeviceRotate(), false, true, this.document, this.configFile);
    }
    
    public void setInvertRowsColsOnDeviceRotate(final boolean value) {
        this.getOthersElement().getElementsByTagName("invert-rows-cols-on-device-rotate").item(0).setTextContent("" + value);
    }
    
    static {
        Config.instance = null;
    }
}
