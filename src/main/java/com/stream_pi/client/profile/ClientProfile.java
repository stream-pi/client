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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.action_api.actionproperty.ClientProperties;
import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.version.Version;
import com.stream_pi.util.xmlconfighelper.XMLConfigHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ClientProfile implements Cloneable
{
    private String name, ID;

    private int rows, cols;

    private double actionDefaultDisplayTextFontSize, actionSize, actionGap;
    
    private HashMap<String, ClientAction> actions;
    private String iconsPath;

    private File file;

    private Logger logger;
    private Document document;

    public ClientProfile(File file, String iconsPath, String name, int rows, int cols, double actionSize, double actionGap, double actionDefaultDisplayTextFontSize) throws MinorException
    {
        this.file = file;
        this.iconsPath = iconsPath;
        actions = new HashMap<>();
        logger = Logger.getLogger(ClientProfile.class.getName());

        setID(file.getName().replace(".xml", ""));
        setName(name);
        setRows(rows);
        setCols(cols);
        setActionSize(actionSize);
        setActionGap(actionGap);
        setActionDefaultDisplayTextFontSize(actionDefaultDisplayTextFontSize);

        if(file.isFile())
        {
            throw new MinorException(I18N.getString("Unable to create profile file!")+"\n"+I18N.getString("profile.ClientProfile.duplicateProfileFileExists", file.getAbsolutePath()));
        }
        else
        {
            createNewProfileFile(file);
        }
    }

    public ClientProfile(File file, String iconsPath) throws MinorException
    {
        this.file = file;
        this.iconsPath = iconsPath;
        actions = new HashMap<>();
        logger = Logger.getLogger(ClientProfile.class.getName());


        initDocument();
        load();
    }

    private void initDocument() throws MinorException
    {
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MinorException(I18N.getString("profile.ClientProfile.failedToParseProfile", file.getAbsolutePath(), e.getLocalizedMessage()));
        }

        setID(file.getName().replace(".xml", ""));
    }


    private Element getProfileElement()
    {
        return (Element) document.getElementsByTagName("profile").item(0);
    }

    private Element getActionsElement()
    {
        return (Element) document.getElementsByTagName("actions").item(0);
    }

    public void load() throws MinorException
    {
        try
        {
            actions.clear();

            logger.info("Loading profile "+getID()+" ...");

            setName(XMLConfigHelper.getStringProperty(getProfileElement(), "name"));
            setRows(XMLConfigHelper.getIntProperty(getProfileElement(), "rows"));
            setCols(XMLConfigHelper.getIntProperty(getProfileElement(), "cols"));
            setActionSize(XMLConfigHelper.getDoubleProperty(getProfileElement(), "action-size"));
            setActionGap(XMLConfigHelper.getDoubleProperty(getProfileElement(), "action-gap"));
            setActionDefaultDisplayTextFontSize(XMLConfigHelper.getDoubleProperty(getProfileElement(), "action-default-display-text-font-size"));

            //Load Actions

            NodeList actionsNodesList = getActionsElement().getChildNodes();

            int actionsSize = actionsNodesList.getLength();

            logger.info("Actions Size : "+actionsSize);

            for(int item = 0; item<actionsSize; item++)
            {
                Node eachActionNode = actionsNodesList.item(item);

                if(eachActionNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element eachActionElement = (Element) eachActionNode;

                if(!eachActionElement.getNodeName().equals("action"))
                    continue;

                

                String id = XMLConfigHelper.getStringProperty(eachActionElement, "id");
                String parent = XMLConfigHelper.getStringProperty(eachActionElement, "parent");

                logger.info("Loading action "+id+" ...");

                ActionType actionType = ActionType.valueOf(XMLConfigHelper.getStringProperty(eachActionElement, "action-type"));

                ClientAction action = new ClientAction(id, actionType);
                action.setParent(parent);

                ClientProperties properties = new ClientProperties();

                if(actionType == ActionType.FOLDER)
                    properties.setDuplicatePropertyAllowed(true);


                if(actionType == ActionType.NORMAL || actionType == ActionType.TOGGLE || actionType == ActionType.GAUGE)
                {
                    action.setVersion(new Version(XMLConfigHelper.getStringProperty(eachActionElement, "version")));
                    action.setUniqueID(XMLConfigHelper.getStringProperty(eachActionElement, "unique-ID"));
                    action.setDelayBeforeExecuting(Integer.parseInt(
                            XMLConfigHelper.getStringProperty(eachActionElement, "delay-before-running")
                    ));

                    if (actionType == ActionType.GAUGE)
                    {
                        action.setGaugeAnimated(XMLConfigHelper.getBooleanProperty(eachActionElement, "is-animated"));
                    }
                }

                Node propertiesNode = eachActionElement.getElementsByTagName("properties").item(0);

                NodeList propertiesNodesList = propertiesNode.getChildNodes();

                int propertiesSize = propertiesNodesList.getLength();

                for(int propItem = 0; propItem < propertiesSize; propItem++)
                {
                    Node eachPropertyNode = propertiesNodesList.item(propItem);

                    if(eachPropertyNode.getNodeType() != Node.ELEMENT_NODE)
                        continue;
    
                    Element eachPropertyElement = (Element) eachPropertyNode;
    
                    if(!eachPropertyElement.getNodeName().equals("property"))
                        continue;
    
                    

                    String propertyName = XMLConfigHelper.getStringProperty(eachPropertyElement, "name");
                    String propertyValue = XMLConfigHelper.getStringProperty(eachPropertyElement, "value");

                    logger.info("Property Name : "+propertyName);
                    logger.info("Property Value : "+propertyValue);

                    Property p = new Property(propertyName, Type.STRING);
                    p.setRawValue(propertyValue);

                    properties.addProperty(p);
                }

                action.setClientProperties(properties);



                Element displayElement = (Element) eachActionElement.getElementsByTagName("display").item(0);

                //display

                //location & span

                try
                {
                    Element locationElement = (Element) displayElement.getElementsByTagName("location").item(0);
                    int row = XMLConfigHelper.getIntProperty(locationElement, "row");
                    int col = XMLConfigHelper.getIntProperty(locationElement, "col");
                    int rowSpan = XMLConfigHelper.getIntProperty(locationElement, "row-span");
                    int colSpan = XMLConfigHelper.getIntProperty(locationElement, "col-span");

                    action.setLocation(new Location(row, col, rowSpan, colSpan));
                }
                catch (Exception e)
                {
                    logger.info("Action has no location, most probably a combine action child");
                }

                //background

                Element backgroundElement = (Element) displayElement.getElementsByTagName("background").item(0);

                action.setBgColourHex(XMLConfigHelper.getStringProperty(backgroundElement, "colour-hex"));

                Element iconElement = (Element) backgroundElement.getElementsByTagName("icon").item(0);

                String currentIconState = XMLConfigHelper.getStringProperty(iconElement, "current-state");

                action.setCurrentIconState(currentIconState);

                Element statesElements = (Element) iconElement.getElementsByTagName("states").item(0);

                NodeList statesNodeList = statesElements.getChildNodes();

                for (int i = 0;i<statesNodeList.getLength();i++)
                {
                    Node eachStateNode = statesNodeList.item(i);

                    if(eachStateNode.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    Element eachIconStateElement = (Element) eachStateNode;

                    if(!eachIconStateElement.getNodeName().equals("state"))
                        continue;

                    String state = eachIconStateElement.getTextContent();

                    File f = new File(iconsPath + File.separator + id + "___" + state);

                    try
                    {
                        if (f.exists())
                        {
                            byte[] iconFileByteArray = Files.readAllBytes(f.toPath());
                            action.addIcon(state, iconFileByteArray);
                        }
                        else
                        {
                            logger.severe("Icon for "+id+"; state "+state+" not found! Possibly because of changed paths.");
                        }
                    }
                    catch (Exception e)
                    {
                        logger.severe("Icon for "+id+"; state "+state+" unable to load!");
                        e.printStackTrace();
                    }
                }



                Element textElement = (Element) displayElement.getElementsByTagName("display-text").item(0);
            
                boolean showText = XMLConfigHelper.getBooleanProperty(textElement, "show");
                String displayTextFontColour = XMLConfigHelper.getStringProperty(textElement, "colour-hex");
                DisplayTextAlignment displayTextAlignment = DisplayTextAlignment.valueOf(XMLConfigHelper.getStringProperty(textElement, "alignment"));


                action.setDisplayTextAlignment(displayTextAlignment);
                action.setShowDisplayText(showText);

                action.setDisplayTextFontColourHex(displayTextFontColour);


                String displayText = XMLConfigHelper.getStringProperty(textElement, "text");

                action.setDisplayText(displayText);

                double fontSize = XMLConfigHelper.getDoubleProperty(textElement, "font-size");

                action.setDisplayTextFontSize(fontSize);

                action.setCurrentToggleStatus(false); // Always fault at default

                addAction(action);


                logger.info("... Done!");
            }

            logger.info("Loaded profile "+getID()+" ("+getName()+") !");
        }
        catch (Exception e)
        {
            e.printStackTrace();

            throw new MinorException(I18N.getString("profile.ClientProfile.failedToParseProfile", file.getAbsolutePath(), e.getLocalizedMessage()));
        }

    }

    public void addAction(ClientAction action) throws CloneNotSupportedException
    {
        actions.put(action.getID(), (ClientAction) action.clone());
    }



    public void deleteProfile()
    {
        for (int i = 0;i < getActions().size(); i++)
        {
            deleteActionIconByConfigIndex(i);
        }

        file.delete();
    }


    private void deleteActionIconByConfigIndex(int index)
    {
        Element actionElement = (Element) getActionsElement().getElementsByTagName("action").item(index);

        Element displayElement = (Element) actionElement.getElementsByTagName("display").item(0);
        Element backgroundElement = (Element) displayElement.getElementsByTagName("background").item(0);
        Element iconElement = (Element) backgroundElement.getElementsByTagName("icon").item(0);

        Element statesElements = (Element) iconElement.getElementsByTagName("states").item(0);

        NodeList statesNodeList = statesElements.getChildNodes();

        for (int i = 0;i<statesNodeList.getLength();i++)
        {
            Node eachStateNode = statesNodeList.item(i);

            if(eachStateNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element eachIconStateElement = (Element) eachStateNode;

            if(!eachIconStateElement.getNodeName().equals("state"))
                continue;

            String state = eachIconStateElement.getTextContent();

            new File(iconsPath+ File.separator + ID+"___"+state).delete();
        }
    }


    public void saveAction(ClientAction action) throws Exception
    {

        int ind = getActionIndexInConfig(action.getID());
        if(ind != -1)
        {
            Element actionElement = (Element) getActionsElement().getElementsByTagName("action").item(ind);
            getActionsElement().removeChild(actionElement);
        }

        Element newActionElement = document.createElement("action");
        getActionsElement().appendChild(newActionElement);

        Element idElement = document.createElement("id");
        idElement.setTextContent(action.getID());
        newActionElement.appendChild(idElement);

        Element parentElement = document.createElement("parent");
        parentElement.setTextContent(action.getParent());
        newActionElement.appendChild(parentElement);

        Element actionTypeElement = document.createElement("action-type");
        actionTypeElement.setTextContent(action.getActionType()+"");
        newActionElement.appendChild(actionTypeElement);

        if(action.getActionType() == ActionType.NORMAL || action.getActionType() == ActionType.TOGGLE || action.getActionType() == ActionType.GAUGE)
        {
            Element versionElement = document.createElement("version");
            versionElement.setTextContent(action.getVersion().getText());
            newActionElement.appendChild(versionElement);

            System.out.println(action.getUniqueID());

            Element uniqueIDElement = document.createElement("unique-ID");
            uniqueIDElement.setTextContent(action.getUniqueID());
            newActionElement.appendChild(uniqueIDElement);

            Element delayBeforeRunningElement = document.createElement("delay-before-running");
            delayBeforeRunningElement.setTextContent(action.getDelayBeforeExecuting()+"");
            newActionElement.appendChild(delayBeforeRunningElement);

            if (action.getActionType() == ActionType.GAUGE)
            {
                Element isAnimatedElement = document.createElement("is-animated");
                isAnimatedElement.setTextContent(action.isGaugeAnimated()+"");
                newActionElement.appendChild(isAnimatedElement);
            }
        }

        Element displayElement = document.createElement("display");
        newActionElement.appendChild(displayElement);
    
        Element backgroundElement = document.createElement("background");
        displayElement.appendChild(backgroundElement);

        Element colourHexElement = document.createElement("colour-hex");
        colourHexElement.setTextContent(action.getBgColourHex());
        backgroundElement.appendChild(colourHexElement);


        Element iconElement = document.createElement("icon");


        Element currentIconStateElement = document.createElement("current-state");
        currentIconStateElement.setTextContent(action.getCurrentIconState());
        iconElement.appendChild(currentIconStateElement);



        Element iconStatesElement = document.createElement("states");

        for(String state : action.getIcons().keySet())
        {
            Element eachStateElement = document.createElement("state");
            eachStateElement.setTextContent(state);
            iconStatesElement.appendChild(eachStateElement);
        }

        iconElement.appendChild(iconStatesElement);



        backgroundElement.appendChild(iconElement);

        Element textElement = document.createElement("display-text");
        displayElement.appendChild(textElement);

        Element textTolourHexElement = document.createElement("colour-hex");
        textTolourHexElement.setTextContent(action.getDisplayTextFontColourHex());
        textElement.appendChild(textTolourHexElement);

        Element textShowElement = document.createElement("show");
        textShowElement.setTextContent(action.isShowDisplayText()+"");
        textElement.appendChild(textShowElement);

        Element textDisplayTextElement = document.createElement("text");
        textDisplayTextElement.setTextContent(action.getDisplayText());
        textElement.appendChild(textDisplayTextElement);

        Element textDisplayTextFontSizeElement = document.createElement("font-size");
        textDisplayTextFontSizeElement.setTextContent(action.getDisplayTextFontSize()+"");
        textElement.appendChild(textDisplayTextFontSizeElement);

        Element textAlignmentElement = document.createElement("alignment");
        textAlignmentElement.setTextContent(action.getDisplayTextAlignment()+"");
        textElement.appendChild(textAlignmentElement);

        if (action.getLocation() != null)
        {
            Element locationElement = document.createElement("location");
            displayElement.appendChild(locationElement);

            Element colElement = document.createElement("col");
            colElement.setTextContent(action.getLocation().getCol()+"");
            locationElement.appendChild(colElement);

            Element rowElement = document.createElement("row");
            rowElement.setTextContent(action.getLocation().getRow()+"");
            locationElement.appendChild(rowElement);

            Element colSpanElement = document.createElement("col-span");
            colSpanElement.setTextContent(action.getLocation().getColSpan()+"");
            locationElement.appendChild(colSpanElement);

            Element rowSpanElement = document.createElement("row-span");
            rowSpanElement.setTextContent(action.getLocation().getRowSpan()+"");
            locationElement.appendChild(rowSpanElement);
        }

        Element propertiesElement = document.createElement("properties");
        newActionElement.appendChild(propertiesElement);

        for(String key : action.getClientProperties().getNames())
        {
            for(Property eachProperty : action.getClientProperties().getMultipleProperties(key))
            {
                Element propertyElement = document.createElement("property");
                propertiesElement.appendChild(propertyElement);
    
                Element nameElement = document.createElement("name");
                nameElement.setTextContent(eachProperty.getName());
                propertyElement.appendChild(nameElement);

                Element valueElement = document.createElement("value");
                valueElement.setTextContent(eachProperty.getRawValue());
                propertyElement.appendChild(valueElement);
            }
        }


        save();
    }

    private int getActionIndexInConfig(String actionID)
    {
        NodeList actionsList = getActionsElement().getChildNodes();

        int actionsSize = actionsList.getLength();

        int index = 0;

        for(int i = 0;i<actionsSize;i++)
        {
            Node eachActionNode = actionsList.item(index);

            if(eachActionNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
             
            if(!eachActionNode.getNodeName().equals("action")) 
                continue;
            
            Element eachActionElement = (Element) eachActionNode;

            Element idElement = (Element) eachActionElement.getElementsByTagName("id").item(0);

        
            if(idElement.getTextContent().equals(actionID))
                return index;


            index++;
        }


        return -1;
    }
    
    public void saveActionIcon(String actionID, byte[] array, String state)
    {
        int index = getActionIndexInConfig(actionID);

        getActionByID(actionID).addIcon(state, array);


        File iconFile = new File(iconsPath + File.separator + actionID + "___" + state);
        if(iconFile.exists())
        {
            iconFile.delete();
        }

        try
        {
            OutputStream outputStream = new FileOutputStream(iconFile);
            outputStream.write(array);
            outputStream.flush();
            outputStream.close();


            Element actionElement = (Element) getActionsElement().getElementsByTagName("action").item(index);

            getActionsElement().removeChild(actionElement);

            Element displayElement = (Element) actionElement.getElementsByTagName("display").item(0);
            Element backgroundElement = (Element) displayElement.getElementsByTagName("background").item(0);
            Element iconElement = (Element) backgroundElement.getElementsByTagName("icon").item(0);

            Element statesElements = (Element) iconElement.getElementsByTagName("states").item(0);

            Element stateElement = document.createElement("state");
            stateElement.setTextContent(state);

            statesElements.appendChild(stateElement);

            getActionsElement().appendChild(actionElement);

            save();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void save() throws TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(file);
        Source input = new DOMSource(document);

        transformer.transform(input, output);
    }


    private void createNewProfileFile(File file) throws MinorException
    {
        try
        {
            file.createNewFile();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();

            Element configElement = document.createElement("config");

            Element profileElement = document.createElement("profile");
            addElementsToProfileElement(document, profileElement, getName(), getRows(), getCols(), getActionSize(), getActionGap(), getActionDefaultDisplayTextFontSize());

            Element actionsElement = document.createElement("actions");

            configElement.appendChild(profileElement);
            configElement.appendChild(actionsElement);


            document.appendChild(configElement);

            save();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MinorException(I18N.getString("profile.ClientProfile.unableToCreateProfileFile")+"\n"+e.getLocalizedMessage());
        }
    }

    public void saveProfileDetails() throws TransformerException
    {
        XMLConfigHelper.removeChilds(getProfileElement());

        addElementsToProfileElement(document, getProfileElement(), getName(), getRows(), getCols(), getActionSize(), getActionGap(), getActionDefaultDisplayTextFontSize());

        save();
    }

    public void addElementsToProfileElement(Document document, Element profileElement,
                                            String name,
                                            int rows,
                                            int cols,
                                            double actionSize,
                                            double actionGap,
                                            double actionDefaultDisplayTextFontSize)
    {
        Element nameElement = document.createElement("name");
        nameElement.setTextContent(name);
        profileElement.appendChild(nameElement);

        Element rowsElement = document.createElement("rows");
        rowsElement.setTextContent(rows + "");
        profileElement.appendChild(rowsElement);

        Element colsElement = document.createElement("cols");
        colsElement.setTextContent(cols + "");
        profileElement.appendChild(colsElement);

        Element actionSizeElement = document.createElement("action-size");
        actionSizeElement.setTextContent(actionSize + "");
        profileElement.appendChild(actionSizeElement);

        Element actionGapElement = document.createElement("action-gap");
        actionGapElement.setTextContent(actionGap + "");
        profileElement.appendChild(actionGapElement);

        Element actionDefaultDisplayTextFontSizeElement = document.createElement("action-default-display-text-font-size");
        actionDefaultDisplayTextFontSizeElement.setTextContent(actionDefaultDisplayTextFontSize + "");
        profileElement.appendChild(actionDefaultDisplayTextFontSizeElement);
    }

    public void saveActions() throws Exception
    {
        XMLConfigHelper.removeChilds(getActionsElement());
        save();
        for(ClientAction action : getActions())
        {
            saveAction(action);
        }
    }



    public void removeAction(String ID)
    {
        int index = getActionIndexInConfig(ID);

        if(index>-1)
        {
            deleteActionIconByConfigIndex(index);
            actions.remove(ID);
        }

    }

    public ArrayList<ClientAction> getActions()
    {
        ArrayList<ClientAction> p = new ArrayList<>();
        for(String profile : actions.keySet())
            p.add(actions.get(profile));
        return p;
    }

    public String getID()
    {
        return ID;
    }

    public String getName()
    {
        return name;
    }

    public int getRows()
    {
        return rows;
    }

    public int getCols()
    {
        return cols;
    }

    public double getActionSize()
    {
        return actionSize;
    }

    public ClientAction getActionByID(String ID)
    {
        return actions.getOrDefault(ID, null);
    }

    public double getActionGap()
    {
        return actionGap;
    }

    public void setRows(int rows)
    {
        this.rows = rows;
    }

    public void setCols(int cols)
    {
        this.cols = cols;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public void setActionSize(double actionSize)
    {
        this.actionSize = actionSize;
    }

    public void setActionGap(double actionGap)
    {
        this.actionGap = actionGap;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setActionDefaultDisplayTextFontSize(double actionDefaultDisplayTextFontSize)
    {
        this.actionDefaultDisplayTextFontSize = actionDefaultDisplayTextFontSize;
    }

    public double getActionDefaultDisplayTextFontSize()
    {
        return actionDefaultDisplayTextFontSize;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
