/*
ServerInfo.java

Stores basic information about the server - name, platform type

Contributors: Debayan Sutradhar (@dubbadhar)
 */

package com.stream_pi.client.info;

import com.gluonhq.attach.storage.StorageService;
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
    private Platform platform;

    private String prePath;

    private Version minThemeSupportVersion;
    private Version minPluginSupportVersion;
    private Version commStandardVersion;

    private String runnerFileName;

    private static ClientInfo instance = null;

    private ClientInfo()
    {
        version = new Version(1,0,0);
        minThemeSupportVersion = new Version(1,0,0);
        minPluginSupportVersion = new Version(1,0,0);
        commStandardVersion = new Version(1,0,0);

        releaseStatus = ReleaseStatus.EA;

        String osName = System.getProperty("os.name").toLowerCase();

        prePath = System.getProperty("user.home")+"/Stream-Pi/Client/";

        if(osName.contains("windows"))
        {
            platform = Platform.WINDOWS;
        }
        else if (osName.contains("linux"))
        {
            platform = Platform.LINUX;
        }
        else if(osName.contains("android")) // SPECIFY -Dsvm.targetName=android WHILE BUILDING ANDROID NATIVE IMAGE
        {
            StorageService.create().ifPresent(s->{
                s.getPublicStorage("Documents").ifPresentOrElse(sp->{
                    prePath = sp.getAbsolutePath()+"/Stream-Pi/Client/";
                }, ()->
                {
                    prePath = null;
                });
            });

            platform = Platform.ANDROID;
        }
        else if(osName.contains("ios")) // SPECIFY -Dsvm.targetName=ios WHILE BUILDING ANDROID NATIVE IMAGE
        {
            StorageService.create().ifPresent(s->{
                s.getPrivateStorage().ifPresentOrElse(sp->{
                    prePath = sp.getAbsolutePath()+"/Stream-Pi/Client/";
                }, ()->
                {
                    prePath = null;
                });
            });

            platform = Platform.IOS;
        }
        else if (osName.contains("mac"))
        {
            platform = Platform.MAC;
        }
        else
        {
            platform = Platform.UNKNOWN;
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

    public Platform getPlatform()
    {
        return platform;
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

    public Version getCommStandardVersion()
    {
        return commStandardVersion;
    }
}
