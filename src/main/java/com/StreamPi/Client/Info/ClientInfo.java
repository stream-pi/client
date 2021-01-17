/*
ServerInfo.java

Stores basic information about the server - name, platform type

Contributors: Debayan Sutradhar (@dubbadhar)
 */

package com.StreamPi.Client.Info;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Platform.Platform;
import com.StreamPi.Util.Platform.ReleaseStatus;
import com.StreamPi.Util.Version.Version;

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

        // Hardcoded values for android because os.name returns linux on android
        
        //platformType = Platform.ANDROID;
        //prePath = "/sdcard/StreamPiClient/";

        if(platformType == null)
        {
            String osName = System.getProperty("os.name").toLowerCase();
            System.out.println("CCVVBB : '"+osName+"'");

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
                prePath = "/sdcard/StreamPiClient/";
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

    private boolean frameBufferMode = false;
    
    public void setFrameBufferMode(boolean frameBufferMode)
    {
        this.frameBufferMode = frameBufferMode;
    }

    public boolean isFrameBufferMode() 
    {
        return frameBufferMode;
    }

    public static synchronized ClientInfo getInstance(){
        if(instance == null)
        {
            instance = new ClientInfo();
        }

        return instance;
    }

    public String getPrePath() {
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
