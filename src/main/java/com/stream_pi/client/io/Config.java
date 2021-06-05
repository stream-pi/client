/*
Config.java

Contributor(s) : Debayan Sutradhar (@rnayabed)

handler for config.xml
 */

package com.stream_pi.client.io;

import java.io.File;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.stream_pi.client.Main;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.iohelper.IOHelper;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.xmlconfighelper.XMLConfigHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Config
{
    private static Config instance = null;

    private final File configFile;

    private Document document;

    private Config() throws SevereException
    {
        try
        {
            configFile = new File(ClientInfo.getInstance().getPrePath()+"config.xml");
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(configFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SevereException("Config", "unable to read config.xml");
        }
    }

    public static synchronized Config getInstance() throws SevereException
    {
        if(instance == null)
            instance = new Config();

        return instance;
    }

    public static void unzipToDefaultPrePath() throws Exception
    {
        IOHelper.unzip(Objects.requireNonNull(Main.class.getResourceAsStream("Default.zip")), ClientInfo.getInstance().getPrePath());
        Config.getInstance().setThemesPath(ClientInfo.getInstance().getPrePath()+"Themes/");
        Config.getInstance().setIconsPath(ClientInfo.getInstance().getPrePath()+"Icons/");
        Config.getInstance().setProfilesPath(ClientInfo.getInstance().getPrePath()+"Profiles/");

        Config.getInstance().setIsFullScreenMode(StartupFlags.DEFAULT_FULLSCREEN_MODE);

        Config.getInstance().save();
    }

    public void save() throws SevereException
    {
        try
        {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(configFile);
            Source input = new DOMSource(document);

            transformer.transform(input, output);
        }
        catch (Exception e)
        {
            throw new SevereException("Config", "unable to save config.xml");
        }
    }


    //Client Element
    public Element getClientElement()
    {
        return (Element) document.getElementsByTagName("client").item(0);
    }
    
    //Default Values
    public String getDefaultClientNickName()
    {
        return "Stream-Pi Client";
    }

    public String getDefaultStartupProfileID()
    {
        return "default";
    }

    public String getDefaultCurrentThemeFullName()
    {
        return "com.stream_pi.defaultlight";
    }

    public String getDefaultThemesPath()
    {
        return ClientInfo.getInstance().getPrePath()+"Themes/";
    }

    public String getDefaultProfilesPath()
    {
        return ClientInfo.getInstance().getPrePath()+"Profiles/";
    }

    public String getDefaultIconsPath()
    {
        return ClientInfo.getInstance().getPrePath()+"Icons/";
    }

    //Getters

    public String getClientNickName()
    {
        return XMLConfigHelper.getStringProperty(getClientElement(), "nickname", getDefaultClientNickName(), false, true, document, configFile);
    }

    public String getStartupProfileID()
    {
        return XMLConfigHelper.getStringProperty(getClientElement(), "startup-profile", getDefaultStartupProfileID(), false, true, document, configFile);
    }

    public String getCurrentThemeFullName()
    {
        return XMLConfigHelper.getStringProperty(getClientElement(), "current-theme-full-name", getDefaultCurrentThemeFullName(), false, true, document, configFile);
    }

    public String getThemesPath()
    {
        Platform platform = ClientInfo.getInstance().getPlatform();
        if(platform != Platform.ANDROID &&
                platform != Platform.IOS)
            return ClientInfo.getInstance().getPrePath() + "Themes/";

        return XMLConfigHelper.getStringProperty(getClientElement(), "themes-path", getDefaultThemesPath(), false, true, document, configFile);
    }

    public String getProfilesPath()
    {
        Platform platform = ClientInfo.getInstance().getPlatform();
        if(platform != Platform.ANDROID &&
                platform != Platform.IOS)
            return ClientInfo.getInstance().getPrePath() + "Profiles/";
        
        return XMLConfigHelper.getStringProperty(getClientElement(), "profiles-path", getDefaultProfilesPath(), false, true, document, configFile);
    }

    public String getIconsPath()
    {
        Platform platform = ClientInfo.getInstance().getPlatform();
        if(platform != Platform.ANDROID &&
                platform != Platform.IOS)
            return ClientInfo.getInstance().getPrePath() + "Icons/";
        
        return XMLConfigHelper.getStringProperty(getClientElement(), "icons-path", getDefaultIconsPath(), false, true, document, configFile);
    }

    

    //Setters

    public void setNickName(String nickName)
    {
        getClientElement().getElementsByTagName("nickname").item(0).setTextContent(nickName);
    }

    public void setStartupProfileID(String id)
    {
        getClientElement().getElementsByTagName("startup-profile").item(0).setTextContent(id);
    }

    public void setCurrentThemeFullName(String name)
    {
        getClientElement().getElementsByTagName("current-theme-full-name").item(0).setTextContent(name);
    }

    public void setProfilesPath(String profilesPath)
    {
        getClientElement().getElementsByTagName("profiles-path").item(0).setTextContent(profilesPath);
    }

    public void setIconsPath(String iconsPath)
    {
        getClientElement().getElementsByTagName("icons-path").item(0).setTextContent(iconsPath);
    }

    public void setThemesPath(String themesPath)
    {
        getClientElement().getElementsByTagName("themes-path").item(0).setTextContent(themesPath);
    }












    //comms-server
    public Element getCommsServerElement()
    {
        return (Element) document.getElementsByTagName("comms-server").item(0);
    }

    public String getDefaultSavedServerHostNameOrIP()
    {
        return "127.0.0.1";
    }

    public int getDefaultSavedServerPort()
    {
        return -1;
    }


    public String getSavedServerHostNameOrIP()
    {
        return XMLConfigHelper.getStringProperty(getCommsServerElement(), "hostname-ip", getDefaultSavedServerHostNameOrIP(), false, true, document, configFile);
    }

    public int getSavedServerPort()
    {
        return XMLConfigHelper.getIntProperty(getCommsServerElement(), "port", getDefaultSavedServerPort(), false, true, document, configFile);
    }

    public void setServerHostNameOrIP(String hostNameOrIP)
    {
        getCommsServerElement().getElementsByTagName("hostname-ip").item(0).setTextContent(hostNameOrIP);
    }

    public void setServerPort(int port)
    {
        getCommsServerElement().getElementsByTagName("port").item(0).setTextContent(port+"");
    }





    


    //others
    public Element getOthersElement()
    {
        return (Element) document.getElementsByTagName("others").item(0);
    }

    //others-default

    public boolean getDefaultStartOnBoot()
    {
        return false;
    }

    public boolean getDefaultIsShowCursor()
    {
        return true;
    }

    public boolean getDefaultFirstTimeUse()
    {
        return true;
    }


    
    public boolean isShowCursor()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "show-cursor", getDefaultIsShowCursor(), false, true, document, configFile);
    }

    
    public boolean isStartOnBoot()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "start-on-boot", getDefaultStartOnBoot(), false, true, document, configFile);
    }

    
    public boolean isFirstTimeUse()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "first-time-use", true, false, true, document, configFile);
    }

    public boolean isVibrateOnActionClicked()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "vibrate-on-action-clicked", false, false, true, document, configFile);
    }

    public boolean isConnectOnStartup()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "connect-on-startup", false, false, true, document, configFile);
    }

    public boolean getIsFullScreenMode()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "full-screen-mode", false, false, true, document, configFile);
    }


    public void setStartOnBoot(boolean value)
    {
        getOthersElement().getElementsByTagName("start-on-boot").item(0).setTextContent(value+"");
    }

    public void setShowCursor(boolean value)
    {
        getOthersElement().getElementsByTagName("show-cursor").item(0).setTextContent(value+"");
    }

    public void setFullscreen(boolean value)
    {
        getOthersElement().getElementsByTagName("fullscreen").item(0).setTextContent(value+"");
    }

    public void setFirstTimeUse(boolean value)
    {
        getOthersElement().getElementsByTagName("first-time-use").item(0).setTextContent(value+"");
    }

    public void setVibrateOnActionClicked(boolean value)
    {
        getOthersElement().getElementsByTagName("vibrate-on-action-clicked").item(0).setTextContent(value+"");
    }

    public void setConnectOnStartup(boolean value)
    {
        getOthersElement().getElementsByTagName("connect-on-startup").item(0).setTextContent(value+"");
    }

    public void setIsFullScreenMode(boolean value)
    {
        getOthersElement().getElementsByTagName("full-screen-mode").item(0).setTextContent(value+"");
    }



    private Element getStartupWindowSizeElement()
    {
        return (Element) getClientElement().getElementsByTagName("startup-window-size").item(0);
    }

    public double getStartupWindowWidth()
    {
        return XMLConfigHelper.getDoubleProperty(getStartupWindowSizeElement(), "width",
                getDefaultStartupWindowWidth(), false, true, document, configFile);
    }

    public double getStartupWindowHeight()
    {
        return XMLConfigHelper.getDoubleProperty(getStartupWindowSizeElement(), "height",
                getDefaultStartupWindowHeight(), false, true, document, configFile);
    }


    public int getDefaultStartupWindowWidth()
    {
        return 800;
    }

    public int getDefaultStartupWindowHeight()
    {
        return 400;
    }

    public void setStartupWindowSize(double width, double height)
    {
        setStartupWindowWidth(width);
        setStartupWindowHeight(height);
    }

    public void setStartupWindowWidth(double width)
    {
        getStartupWindowSizeElement().getElementsByTagName("width").item(0).setTextContent(width+"");
    }

    public void setStartupWindowHeight(double height)
    {
        getStartupWindowSizeElement().getElementsByTagName("height").item(0).setTextContent(height+"");
    }

    public void setStartupIsXMode(boolean value)
    {
        getOthersElement().getElementsByTagName("start-on-boot-x-mode").item(0).setTextContent(value+"");
    }

    public boolean getDefaultIsStartupXMode()
    {
        return false;
    }

    public boolean isStartupXMode()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "start-on-boot-x-mode", getDefaultIsStartupXMode(), false, true, document, configFile);
    }


    public boolean getDefaultIsTryConnectingWhenActionClicked()
    {
        return false;
    }

    public boolean isTryConnectingWhenActionClicked()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "try-connecting-when-action-clicked", getDefaultIsTryConnectingWhenActionClicked(), false, true, document, configFile);
    }

    public void setTryConnectingWhenActionClicked(boolean value)
    {
        getOthersElement().getElementsByTagName("try-connecting-when-action-clicked").item(0).setTextContent(value+"");
    }


    public boolean getDefaultScreenSaverEnabled()
    {
        return false;
    }

    public boolean isScreenSaverEnabled()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "screen-saver", getDefaultScreenSaverEnabled(), false, true, document, configFile);
    }

    public void setScreenSaverEnabled(boolean value)
    {
        getOthersElement().getElementsByTagName("screen-saver").item(0).setTextContent(value+"");
    }

    public int getDefaultScreenSaverTimeout()
    {
        return 60;
    }

    public int getScreenSaverTimeout()
    {
        return XMLConfigHelper.getIntProperty(getOthersElement(), "screen-saver-timeout-seconds", getDefaultScreenSaverTimeout(), false, true, document, configFile);
    }

    public void setScreenSaverTimeout(String value)
    {
        getOthersElement().getElementsByTagName("screen-saver-timeout-seconds").item(0).setTextContent(value);
    }
}
