package com.stream_pi.client.profile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.action_api.actionproperty.ClientProperties;
import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.version.Version;
import com.stream_pi.util.xmlconfighelper.XMLConfigHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ClientProfile implements Cloneable{
    private String name, ID;

    private int rows, cols, actionSize, actionGap;

    
    private HashMap<String, Action> actions;
    private String iconsPath;

    private File file;

    private Logger logger;
    private Document document;

    public ClientProfile(File file, String iconsPath) throws MinorException
    {
        this.file = file;
        this.iconsPath = iconsPath;

        actions = new HashMap<>();

        logger = Logger.getLogger(ClientProfile.class.getName());

        if(!file.exists() && !file.isFile())
            createConfigFile(file);

        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MinorException("profile", "Unable to read profile config file.");
        }


        setID(file.getName().replace(".xml", ""));
        load();
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

            String name = XMLConfigHelper.getStringProperty(getProfileElement(), "name");
            int rows = XMLConfigHelper.getIntProperty(getProfileElement(), "rows");
            int cols = XMLConfigHelper.getIntProperty(getProfileElement(), "cols");
            int actionSize = XMLConfigHelper.getIntProperty(getProfileElement(), "action-size");
            int actionGap = XMLConfigHelper.getIntProperty(getProfileElement(), "action-gap");

            setName(name);
            setRows(rows);
            setCols(cols);
            setActionSize(actionSize);
            setActionGap(actionGap);


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

                Action action = new Action(id, actionType);
                action.setParent(parent);

                ClientProperties properties = new ClientProperties();

                if(actionType == ActionType.FOLDER)
                    properties.setDuplicatePropertyAllowed(true);


                if(actionType == ActionType.NORMAL || actionType == ActionType.TOGGLE)
                {
                    action.setVersion(new Version(XMLConfigHelper.getStringProperty(eachActionElement, "version")));
                    action.setModuleName(XMLConfigHelper.getStringProperty(eachActionElement, "module-name"));
                    action.setDelayBeforeExecuting(Integer.parseInt(
                            XMLConfigHelper.getStringProperty(eachActionElement, "delay-before-running")
                    ));
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

                //location

                try
                {
                    Element locationElement = (Element) displayElement.getElementsByTagName("location").item(0);
                    int row = XMLConfigHelper.getIntProperty(locationElement, "row");
                    int col = XMLConfigHelper.getIntProperty(locationElement, "col");
                    action.setLocation(new Location(row, col));
                }
                catch (Exception e)
                {
                    logger.info("Action has no location, most probably a combine action child");
                }

                //background

                Element backgroundElement = (Element) displayElement.getElementsByTagName("background").item(0);

                action.setBgColourHex(XMLConfigHelper.getStringProperty(backgroundElement, "colour-hex"));

                Element iconElement = (Element) backgroundElement.getElementsByTagName("icon").item(0);
                
                //boolean showIcon = XMLConfigHelper.getBooleanProperty(iconElement, "show");
                //boolean hasIcon = XMLConfigHelper.getBooleanProperty(iconElement, "has");

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

                    File f = new File(iconsPath+"/"+id+"___"+state);

                    try
                    {
                        byte[] iconFileByteArray = Files.readAllBytes(f.toPath());
                        action.addIcon(state, iconFileByteArray);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }



                Element textElement = (Element) displayElement.getElementsByTagName("text").item(0);
            
                boolean showText = XMLConfigHelper.getBooleanProperty(textElement, "show");
                String displayTextFontColour = XMLConfigHelper.getStringProperty(textElement, "colour-hex");
                DisplayTextAlignment displayTextAlignment = DisplayTextAlignment.valueOf(XMLConfigHelper.getStringProperty(textElement, "alignment"));


                action.setDisplayTextAlignment(displayTextAlignment);
                action.setShowDisplayText(showText);

                action.setDisplayTextFontColourHex(displayTextFontColour);


                String displayText = XMLConfigHelper.getStringProperty(textElement, "display-text");

                action.setDisplayText(displayText);

                action.setCurrentToggleStatus(false); // Always fault at default

                addAction(action);


                logger.info("... Done!");
            }

            logger.info("Loaded profile "+getID()+" ("+getName()+") !");
        }
        catch (Exception e)
        {
            e.printStackTrace();

            throw new MinorException("profile", "profile is corrupt.");
        }

    }

    public void addAction(Action action) throws CloneNotSupportedException {
        actions.put(action.getID(), (Action) action.clone());
    }




    private void createConfigFile(File file) throws MinorException
    {
        try
        {
            file.createNewFile();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document newDocument = dBuilder.newDocument();


            Element rootElement = newDocument.createElement("config");
            newDocument.appendChild(rootElement);

            Element profileElement = newDocument.createElement("profile");
            rootElement.appendChild(profileElement);

            Element actionsElement = newDocument.createElement("actions");
            rootElement.appendChild(actionsElement);

            Element nameElement = newDocument.createElement("name");
            nameElement.setTextContent("Untitled profile");
            profileElement.appendChild(nameElement);

            Element rowsElement = newDocument.createElement("rows");
            rowsElement.setTextContent("3");
            profileElement.appendChild(rowsElement);

            Element colsElement = newDocument.createElement("cols");
            colsElement.setTextContent("3");
            profileElement.appendChild(colsElement);

            Element actionSizeElement = newDocument.createElement("action-size");
            actionSizeElement.setTextContent("100");
            profileElement.appendChild(actionSizeElement);

            Element actionGapElement = newDocument.createElement("action-gap");
            actionGapElement.setTextContent("5");
            profileElement.appendChild(actionGapElement);




            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newDocument);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new MinorException(e.getMessage());
        }
    }



    public void deleteProfile()
    {
        file.delete();
    }


    public void saveAction(Action action) throws Exception {

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

        if(action.getActionType() == ActionType.NORMAL || action.getActionType() == ActionType.TOGGLE)
        {
            Element versionElement = document.createElement("version");
            versionElement.setTextContent(action.getVersion().getText());
            newActionElement.appendChild(versionElement);

            System.out.println(action.getModuleName());

            Element moduleNameElement = document.createElement("module-name");
            moduleNameElement.setTextContent(action.getModuleName());
            newActionElement.appendChild(moduleNameElement);

            Element delayBeforeRunningElement = document.createElement("delay-before-running");
            delayBeforeRunningElement.setTextContent(action.getDelayBeforeExecuting()+"");
            newActionElement.appendChild(delayBeforeRunningElement);
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

        Element textElement = document.createElement("text");
        displayElement.appendChild(textElement);

        Element textTolourHexElement = document.createElement("colour-hex");
        textTolourHexElement.setTextContent(action.getDisplayTextFontColourHex());
        textElement.appendChild(textTolourHexElement);

        Element textShowElement = document.createElement("show");
        textShowElement.setTextContent(action.isShowDisplayText()+"");
        textElement.appendChild(textShowElement);

        Element textDisplayTextElement = document.createElement("display-text");
        textDisplayTextElement.setTextContent(action.getDisplayText());
        textElement.appendChild(textDisplayTextElement);

        Element textAlignmentElement = document.createElement("alignment");
        textAlignmentElement.setTextContent(action.getDisplayTextAlignment()+"");
        textElement.appendChild(textAlignmentElement);


        Element locationElement = document.createElement("location");
        displayElement.appendChild(locationElement);

        Element colElement = document.createElement("col");
        colElement.setTextContent(action.getLocation().getCol()+"");
        locationElement.appendChild(colElement);

        Element rowElement = document.createElement("row");
        rowElement.setTextContent(action.getLocation().getRow()+"");
        locationElement.appendChild(rowElement);


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
    
    public void saveActionIcon(String actionID, byte[] array, String state) throws MinorException
    {
        int index = getActionIndexInConfig(actionID);

        logger.info("INDEXXXX : "+index);

        getActionFromID(actionID).addIcon(state, array);


        File iconFile = new File(iconsPath+"/"+actionID+"___"+state);
        if(iconFile.exists())
        {
            boolean result = iconFile.delete();
            System.out.println("result : "+result);
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
    
    private void save() throws Exception
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(file);
        Source input = new DOMSource(document);

        transformer.transform(input, output);
    }

    public void saveProfileDetails() throws Exception {
        XMLConfigHelper.removeChilds(getProfileElement());

        Element nameElement = document.createElement("name");
        nameElement.setTextContent(getName());
        getProfileElement().appendChild(nameElement);

        Element rowsElement = document.createElement("rows");
        rowsElement.setTextContent(getRows()+"");
        getProfileElement().appendChild(rowsElement);

        Element colsElement = document.createElement("cols");
        colsElement.setTextContent(getCols()+"");
        getProfileElement().appendChild(colsElement);

        Element actionSizeElement = document.createElement("action-size");
        actionSizeElement.setTextContent(getActionSize()+"");
        getProfileElement().appendChild(actionSizeElement);

        Element actionGapElement = document.createElement("action-gap");
        actionGapElement.setTextContent(getActionGap()+"");
        getProfileElement().appendChild(actionGapElement);
        
        save();
    }

    public void saveActions() throws Exception
    {
        XMLConfigHelper.removeChilds(getActionsElement());
        save();
        for(Action action : getActions())
        {
            logger.info("ACTION ID :"+action.getID());
            logger.info("Action ICON : "+action.isHasIcon());
            saveAction(action);
        }
    }



    public void removeAction(String ID) throws Exception {
        int index = getActionIndexInConfig(ID);

        if(index>-1)
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

                new File(iconsPath+"/"+ID+"___"+state).delete();
            }



            actions.remove(ID);
        }

    }

    public ArrayList<Action> getActions()
    {
        ArrayList<Action> p = new ArrayList<>();
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

    public int getActionSize()
    {
        return actionSize;
    }

    public Action getActionFromID(String ID)
    {
        return actions.getOrDefault(ID, null);
    }

    public int getActionGap()
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

    public void setActionSize(int actionSize)
    {
        this.actionSize = actionSize;
    }

    public void setActionGap(int actionGap)
    {
        this.actionGap = actionGap;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
