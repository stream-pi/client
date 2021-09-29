package com.stream_pi.client.connection;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.action_api.actionproperty.ClientProperties;
import com.stream_pi.action_api.actionproperty.gaugeproperties.GaugeProperties;
import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.util.comms.Message;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.version.Version;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;

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

        clientListener.getExecutor().submit(new Task<Void>() {
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
                        throw new MinorException("Connection Error", "Unable to connect to server. Please check settings and connection and try again.");
                    }
                    finally
                    {
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
                        throw new MinorException("Unable to set up io Streams to server. Check connection and try again.");
                    }

                    start();
                } catch (MinorException e)
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
            oos.writeObject(message);
            oos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new SevereException("Unable to write to io Stream!");
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
                        case "ready" :                  onServerReady();
                            break;

                        case "action_icon" :            onActionIconReceived(message);
                            break;

                        case "disconnect" :             serverDisconnected(message);
                            break;

                        case "get_client_details" :     sendClientDetails();
                            break;

                        case "get_client_screen_details" : sendClientScreenDetails();
                            break;

                        case "get_profiles" :           sendProfileNamesToServer();
                            break;

                        case "get_profile_details":     sendProfileDetailsToServer(message);
                            break;

                        case "save_action_details":     saveActionDetails(message);
                            break;

                        case "delete_action":           deleteAction(message);
                            break;

                        case "get_themes":              sendThemesToServer();
                            break;

                        case "save_client_details":     saveClientDetails(message);
                            break;

                        case "save_client_profile":     saveProfileDetails(message);
                            break;

                        case "delete_profile":          deleteProfile(message);
                            break;

                        case "action_failed":           actionFailed(message);
                            break;

                        case "set_toggle_status":       onSetToggleStatus(message);
                            break;

                        case "set_action_gauge_properties": onSetActionGaugeProperties(message);
                            break;

                        case "set_action_gauge_value": onSetActionGaugeValue(message);
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
                        //isDisconnect.set(true); //Prevent running disconnect
                        throw new MinorException("Accidentally disconnected from Server! (Failed at readUTF)");
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

    private void onSetToggleStatus(Message message)
    {
        String[] arr = message.getStringArrValue();

        String profileID = arr[0];
        String actionID = arr[1];
        boolean newStatus = arr[2].equals("true");

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
        String[] arr = message.getStringArrValue();

        String profileID = arr[0];
        String actionID = arr[1];

        ActionBox actionBox = clientListener.getActionBoxByProfileAndID(profileID, actionID);

        if(actionBox!=null)
        {
            Platform.runLater(()->
            {
                actionBox.updateGauge((GaugeProperties) message.getObject());
            });
        }
    }

    private void onSetActionGaugeValue(Message message)
    {
        String[] arr = message.getStringArrValue();

        String profileID = arr[0];
        String actionID = arr[1];

        ActionBox actionBox = clientListener.getActionBoxByProfileAndID(profileID, actionID);

        if(actionBox!=null)
        {
            Platform.runLater(()->{
                actionBox.updateGaugeValue(message.getDoubleValue());
            });
        }
    }

    private void onActionIconReceived(Message message) throws MinorException
    {
        String profileID = message.getStringArrValue()[0];
        String actionID = message.getStringArrValue()[1];
        String state = message.getStringArrValue()[2];

        clientListener.getClientProfiles().getProfileFromID(profileID).saveActionIcon(
                actionID,
                message.getByteArrValue(),
                state
        );

        Action a = clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);
        clientListener.renderAction(profileID, a);
    }



    //commands

    public synchronized void sendIcon(String profileID, String actionID, String state, byte[] icon) throws SevereException
    {
        try
        {
            Thread.sleep(50);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        Message message = new Message("action_icon");
        message.setStringArrValue(profileID, actionID, state);
        message.setByteArrValue(icon);
        sendMessage(message);
    }

    public void disconnect() throws SevereException
    {
        disconnect("");
    }

    public void disconnect(String message) throws SevereException
    {
        if(stop.get())
            return;

        stop.set(true);

        logger.info("Sending server disconnect message ...");

        Message m = new Message("disconnect");
        m.setStringValue(message);
        sendMessage(m);

        try
        {
            if(!socket.isClosed())
                socket.close();

            clientListener.setConnected(false);
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

        String[] arr = new String[clientListener.getThemes().getThemeList().size()*4];

        int x = 0;
        for(int i = 0;i<clientListener.getThemes().getThemeList().size();i++)
        {
            Theme theme = clientListener.getThemes().getThemeList().get(i);

            arr[x] = theme.getFullName();
            arr[x+1] = theme.getShortName();
            arr[x+2] = theme.getAuthor();
            arr[x+3] = theme.getVersion().getText();

            x+=4;
        }

        message.setStringArrValue(arr);
        sendMessage(message);
    }


    public void sendActionIcon(String clientProfileID, String actionID, String state) throws SevereException
    {
        sendIcon(clientProfileID,
                actionID,
                state,
                clientListener.getClientProfiles()
                        .getProfileFromID(clientProfileID)
                        .getActionFromID(actionID).getIcon(state));
    }

    public void serverDisconnected(Message message)
    {
        stop.set(true);
        String txt = "Disconnected!";

        String m = message.getStringValue();

        if(!m.isBlank())
            txt = "Message : "+m;

        exceptionAndAlertHandler.handleMinorException(new MinorException("Disconnected from Server",txt));

        if(!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        clientListener.setConnected(false);
        clientListener.onDisconnect();
        clientListener.updateSettingsConnectDisconnectButton();
    }

    public void sendClientScreenDetails() throws SevereException
    {
        Message toBeSent = new Message("client_screen_details");

        toBeSent.setDoubleArrValue(
                clientListener.getStageWidth(),
                clientListener.getStageHeight()
        );

        sendMessage(toBeSent);
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
        String clientVersion = clientInfo.getVersion().getText();
        String releaseStatus = clientInfo.getReleaseStatus().toString();
        String clientCommStandard = clientInfo.getCommStandardVersion().getText();
        String clientMinThemeStandard = clientInfo.getMinThemeSupportVersion().getText();
        String clientNickname = Config.getInstance().getClientNickName();
        String screenWidth = clientListener.getStageWidth()+"";
        String screenHeight = clientListener.getStageHeight()+"";
        String OS = clientInfo.getPlatform()+"";
        String defaultProfileID = Config.getInstance().getStartupProfileID();


        Message toBeSent = new Message(header);

        String tbs = null;

        Orientation orientation = clientListener.getCurrentOrientation();
        if(orientation!=null)
        {
            tbs = orientation.toString();
        }

        toBeSent.setStringArrValue(
                clientVersion,
                releaseStatus,
                clientCommStandard,
                clientMinThemeStandard,
                clientNickname,
                screenWidth,
                screenHeight,
                OS,
                defaultProfileID,
                clientListener.getDefaultThemeFullName(),
                tbs
        );

        sendMessage(toBeSent);
    }


    public void sendProfileNamesToServer() throws SevereException
    {
        Message message = new Message("profiles");

        String[] profilesArray = new String[clientListener.getClientProfiles().getClientProfiles().size()];
        int[] profilesActionsArray = new int[profilesArray.length];

        for(int i = 0;i<profilesArray.length;i++)
        {
            ClientProfile clientProfile = clientListener.getClientProfiles().getClientProfiles().get(i);
            profilesArray[i] = clientProfile.getID();
            profilesActionsArray[i] = clientProfile.getActions().size();
        }

        message.setStringArrValue(profilesArray);
        message.setIntArrValue(profilesActionsArray);
        sendMessage(message);
    }


    public void sendProfileDetailsToServer(Message message) throws SevereException
    {
        String ID = message.getStringValue();

        Message tbs1 = new Message("profile_details");

        ClientProfile clientProfile = clientListener.getClientProfiles().getProfileFromID(ID);

        String[] arr = new String[]{
                ID,
                clientProfile.getName(),
                clientProfile.getRows()+"",
                clientProfile.getCols()+"",
                clientProfile.getActionSize()+"",
                clientProfile.getActionGap()+""
        };

        tbs1.setStringArrValue(arr);

        sendMessage(tbs1);

        for(Action action : clientProfile.getActions())
        {
            sendActionDetails(clientProfile.getID(), action);
        }

        for(Action action : clientProfile.getActions())
        {
            if(action.isHasIcon())
            {
                for(String key : action.getIcons().keySet())
                {
                    sendActionIcon(clientProfile.getID(), action.getID(), key);
                }
            }
        }

    }
    
    public void sendActionDetails(String profileID, Action action) throws SevereException
    {
        
        if(action == null)
        {
            logger.info("NO SUCH ACTION");
            return;
        }

        ArrayList<String> a = new ArrayList<>();

        a.add(profileID);
        a.add(action.getID());
        a.add(action.getActionType()+"");

        if(action.getActionType() == ActionType.NORMAL ||
                action.getActionType() == ActionType.TOGGLE || action.getActionType() == ActionType.GAUGE)
        {
            a.add(action.getVersion().getText());
        }
        else
        {
            a.add("no");
        }

        if(action.getActionType() ==ActionType.NORMAL ||
                action.getActionType() == ActionType.TOGGLE || action.getActionType() == ActionType.GAUGE)
        {
            a.add(action.getModuleName());
        }
        else
        {
            a.add("nut");
        }

        //display

        a.add(action.getBgColourHex());

        //icon


        StringBuilder allIconStatesNames = new StringBuilder();
        for(String eachState : action.getIcons().keySet())
        {
            allIconStatesNames.append(eachState).append("::");
        }
        a.add(allIconStatesNames.toString());


        a.add(action.getCurrentIconState()+"");

        //text
        a.add(action.isShowDisplayText()+"");
        a.add(action.getDisplayTextFontColourHex());
        a.add(action.getDisplayText());
        a.add(action.getNameFontSize()+"");
        a.add(action.getDisplayTextAlignment()+"");

        //location

        if(action.getLocation() == null)
        {
            a.add("-1");
            a.add("-1");
        }
        else
        {
            a.add(action.getLocation().getRow()+"");
            a.add(action.getLocation().getCol()+"");
        }

        a.add(action.getRowSpan()+"");
        a.add(action.getColSpan()+"");

        a.add(action.getParent());

        a.add(action.getDelayBeforeExecuting()+"");

        //client properties

        a.add(action.isGaugeAnimated()+"");

        ClientProperties clientProperties = action.getClientProperties();

        a.add(clientProperties.getSize()+"");

        for(Property property : clientProperties.get())
        {
            a.add(property.getName());
            a.add(property.getRawValue());
        }



        Message message = new Message("action_details");

        String[] x = new String[a.size()];
        x = a.toArray(x);


        message.setStringArrValue(x);
        sendMessage(message);

    }

    public void saveActionDetails(Message message)
    {
        String[] r = message.getStringArrValue();

        String profileID = r[0];

        String actionID = r[1];
        ActionType actionType = ActionType.valueOf(r[2]);

        //3 - Version
        //4 - ModuleName

        //display
        String bgColorHex = r[5];

        String[] iconStates = r[6].split("::");
        String currentIconState = r[7];

        //text
        boolean isShowDisplayText = r[8].equals("true");
        String displayFontColor = r[9];
        String displayText = r[10];
        double displayLabelFontSize = Double.parseDouble(r[11]);
        DisplayTextAlignment displayTextAlignment = DisplayTextAlignment.valueOf(r[12]);

        //location
        String row = r[13];
        String col = r[14];

        Location location = new Location(Integer.parseInt(row), Integer.parseInt(col));

        Action action = new Action(actionID, actionType);

        String rowSpan = r[15];
        String colSpan = r[16];

        action.setRowSpan(Integer.parseInt(rowSpan));
        action.setColSpan(Integer.parseInt(colSpan));

        if(actionType == ActionType.NORMAL || actionType == ActionType.TOGGLE || actionType == ActionType.GAUGE)
        {
            try
            {
                action.setVersion(new Version(r[3]));
                action.setModuleName(r[4]);
            }
            catch (Exception e)
            {
                logger.severe(e.getMessage());
                e.printStackTrace();
            }
        }


        action.setBgColourHex(bgColorHex);

        action.setShowDisplayText(isShowDisplayText);
        action.setDisplayTextFontColourHex(displayFontColor);
        action.setDisplayText(displayText);
        action.setDisplayTextAlignment(displayTextAlignment);
        action.setNameFontSize(displayLabelFontSize);
        action.setCurrentIconState(currentIconState);

        action.setLocation(location);


        String parent = r[17];
        action.setParent(parent);

        //client properties

        action.setDelayBeforeExecuting(Integer.parseInt(r[18]));

        boolean isAnimatedGauge = r[19].equals("true");

        if (action.getActionType() == ActionType.GAUGE)
        {
            action.setGaugeAnimated(isAnimatedGauge);
        }

        int clientPropertiesSize = Integer.parseInt(r[20]);

        ClientProperties clientProperties = new ClientProperties();

        if(actionType == ActionType.FOLDER)
            clientProperties.setDuplicatePropertyAllowed(true);

        for(int i = 21;i<((clientPropertiesSize*2) + 21); i+=2)
        {
            Property property = new Property(r[i], Type.STRING);
            property.setRawValue(r[i+1]);

            clientProperties.addProperty(property);
        }

        action.setClientProperties(clientProperties);

        try
        {
            Action old = clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(action.getID());


            boolean refreshGrid = false;

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

                        new File(Config.getInstance().getIconsPath()+"/"+actionID+"___"+oldState).delete();
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
            String[] arr = message.getStringArrValue();
            String profileID = arr[0];
            String actionID = arr[1];

            Action acc =  clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);

            if(acc == null)
            {
                exceptionAndAlertHandler.handleMinorException(new MinorException("Unable to delete action "+actionID+" since it does not exist."));
                return;
            }

            if(acc.getActionType() == ActionType.FOLDER)
            {
                ArrayList<String> idsToBeRemoved = new ArrayList<>();

                ArrayList<String> folders = new ArrayList<>();
                String folderToBeDeletedID = clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID).getID();

                folders.add(folderToBeDeletedID);

                boolean startOver = true;
                while(startOver)
                {
                    startOver = false;
                    for(Action action : clientListener.getClientProfiles().getProfileFromID(profileID).getActions())
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

            if(acc.getLocation().getCol()!=-1)
            {
                if (clientListener.getCurrentProfile().getID().equals(profileID)
                        && clientListener.getCurrentParent().equals(acc.getParent()))
                {
                    clientListener.clearActionBox(
                            acc.getLocation().getCol(),
                            acc.getLocation().getRow(),
                            acc.getColSpan(),
                            acc.getRowSpan()
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
            boolean reInit = false;

            String[] sep = message.getStringArrValue();

            Config.getInstance().setNickName(sep[0]);


            Config.getInstance().setStartupProfileID(sep[1]);

            String oldThemeFullName = Config.getInstance().getCurrentThemeFullName();
            String newThemeFullName = sep[2];

            Config.getInstance().setCurrentThemeFullName(sep[2]);
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
        String[] sep = message.getStringArrValue();

        ClientProfile clientProfile = clientListener.getClientProfiles().getProfileFromID(sep[0]);

        if(clientProfile == null)
        {
            clientProfile = new ClientProfile(new File(Config.getInstance().getProfilesPath()+"/"+sep[0]+".xml"),
                    Config.getInstance().getIconsPath());
        }

        clientProfile.setName(sep[1]);
        clientProfile.setRows(Integer.parseInt(sep[2]));
        clientProfile.setCols(Integer.parseInt(sep[3]));
        clientProfile.setActionSize(Integer.parseInt(sep[4]));
        clientProfile.setActionGap(Integer.parseInt(sep[5]));

        try
        {
            clientListener.getClientProfiles().addProfile(clientProfile);
            clientProfile.saveProfileDetails();
            clientListener.refreshGridIfCurrentProfile(sep[0]);
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
        clientListener.getClientProfiles().deleteProfile(clientListener.getClientProfiles().getProfileFromID(
                message.getStringValue()
        ));
        
        if(clientListener.getCurrentProfile().getID().equals(message.getStringValue()))
        {
            Platform.runLater(clientListener::renderRootDefaultProfile);
        }
    }

    public void onActionClicked(String profileID, String actionID, boolean toggleState) throws SevereException
    {
        Message m = new Message("action_clicked");
        m.setStringArrValue(profileID, actionID, toggleState+"");
        sendMessage(m);
    }

    public void updateOrientationOnClient(Orientation orientation) throws SevereException
    {
        Message m = new Message("client_orientation");
        m.setStringValue(orientation.toString());
        sendMessage(m);
    }

    public void actionFailed(Message message)
    {
        String[] r = message.getStringArrValue();
        String profileID = r[0];
        String actionID = r[1];
        clientListener.onActionFailed(profileID, actionID);
    }
}
