// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.profile;

import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.NodeList;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import java.nio.file.Files;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.util.version.Version;
import com.stream_pi.action_api.actionproperty.ClientProperties;
import com.stream_pi.action_api.action.ActionType;
import org.w3c.dom.Node;
import com.stream_pi.util.xmlconfighelper.XMLConfigHelper;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import com.stream_pi.util.exception.MinorException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.util.logging.Logger;
import java.io.File;
import com.stream_pi.action_api.action.Action;
import java.util.HashMap;

public class ClientProfile implements Cloneable
{
    private String name;
    private String ID;
    private int rows;
    private int cols;
    private int actionSize;
    private int actionGap;
    private HashMap<String, Action> actions;
    private String iconsPath;
    private File file;
    private Logger logger;
    private Document document;
    
    public ClientProfile(final File file, final String iconsPath) throws MinorException {
        this.file = file;
        this.iconsPath = iconsPath;
        this.actions = new HashMap<String, Action>();
        this.logger = Logger.getLogger(ClientProfile.class.getName());
        if (!file.exists() && !file.isFile()) {
            this.createConfigFile(file);
        }
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.document = docBuilder.parse(file);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new MinorException("profile", "Unable to read profile config file.");
        }
        this.setID(file.getName().replace(".xml", ""));
        this.load();
    }
    
    private Element getProfileElement() {
        return (Element)this.document.getElementsByTagName("profile").item(0);
    }
    
    private Element getActionsElement() {
        return (Element)this.document.getElementsByTagName("actions").item(0);
    }
    
    public void load() throws MinorException {
        try {
            this.actions.clear();
            this.logger.info("Loading profile " + this.getID() + " ...");
            final String name = XMLConfigHelper.getStringProperty((Node)this.getProfileElement(), "name");
            final int rows = XMLConfigHelper.getIntProperty((Node)this.getProfileElement(), "rows");
            final int cols = XMLConfigHelper.getIntProperty((Node)this.getProfileElement(), "cols");
            final int actionSize = XMLConfigHelper.getIntProperty((Node)this.getProfileElement(), "action-size");
            final int actionGap = XMLConfigHelper.getIntProperty((Node)this.getProfileElement(), "action-gap");
            this.setName(name);
            this.setRows(rows);
            this.setCols(cols);
            this.setActionSize(actionSize);
            this.setActionGap(actionGap);
            final NodeList actionsNodesList = this.getActionsElement().getChildNodes();
            final int actionsSize = actionsNodesList.getLength();
            this.logger.info("Actions Size : " + actionsSize);
            for (int item = 0; item < actionsSize; ++item) {
                final Node eachActionNode = actionsNodesList.item(item);
                if (eachActionNode.getNodeType() == 1) {
                    final Element eachActionElement = (Element)eachActionNode;
                    if (eachActionElement.getNodeName().equals("action")) {
                        final String id = XMLConfigHelper.getStringProperty((Node)eachActionElement, "id");
                        final String parent = XMLConfigHelper.getStringProperty((Node)eachActionElement, "parent");
                        this.logger.info("Loading action " + id + " ...");
                        final ActionType actionType = ActionType.valueOf(XMLConfigHelper.getStringProperty((Node)eachActionElement, "action-type"));
                        final Action action = new Action(id, actionType);
                        action.setParent(parent);
                        final ClientProperties properties = new ClientProperties();
                        if (actionType == ActionType.FOLDER) {
                            properties.setDuplicatePropertyAllowed(true);
                        }
                        if (actionType == ActionType.NORMAL || actionType == ActionType.TOGGLE) {
                            action.setVersion(new Version(XMLConfigHelper.getStringProperty((Node)eachActionElement, "version")));
                            action.setModuleName(XMLConfigHelper.getStringProperty((Node)eachActionElement, "module-name"));
                            action.setDelayBeforeExecuting(Integer.parseInt(XMLConfigHelper.getStringProperty((Node)eachActionElement, "delay-before-running")));
                        }
                        final Node propertiesNode = eachActionElement.getElementsByTagName("properties").item(0);
                        final NodeList propertiesNodesList = propertiesNode.getChildNodes();
                        for (int propertiesSize = propertiesNodesList.getLength(), propItem = 0; propItem < propertiesSize; ++propItem) {
                            final Node eachPropertyNode = propertiesNodesList.item(propItem);
                            if (eachPropertyNode.getNodeType() == 1) {
                                final Element eachPropertyElement = (Element)eachPropertyNode;
                                if (eachPropertyElement.getNodeName().equals("property")) {
                                    final String propertyName = XMLConfigHelper.getStringProperty((Node)eachPropertyElement, "name");
                                    final String propertyValue = XMLConfigHelper.getStringProperty((Node)eachPropertyElement, "value");
                                    this.logger.info("Property Name : " + propertyName);
                                    this.logger.info("Property Value : " + propertyValue);
                                    final Property p = new Property(propertyName, Type.STRING);
                                    p.setRawValue(propertyValue);
                                    properties.addProperty(p);
                                }
                            }
                        }
                        action.setClientProperties(properties);
                        final Element displayElement = (Element)eachActionElement.getElementsByTagName("display").item(0);
                        try {
                            final Element locationElement = (Element)displayElement.getElementsByTagName("location").item(0);
                            final int row = XMLConfigHelper.getIntProperty((Node)locationElement, "row");
                            final int col = XMLConfigHelper.getIntProperty((Node)locationElement, "col");
                            action.setLocation(new Location(row, col));
                        }
                        catch (Exception e) {
                            this.logger.info("Action has no location, most probably a combine action child");
                        }
                        final Element backgroundElement = (Element)displayElement.getElementsByTagName("background").item(0);
                        action.setBgColourHex(XMLConfigHelper.getStringProperty((Node)backgroundElement, "colour-hex"));
                        final Element iconElement = (Element)backgroundElement.getElementsByTagName("icon").item(0);
                        final String currentIconState = XMLConfigHelper.getStringProperty((Node)iconElement, "current-state");
                        action.setCurrentIconState(currentIconState);
                        final Element statesElements = (Element)iconElement.getElementsByTagName("states").item(0);
                        final NodeList statesNodeList = statesElements.getChildNodes();
                        for (int i = 0; i < statesNodeList.getLength(); ++i) {
                            final Node eachStateNode = statesNodeList.item(i);
                            if (eachStateNode.getNodeType() == 1) {
                                final Element eachIconStateElement = (Element)eachStateNode;
                                if (eachIconStateElement.getNodeName().equals("state")) {
                                    final String state = eachIconStateElement.getTextContent();
                                    final File f = new File(this.iconsPath + "/" + id + "___" + state);
                                    try {
                                        final byte[] iconFileByteArray = Files.readAllBytes(f.toPath());
                                        action.addIcon(state, iconFileByteArray);
                                    }
                                    catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            }
                        }
                        final Element textElement = (Element)displayElement.getElementsByTagName("text").item(0);
                        final boolean showText = XMLConfigHelper.getBooleanProperty((Node)textElement, "show");
                        final String displayTextFontColour = XMLConfigHelper.getStringProperty((Node)textElement, "colour-hex");
                        final DisplayTextAlignment displayTextAlignment = DisplayTextAlignment.valueOf(XMLConfigHelper.getStringProperty((Node)textElement, "alignment"));
                        action.setDisplayTextAlignment(displayTextAlignment);
                        action.setShowDisplayText(showText);
                        action.setDisplayTextFontColourHex(displayTextFontColour);
                        final String displayText = XMLConfigHelper.getStringProperty((Node)textElement, "display-text");
                        action.setDisplayText(displayText);
                        final double fontSize = XMLConfigHelper.getDoubleProperty((Node)textElement, "font-size");
                        action.setNameFontSize(fontSize);
                        action.setCurrentToggleStatus(false);
                        this.addAction(action);
                        this.logger.info("... Done!");
                    }
                }
            }
            this.logger.info("Loaded profile " + this.getID() + " (" + this.getName() + ") !");
        }
        catch (Exception e3) {
            e3.printStackTrace();
            throw new MinorException("profile", "profile is corrupt.");
        }
    }
    
    public void addAction(final Action action) throws CloneNotSupportedException {
        this.actions.put(action.getID(), action.clone());
    }
    
    private void createConfigFile(final File file) throws MinorException {
        try {
            file.createNewFile();
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document newDocument = dBuilder.newDocument();
            final Element rootElement = newDocument.createElement("config");
            newDocument.appendChild(rootElement);
            final Element profileElement = newDocument.createElement("profile");
            rootElement.appendChild(profileElement);
            final Element actionsElement = newDocument.createElement("actions");
            rootElement.appendChild(actionsElement);
            final Element nameElement = newDocument.createElement("name");
            nameElement.setTextContent("Untitled profile");
            profileElement.appendChild(nameElement);
            final Element rowsElement = newDocument.createElement("rows");
            rowsElement.setTextContent("3");
            profileElement.appendChild(rowsElement);
            final Element colsElement = newDocument.createElement("cols");
            colsElement.setTextContent("3");
            profileElement.appendChild(colsElement);
            final Element actionSizeElement = newDocument.createElement("action-size");
            actionSizeElement.setTextContent("100");
            profileElement.appendChild(actionSizeElement);
            final Element actionGapElement = newDocument.createElement("action-gap");
            actionGapElement.setTextContent("5");
            profileElement.appendChild(actionGapElement);
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(newDocument);
            final StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new MinorException(e.getMessage());
        }
    }
    
    public void deleteProfile() {
        this.file.delete();
    }
    
    public void saveAction(final Action action) throws Exception {
        final int ind = this.getActionIndexInConfig(action.getID());
        if (ind != -1) {
            final Element actionElement = (Element)this.getActionsElement().getElementsByTagName("action").item(ind);
            this.getActionsElement().removeChild(actionElement);
        }
        final Element newActionElement = this.document.createElement("action");
        this.getActionsElement().appendChild(newActionElement);
        final Element idElement = this.document.createElement("id");
        idElement.setTextContent(action.getID());
        newActionElement.appendChild(idElement);
        final Element parentElement = this.document.createElement("parent");
        parentElement.setTextContent(action.getParent());
        newActionElement.appendChild(parentElement);
        final Element actionTypeElement = this.document.createElement("action-type");
        actionTypeElement.setTextContent("" + action.getActionType());
        newActionElement.appendChild(actionTypeElement);
        if (action.getActionType() == ActionType.NORMAL || action.getActionType() == ActionType.TOGGLE) {
            final Element versionElement = this.document.createElement("version");
            versionElement.setTextContent(action.getVersion().getText());
            newActionElement.appendChild(versionElement);
            System.out.println(action.getModuleName());
            final Element moduleNameElement = this.document.createElement("module-name");
            moduleNameElement.setTextContent(action.getModuleName());
            newActionElement.appendChild(moduleNameElement);
            final Element delayBeforeRunningElement = this.document.createElement("delay-before-running");
            delayBeforeRunningElement.setTextContent("" + action.getDelayBeforeExecuting());
            newActionElement.appendChild(delayBeforeRunningElement);
        }
        final Element displayElement = this.document.createElement("display");
        newActionElement.appendChild(displayElement);
        final Element backgroundElement = this.document.createElement("background");
        displayElement.appendChild(backgroundElement);
        final Element colourHexElement = this.document.createElement("colour-hex");
        colourHexElement.setTextContent(action.getBgColourHex());
        backgroundElement.appendChild(colourHexElement);
        final Element iconElement = this.document.createElement("icon");
        final Element currentIconStateElement = this.document.createElement("current-state");
        currentIconStateElement.setTextContent(action.getCurrentIconState());
        iconElement.appendChild(currentIconStateElement);
        final Element iconStatesElement = this.document.createElement("states");
        for (final String state : action.getIcons().keySet()) {
            final Element eachStateElement = this.document.createElement("state");
            eachStateElement.setTextContent(state);
            iconStatesElement.appendChild(eachStateElement);
        }
        iconElement.appendChild(iconStatesElement);
        backgroundElement.appendChild(iconElement);
        final Element textElement = this.document.createElement("text");
        displayElement.appendChild(textElement);
        final Element textTolourHexElement = this.document.createElement("colour-hex");
        textTolourHexElement.setTextContent(action.getDisplayTextFontColourHex());
        textElement.appendChild(textTolourHexElement);
        final Element textShowElement = this.document.createElement("show");
        textShowElement.setTextContent("" + action.isShowDisplayText());
        textElement.appendChild(textShowElement);
        final Element textDisplayTextElement = this.document.createElement("display-text");
        textDisplayTextElement.setTextContent(action.getDisplayText());
        textElement.appendChild(textDisplayTextElement);
        final Element textDisplayTextFontSizeElement = this.document.createElement("font-size");
        textDisplayTextFontSizeElement.setTextContent("" + action.getNameFontSize());
        textElement.appendChild(textDisplayTextFontSizeElement);
        final Element textAlignmentElement = this.document.createElement("alignment");
        textAlignmentElement.setTextContent("" + action.getDisplayTextAlignment());
        textElement.appendChild(textAlignmentElement);
        final Element locationElement = this.document.createElement("location");
        displayElement.appendChild(locationElement);
        final Element colElement = this.document.createElement("col");
        colElement.setTextContent("" + action.getLocation().getCol());
        locationElement.appendChild(colElement);
        final Element rowElement = this.document.createElement("row");
        rowElement.setTextContent("" + action.getLocation().getRow());
        locationElement.appendChild(rowElement);
        final Element propertiesElement = this.document.createElement("properties");
        newActionElement.appendChild(propertiesElement);
        for (final String key : action.getClientProperties().getNames()) {
            for (final Property eachProperty : action.getClientProperties().getMultipleProperties(key)) {
                final Element propertyElement = this.document.createElement("property");
                propertiesElement.appendChild(propertyElement);
                final Element nameElement = this.document.createElement("name");
                nameElement.setTextContent(eachProperty.getName());
                propertyElement.appendChild(nameElement);
                final Element valueElement = this.document.createElement("value");
                valueElement.setTextContent(eachProperty.getRawValue());
                propertyElement.appendChild(valueElement);
            }
        }
        this.save();
    }
    
    private int getActionIndexInConfig(final String actionID) {
        final NodeList actionsList = this.getActionsElement().getChildNodes();
        final int actionsSize = actionsList.getLength();
        int index = 0;
        for (int i = 0; i < actionsSize; ++i) {
            final Node eachActionNode = actionsList.item(index);
            if (eachActionNode.getNodeType() == 1) {
                if (eachActionNode.getNodeName().equals("action")) {
                    final Element eachActionElement = (Element)eachActionNode;
                    final Element idElement = (Element)eachActionElement.getElementsByTagName("id").item(0);
                    if (idElement.getTextContent().equals(actionID)) {
                        return index;
                    }
                    ++index;
                }
            }
        }
        return -1;
    }
    
    public void saveActionIcon(final String actionID, final byte[] array, final String state) throws MinorException {
        final int index = this.getActionIndexInConfig(actionID);
        this.logger.info("INDEXXXX : " + index);
        this.getActionFromID(actionID).addIcon(state, array);
        final File iconFile = new File(this.iconsPath + "/" + actionID + "___" + state);
        if (iconFile.exists()) {
            final boolean result = iconFile.delete();
            System.out.println("result : " + result);
        }
        try {
            final OutputStream outputStream = new FileOutputStream(iconFile);
            outputStream.write(array);
            outputStream.flush();
            outputStream.close();
            final Element actionElement = (Element)this.getActionsElement().getElementsByTagName("action").item(index);
            this.getActionsElement().removeChild(actionElement);
            final Element displayElement = (Element)actionElement.getElementsByTagName("display").item(0);
            final Element backgroundElement = (Element)displayElement.getElementsByTagName("background").item(0);
            final Element iconElement = (Element)backgroundElement.getElementsByTagName("icon").item(0);
            final Element statesElements = (Element)iconElement.getElementsByTagName("states").item(0);
            final Element stateElement = this.document.createElement("state");
            stateElement.setTextContent(state);
            statesElements.appendChild(stateElement);
            this.getActionsElement().appendChild(actionElement);
            this.save();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void save() throws TransformerException {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        final Result output = new StreamResult(this.file);
        final Source input = new DOMSource(this.document);
        transformer.transform(input, output);
    }
    
    public void saveProfileDetails() throws TransformerException {
        XMLConfigHelper.removeChilds((Node)this.getProfileElement());
        final Element nameElement = this.document.createElement("name");
        nameElement.setTextContent(this.getName());
        this.getProfileElement().appendChild(nameElement);
        final Element rowsElement = this.document.createElement("rows");
        rowsElement.setTextContent("" + this.getRows());
        this.getProfileElement().appendChild(rowsElement);
        final Element colsElement = this.document.createElement("cols");
        colsElement.setTextContent("" + this.getCols());
        this.getProfileElement().appendChild(colsElement);
        final Element actionSizeElement = this.document.createElement("action-size");
        actionSizeElement.setTextContent("" + this.getActionSize());
        this.getProfileElement().appendChild(actionSizeElement);
        final Element actionGapElement = this.document.createElement("action-gap");
        actionGapElement.setTextContent("" + this.getActionGap());
        this.getProfileElement().appendChild(actionGapElement);
        this.save();
    }
    
    public void saveActions() throws Exception {
        XMLConfigHelper.removeChilds((Node)this.getActionsElement());
        this.save();
        for (Action action : this.getActions()) {
            this.logger.info("ACTION ID :" + action.getID());
            this.logger.info("Action ICON : " + action.isHasIcon());
            this.saveAction(action);
        }
    }
    
    public void removeAction(final String ID) throws Exception {
        final int index = this.getActionIndexInConfig(ID);
        if (index > -1) {
            final Element actionElement = (Element)this.getActionsElement().getElementsByTagName("action").item(index);
            final Element displayElement = (Element)actionElement.getElementsByTagName("display").item(0);
            final Element backgroundElement = (Element)displayElement.getElementsByTagName("background").item(0);
            final Element iconElement = (Element)backgroundElement.getElementsByTagName("icon").item(0);
            final Element statesElements = (Element)iconElement.getElementsByTagName("states").item(0);
            final NodeList statesNodeList = statesElements.getChildNodes();
            for (int i = 0; i < statesNodeList.getLength(); ++i) {
                final Node eachStateNode = statesNodeList.item(i);
                if (eachStateNode.getNodeType() == 1) {
                    final Element eachIconStateElement = (Element)eachStateNode;
                    if (eachIconStateElement.getNodeName().equals("state")) {
                        final String state = eachIconStateElement.getTextContent();
                        new File(this.iconsPath + "/" + ID + "___" + state).delete();
                    }
                }
            }
            this.actions.remove(ID);
        }
    }
    
    public ArrayList<Action> getActions() {
        final ArrayList<Action> p = new ArrayList<Action>();
        for (final String profile : this.actions.keySet()) {
            p.add(this.actions.get(profile));
        }
        return p;
    }
    
    public String getID() {
        return this.ID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public int getCols() {
        return this.cols;
    }
    
    public int getActionSize() {
        return this.actionSize;
    }
    
    public Action getActionFromID(final String ID) {
        return this.actions.getOrDefault(ID, null);
    }
    
    public int getActionGap() {
        return this.actionGap;
    }
    
    public void setRows(final int rows) {
        this.rows = rows;
    }
    
    public void setCols(final int cols) {
        this.cols = cols;
    }
    
    public void setID(final String ID) {
        this.ID = ID;
    }
    
    public void setActionSize(final int actionSize) {
        this.actionSize = actionSize;
    }
    
    public void setActionGap(final int actionGap) {
        this.actionGap = actionGap;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
