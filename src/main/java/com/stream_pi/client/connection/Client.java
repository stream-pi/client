// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.connection;

import java.util.Objects;
import java.io.File;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.util.version.Version;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.action_api.actionproperty.ClientProperties;
import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.action.ActionType;
import java.util.ArrayList;
import java.util.Iterator;
import com.stream_pi.client.profile.ClientProfile;
import javafx.geometry.Orientation;
import com.stream_pi.client.io.Config;
import com.stream_pi.theme_api.Theme;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionBox;
import javafx.application.Platform;
import com.stream_pi.util.comms.Message;
import com.stream_pi.util.exception.SevereException;
import java.io.IOException;
import com.stream_pi.util.exception.MinorException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import javafx.concurrent.Task;
import java.util.logging.Logger;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.controller.ClientListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread
{
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private AtomicBoolean stop;
    private ClientListener clientListener;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ClientInfo clientInfo;
    private String serverIP;
    private int serverPort;
    private Logger logger;
    private Runnable onConnectAndSetupToBeRun;
    
    public Client(final String serverIP, final int serverPort, final ClientListener clientListener, final ExceptionAndAlertHandler exceptionAndAlertHandler, final Runnable onConnectAndSetupToBeRun) {
        this.stop = new AtomicBoolean(false);
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientInfo = ClientInfo.getInstance();
        this.clientListener = clientListener;
        this.onConnectAndSetupToBeRun = onConnectAndSetupToBeRun;
        this.logger = Logger.getLogger(Client.class.getName());
        clientListener.getExecutor().submit((Runnable)new Task<Void>() {
            protected Void call() {
                try {
                    try {
                        Client.this.logger.info("Trying to connect to server at " + serverIP + ":" + serverPort);
                        (Client.this.socket = new Socket()).connect(new InetSocketAddress(serverIP, serverPort), 5000);
                        clientListener.setConnected(true);
                        Client.this.logger.info("Connected to " + Client.this.socket.getRemoteSocketAddress() + " !");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        clientListener.setConnected(false);
                        throw new MinorException("Connection Error", "Unable to connect to server. Please check settings and connection and try again.");
                    }
                    finally {
                        clientListener.updateSettingsConnectDisconnectButton();
                    }
                    try {
                        Client.this.oos = new ObjectOutputStream(Client.this.socket.getOutputStream());
                        Client.this.ois = new ObjectInputStream(Client.this.socket.getInputStream());
                    }
                    catch (IOException e) {
                        Client.this.logger.severe(e.getMessage());
                        e.printStackTrace();
                        throw new MinorException("Unable to set up io Streams to server. Check connection and try again.");
                    }
                    Client.this.start();
                }
                catch (MinorException e2) {
                    exceptionAndAlertHandler.handleMinorException(e2);
                }
                return null;
            }
        });
    }
    
    public synchronized void exit() {
        if (this.stop.get()) {
            return;
        }
        this.logger.info("Stopping ...");
        try {
            if (this.socket != null) {
                this.disconnect();
            }
        }
        catch (SevereException e) {
            this.logger.severe(e.getMessage());
            this.exceptionAndAlertHandler.handleSevereException(e);
            e.printStackTrace();
        }
    }
    
    public synchronized void sendMessage(final Message message) throws SevereException {
        try {
            this.oos.writeObject(message);
            this.oos.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new SevereException("Unable to write to io Stream!");
        }
    }
    
    @Override
    public void run() {
        try {
            while (!this.stop.get()) {
                try {
                    final Message message = (Message)this.ois.readObject();
                    final String header = message.getHeader();
                    this.logger.info("Message Received. Heading : " + header);
                    final String s = header;
                    switch (s) {
                        case "ready": {
                            this.onServerReady();
                            continue;
                        }
                        case "action_icon": {
                            this.onActionIconReceived(message);
                            continue;
                        }
                        case "disconnect": {
                            this.serverDisconnected(message);
                            continue;
                        }
                        case "get_client_details": {
                            this.sendClientDetails();
                            continue;
                        }
                        case "get_client_screen_details": {
                            this.sendClientScreenDetails();
                            continue;
                        }
                        case "get_profiles": {
                            this.sendProfileNamesToServer();
                            continue;
                        }
                        case "get_profile_details": {
                            this.sendProfileDetailsToServer(message);
                            continue;
                        }
                        case "save_action_details": {
                            this.saveActionDetails(message);
                            continue;
                        }
                        case "delete_action": {
                            this.deleteAction(message);
                            continue;
                        }
                        case "get_themes": {
                            this.sendThemesToServer();
                            continue;
                        }
                        case "save_client_details": {
                            this.saveClientDetails(message);
                            continue;
                        }
                        case "save_client_profile": {
                            this.saveProfileDetails(message);
                            continue;
                        }
                        case "delete_profile": {
                            this.deleteProfile(message);
                            continue;
                        }
                        case "action_failed": {
                            this.actionFailed(message);
                            continue;
                        }
                        case "set_toggle_status": {
                            this.onSetToggleStatus(message);
                            continue;
                        }
                        default: {
                            this.logger.warning("Command '" + header + "' does not match records. Make sure client and server versions are equal.");
                            continue;
                        }
                    }
                    continue;
                }
                catch (IOException | ClassNotFoundException ex) {
                    final Exception ex2;
                    final Exception e = ex2;
                    this.logger.severe(e.getMessage());
                    e.printStackTrace();
                    this.clientListener.setConnected(false);
                    this.clientListener.updateSettingsConnectDisconnectButton();
                    this.clientListener.onDisconnect();
                    if (!this.stop.get()) {
                        throw new MinorException("Accidentally disconnected from Server! (Failed at readUTF)");
                    }
                    this.exit();
                    return;
                }
                break;
            }
        }
        catch (SevereException e2) {
            this.logger.severe(e2.getMessage());
            e2.printStackTrace();
            this.exceptionAndAlertHandler.handleSevereException(e2);
        }
        catch (MinorException e3) {
            this.logger.severe(e3.getMessage());
            e3.printStackTrace();
            this.exceptionAndAlertHandler.handleMinorException(e3);
        }
    }
    
    private void onSetToggleStatus(final Message message) {
        final String[] arr = message.getStringArrValue();
        final String profileID = arr[0];
        final String actionID = arr[1];
        final boolean newStatus = arr[2].equals("true");
        final boolean currentStatus = this.clientListener.getToggleStatus(profileID, actionID);
        if (currentStatus == newStatus) {
            return;
        }
        final ActionBox actionBox = this.clientListener.getActionBoxByProfileAndID(profileID, actionID);
        if (actionBox != null) {
            actionBox.setCurrentToggleStatus(newStatus);
            Platform.runLater(() -> actionBox.toggle(newStatus));
        }
    }
    
    private void onActionIconReceived(final Message message) throws MinorException {
        final String profileID = message.getStringArrValue()[0];
        final String actionID = message.getStringArrValue()[1];
        final String state = message.getStringArrValue()[2];
        this.clientListener.getClientProfiles().getProfileFromID(profileID).saveActionIcon(actionID, message.getByteArrValue(), state);
        final Action a = this.clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);
        this.clientListener.renderAction(profileID, a);
    }
    
    public synchronized void sendIcon(final String profileID, final String actionID, final String state, final byte[] icon) throws SevereException {
        try {
            Thread.sleep(50L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Message message = new Message("action_icon");
        message.setStringArrValue(new String[] { profileID, actionID, state });
        message.setByteArrValue(icon);
        this.sendMessage(message);
    }
    
    public void disconnect() throws SevereException {
        this.disconnect("");
    }
    
    public void disconnect(final String message) throws SevereException {
        if (this.stop.get()) {
            return;
        }
        this.stop.set(true);
        this.logger.info("Sending server disconnect message ...");
        final Message m = new Message("disconnect");
        m.setStringValue(message);
        this.sendMessage(m);
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
            this.clientListener.setConnected(false);
            this.clientListener.updateSettingsConnectDisconnectButton();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new SevereException("Unable to close socket");
        }
    }
    
    public void onServerReady() {
        if (this.onConnectAndSetupToBeRun != null) {
            this.onConnectAndSetupToBeRun.run();
            this.onConnectAndSetupToBeRun = null;
        }
    }
    
    public void sendThemesToServer() throws SevereException {
        final Message message = new Message("themes");
        final String[] arr = new String[this.clientListener.getThemes().getThemeList().size() * 4];
        int x = 0;
        for (int i = 0; i < this.clientListener.getThemes().getThemeList().size(); ++i) {
            final Theme theme = this.clientListener.getThemes().getThemeList().get(i);
            arr[x] = theme.getFullName();
            arr[x + 1] = theme.getShortName();
            arr[x + 2] = theme.getAuthor();
            arr[x + 3] = theme.getVersion().getText();
            x += 4;
        }
        message.setStringArrValue(arr);
        this.sendMessage(message);
    }
    
    public void sendActionIcon(final String clientProfileID, final String actionID, final String state) throws SevereException {
        this.sendIcon(clientProfileID, actionID, state, this.clientListener.getClientProfiles().getProfileFromID(clientProfileID).getActionFromID(actionID).getIcon(state));
    }
    
    public void serverDisconnected(final Message message) {
        this.stop.set(true);
        String txt = "Disconnected!";
        final String m = message.getStringValue();
        if (!m.isBlank()) {
            txt = "Message : " + m;
        }
        this.exceptionAndAlertHandler.handleMinorException(new MinorException("Disconnected from Server", txt));
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.clientListener.setConnected(false);
        this.clientListener.onDisconnect();
        this.clientListener.updateSettingsConnectDisconnectButton();
    }
    
    public void sendClientScreenDetails() throws SevereException {
        final Message toBeSent = new Message("client_screen_details");
        toBeSent.setDoubleArrValue(new double[] { this.clientListener.getStageWidth(), this.clientListener.getStageHeight() });
        this.sendMessage(toBeSent);
    }
    
    public void sendClientDetails() throws SevereException {
        this.sendClientDetails("register_client_details");
    }
    
    public void updateClientDetails() throws SevereException {
        this.sendClientDetails("update_client_details");
    }
    
    public void sendClientDetails(final String header) throws SevereException {
        final String clientVersion = this.clientInfo.getVersion().getText();
        final String releaseStatus = this.clientInfo.getReleaseStatus().toString();
        final String clientCommStandard = this.clientInfo.getCommStandardVersion().getText();
        final String clientMinThemeStandard = this.clientInfo.getMinThemeSupportVersion().getText();
        final String clientNickname = Config.getInstance().getClientNickName();
        final String screenWidth = "" + this.clientListener.getStageWidth();
        final String screenHeight = "" + this.clientListener.getStageHeight();
        final String OS = "" + this.clientInfo.getPlatform();
        final String defaultProfileID = Config.getInstance().getStartupProfileID();
        final Message toBeSent = new Message(header);
        String tbs = null;
        final Orientation orientation = this.clientListener.getCurrentOrientation();
        if (orientation != null) {
            tbs = orientation.toString();
        }
        toBeSent.setStringArrValue(new String[] { clientVersion, releaseStatus, clientCommStandard, clientMinThemeStandard, clientNickname, screenWidth, screenHeight, OS, defaultProfileID, this.clientListener.getDefaultThemeFullName(), tbs });
        this.sendMessage(toBeSent);
    }
    
    public void sendProfileNamesToServer() throws SevereException {
        final Message message = new Message("profiles");
        final String[] profilesArray = new String[this.clientListener.getClientProfiles().getClientProfiles().size()];
        final int[] profilesActionsArray = new int[profilesArray.length];
        for (int i = 0; i < profilesArray.length; ++i) {
            final ClientProfile clientProfile = this.clientListener.getClientProfiles().getClientProfiles().get(i);
            profilesArray[i] = clientProfile.getID();
            profilesActionsArray[i] = clientProfile.getActions().size();
        }
        message.setStringArrValue(profilesArray);
        message.setIntArrValue(profilesActionsArray);
        this.sendMessage(message);
    }
    
    public void sendProfileDetailsToServer(final Message message) throws SevereException {
        final String ID = message.getStringValue();
        final Message tbs1 = new Message("profile_details");
        final ClientProfile clientProfile = this.clientListener.getClientProfiles().getProfileFromID(ID);
        final String[] arr = { ID, clientProfile.getName(), "" + clientProfile.getRows(), "" + clientProfile.getCols(), "" + clientProfile.getActionSize(), "" + clientProfile.getActionGap() };
        tbs1.setStringArrValue(arr);
        this.sendMessage(tbs1);
        for (final Action action : clientProfile.getActions()) {
            this.sendActionDetails(clientProfile.getID(), action);
        }
        for (final Action action : clientProfile.getActions()) {
            if (action.isHasIcon()) {
                for (final String key : action.getIcons().keySet()) {
                    this.sendActionIcon(clientProfile.getID(), action.getID(), key);
                }
            }
        }
    }
    
    public void sendActionDetails(final String profileID, final Action action) throws SevereException {
        if (action == null) {
            this.logger.info("NO SUCH ACTION");
            return;
        }
        final ArrayList<String> a = new ArrayList<String>();
        a.add(profileID);
        a.add(action.getID());
        a.add("" + action.getActionType());
        if (action.getActionType() == ActionType.NORMAL || action.getActionType() == ActionType.TOGGLE) {
            a.add(action.getVersion().getText());
        }
        else {
            a.add("no");
        }
        if (action.getActionType() == ActionType.NORMAL || action.getActionType() == ActionType.TOGGLE) {
            a.add(action.getModuleName());
        }
        else {
            a.add("nut");
        }
        a.add(action.getBgColourHex());
        final StringBuilder allIconStatesNames = new StringBuilder();
        for (final String eachState : action.getIcons().keySet()) {
            allIconStatesNames.append(eachState).append("::");
        }
        a.add(allIconStatesNames.toString());
        a.add(action.getCurrentIconState());
        a.add("" + action.isShowDisplayText());
        a.add(action.getDisplayTextFontColourHex());
        a.add(action.getDisplayText());
        a.add("" + action.getNameFontSize());
        a.add("" + action.getDisplayTextAlignment());
        if (action.getLocation() == null) {
            a.add("-1");
            a.add("-1");
        }
        else {
            a.add("" + action.getLocation().getRow());
            a.add("" + action.getLocation().getCol());
        }
        a.add(action.getParent());
        a.add("" + action.getDelayBeforeExecuting());
        final ClientProperties clientProperties = action.getClientProperties();
        a.add("" + clientProperties.getSize());
        for (final Property property : clientProperties.get()) {
            a.add(property.getName());
            a.add(property.getRawValue());
        }
        final Message message = new Message("action_details");
        String[] x = new String[a.size()];
        x = a.toArray(x);
        message.setStringArrValue(x);
        this.sendMessage(message);
    }
    
    public void saveActionDetails(final Message message) {
        final String[] r = message.getStringArrValue();
        final String profileID = r[0];
        final String actionID = r[1];
        final ActionType actionType = ActionType.valueOf(r[2]);
        final String bgColorHex = r[5];
        final String[] iconStates = r[6].split("::");
        final String currentIconState = r[7];
        final boolean isShowDisplayText = r[8].equals("true");
        final String displayFontColor = r[9];
        final String displayText = r[10];
        final double displayLabelFontSize = Double.parseDouble(r[11]);
        final DisplayTextAlignment displayTextAlignment = DisplayTextAlignment.valueOf(r[12]);
        final String row = r[13];
        final String col = r[14];
        final Location location = new Location(Integer.parseInt(row), Integer.parseInt(col));
        final Action action = new Action(actionID, actionType);
        Label_0190: {
            if (actionType != ActionType.NORMAL) {
                if (actionType != ActionType.TOGGLE) {
                    break Label_0190;
                }
            }
            try {
                action.setVersion(new Version(r[3]));
                action.setModuleName(r[4]);
            }
            catch (Exception e) {
                this.logger.severe(e.getMessage());
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
        final String parent = r[15];
        action.setParent(parent);
        action.setDelayBeforeExecuting(Integer.parseInt(r[16]));
        final int clientPropertiesSize = Integer.parseInt(r[17]);
        final ClientProperties clientProperties = new ClientProperties();
        if (actionType == ActionType.FOLDER) {
            clientProperties.setDuplicatePropertyAllowed(true);
        }
        for (int i = 18; i < clientPropertiesSize * 2 + 18; i += 2) {
            final Property property = new Property(r[i], Type.STRING);
            property.setRawValue(r[i + 1]);
            clientProperties.addProperty(property);
        }
        action.setClientProperties(clientProperties);
        try {
            final Action old = this.clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(action.getID());
            if (old != null) {
                for (String oldState : old.getIcons().keySet()) {
                    boolean isPresent = false;
                    for (final String state : iconStates) {
                        if (state.equals(oldState)) {
                            isPresent = true;
                            action.addIcon(state, old.getIcon(state));
                            break;
                        }
                    }
                    if (!isPresent) {
                        new File(Config.getInstance().getIconsPath() + "/" + actionID + "___" + oldState).delete();
                    }
                }
            }
            this.clientListener.getClientProfiles().getProfileFromID(profileID).addAction(action);
            this.clientListener.getClientProfiles().getProfileFromID(profileID).saveAction(action);
            this.clientListener.renderAction(profileID, action);
            if (this.clientListener.getScreenSaver() != null) {
                Platform.runLater(() -> this.clientListener.getScreenSaver().restart());
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            this.exceptionAndAlertHandler.handleMinorException(new MinorException(e2.getMessage()));
        }
    }
    
    public void deleteAction(final Message message) {
        try {
            final String[] arr = message.getStringArrValue();
            final String profileID = arr[0];
            final String actionID = arr[1];
            final Action acc = this.clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID);
            if (acc == null) {
                this.exceptionAndAlertHandler.handleMinorException(new MinorException("Unable to delete action " + actionID + " since it does not exist."));
                return;
            }
            if (acc.getActionType() == ActionType.FOLDER) {
                final ArrayList<String> idsToBeRemoved = new ArrayList<String>();
                final ArrayList<String> folders = new ArrayList<String>();
                final String folderToBeDeletedID = this.clientListener.getClientProfiles().getProfileFromID(profileID).getActionFromID(actionID).getID();
                folders.add(folderToBeDeletedID);
                boolean startOver = true;
                while (startOver) {
                    startOver = false;
                    for (final Action action : this.clientListener.getClientProfiles().getProfileFromID(profileID).getActions()) {
                        if (folders.contains(action.getParent()) && !idsToBeRemoved.contains(action.getID())) {
                            idsToBeRemoved.add(action.getID());
                            if (action.getActionType() != ActionType.FOLDER) {
                                continue;
                            }
                            folders.add(action.getID());
                            startOver = true;
                        }
                    }
                }
                for (final String ids : idsToBeRemoved) {
                    this.clientListener.getClientProfiles().getProfileFromID(profileID).removeAction(ids);
                }
                final ClientListener clientListener = this.clientListener;
                Objects.requireNonNull(clientListener);
                Platform.runLater(clientListener::renderRootDefaultProfile);
            }
            else if (acc.getActionType() == ActionType.COMBINE) {
                for (final Property property : acc.getClientProperties().get()) {
                    this.clientListener.getClientProfiles().getProfileFromID(profileID).removeAction(property.getRawValue());
                }
            }
            this.clientListener.getClientProfiles().getProfileFromID(profileID).removeAction(acc.getID());
            this.clientListener.getClientProfiles().getProfileFromID(profileID).saveActions();
            if (acc.getLocation().getCol() != -1) {
                Platform.runLater(() -> {
                    if (this.clientListener.getCurrentProfile().getID().equals(profileID) && this.clientListener.getCurrentParent().equals(acc.getParent())) {
                        this.clientListener.clearActionBox(acc.getLocation().getCol(), acc.getLocation().getRow());
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.exceptionAndAlertHandler.handleMinorException(new MinorException(e.getMessage()));
        }
    }
    
    public void saveClientDetails(final Message message) {
        try {
            final boolean reInit = false;
            final String[] sep = message.getStringArrValue();
            Config.getInstance().setNickName(sep[0]);
            Config.getInstance().setStartupProfileID(sep[1]);
            final String oldThemeFullName = Config.getInstance().getCurrentThemeFullName();
            final String newThemeFullName = sep[2];
            Config.getInstance().setCurrentThemeFullName(sep[2]);
            Config.getInstance().save();
            if (!oldThemeFullName.equals(newThemeFullName)) {
                Platform.runLater(() -> {
                    try {
                        this.clientListener.initThemes();
                    }
                    catch (SevereException e2) {
                        this.exceptionAndAlertHandler.handleSevereException(e2);
                    }
                    return;
                });
            }
            final ClientListener clientListener = this.clientListener;
            Objects.requireNonNull(clientListener);
            Platform.runLater(clientListener::loadSettings);
        }
        catch (SevereException e) {
            e.printStackTrace();
            this.exceptionAndAlertHandler.handleSevereException(e);
        }
    }
    
    public void saveProfileDetails(final Message message) throws SevereException, MinorException {
        final String[] sep = message.getStringArrValue();
        ClientProfile clientProfile = this.clientListener.getClientProfiles().getProfileFromID(sep[0]);
        if (clientProfile == null) {
            clientProfile = new ClientProfile(new File(Config.getInstance().getProfilesPath() + "/" + sep[0] + ".xml"), Config.getInstance().getIconsPath());
        }
        clientProfile.setName(sep[1]);
        clientProfile.setRows(Integer.parseInt(sep[2]));
        clientProfile.setCols(Integer.parseInt(sep[3]));
        clientProfile.setActionSize(Integer.parseInt(sep[4]));
        clientProfile.setActionGap(Integer.parseInt(sep[5]));
        try {
            this.clientListener.getClientProfiles().addProfile(clientProfile);
            clientProfile.saveProfileDetails();
            this.clientListener.refreshGridIfCurrentProfile(sep[0]);
            final ClientListener clientListener = this.clientListener;
            Objects.requireNonNull(clientListener);
            Platform.runLater(clientListener::loadSettings);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SevereException(e.getMessage());
        }
    }
    
    public void deleteProfile(final Message message) {
        this.clientListener.getClientProfiles().deleteProfile(this.clientListener.getClientProfiles().getProfileFromID(message.getStringValue()));
        if (this.clientListener.getCurrentProfile().getID().equals(message.getStringValue())) {
            final ClientListener clientListener = this.clientListener;
            Objects.requireNonNull(clientListener);
            Platform.runLater(clientListener::renderRootDefaultProfile);
        }
    }
    
    public void onActionClicked(final String profileID, final String actionID, final boolean toggleState) throws SevereException {
        final Message m = new Message("action_clicked");
        m.setStringArrValue(new String[] { profileID, actionID, "" + toggleState });
        this.sendMessage(m);
    }
    
    public void updateOrientationOnClient(final Orientation orientation) throws SevereException {
        final Message m = new Message("client_orientation");
        m.setStringValue(orientation.toString());
        this.sendMessage(m);
    }
    
    public void actionFailed(final Message message) {
        final String[] r = message.getStringArrValue();
        final String profileID = r[0];
        final String actionID = r[1];
        this.clientListener.onActionFailed(profileID, actionID);
    }
}
