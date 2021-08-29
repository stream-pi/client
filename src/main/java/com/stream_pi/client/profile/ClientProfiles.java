package com.stream_pi.client.profile;

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
        clientProfiles.remove(clientProfile.getID());

        clientProfile.deleteProfile();
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
            throw new SevereException("Profiles","Profile folder doesn't exist! Cant continue.");
        }


        File[] profilesFiles = profilesFolder.listFiles();
        if(profilesFiles == null)
        {
            throw new SevereException("Profiles","profilesFiles returned null. Cant continue!");
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
                    throw new SevereException("Profiles", "Default profile bad. Can't continue");
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
