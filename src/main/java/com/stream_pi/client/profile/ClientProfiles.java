// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.profile;

import java.util.Iterator;
import com.stream_pi.client.io.Config;
import com.stream_pi.util.exception.SevereException;
import java.util.HashMap;
import com.stream_pi.util.exception.MinorException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.File;

public class ClientProfiles
{
    private File profilesFolder;
    private String defaultProfileID;
    private Logger logger;
    private ArrayList<MinorException> loadingErrors;
    private HashMap<String, ClientProfile> clientProfiles;
    
    public ClientProfiles(final File profilesFolder, final String defaultProfileID) throws SevereException {
        this.logger = Logger.getLogger(ClientProfiles.class.getName());
        this.defaultProfileID = defaultProfileID;
        this.profilesFolder = profilesFolder;
        this.clientProfiles = new HashMap<String, ClientProfile>();
        this.loadingErrors = new ArrayList<MinorException>();
        this.loadProfiles();
    }
    
    public void addProfile(final ClientProfile clientProfile) throws CloneNotSupportedException {
        this.clientProfiles.put(clientProfile.getID(), (ClientProfile)clientProfile.clone());
    }
    
    public void deleteProfile(final ClientProfile clientProfile) {
        this.clientProfiles.remove(clientProfile.getID());
        clientProfile.deleteProfile();
    }
    
    public void loadProfiles() throws SevereException {
        this.logger.info("Loading profiles ...");
        final String iconsPath = Config.getInstance().getIconsPath();
        this.clientProfiles.clear();
        this.loadingErrors.clear();
        if (!this.profilesFolder.isDirectory()) {
            throw new SevereException("Profiles", "Profile folder doesn't exist! Cant continue.");
        }
        final File[] profilesFiles = this.profilesFolder.listFiles();
        if (profilesFiles == null) {
            throw new SevereException("Profiles", "profilesFiles returned null. Cant continue!");
        }
        final File[] array = profilesFiles;
        for (int length = array.length, i = 0; i < length; ++i) {
            final File eachProfileFile = array[i];
            try {
                final ClientProfile profile = new ClientProfile(eachProfileFile, iconsPath);
                try {
                    this.addProfile(profile);
                }
                catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    throw new SevereException(e.getMessage());
                }
            }
            catch (MinorException e2) {
                if (eachProfileFile.getName().replace(".xml", "").equals(this.defaultProfileID)) {
                    throw new SevereException("Profiles", "Default profile bad. Can't continue");
                }
                this.loadingErrors.add(new MinorException(e2.getMessage() + " (" + eachProfileFile.getName().replace(".xml", "")));
                e2.printStackTrace();
            }
        }
        this.logger.info("Loaded all profiles!");
    }
    
    public ArrayList<MinorException> getLoadingErrors() {
        return this.loadingErrors;
    }
    
    public ArrayList<ClientProfile> getClientProfiles() {
        final ArrayList<ClientProfile> p = new ArrayList<ClientProfile>();
        for (final String profile : this.clientProfiles.keySet()) {
            p.add(this.clientProfiles.get(profile));
        }
        return p;
    }
    
    public ClientProfile getProfileFromID(final String profileID) {
        return this.clientProfiles.getOrDefault(profileID, null);
    }
}
