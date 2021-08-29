// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.info;

import java.io.File;
import com.gluonhq.attach.storage.StorageService;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.platform.ReleaseStatus;
import com.stream_pi.util.version.Version;

public class ClientInfo
{
    private Version version;
    private final ReleaseStatus releaseStatus;
    private Platform platform;
    private String prePath;
    private Version minThemeSupportVersion;
    private Version minPluginSupportVersion;
    private Version commStandardVersion;
    private static ClientInfo instance;
    
    private ClientInfo() {
        this.version = new Version(1, 0, 0);
        this.minThemeSupportVersion = new Version(1, 0, 0);
        this.minPluginSupportVersion = new Version(1, 0, 0);
        this.commStandardVersion = new Version(1, 0, 0);
        this.releaseStatus = ReleaseStatus.EA;
        final String osName = System.getProperty("os.name").toLowerCase();
        this.prePath = System.getProperty("user.home") + "/Stream-Pi/Client/";
        if (osName.contains("windows")) {
            this.platform = Platform.WINDOWS;
        }
        else if (osName.contains("linux")) {
            this.platform = Platform.LINUX;
        }
        else if (osName.contains("android") || osName.contains("ios")) {
            StorageService.create().ifPresent(s -> s.getPrivateStorage().ifPresentOrElse(sp -> this.prePath = sp.getAbsolutePath() + "/Stream-Pi/Client/", () -> this.prePath = null));
            this.platform = Platform.valueOf(osName.toUpperCase());
        }
        else if (osName.contains("mac")) {
            this.platform = Platform.MAC;
        }
        else {
            this.platform = Platform.UNKNOWN;
        }
    }
    
    public static synchronized ClientInfo getInstance() {
        if (ClientInfo.instance == null) {
            ClientInfo.instance = new ClientInfo();
        }
        return ClientInfo.instance;
    }
    
    public String getPrePath() {
        return this.prePath;
    }
    
    public Platform getPlatform() {
        return this.platform;
    }
    
    public Version getVersion() {
        return this.version;
    }
    
    public ReleaseStatus getReleaseStatus() {
        return this.releaseStatus;
    }
    
    public Version getMinThemeSupportVersion() {
        return this.minThemeSupportVersion;
    }
    
    public Version getMinPluginSupportVersion() {
        return this.minPluginSupportVersion;
    }
    
    public Version getCommStandardVersion() {
        return this.commStandardVersion;
    }
    
    public boolean isPhone() {
        return this.getPlatform() == Platform.ANDROID || this.getPlatform() == Platform.IOS;
    }
    
    static {
        ClientInfo.instance = null;
    }
}
