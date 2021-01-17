package com.StreamPi.Client.Connection;

import com.StreamPi.ActionAPI.Action.Action;
import com.StreamPi.ActionAPI.Action.ActionType;
import com.StreamPi.ActionAPI.Action.DisplayTextAlignment;
import com.StreamPi.ActionAPI.Action.Location;
import com.StreamPi.ActionAPI.ActionProperty.ClientProperties;
import com.StreamPi.ActionAPI.ActionProperty.Property.Property;
import com.StreamPi.ActionAPI.ActionProperty.Property.Type;
import com.StreamPi.Client.IO.Config;
import com.StreamPi.Client.Info.ClientInfo;
import com.StreamPi.Client.Profile.ClientProfile;
import com.StreamPi.Client.Profile.ClientProfiles;
import com.StreamPi.Client.Window.ExceptionAndAlertHandler;
import com.StreamPi.Client.Window.Dashboard.ActionGridPane.ActionBox;
import com.StreamPi.ThemeAPI.Theme;
import com.StreamPi.Util.Alert.StreamPiAlertType;
import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Exception.SevereException;
import com.StreamPi.Util.Exception.StreamPiException;
import com.StreamPi.Util.Platform.Platform;
import com.StreamPi.Util.Platform.ReleaseStatus;
import com.StreamPi.Util.Version.Version;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Client extends Thread{

    private Socket socket;

    //private Logger logger;

    private DataOutputStream dos;
    private DataInputStream dis;

    private AtomicBoolean stop = new AtomicBoolean(false);

    private ClientListener clientListener;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ClientInfo clientInfo;

    private String serverIP;
    private int serverPort;
    private Logger logger;

    public Client(String serverIP, int serverPort, ClientListener clientListener, ExceptionAndAlertHandler exceptionAndAlertHandler)
    {
        this.serverIP = serverIP;
        this.serverPort = serverPort;

        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientInfo = ClientInfo.getInstance();
        this.clientListener = clientListener;

        logger = Logger.getLogger(Client.class.getName());

        new Thread(new Task<Void>() {
            @Override
            protected Void call()
            {
                try
                {
                    try {
                        logger.info("Trying to connect to server at "+serverIP+":"+serverPort);
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(serverIP, serverPort), 5000);
                        clientListener.setConnected(true);
                        clientListener.updateSettingsConnectDisconnectButton();
                        logger.info("Connected to "+socket.getRemoteSocketAddress()+" !");
                    } catch (IOException e) {
                        e.printStackTrace();

                        clientListener.setConnected(false);
                        clientListener.updateSettingsConnectDisconnectButton();
                        throw new MinorException("Connection Error", "Unable to connect to server. Please check settings and connection and try again.");
                    }

                    try
                    {
                        dos = new DataOutputStream(socket.getOutputStream());
                        dis = new DataInputStream(socket.getInputStream());
                    }
                    catch (IOException e)
                    {
                        logger.severe(e.getMessage());
                        e.printStackTrace();
                        throw new MinorException("Unable to set up IO Streams to server. Check connection and try again.");
                    }
                    start();
                } catch (MinorException e)
                {
                    exceptionAndAlertHandler.handleMinorException(e);
                }
                return null;
            }
        }).start();
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



    public void writeToStream(String text) throws SevereException
    {
        /*try
        {
            logger.debug(text);
            dos.writeUTF(text);
            dos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new SevereException("Unable to write to IO Stream!");
        }*/

        try
        {
            byte[] txtBytes = text.getBytes();

            Thread.sleep(50);
            dos.writeUTF("string:: ::");
            dos.flush();
            dos.writeInt(txtBytes.length);
            dos.flush();
            write(txtBytes);
            dos.flush();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            throw new SevereException("Unable to write to IO Stream!");
        }

    }

    public void write(byte[] array) throws SevereException
    {
        try
        {
            dos.write(array);
        }
        catch (IOException e)
        {
            logger.severe(e.getMessage());
            e.printStackTrace();
            throw new SevereException("Unable to write to IO Stream!");
        }
    }

    @Override
    public void run() {
        try
        {
            while(!stop.get())
            {
                String msg = "";

                try
                {
                    String raw = dis.readUTF();

                    System.out.println("AAAAAAAAAAAAAAAAAA : "+raw);

                    int length = dis.readInt();

                    String[] precursor = raw.split("::");

                    String inputType = precursor[0];
                    String secondArg = precursor[1];


                    //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    /*int count;
                    int chunkSize = 512;
                    while (length>0)
                    {
                        if(chunkSize > length)
                            chunkSize = length;
                        else
                            chunkSize = 512;

                        byte[] buffer = new byte[chunkSize];
                        count = dis.read(buffer);

                        byteArrayOutputStream.write(buffer);

                        length-=count;
                    }*/

                    /*byte[] buffer = new byte[8192];
                    int read;
                    while((read = dis.read(buffer)) != -1){
                        System.out.println("READ : "+read);
                        byteArrayOutputStream.write(buffer, 0, read);
                    }


                    byteArrayOutputStream.close();

                    byte[] bArr = byteArrayOutputStream.toByteArray();*/

                    byte[] bArr = new byte[length];

                    dis.readFully(bArr);

                    if(inputType.equals("string"))
                    {
                        msg = new String(bArr);
                    }
                    else if(inputType.equals("action_icon"))
                    {
                        System.out.println("asdsdsxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        String[] secondArgSep = secondArg.split("!!");

                        String profileID = secondArgSep[0];
                        String actionID = secondArgSep[1];

                        clientListener.getClientProfiles().getProfileFromID(profileID).saveActionIcon(actionID, bArr);

                        Action a = clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);
                        clientListener.clearActionBox(a.getLocation().getCol(), a.getLocation().getRow());
                        clientListener.renderAction(profileID, a);


                        continue;
                    }
                }
                catch (IOException e)
                {
                    logger.severe(e.getMessage());
                    e.printStackTrace();

                    clientListener.setConnected(false);
                    clientListener.updateSettingsConnectDisconnectButton();

                    if(!stop.get())
                    {
                        //isDisconnect.set(true); //Prevent running disconnect
                        throw new MinorException("Accidentally disconnected from Server! (Failed at readUTF)");
                    }

                    exit();

                    return;
                }


                logger.info("Received text : '"+msg+"'");

                String[] sep = msg.split("::");

                String command = sep[0];

                switch (command)
                {
                    case "disconnect" :             serverDisconnected(msg);
                                                    break;

                    case "get_client_details" :     sendClientDetails();
                                                    break;

                    case "get_profiles" :           sendProfileNamesToServer();
                                                    break;

                    case "get_profile_details":     sendProfileDetailsToServer(sep[1]);
                                                    break;

                    case "save_action_details":     saveActionDetails(sep);
                                                    break;

                    case "delete_action":           deleteAction(sep[1], sep[2]);
                                                    break;

                    case "get_themes":              sendThemesToServer();
                                                    break;

                    case "save_client_details":     saveClientDetails(sep);
                                                    break;

                    case "save_client_profile":     saveProfileDetails(sep);
                                                    break;

                    case "delete_profile":          deleteProfile(sep[1]);
                                                    break;

                    case "action_failed":           actionFailed(sep[1], sep[2]);
                                                    break;


                    default:                        logger.warning("Command '"+command+"' does not match records. Make sure client and server versions are equal.");

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




    //commands

    /*public void receiveActionIcon(String[] sep) throws MinorException {
        String profileID = sep[1];
        String actionID = sep[2];
        int bytesToRead = Integer.parseInt(sep[3]);
        int port = Integer.parseInt(sep[4]);

        try
        {
            Socket tempSocket = new Socket(socket.getInetAddress(), port);
            tempSocket.setReceiveBufferSize(bytesToRead);

            tempSocket.setSoTimeout(10000);

            DataInputStream dataInputStream = new DataInputStream(tempSocket.getInputStream());

            byte[] dataIcon = new byte[bytesToRead];

            dataInputStream.read(dataIcon);

            clientProfiles.getProfileFromID(profileID).getActionFromID(actionID).setIcon(dataIcon);

            dataInputStream.close();
            tempSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new MinorException("Unable to Receive icon");
        }
    }*/


    public void sendIcon(String profileID, String actionID, byte[] icon) throws SevereException
    {
        try
        {
            Thread.sleep(50);
            dos.writeUTF("action_icon::"+profileID+"!!"+actionID+"!!::"+icon.length);
            dos.flush();
            dos.writeInt(icon.length);
            dos.flush();
            write(icon);
            dos.flush();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            throw new SevereException("Unable to write to IO Stream!");
        }
    }



    /*public void sendIcon(String profileID, String actionID, byte[] icon) throws SevereException
    {
        try
        {

            ServerSocket tmpServer = new ServerSocket(0);

            dos.writeUTF("action_icon::"+
                    profileID+"::"+
                    actionID+"::"+
                    icon.length+"::"+
                    tmpServer.getLocalPort()+"::");


            Socket socket = tmpServer.accept();
            socket.setSendBufferSize(icon.length);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.write(icon);

            dataOutputStream.close();

            tmpServer.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new SevereException(e.getMessage());
        }
    }*/

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
        writeToStream("disconnect::"+message+"::");

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

    public void sendThemesToServer() throws SevereException
    {
        StringBuilder finalQuery = new StringBuilder("themes::");

        for(Theme theme : clientListener.getThemes().getThemeList())
        {
            finalQuery.append(theme.getFullName())
                    .append("__")
                    .append(theme.getShortName())
                    .append("__")
                    .append(theme.getAuthor())
                    .append("__")
                    .append(theme.getVersion().getText())
                    .append("__::");
        }

        writeToStream(finalQuery.toString());
    }


    public void sendActionIcon(String clientProfileID, String actionID) throws SevereException
    {
        System.out.println("sending action icon "+clientProfileID+", "+actionID);
        sendIcon(clientProfileID, actionID, clientListener.getClientProfiles().getProfileFromID(clientProfileID).getActionFromID(actionID).getIconAsByteArray());
    }

    public void serverDisconnected(String message)
    {
        stop.set(true);
        String txt = "Disconnected!";

        if(!message.equals("disconnect::::"))
            txt = "Message : "+message.split("::")[1];

        exceptionAndAlertHandler.onAlert("Disconnected from Server", txt, StreamPiAlertType.WARNING);

        if(!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        clientListener.setConnected(false);
        clientListener.updateSettingsConnectDisconnectButton();

    }

    public void sendClientDetails() throws SevereException
    {
        Version clientVersion = clientInfo.getVersion();
        ReleaseStatus releaseStatus = clientInfo.getReleaseStatus();
        Version clientCommsStandard = clientInfo.getCommsStandardVersion();
        Version clientMinThemeStandard = clientInfo.getMinThemeSupportVersion();
        String clientNickname = Config.getInstance().getClientNickName();
        double screenWidth = Config.getInstance().getStartupWindowWidth();
        double screenHeight = Config.getInstance().getStartupWindowHeight();
        Platform OS = clientInfo.getPlatformType();
        String defaultProfileID = Config.getInstance().getStartupProfileID();

        writeToStream("client_details::"+
                clientVersion.getText()+"::"+
                releaseStatus+"::"+
                clientCommsStandard.getText()+"::"+
                clientMinThemeStandard.getText()+"::"+
                clientNickname+"::"+
                screenWidth+"::"+
                screenHeight+"::"+
                OS+"::" +
                defaultProfileID+"::"+
                clientListener.getDefaultThemeFullName()+"::");
    }


    public void sendProfileNamesToServer() throws SevereException
    {
        StringBuilder finalQuery = new StringBuilder("profiles::");

        finalQuery.append(clientListener.getClientProfiles().getClientProfiles().size()).append("::");

        for(ClientProfile clientProfile : clientListener.getClientProfiles().getClientProfiles())
        {
            finalQuery.append(clientProfile.getID()).append("::");
        }

        writeToStream(finalQuery.toString());
    }

    public void sendProfileDetailsToServer(String ID) throws SevereException
    {
        StringBuilder finalQuery = new StringBuilder("profile_details::");

        ClientProfile clientProfile = clientListener.getClientProfiles().getProfileFromID(ID);

        finalQuery.append(ID).append("::");

        finalQuery.append(clientProfile.getName())
                .append("::")
                .append(clientProfile.getRows())
                .append("::")
                .append(clientProfile.getCols())
                .append("::")
                .append(clientProfile.getActionSize())
                .append("::")
                .append(clientProfile.getActionGap())
                .append("::");


        writeToStream(finalQuery.toString());


        for(Action action : clientProfile.getActions())
        {
            sendActionDetails(clientProfile.getID(), action);
        }

        for(Action action : clientProfile.getActions())
        {
            if(action.isHasIcon())
            {
                System.out.println("23123123123 : "+action.getID() + action.isHasIcon());
                sendActionIcon(clientProfile.getID(), action.getID());
            }
        }

    }
    
    public void sendActionDetails(String profileID, Action action) throws SevereException {
        
        if(action == null)
        {
            logger.info("NO SUCH ACTION");
            return;
        }

        StringBuilder finalQuery = new StringBuilder("action_details::");

        finalQuery.append(profileID)
                .append("::")
                .append(action.getID())
                .append("::")
                .append(action.getActionType())
                .append("::");

        if(action.getActionType() == ActionType.NORMAL) {
            finalQuery.append(action.getVersion().getText());
        }

        finalQuery.append("::");

        if(action.getActionType() ==ActionType.NORMAL)
        {
            finalQuery.append(action.getModuleName());
        }


        finalQuery.append("::");

        //display

        finalQuery.append(action.getBgColourHex())
                .append("::");

        //icon
        finalQuery.append(action.isHasIcon())
                .append("::")
                .append(action.isShowIcon())
                .append("::");

        //text
        finalQuery.append(action.isShowDisplayText())
                .append("::")
                .append(action.getDisplayTextFontColourHex())
                .append("::")
                .append(action.getDisplayText())
                .append("::")
                .append(action.getDisplayTextAlignment())
                .append("::");

        //location

        if(action.getLocation() == null)
            finalQuery.append("-1::-1::");
        else
            finalQuery.append(action.getLocation().getRow())
                    .append("::")
                    .append(action.getLocation().getCol())
                    .append("::");

        //client properties

        ClientProperties clientProperties = action.getClientProperties();

        finalQuery.append(clientProperties.getSize())
                .append("::");

        for(Property property : clientProperties.get())
        {
            finalQuery.append(property.getName())
                    .append("__")
                    .append(property.getRawValue())
                    .append("__");

            finalQuery.append("!!");
        }

        finalQuery.append("::")
                .append(action.getParent())
                .append("::");


        writeToStream(finalQuery.toString());


    }

    public void saveActionDetails(String[] sep)
    {
        String profileID = sep[1];

        String ID = sep[2];
        ActionType actionType = ActionType.valueOf(sep[3]);

        //4 - Version
        //5 - ModuleName

        //display
        String bgColorHex = sep[6];

        //icon
        boolean isHasIcon = sep[7].equals("true");
        boolean isShowIcon = sep[8].equals("true");

        //text
        boolean isShowDisplayText = sep[9].equals("true");
        String displayFontColor = sep[10];
        String displayText = sep[11];
        DisplayTextAlignment displayTextAlignment = DisplayTextAlignment.valueOf(sep[12]);

        //location
        String row = sep[13];
        String col = sep[14];

        Location location = new Location(Integer.parseInt(row), Integer.parseInt(col));

        Action action = new Action(ID, actionType);

        if(actionType == ActionType.NORMAL)
        {
            try
            {
                action.setVersion(new Version(sep[4]));
                action.setModuleName(sep[5]);
            }
            catch (Exception e)
            {
                logger.severe(e.getMessage());
                e.printStackTrace();
            }
        }


        action.setBgColourHex(bgColorHex);

        action.setShowIcon(isShowIcon);
        action.setHasIcon(isHasIcon);

        System.out.println("IS HAS ICON : "+isHasIcon+", IS SHOW ICON :"+isShowIcon);


        action.setShowDisplayText(isShowDisplayText);
        action.setDisplayTextFontColourHex(displayFontColor);
        action.setDisplayText(displayText);
        action.setDisplayTextAlignment(displayTextAlignment);


        action.setLocation(location);

        //client properties

        int clientPropertiesSize = Integer.parseInt(sep[15]);

        String[] clientPropertiesRaw = sep[16].split("!!");

        ClientProperties clientProperties = new ClientProperties();

        if(actionType == ActionType.FOLDER)
            clientProperties.setDuplicatePropertyAllowed(true);

        for(int i = 0;i<clientPropertiesSize; i++)
        {
            String[] clientPraw = clientPropertiesRaw[i].split("__");

            Property property = new Property(clientPraw[0], Type.STRING);

            if(clientPraw.length > 1)
                property.setRawValue(clientPraw[1]);

            clientProperties.addProperty(property);
        }

        action.setClientProperties(clientProperties);


        String parent = sep[17];
        action.setParent(parent);


        try
        {
            Action old = clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(action.getID());
            
            if(old != null)
            {
                if(action.isHasIcon())
                    action.setIcon(clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(action.getID()).getIconAsByteArray());
            }

            clientListener.getClientProfiles().getProfileFromID(profileID).addAction(action);

            System.out.println("XXXXXXXXXXX " +action.isHasIcon());

            clientListener.getClientProfiles().getProfileFromID(profileID).saveActions();

            if(clientListener.getCurrentProfile().getID().equals(profileID) && action.getLocation().getCol()!=-1)
            {
                javafx.application.Platform.runLater(()->{
                    ActionBox box = clientListener.getActionBox(action.getLocation().getCol(), action.getLocation().getRow());
                    System.out.println(box==null);
                    System.out.println("GATYYY : "+action.getLocation().getCol()+","+action.getLocation().getRow());
                    box.clear();
                    box.setAction(action);
                    box.baseInit();
                    box.init();
                });
            }

            //clientListener.clearActionBox(action.getLocation().getCol(), action.getLocation().getRow());
            //clientListener.renderAction(profileID, action);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleMinorException(new MinorException(e.getMessage()));
        }
    }

    public void deleteAction(String profileID, String actionID)
    {
        try
        {


            Action acc =  clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);

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
                clientListener.clearActionBox(acc.getLocation().getCol(), acc.getLocation().getRow());
                clientListener.addBlankActionBox(acc.getLocation().getCol(), acc.getLocation().getRow());
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleMinorException(new MinorException(e.getMessage()));
        }
    }

    public void saveClientDetails(String[] sep)
    {
        try
        {

            Config.getInstance().setNickName(sep[1]);

            String oldWidth = Config.getInstance().getStartupWindowWidth()+"";
            String oldHeight = Config.getInstance().getStartupWindowHeight()+"";


            Config.getInstance().setStartupWindowSize(
                    Double.parseDouble(sep[2]),
                    Double.parseDouble(sep[3])
            );

            Config.getInstance().setStartupProfileID(sep[4]);

            String oldThemeFullName = Config.getInstance().getCurrentThemeFullName();

            Config.getInstance().setCurrentThemeFullName(sep[5]);

            if(!oldHeight.equals(sep[3]) || !oldWidth.equals(sep[2]) || !oldThemeFullName.equals(sep[5]))
                javafx.application.Platform.runLater(()-> clientListener.init());


            Config.getInstance().save();
            javafx.application.Platform.runLater(()->clientListener.loadSettings());
        }
        catch (SevereException e)
        {
            e.printStackTrace();
            exceptionAndAlertHandler.handleSevereException(e);
        }
    }

    public void saveProfileDetails(String[] sep) throws SevereException, MinorException
    {
        ClientProfile clientProfile = clientListener.getClientProfiles().getProfileFromID(sep[1]);

        if(clientProfile == null)
        {
            clientProfile = new ClientProfile(new File(Config.getInstance().getProfilesPath().toString()+"/"+sep[1]+".xml"),
                    Config.getInstance().getIconsPath());
        }

        clientProfile.setName(sep[2]);
        clientProfile.setRows(Integer.parseInt(sep[3]));
        clientProfile.setCols(Integer.parseInt(sep[4]));
        clientProfile.setActionSize(Integer.parseInt(sep[5]));
        clientProfile.setActionGap(Integer.parseInt(sep[6]));

        try
        {
            clientListener.getClientProfiles().addProfile(clientProfile);
            clientProfile.saveProfileDetails();
            clientListener.refreshGridIfCurrent(sep[1]);
            javafx.application.Platform.runLater(()->clientListener.loadSettings());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SevereException(e.getMessage());
        }
    }

    public void deleteProfile(String ID)
    {
        clientListener.getClientProfiles().deleteProfile(clientListener.getClientProfiles().getProfileFromID(ID));
    }

    public void onActionClicked(String profileID, String actionID) throws SevereException {
        writeToStream("action_clicked::"+profileID+"::"+actionID+"::");
    }

    public void actionFailed(String profileID, String actionID)
    {
        clientListener.onActionFailed(profileID, actionID);
    }
}
