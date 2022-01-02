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

/*
Config.java

Contributor(s) : Debayan Sutradhar (@rnayabed)

handler for config.xml
 */

package com.stream_pi.client.io;

import java.io.File;
import java.util.Locale;
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
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.iohelper.IOHelper;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.version.Version;
import com.stream_pi.util.xmlconfighelper.XMLConfigHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Config
{
    private static Config instance = null;

    private final File configFile;

    private final Document document;

    public Config() throws SevereException
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
            throw new SevereException(I18N.getString("io.config.unableToReadConfig", e.getLocalizedMessage()));
        }
    }

    public static synchronized Config getInstance() throws SevereException
    {
        if(instance == null)
        {
            instance = new Config();
        }

        return instance;
    }

    public static void nullify()
    {
        instance = null;
    }

    public static void unzipToDefaultPrePath() throws MinorException, SevereException
    {
        IOHelper.unzip(Objects.requireNonNull(Main.class.getResourceAsStream("Default.zip")), ClientInfo.getInstance().getPrePath());

        Config tempConfig = new Config();
        tempConfig.setThemesPath(getDefaultThemesPath());
        tempConfig.setIconsPath(getDefaultIconsPath());
        tempConfig.setProfilesPath(getDefaultProfilesPath());
        tempConfig.setCurrentLanguageLocale(getDefaultLanguageLocale());
        tempConfig.setIsFullScreenMode(StartupFlags.DEFAULT_FULLSCREEN_MODE);
        tempConfig.save();
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
            throw new SevereException(I18N.getString("io.config.unableToSaveConfig", e.getLocalizedMessage()));
        }
    }

    public Version getVersion()
    {
        try
        {
            Node versionNode = document.getElementsByTagName("version").item(0);

            if (versionNode == null)
            {
                return null;
            }

            return new Version(versionNode.getTextContent());
        }
        catch (MinorException e)
        {
            return null;
        }
    }


    //Client Element
    
    //Default Values
    public String getDefaultClientName()
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

    public static String getDefaultThemesPath()
    {
        return ClientInfo.getInstance().getPrePath()+"Themes" + File.separator;
    }

    public static String getDefaultProfilesPath()
    {
        return ClientInfo.getInstance().getPrePath()+"Profiles" + File.separator;
    }

    public static String getDefaultIconsPath()
    {
        return ClientInfo.getInstance().getPrePath()+"Icons" + File.separator;
    }

    //Getters

    public String getClientName()
    {
        return XMLConfigHelper.getStringProperty(document, "name", getDefaultClientName(), false, true, document, configFile);
    }

    public String getStartupProfileID()
    {
        return XMLConfigHelper.getStringProperty(document, "startup-profile", getDefaultStartupProfileID(), false, true, document, configFile);
    }

    public String getCurrentThemeFullName()
    {
        return XMLConfigHelper.getStringProperty(document, "current-theme-full-name", getDefaultCurrentThemeFullName(), false, true, document, configFile);
    }

    public String getThemesPath()
    {
        if(ClientInfo.getInstance().isPhone())
            return ClientInfo.getInstance().getPrePath() + "Themes" + File.separator;

        return XMLConfigHelper.getStringProperty(document, "themes-path", getDefaultThemesPath(), false, true, document, configFile);
    }

    public String getProfilesPath()
    {
        if(ClientInfo.getInstance().isPhone())
            return ClientInfo.getInstance().getPrePath() + "Profiles" + File.separator;
        
        return XMLConfigHelper.getStringProperty(document, "profiles-path", getDefaultProfilesPath(), false, true, document, configFile);
    }

    public String getIconsPath()
    {
        if(ClientInfo.getInstance().isPhone())
            return ClientInfo.getInstance().getPrePath() + "Icons" + File.separator;
        
        return XMLConfigHelper.getStringProperty(document, "icons-path", getDefaultIconsPath(), false, true, document, configFile);
    }



    public void setCurrentLanguageLocale(Locale locale)
    {
        getOthersElement().getElementsByTagName("language-locale").item(0).setTextContent(locale.toLanguageTag());
    }

    public Locale getCurrentLanguageLocale()
    {
        return Locale.forLanguageTag(XMLConfigHelper.getStringProperty(getOthersElement(), "language-locale",
                getDefaultLanguageLocale().toLanguageTag(), false, true, document, configFile));
    }

    public static Locale getDefaultLanguageLocale()
    {
        return Locale.getDefault();
    }


    //Setters

    public void setName(String name)
    {
        document.getElementsByTagName("name").item(0).setTextContent(name);
    }

    public void setStartupProfileID(String id)
    {
        document.getElementsByTagName("startup-profile").item(0).setTextContent(id);
    }

    public void setCurrentThemeFullName(String name)
    {
        document.getElementsByTagName("current-theme-full-name").item(0).setTextContent(name);
    }

    public void setProfilesPath(String profilesPath)
    {
        document.getElementsByTagName("profiles-path").item(0).setTextContent(profilesPath);
    }

    public void setIconsPath(String iconsPath)
    {
        document.getElementsByTagName("icons-path").item(0).setTextContent(iconsPath);
    }

    public void setThemesPath(String themesPath)
    {
        document.getElementsByTagName("themes-path").item(0).setTextContent(themesPath);
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





    //screen-mover
    public Element getScreenMoverElement()
    {
        return (Element) document.getElementsByTagName("screen-mover").item(0);
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
        return (Element) document.getElementsByTagName("startup-window-size").item(0);
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

    public int getDefaultScreenMoverInterval()
    {
        return 120000;
    }

    public int getScreenMoverInterval()
    {
        return XMLConfigHelper.getIntProperty(getScreenMoverElement(), "interval", getDefaultScreenMoverInterval(), false, true, document, configFile);
    }

    public void setScreenMoverInterval(String value)
    {
        getScreenMoverElement().getElementsByTagName("interval").item(0).setTextContent(value);
    }

    public int getDefaultScreenMoverXChange()
    {
        return 5;
    }

    public int getScreenMoverXChange()
    {
        return XMLConfigHelper.getIntProperty(getScreenMoverElement(), "x-change", getDefaultScreenMoverXChange(), false, true, document, configFile);
    }

    public void setScreenMoverXChange(String value)
    {
        getScreenMoverElement().getElementsByTagName("x-change").item(0).setTextContent(value);
    }

    public int getDefaultScreenMoverYChange()
    {
        return 5;
    }

    public int getScreenMoverYChange()
    {
        return XMLConfigHelper.getIntProperty(getScreenMoverElement(), "y-change", getDefaultScreenMoverYChange(), false, true, document, configFile);
    }

    public void setScreenMoverYChange(String value)
    {
        getScreenMoverElement().getElementsByTagName("y-change").item(0).setTextContent(value);
    }

    public boolean getDefaultScreenMoverEnabled()
    {
        return false;
    }

    public boolean isScreenMoverEnabled()
    {
        return XMLConfigHelper.getBooleanProperty(getScreenMoverElement(), "status", getDefaultScreenMoverEnabled(), false, true, document, configFile);
    }

    public void setScreenMoverEnabled(boolean value)
    {
        getScreenMoverElement().getElementsByTagName("status").item(0).setTextContent(value+"");
    }

    public boolean getDefaultInvertRowsColsOnDeviceRotate()
    {
        return true;
    }

    public boolean isInvertRowsColsOnDeviceRotate()
    {
        return XMLConfigHelper.getBooleanProperty(getOthersElement(), "invert-rows-cols-on-device-rotate", getDefaultInvertRowsColsOnDeviceRotate(), false, true, document, configFile);
    }

    public void setInvertRowsColsOnDeviceRotate(boolean value)
    {
        getOthersElement().getElementsByTagName("invert-rows-cols-on-device-rotate").item(0).setTextContent(value+"");
    }
}
