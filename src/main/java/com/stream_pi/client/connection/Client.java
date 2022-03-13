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

package com.stream_pi.client.connection;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.action_api.actionproperty.ClientProperties;
import com.stream_pi.action_api.actionproperty.gaugeproperties.GaugeProperties;
import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.actionproperty.property.StringProperty;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.action_api.externalplugin.inputevent.StreamPiInputEvent;
import com.stream_pi.client.controller.ClientExecutorService;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientAction;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.theme_api.ThemeAPI;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.comms.DisconnectReason;
import com.stream_pi.util.comms.Message;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.version.Version;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Client extends Thread
{

    private Socket socket;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private AtomicBoolean stop = new AtomicBoolean(false);

    private ClientListener clientListener;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ClientInfo clientInfo;

    private String serverIP;
    private int serverPort;
    private String serverName;

    private Logger logger;

    private Runnable onConnectAndSetupToBeRun;

    public Client(String serverIP, int serverPort, ClientListener clientListener,
                  ExceptionAndAlertHandler exceptionAndAlertHandler, Runnable onConnectAndSetupToBeRun)
    {
        this.serverIP = serverIP;
        this.serverPort = serverPort;

        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientInfo = ClientInfo.getInstance();
        this.clientListener = clientListener;

        this.onConnectAndSetupToBeRun = onConnectAndSetupToBeRun;

        logger = Logger.getLogger(Client.class.getName());

        if (clientListener.getLastClientFailSystemMills() > -1)
        {
            if ((System.currentTimeMillis() - clientListener.getLastClientFailSystemMills()) < 500 || StreamPiAlert.getParent().getChildren().size() > 0)
            {
                return;
            }
        }

        clientListener.setLastClientFailSystemMills();

        clientListener.setIsConnecting(true);

        ClientExecutorService.getExecutorService().submit(new Task<Void>() {
            @Override
            protected Void call()
            {
                try
                {
                    try
                    {
                        logger.info("Trying to connect to server at "+serverIP+":"+serverPort);
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(serverIP, serverPort), 5000);
                        clientListener.setConnected(true);
                        logger.info("Connected to "+socket.getRemoteSocketAddress()+" !");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        clientListener.setConnected(false);
                        throw new MinorException(I18N.getString("connection.Client.failedToConnectToServer"));
                    }
                    finally
                    {
                        clientListener.setIsConnecting(false);
                        clientListener.updateSettingsConnectDisconnectButton();
                    }

                    try
                    {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        ois = new ObjectInputStream(socket.getInputStream());
                    }
                    catch (IOException e)
                    {
                        logger.severe(e.getMessage());
                        e.printStackTrace();
                        throw new MinorException(I18N.getString("connection.Client.failedToSetUpIOStreams", e.getLocalizedMessage()));
                    }

                    start();
                }
                catch (MinorException e)
                {
                    exceptionAndAlertHandler.handleMinorException(e);
                }
                return null;
            }
        });
    }

    public synchronized void exit()
    {
        if(stop.get())
            return;

        logger.info("Stopping ...");

        try
        {
            if(socket!=null)
            {
                disconnect();
            }
        }
        catch (SevereException e)
        {
            logger.severe(e.getMessage());
            exceptionAndAlertHandler.handleSevereException(e);
            e.printStackTrace();
        }
    }


    public synchronized void sendMessage(Message message) throws SevereException
    {
        try
        {
            logger.info("Sending message with heading "+message.getHeader()+" ...");
            oos.writeObject(message);
            oos.flush();
            logger.info("... Done!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new SevereException(I18N.getString("connection.Client.failedToWriteToIOStream", e.getLocalizedMessage()));
        }
    }

    @Override
    public void run() {
        try
        {
            while(!stop.get())
            {
                try
                {
                    Message message = (Message) ois.readObject();

                    String header = message.getHeader();

                    logger.info("Message Received. Heading : "+header);

                    switch (header)
                    {
                        case "ready" :                                  onServerReady();
                            break;

                        case "action_icon" :                            onActionIconReceived(message);
                            break;

                        case "disconnect" :                             serverDisconnected(message);
                            break;

                        case "get_client_details" :                     sendClientDetails();
                            break;

                        case "server_details" :                         registerServerDetails(message);
                            break;

                        case "get_profiles" :                           sendProfileNamesToServer();
                            break;

                        case "get_profile_details":                     sendProfileDetailsToServer(message);
                            break;

                        case "save_action_details":                     saveActionDetails(message);
                            break;

                        case "delete_action":                           deleteAction(message);
                            break;

                        case "get_themes":                              sendThemesToServer();
                            break;

                        case "save_client_details":                     saveClientDetails(message);
                            break;

                        case "save_client_profile":                     saveProfileDetails(message);
                            break;

                        case "delete_profile":                          deleteProfile(message);
                            break;

                        case "action_failed":                           actionFailed(message);
                            break;

                        case "set_toggle_status":                       onSetToggleStatus(message);
                            break;

                        case "set_action_gauge_properties":             onSetActionGaugeProperties(message);
                            break;

                        case "set_action_gauge_value":                  onSetActionGaugeValue(message);
                            break;

                        case "update_action_temporary_display_text":    updateActionTemporaryDisplayText(message);
                            break;

                        default:                        logger.warning("Command '"+header+"' does not match records. Make sure client and server versions are equal.");

                    }
                }
                catch (IOException | ClassNotFoundException e)
                {
                    logger.severe(e.getMessage());
                    e.printStackTrace();

                    clientListener.setConnected(false);
                    clientListener.updateSettingsConnectDisconnectButton();
                    clientListener.onDisconnect();

                    if(!stop.get())
                    {
                        throw new MinorException(I18N.getString("connection.Client.accidentallyDisconnectedFromServer", serverName));
                    }

                    exit();

                    return;
                }
            }
        }
        catch (SevereException e)
        {
            logger.severe(e.getMessage());
            e.printStackTrace();

            exceptionAndAlertHandler.handleSevereException(e);
        }
        catch (MinorException e)
        {
            logger.severe(e.getMessage());
            e.printStackTrace();

            exceptionAndAlertHandler.handleMinorException(e);
        }
    }

    private void registerServerDetails(Message message)
    {
        serverName = (String) message.getValue("name");
    }

    private void onSetToggleStatus(Message message)
    {
        String profileID = (String) message.getValue("profile_ID");
        String actionID = (String) message.getValue("ID");
        boolean newStatus = (boolean) message.getValue("toggle_status");

        boolean currentStatus = clientListener.getToggleStatus(profileID,actionID);

        if(currentStatus == newStatus)
        {
            return;
        }

        ActionBox actionBox = clientListener.getActionBoxByProfileAndID(profileID, actionID);

        if(actionBox!=null)
        {
            actionBox.setCurrentToggleStatus(newStatus);
            Platform.runLater(()-> actionBox.toggle(newStatus));
        }
    }

    private void onSetActionGaugeProperties(Message message)
    {
        String profileID = (String) message.getValue("profile_ID");
        String actionID = (String) message.getValue("ID");

        ActionBox actionBox = clientListener.getActionBoxByProfileAndID(profileID, actionID);

        if(actionBox!=null)
        {
            Platform.runLater(()-> actionBox.updateGauge((GaugeProperties) message.getValue("gauge_properties")));
        }
    }

    private void onSetActionGaugeValue(Message message)
    {
        String profileID = (String) message.getValue("profile_ID");
        String actionID = (String) message.getValue("ID");

        ActionBox actionBox = clientListener.getActionBoxByProfileAndID(profileID, actionID);

        if(actionBox!=null)
        {
            Platform.runLater(()-> actionBox.updateGaugeValue((double) message.getValue("gauge_value")));
        }
    }

    private void updateActionTemporaryDisplayText(Message message)
    {
        String profileID = (String) message.getValue("profile_ID");
        String actionID = (String) message.getValue("ID");
        String displayText = (String) message.getValue("display_text");

        Platform.runLater(()-> clientListener.getActionBoxByProfileAndID(profileID, actionID).updateTemporaryDisplayText(displayText));
    }

    private void onActionIconReceived(Message message) throws MinorException
    {
        String profileID = (String) message.getValue("profile_ID");
        String actionID = (String) message.getValue("ID");
        String state = (String) message.getValue("icon_state");

        clientListener.getClientProfiles().getProfileFromID(profileID).saveActionIcon(
                actionID,
                (byte[]) message.getValue("icon"),
                state
        );

        ClientAction a = clientListener.getClientProfiles().getProfileFromID(profileID).getActionByID(actionID);
        clientListener.renderAction(profileID, a);
    }


    public void disconnect() throws SevereException
    {
        disconnect(null);
    }

    public void disconnect(DisconnectReason disconnectReason) throws SevereException
    {
        if(stop.get())
            return;

        stop.set(true);

        logger.info("Sending server disconnect message ...");

        Message m = new Message("disconnect");
        m.setValue("reason", disconnectReason);
        sendMessage(m);

        try
        {
            if(!socket.isClosed())
                socket.close();

            clientListener.setConnected(false);
            clientListener.onDisconnect();
            clientListener.updateSettingsConnectDisconnectButton();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new SevereException("Unable to close socket");
        }
    }

    public void onServerReady()
    {
        if(onConnectAndSetupToBeRun!=null)
        {
            onConnectAndSetupToBeRun.run();
            onConnectAndSetupToBeRun = null;
        }
    }

    public void sendThemesToServer() throws SevereException
    {
        Message message = new Message("themes");

        message.setValue("size", clientListener.getThemes().getThemeList().size());

        for(int i = 0;i<clientListener.getThemes().getThemeList().size();i++)
        {
            Theme theme = clientListener.getThemes().getThemeList().get(i);

            message.setValue("theme_"+i+"_full_name", theme.getFullName());
            message.setValue("theme_"+i+"_short_name", theme.getShortName());
            message.setValue("theme_"+i+"_author", theme.getAuthor());
            message.setValue("theme_"+i+"_version", theme.getVersion());

        }
        sendMessage(message);
    }


    public void serverDisconnected(Message message)
    {
        stop.set(true);


        if (message.getValue("reason") == null)
        {
            new StreamPiAlert(I18N.getString("connection.Client.disconnectedFromServer", serverName), StreamPiAlertType.WARNING).show();
        }
        else
        {
            new StreamPiAlert(I18N.getString("connection.Client.disconnectedFromServer", serverName), ((DisconnectReason) message.getValue("reason")).getMessage(), StreamPiAlertType.WARNING).show();
        }


        if(!socket.isClosed())
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        clientListener.setConnected(false);
        clientListener.onDisconnect();
        clientListener.updateSettingsConnectDisconnectButton();
    }

    public void sendClientScreenDetails() throws SevereException
    {
        Message message = new Message("client_screen_details");

        message.setValue("display_width", clientListener.getStageWidth());
        message.setValue("display_height", clientListener.getStageHeight());

        sendMessage(message);
    }

    public void sendClientDetails() throws SevereException
    {
        sendClientDetails("register_client_details");
    }

    public void updateClientDetails() throws SevereException
    {
        sendClientDetails("update_client_details");
    }


    public void sendClientDetails(String header) throws SevereException
    {
        Message message = new Message(header);

        message.setValue("version", clientInfo.getVersion());
        message.setValue("release_status", clientInfo.getReleaseStatus());
        message.setValue("communication_protocol_version", clientInfo.getCommunicationProtocolVersion());
        message.setValue("theme_api_version", ThemeAPI.VERSION);
        message.setValue("orientation", clientInfo.getOrientation());
        message.setValue("name", Config.getInstance().getClientName());
        message.setValue("platform",clientInfo.getPlatform());
        message.setValue("display_width", clientListener.getStageWidth());
        message.setValue("display_height", clientListener.getStageHeight());
        message.setValue("default_profile_ID", Config.getInstance().getStartupProfileID());
        message.setValue("default_theme_full_name", Config.getInstance().getCurrentThemeFullName());

        sendMessage(message);
    }


    public void sendProfileNamesToServer() throws SevereException
    {
        Message message = new Message("profiles");

        message.setValue("size", clientListener.getClientProfiles().getClientProfiles().size());

        for(int i = 0;i<clientListener.getClientProfiles().getClientProfiles().size();i++)
        {
            ClientProfile clientProfile = clientListener.getClientProfiles().getClientProfiles().get(i);
            message.setValue("profile_"+i+"_ID", clientProfile.getID());
            message.setValue("profile_"+i+"_actions_size", clientProfile.getActions().size());
        }

        sendMessage(message);
    }


    public void sendProfileDetailsToServer(Message message) throws SevereException
    {
        String ID = (String) message.getValue("ID");

        Message tbs1 = new Message("profile_details");

        ClientProfile clientProfile = clientListener.getClientProfiles().getProfileFromID(ID);

        tbs1.setValue("ID", ID);
        tbs1.setValue("name", clientProfile.getName());
        tbs1.setValue("rows", clientProfile.getRows());
        tbs1.setValue("cols", clientProfile.getCols());
        tbs1.setValue("action_size", clientProfile.getActionSize());
        tbs1.setValue("action_gap", clientProfile.getActionGap());
        tbs1.setValue("action_default_display_text_font_size", clientProfile.getActionDefaultDisplayTextFontSize());

        sendMessage(tbs1);

        for(ClientAction action : clientProfile.getActions())
        {
            sendActionDetails(clientProfile.getID(), action);
        }
    }

    public void sendActionDetails(String profileID, ClientAction action) throws SevereException
    {

        if(action == null)
        {
            logger.info("NO SUCH ACTION");
            return;
        }

        Message message = new Message("action_details");

        message.setValue("profile_ID", profileID);
        message.setValue("ID", action.getID());
        message.setValue("type", action.getActionType());

        if(action.getActionType() == ActionType.NORMAL ||
                action.getActionType() == ActionType.TOGGLE || action.getActionType() == ActionType.GAUGE)
        {
            message.setValue("unique_ID", action.getUniqueID());
            message.setValue("version", action.getVersion());
        }

        message.setValue("bg_colour_hex", action.getBgColourHex());


        message.setValue("icon_state_names_size", action.getIcons().size());

        int i = 0;
        for(String eachState : action.getIcons().keySet())
        {
            message.setValue("icon_state_"+i, eachState);
            message.setValue("icon_"+i, action.getIcon(eachState));
            i+=1;
        }

        message.setValue("current_icon_state", action.getCurrentIconState());

        //text
        message.setValue("is_show_display_text", action.isShowDisplayText());
        message.setValue("display_text_font_colour_hex", action.getDisplayTextFontColourHex());
        message.setValue("display_text", action.getDisplayText());
        message.setValue("display_text_font_size", action.getDisplayTextFontSize());
        message.setValue("display_text_alignment", action.getDisplayTextAlignment());

        //location
        message.setValue("location", action.getLocation());

        message.setValue("parent", action.getParent());

        message.setValue("delay_before_executing", action.getDelayBeforeExecuting());


        //client properties

        message.setValue("is_gauge_animated", action.isGaugeAnimated());


        ClientProperties clientProperties = action.getClientProperties();

        message.setValue("client_properties_size", clientProperties.getSize());

        for (int x = 0;x< clientProperties.getSize(); x++)
        {
            message.setValue("client_property_"+x+"_name", clientProperties.get().get(x).getName());
            message.setValue("client_property_"+x+"_raw_value", clientProperties.get().get(x).getRawValue());
        }

        sendMessage(message);
    }

    public void saveActionDetails(Message message)
    {
        String profileID = (String) message.getValue("profile_ID");

        String ID = (String) message.getValue("ID");
        ActionType actionType = (ActionType) message.getValue("type");

        //display
        String bgColourHex = (String) message.getValue("bg_colour_hex");

        int allIconStateNamesSize = (int) message.getValue("icon_state_names_size");

        String[] iconStates = new String[allIconStateNamesSize];

        for(int i = 0;i<iconStates.length; i++)
        {
            iconStates[i] = (String) message.getValue("icon_state_"+i);
        }

        String currentIconState = (String) message.getValue("current_icon_state");

        //text
        boolean isShowDisplayText = (boolean) message.getValue("is_show_display_text");
        String displayTextFontColourHex = (String) message.getValue("display_text_font_colour_hex");
        String displayText = (String) message.getValue("display_text");
        double displayTextFontSize = (Double) message.getValue("display_text_font_size");
        DisplayTextAlignment displayTextAlignment = (DisplayTextAlignment) message.getValue("display_text_alignment");


        Location location = (Location) message.getValue("location");

        String parent = (String) message.getValue("parent");

        int delayBeforeExecuting = (int) message.getValue("delay_before_executing");

        ClientAction action = new ClientAction(ID, actionType);

        if(actionType == ActionType.NORMAL || actionType == ActionType.TOGGLE || actionType == ActionType.GAUGE)
        {
            try
            {
                action.setVersion((Version) message.getValue("version"));
                action.setUniqueID((String) message.getValue("unique_ID"));
            }
            catch (Exception e)
            {
                logger.severe(e.getMessage());
                e.printStackTrace();
            }
        }


        action.setBgColourHex(bgColourHex);

        action.setShowDisplayText(isShowDisplayText);
        action.setDisplayTextFontColourHex(displayTextFontColourHex);
        action.setDisplayText(displayText);
        action.setDisplayTextAlignment(displayTextAlignment);
        action.setDisplayTextFontSize(displayTextFontSize);
        action.setCurrentIconState(currentIconState);

        action.setLocation(location);

        action.setParent(parent);

        //client properties

        action.setDelayBeforeExecuting(delayBeforeExecuting);

        boolean isAnimatedGauge = (boolean) message.getValue("is_gauge_animated");

        int clientPropertiesSize = (int) message.getValue("client_properties_size");

        ClientProperties clientProperties = new ClientProperties();

        if(actionType == ActionType.FOLDER)
            clientProperties.setDuplicatePropertyAllowed(true);

        for(int i = 0;i<clientPropertiesSize; i++)
        {
            StringProperty property = new StringProperty((String) message.getValue("client_property_"+i+"_name"));
            property.setRawValue((String) message.getValue("client_property_"+i+"_raw_value"));

            clientProperties.addProperty(property);
        }
        action.setClientProperties(clientProperties);


        try
        {
            ClientAction old = clientListener.getClientProfiles().getProfileFromID(profileID).getActionByID(action.getID());


            if(old != null)
            {
                for(String oldState : old.getIcons().keySet())
                {
                    boolean isPresent = false;
                    for(String state : iconStates)
                    {
                        if(state.equals(oldState))
                        {
                            isPresent = true;
                            action.addIcon(state, old.getIcon(state));
                            break;
                        }
                    }

                    if(!isPresent)
                    {
                        // State no longer exists. Delete.

                        new File(Config.getInstance().getIconsPath()+ File.separator +ID+"___"+oldState).delete();
                    }
                }
            }

            clientListener.getClientProfiles().getProfileFromID(profileID).addAction(action);

            clientListener.getClientProfiles().getProfileFromID(profileID).saveAction(action);

            clientListener.renderAction(profileID, action);

            if(clientListener.getScreenSaver()!=null)
            {
                Platform.runLater(()->clientListener.getScreenSaver().restart());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleMinorException(new MinorException(e.getMessage()));
        }
    }

    public void deleteAction(Message message)
    {
        try
        {
            String profileID = (String) message.getValue("profile_ID");
            String actionID = (String) message.getValue("ID");

            ClientAction acc =  clientListener.getClientProfiles().getProfileFromID(profileID).getActionByID(actionID);

            if(acc == null)
            {
                exceptionAndAlertHandler.handleMinorException(new MinorException("Unable to delete action "+actionID+" since it does not exist."));
                return;
            }

            if(acc.getActionType() == ActionType.FOLDER)
            {
                ArrayList<String> idsToBeRemoved = new ArrayList<>();

                ArrayList<String> folders = new ArrayList<>();
                String folderToBeDeletedID = clientListener.getClientProfiles().getProfileFromID(profileID).getActionByID(actionID).getID();

                folders.add(folderToBeDeletedID);

                boolean startOver = true;
                while(startOver)
                {
                    startOver = false;
                    for(ClientAction action : clientListener.getClientProfiles().getProfileFromID(profileID).getActions())
                    {
                        if(folders.contains(action.getParent()))
                        {
                            if(!idsToBeRemoved.contains(action.getID()))
                            {
                                idsToBeRemoved.add(action.getID());
                                if(action.getActionType() == ActionType.FOLDER)
                                {
                                    folders.add(action.getID());
                                    startOver = true;
                                }
                            }
                        }
                    }
                }


                for(String ids : idsToBeRemoved)
                {
                    clientListener.getClientProfiles().getProfileFromID(profileID).removeAction(ids);
                }

                Platform.runLater(clientListener::renderRootDefaultProfile);

            }
            else if (acc.getActionType() == ActionType.COMBINE)
            {
                for(Property property : acc.getClientProperties().get())
                {
                    clientListener.getClientProfiles().getProfileFromID(profileID).removeAction(property.getRawValue());
                }
            }


            clientListener.getClientProfiles().getProfileFromID(profileID).removeAction(acc.getID());

            clientListener.getClientProfiles().getProfileFromID(profileID).saveActions();

            if(acc.getLocation() != null)
            {
                if (clientListener.getCurrentProfile().getID().equals(profileID)
                        && clientListener.getCurrentParent().equals(acc.getParent()))
                {
                    clientListener.clearActionBox(
                            acc.getLocation().getCol(),
                            acc.getLocation().getRow(),
                            acc.getLocation().getColSpan(),
                            acc.getLocation().getRowSpan()
                    );
                }
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleMinorException(new MinorException(e.getMessage()));
        }
    }

    public void saveClientDetails(Message message)
    {
        try
        {
            Config.getInstance().setName((String) message.getValue("name"));


            Config.getInstance().setStartupProfileID((String) message.getValue("default_profile_ID"));

            String oldThemeFullName = Config.getInstance().getCurrentThemeFullName();
            String newThemeFullName = (String) message.getValue("default_theme_full_name");

            Config.getInstance().setCurrentThemeFullName(newThemeFullName);
            Config.getInstance().save();

            if(!oldThemeFullName.equals(newThemeFullName))
            {
                Platform.runLater(()-> {
                    try {
                        clientListener.initThemes();
                    } catch (SevereException e) {
                        exceptionAndAlertHandler.handleSevereException(e);
                    }
                });
            }

            Platform.runLater(clientListener::loadSettings);
        }
        catch (SevereException e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleSevereException(e);
        }
    }

    public void saveProfileDetails(Message message) throws SevereException, MinorException
    {
        String ID = (String) message.getValue("ID");
        ClientProfile clientProfile = clientListener.getClientProfiles().getProfileFromID(ID);

        String name = (String) message.getValue("name");
        int rows = (int) message.getValue("rows");
        int cols = (int) message.getValue("cols");
        double actionSize = (double) message.getValue("action_size");
        double actionGap = (double) message.getValue("action_gap");
        double actionDefaultDisplayTextFontSize = (double) message.getValue("action_default_display_text_font_size");

        try
        {
            if(clientProfile == null)
            {
                clientProfile = new ClientProfile(new File(Config.getInstance().getProfilesPath()+File.separator+ID+".xml"),
                        Config.getInstance().getIconsPath(), name, rows, cols, actionSize, actionGap, actionDefaultDisplayTextFontSize);

            }
            else
            {
                clientProfile.setName(name);
                clientProfile.setRows(rows);
                clientProfile.setCols(cols);
                clientProfile.setActionSize(actionSize);
                clientProfile.setActionGap(actionGap);
                clientProfile.setActionDefaultDisplayTextFontSize(actionDefaultDisplayTextFontSize);
                clientProfile.saveProfileDetails();
            }

            clientListener.getClientProfiles().addProfile(clientProfile);
            clientListener.refreshGridIfCurrentProfile(ID);
            Platform.runLater(clientListener::loadSettings);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SevereException(e.getMessage());
        }
    }

    public void deleteProfile(Message message)
    {
        String ID = (String) message.getValue("ID");
        clientListener.getClientProfiles().deleteProfile(clientListener.getClientProfiles().getProfileFromID(
                ID
        ));

        if(clientListener.getCurrentProfile().getID().equals(ID))
        {
            Platform.runLater(clientListener::renderRootDefaultProfile);
        }
    }

    /*public void onActionClicked(String profileID, String actionID, boolean toggleState) throws SevereException
    {
        Message message = new Message("action_clicked");

        message.setValue("profile_ID", profileID);
        message.setValue("ID", actionID);
        message.setValue("toggle_status", toggleState);
        sendMessage(message);
    }*/

    public void updateOrientationOnClient(Orientation orientation) throws SevereException
    {
        Message m = new Message("client_orientation");
        m.setValue("orientation", orientation);
        sendMessage(m);
    }

    public void actionFailed(Message message)
    {
        String profileID = (String) message.getValue("profile_ID");
        String actionID = (String) message.getValue("ID");
        clientListener.onActionFailed(profileID, actionID);
    }

    public void refreshAllGauges() throws SevereException
    {
        Message m = new Message("refresh_all_gauges");
        sendMessage(m);
    }

    public void sendInputEvent(String profileID, String actionID, StreamPiInputEvent event) throws SevereException
    {

        System.out.println("7");
        Message m = new Message("input_event_in_action");
        m.setValue("profile_ID", profileID);
        m.setValue("ID", actionID);
        m.setValue("event", event);
        sendMessage(m);
    }

    public void setToggleStatus(String profileID, String actionID, boolean toggleStatus) throws SevereException
    {
        Message message = new Message("set_toggle_status");

        message.setValue("profile_ID", profileID);
        message.setValue("ID", actionID);
        message.setValue("toggle_status", toggleStatus);
        sendMessage(message);
    }
}
