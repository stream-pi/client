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

package com.stream_pi.client.profile;

import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.io.Config;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ClientProfiles {


    private File profilesFolder;
    private String defaultProfileID;

    private Logger logger;

    public ClientProfiles(File profilesFolder, String defaultProfileID) throws SevereException
    {
       logger = Logger.getLogger(ClientProfiles.class.getName());

        this.defaultProfileID = defaultProfileID;
        this.profilesFolder = profilesFolder;
        clientProfiles = new HashMap<>();
        loadingErrors = new ArrayList<>();

        loadProfiles();
    }

    public void addProfile(ClientProfile clientProfile) throws CloneNotSupportedException {
        clientProfiles.put(clientProfile.getID(), (ClientProfile) clientProfile.clone());
    }

    public void deleteProfile(ClientProfile clientProfile)
    {
        clientProfile.deleteProfile();
        clientProfiles.remove(clientProfile.getID());
    }

    private ArrayList<MinorException> loadingErrors;
    private HashMap<String, ClientProfile> clientProfiles;

    public void loadProfiles() throws SevereException
    {
        logger.info("Loading profiles ...");


        String iconsPath = Config.getInstance().getIconsPath();

        clientProfiles.clear();
        loadingErrors.clear();

        if(!profilesFolder.isDirectory())
        {
            throw new SevereException(I18N.getString("profile.ClientProfiles.profileFolderNotADirectoryOrDoesNotExist", profilesFolder.getAbsolutePath()));
        }


        File[] profilesFiles = profilesFolder.listFiles();
        if(profilesFiles == null)
        {
            throw new SevereException(I18N.getString("profile.ClientProfiles.profileFoldersIsNull", profilesFolder.getAbsolutePath()));
        }

        for(File eachProfileFile : profilesFiles)
        {
            try
            {
                ClientProfile profile = new ClientProfile(eachProfileFile, iconsPath);
                try
                {
                    addProfile(profile);
                }
                catch (CloneNotSupportedException e)
                {
                    e.printStackTrace();
                    throw new SevereException(e.getMessage());
                }
            }
            catch (MinorException e)
            {
                if(eachProfileFile.getName().replace(".xml","").equals(defaultProfileID))
                {
                    throw new SevereException(I18N.getString("profile.ClientProfiles.defaultProfileBad", defaultProfileID, e.getMessage()));
                }

                loadingErrors.add(new MinorException(e.getMessage()+" ("+eachProfileFile.getName().replace(".xml", "")));

                e.printStackTrace();
            }
        }

        logger.info("Loaded all profiles!");
    }

    public ArrayList<MinorException> getLoadingErrors()
    {
        return loadingErrors;
    }

    public ArrayList<ClientProfile> getClientProfiles()
    {
        ArrayList<ClientProfile> p = new ArrayList<>();
        for(String profile : clientProfiles.keySet())
            p.add(clientProfiles.get(profile));
        return p;
    }
    
    public ClientProfile getProfileFromID(String profileID)
    {
        return clientProfiles.getOrDefault(profileID, null);
    }



}
