/*
ServerInfo.java

Stores basic information about the server - name, platform type

Contributors: Debayan Sutradhar (@dubbadhar)
 */

package com.stream_pi.client.info;

import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.platform.ReleaseStatus;
import com.stream_pi.util.version.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.function.Function;

public class ClientInfo {
    private Version version;
    private final ReleaseStatus releaseStatus;
    private Platform platformType = null;

    private String prePath;

    private Version minThemeSupportVersion;
    private Version minPluginSupportVersion;
    private Version commsStandardVersion;

    private String runnerFileName;

    private static ClientInfo instance = null;

    private ClientInfo(){

        try {
            version = new Version("1.0.0");
            minThemeSupportVersion = new Version("1.0.0");
            minPluginSupportVersion = new Version("1.0.0");
            commsStandardVersion = new Version("1.0.0");
        } catch (MinorException e) {
            e.printStackTrace();
        }

        releaseStatus = ReleaseStatus.EA;

        if(platformType == null)
        {
            String osName = System.getProperty("os.name").toLowerCase();

            if(osName.contains("windows"))
            {
                prePath = "data/";
                platformType = Platform.WINDOWS;
            }
            else if (osName.contains("linux"))
            {
                if(osName.contains("raspberrypi"))
                {
                    prePath = "data/";
                    platformType = Platform.LINUX_RPI;
                }
                else
                {
                    prePath = "data/";
                    platformType = Platform.LINUX;
                }
            }
            else if(osName.contains("android")) // SPECIFY -Dsvm.targetName=android WHILE BUILDING ANDROID NATIVE IMAGE
            {
                prePath = "/sdcard/Android/data/com.stream_pi.client/";
                platformType = Platform.ANDROID;
            }
            else if (osName.contains("mac"))
            {
                prePath = "data/";
                platformType = Platform.MAC;
            }
            else
            {
                prePath = "data/";
                platformType = Platform.UNKNOWN;
            }
        }


    }

    public void setRunnerFileName(String runnerFileName)
    {
        this.runnerFileName = runnerFileName;
    }

    public String getRunnerFileName() 
    {
        return runnerFileName;
    }

    public static synchronized ClientInfo getInstance(){
        if(instance == null)
        {
            instance = new ClientInfo();
        }

        return instance;
    }

    private boolean isShowShutDownButton = false;

    public void setShowShutDownButton(boolean showShutDownButton) {
        isShowShutDownButton = showShutDownButton;
    }

    private boolean isXMode = false;

    public void setXMode(boolean isXMode)
    {
        this.isXMode = isXMode;
    }

    public boolean isXMode() {
        return isXMode;
    }

    public boolean isShowShutDownButton() {
        return isShowShutDownButton;
    }

    public String getPrePath()
    {
        return prePath;
    }

    public Platform getPlatformType()
    {
        return platformType;
    }

    public Version getVersion() {
        return version;
    }

    public ReleaseStatus getReleaseStatus()
    {
        return releaseStatus;
    }

    public Version getMinThemeSupportVersion()
    {
        return minThemeSupportVersion;
    }

    public Version getMinPluginSupportVersion()
    {
        return minPluginSupportVersion;
    }

    public Version getCommsStandardVersion()
    {
        return commsStandardVersion;
    }
}
