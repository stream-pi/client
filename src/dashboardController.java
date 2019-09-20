import animatefx.animation.*;
import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {
    @FXML
    public AnchorPane basePane;
    @FXML
    public VBox actionsVBox;
    @FXML
    public ScrollPane settingsPane;
    @FXML
    public JFXTextField serverIPField;
    @FXML
    public JFXTextField serverPortField;
    @FXML
    public Label currentStatusLabel;
    @FXML
    public Label unableToConnectReasonLabel;
    @FXML
    public JFXToggleButton animationsToggleButton;
    @FXML
    public JFXButton closeSettingsButton;
    @FXML
    public JFXTextField screenWidthField;
    @FXML
    public JFXTextField screenHeightField;
    @FXML
    public VBox loadingPane;
    @FXML
    public Button openSettingsButtonDebug;
    @FXML
    public Button closeSettingsButtonDebug;
    @FXML
    public JFXToggleButton debugModeToggleButton;
    @FXML
    public VBox goodbyePane;
    @FXML
    public StackPane alertStackPane;

    int maxActionsPerRow;
    int maxNoOfRows;
    String[][] actions;
    String thisDeviceIP;

    String serverIP = Main.config.get("server_ip");
    String serverPort = Main.config.get("server_port");
    boolean isConnected = false;
    String separator = "::";
    final Paint WHITE_PAINT = Paint.valueOf("#ffffff");

    boolean isSettingsOpen = false;
    boolean debugMode = false;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenHeightField.setText(Main.config.get("height"));
        screenWidthField.setText(Main.config.get("width"));
        basePane.setStyle("-fx-background-color : "+Main.config.get("bg_colour"));
        loadingPane.setStyle("-fx-background-color : "+Main.config.get("bg_colour"));
        settingsPane.setStyle("-fx-background-color : "+Main.config.get("bg_colour"));
        loadingPane.setOpacity(0);
        serverIPField.setText(serverIP);
        serverPortField.setText(serverPort);
        unableToConnectReasonLabel.setText("");

        if(Main.config.get("animations_mode").equals("0"))
        {
            animationsToggleButton.setSelected(false);
            settingsPane.setOpacity(0);
        }
        else
        {
            animationsToggleButton.setSelected(true);
            settingsPane.setTranslateY(Integer.parseInt(Main.config.get("height")));
        }

        if(Main.config.get("debug_mode").equals("0"))
        {
            debugMode = false;
            debugModeToggleButton.setSelected(false);

            openSettingsButtonDebug.setDisable(true);
            openSettingsButtonDebug.setVisible(false);
            closeSettingsButtonDebug.setDisable(true);
            closeSettingsButtonDebug.setVisible(false);
            returnToParentLayerButton.setDisable(true);
            returnToParentLayerButton.setVisible(false);
        }
        else
        {
            debugMode = true;
            debugModeToggleButton.setSelected(true);
        }

        settingsPane.setOnSwipeDown(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent event) {
                if(isConnected)
                {
                   closeSettings();
                }
            }
        });

        actionsVBox.setOnSwipeRight(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent event) {
                returnToParentLayerButtonClicked();
            }
        });

        maxActionsPerRow = (int) Math.floor((Integer.parseInt(Main.config.get("width"))-10) / 120);
        maxNoOfRows = (int) Math.floor((Integer.parseInt(Main.config.get("height"))-10) / 120);

        socketCommThread = new Thread(socketCommTask);
        socketCommThread.setDaemon(true);
        socketCommThread.start();

        checkServerConnection();
    }

    @FXML
    public void animationsToggleButtonClicked()
    {
        if(animationsToggleButton.isSelected())
        {
            updateConfig("animations_mode","1");
        }
        else
        {
            updateConfig("animations_mode","0");
        }
    }

    @FXML
    public void debugModeToggleButtonClicked()
    {
        if(debugModeToggleButton.isSelected())
        {
            updateConfig("debug_mode","1");
            openSettingsButtonDebug.setDisable(false);
            openSettingsButtonDebug.setVisible(true);
            closeSettingsButtonDebug.setDisable(false);
            closeSettingsButtonDebug.setVisible(true);
        }
        else
        {
            updateConfig("debug_mode","0");
            openSettingsButtonDebug.setDisable(true);
            openSettingsButtonDebug.setVisible(false);
            closeSettingsButtonDebug.setDisable(true);
            closeSettingsButtonDebug.setVisible(false);
        }
    }

    Socket s;
    DataInputStream is;
    DataOutputStream os;
    int serverPortTemp = 23;
    Thread socketCommThread;
    boolean isWorking = false;
    public void checkServerConnection()
    {
        new Thread(new Task<Void>() {
            @Override
            protected Void call(){
                if(isWorking)
                    return null;

                isWorking = true;
                System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

                String serverIPTemp = serverIPField.getText();

                try
                {
                    thisDeviceIP = Inet4Address.getLocalHost().getHostAddress();
                    if(isSettingsOpen && !isConnected)
                    {
                        closeSettings();
                    }

                    openLoadingPane();

                    if(isConnected)
                    {
                        //writeToOS("client_quit::");
                        isConnected = false;
                        //Thread.sleep(500);
                        s.close();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                actionsVBox.getChildren().clear();
                            }
                        });
                        Thread.sleep(3000);
                    }

                    Thread.sleep(1100);
                    
                    try
                    {
                        serverPortTemp = Integer.parseInt(serverPortField.getText());
                    }
                    catch (Exception e)
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                unableToConnectReasonLabel.setText("Please enter a valid Server Port!");
                                closeLoadingPane();
                                openSettings();
                            }
                        });
                        return null;
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            actionsVBox.getChildren().clear();
                        }
                    });

                    s = new Socket();
                    s.connect(new InetSocketAddress(serverIPTemp,serverPortTemp), 5000);
                    //s.setSoTimeout(30000);
                    s.setSendBufferSize(950000000);
                    s.setReceiveBufferSize(950000000);
                    is = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                    os = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));


                    updateConfig("server_ip",serverIPTemp);
                    updateConfig("server_port",serverPortField.getText());
                    currentStatusLabel.setTextFill(Paint.valueOf("#008000"));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            currentStatusLabel.setText("Current Status :  CONNECTED to "+serverIPTemp+":"+serverPortTemp);
                            unableToConnectReasonLabel.setText("");

                        }
                    });
                    writeToOS("hi there");
                    os.flush();
                    if(isSettingsOpen)
                    {
                        closeSettings();
                    }
                    loadActions();
                    isConnected = true;
                }
                catch (Exception e)
                {
                    System.out.println("CFBBBBB");
                    currentStatusLabel.setTextFill(Paint.valueOf("#FF0000"));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            currentStatusLabel.setText("Current Status :  FAILED TO CONNECT to "+serverIPTemp+":"+serverPortTemp);
                            unableToConnectReasonLabel.setText(e.getLocalizedMessage());

                        }
                    });
                    if(debugMode)
                        e.printStackTrace();
                    isConnected = false;
                    openSettings();
                }
                isWorking = false;
                return null;
            }
        }).start();
    }


    @FXML
    public JFXButton applySettingsAndRestartButton;

    @FXML
    public void applySettingsAndRestartButtonClicked()
    {
        try
        {
            String uw = screenWidthField.getText();
            String uh = screenHeightField.getText();
            String portVal = serverPortField.getText();
            String ipVal = serverIPField.getText();
            Integer.parseInt(uw);
            Integer.parseInt(uh);

            if(!Main.config.get("height").equals(uh) || !Main.config.get("width").equals(uw))
            {
                updateConfig("height",uh);
                updateConfig("width",uw);
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(3000);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                showErrorAlert("Alert","Screen Settings have been updated, restart to see effect");
                            }
                        });
                        return null;
                    }
                }).start();
            }

            if(!Main.config.get("server_ip").equals(ipVal) || !Main.config.get("server_port").equals(portVal) || !isConnected)
            {
                checkServerConnection();
            }
        }
        catch (Exception e)
        {
            showErrorAlert("Alert","Please make sure screen dimensions are valid.");
        }
    }

    public void showErrorAlert(String heading, String content)
    {
        System.out.println("XD");
        JFXDialogLayout l = new JFXDialogLayout();
        l.getStyleClass().add("dialog_style");
        Label headingLabel = new Label(heading);
        headingLabel.setTextFill(WHITE_PAINT);
        headingLabel.setFont(Font.font("Roboto Regular",25));
        l.setHeading(headingLabel);
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Roboto Regular",15));
        contentLabel.setTextFill(WHITE_PAINT);
        contentLabel.setWrapText(true);
        l.setBody(contentLabel);
        JFXButton okButton = new JFXButton("OK");
        okButton.setTextFill(WHITE_PAINT);
        l.setActions(okButton);

        JFXDialog alertDialog = new JFXDialog(alertStackPane,l, JFXDialog.DialogTransition.CENTER);
        alertDialog.setOverlayClose(false);
        alertDialog.getStyleClass().add("dialog_box");
        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                alertDialog.close();
                alertDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                    @Override
                    public void handle(JFXDialogEvent event) {
                        alertStackPane.toBack();
                    }
                });
            }
        });

        alertStackPane.toFront();
        alertDialog.show();
    }

    public void updateConfig(String keyName, String newValue)
    {
        Main.config.put(keyName,newValue);
        String toBeWritten = Main.config.get("width")+separator+Main.config.get("height")+separator+Main.config.get("bg_colour")+separator+Main.config.get("server_ip")+separator+Main.config.get("server_port")+separator+Main.config.get("device_nick_name")+separator+Main.config.get("animations_mode")+separator+Main.config.get("debug_mode")+separator;
        io.writeToFile(toBeWritten,"config");
    }

    Task socketCommTask = new Task<Void>() {
        @Override
        protected Void call() {
            while(true)
            {
            try
            {

                    if(isConnected) {
                        //System.out.println("connected!");
                        String responseFromServerRaw = readFromIS();
                        if (responseFromServerRaw == null)
                        {
                            //System.out.println("2323");
                            checkServerConnection();
                            return null;
                        }
                        else
                        {
                            System.out.println("RFS : "+responseFromServerRaw);
                            String[] response = responseFromServerRaw.split(separator);
                            String msgHeading = response[0];
                            //System.out.println("'"+msgHeading+"'");
                            if(msgHeading.equals("client_details"))
                            {
                                Thread.sleep(1000);
                                writeToOS("client_details"+separator+thisDeviceIP+separator+Main.config.get("device_nick_name")+separator+Main.config.get("width")+separator+Main.config.get("height")+separator+maxActionsPerRow+separator+maxNoOfRows+separator);
                                //client_details::<deviceIP>::<nick_name>::<device_width>::<device_height>::<max_actions_per_row>::<max_no_of_rows>::
                                // <maxcols>
                            }
                            else if(msgHeading.equals("delete_action"))
                            {
                                System.out.println("Deleting...");
                                new File("actions/details/"+response[1]).delete();
                                new File("actions/icons/"+response[2]).delete();
                                loadActions();
                            }
                            else if(msgHeading.equals("actions_update"))
                            {
                                //delete all details...
                                for(String[] eachAction : actions)
                                {
                                    new File("actions/details/"+eachAction[0]).delete();
                                }
                                //System.out.println("sd213123");
                                int noOfActions = Integer.parseInt(response[1]);
                                int currentIndex = 2;
                                for(int i = 0;i<noOfActions;i++)
                                {
                                    String[] newAction = response[currentIndex].split("__");
                                    String actionID = newAction[0];
                                    String actionCasualName = newAction[1];
                                    String actionType = newAction[2];
                                    String actionContent = newAction[3];
                                    String actionIconFileName = newAction[4];
                                    String actionRowNo = newAction[5];
                                    String actionColNo = newAction[6];
                                    String actionLayerIndex = newAction[7];

                                    io.writeToFile(actionCasualName+separator+actionType+separator+actionContent+separator+actionIconFileName+separator+actionRowNo+separator+actionColNo+separator+actionLayerIndex+separator,"actions/details/"+actionID);
                                    //io.writeToFile(actionCasualName+separator+actionType+separator+actionContent+separator+actionID+separator+actionImageFileName+separator+actionRowNo+separator+actionColNo,"actions/details/"+actionID);
                                    currentIndex ++;
                                }
                                //System.out.println("updated!");
                                openLoadingPane();
                                isUpdateStuff = true;
                                loadActions();
                            }
                            else if(msgHeading.equals("update_icon"))
                            {
                                String iconName = response[1];
                                String actionImageBase64 = response[2];

                                byte[] img = Base64.getDecoder().decode(actionImageBase64);
                                io.writeToFileRaw(img,"actions/icons/"+iconName);
                                isUpdateStuff = true;
                                loadActions();
                            }
                            else if(msgHeading.equals("get_actions"))
                            {
                                //System.out.println("145455");
                                String towrite = "client_actions"+separator+actions.length+separator;

                                for(String[] eachAction : actions)
                                {
                                    //FileInputStream fs = new FileInputStream("actions/icons/"+eachAction[3]);
                                    //byte[] imageB = fs.readAllBytes();
                                    //fs.close();
                                    //String base64Image = Base64.getEncoder().encodeToString(imageB);
                                    towrite+=eachAction[0]+"__"+eachAction[1]+"__"+eachAction[2]+"__"+eachAction[3]+"__"+eachAction[4]+"__"+eachAction[5]+"__"+eachAction[6]+"__"+eachAction[7]+separator;
                                }
                                writeToOS(towrite+maxLayers+separator);
                                iconsSent.clear();
                            }
                            else if(msgHeading.equals("client_actions_icons_get"))
                            {
                                for(String[] eachAction : actions)
                                {
                                    if(!iconsSent.contains(eachAction[4]))
                                    {
                                        iconsSent.add(eachAction[4]);
                                        FileInputStream fs = new FileInputStream("actions/icons/"+eachAction[4]);
                                        byte[] imageB = fs.readAllBytes();
                                        fs.close();
                                        String base64Image = Base64.getEncoder().encodeToString(imageB);
                                        System.out.println(eachAction[4]+"GAYFAG");
                                        writeToOS("action_icon::"+eachAction[4]+"::"+base64Image+"::");
                                        Thread.sleep(500);
                                    }
                                }
                                closeLoadingPane();
                            }
                        }
                        //System.out.println("'"+responseFromServerRaw+"'");
                    }
                    Thread.sleep(100);
                }
            catch (Exception e)
            {
                if(!isShutdown)
                {
                    checkServerConnection();
                    if(debugMode)
                        e.printStackTrace();
                }
            }
            }

        }
    };

    private void deleteFiles(File file) {
        if (file.isDirectory())
            for (File f : file.listFiles())
                deleteFiles(f);
        else
            file.delete();
    }

    ArrayList<String> iconsSent = new ArrayList<>();

    boolean currentlyWriting = false;
    public void writeToOS(String txt) throws Exception
    {
        //System.out.println("txt  : "+txt);
        /*txt = txt + "<END>";
        currentlyWriting = true;
        String[] chunks = Iterables.toArray(Splitter.fixedLength(1000).split(txt),String.class);
        for(int i = 0;i<chunks.length;i++)
        {
            os.writeUTF(chunks[i]);
            Thread.sleep(100);

        }*/
        //currentlyWriting = true;
        os.writeUTF(txt);

        //os.write(txt.getBytes(StandardCharsets.UTF_8).length);
        //os.write(txt.getBytes(StandardCharsets.UTF_8));
        //currentlyWriting = false;
        os.flush();
        ////System.out.println("txt : "+txt);
    }

    public String readFromIS() throws Exception
    {
        String eachChunk = is.readUTF();
        return eachChunk;
        /*String finalResult = "";
        while(true)
        {
            if(currentlyWriting)
            {
                Thread.sleep(500);
                continue;
            }
            String eachChunk = is.readUTF();
            if(!eachChunk.endsWith("<END>"))
            {
                finalResult += eachChunk;
            }
            else
            {
                finalResult += eachChunk.replace("<END>","");
                break;
            }
        }

        //System.out.println("txtrr : "+finalResult);
        return finalResult;*/
    }

    boolean isUpdateStuff = false;
    int currentLayer = 0;

    public void drawLayer(int layer)
    {

        HBox[] rows = new HBox[maxNoOfRows];

        for(int j = 0;j<maxNoOfRows;j++)
        {
            rows[j] = new HBox();
            rows[j].setSpacing(20);
            rows[j].setAlignment(Pos.CENTER);

            Pane[] actionPane = new Pane[maxActionsPerRow];
            for(int k = 0;k<maxActionsPerRow;k++)
            {
                actionPane[k] = new Pane();
                actionPane[k].setPrefSize(90,90);
                actionPane[k].getStyleClass().add("action_box");
                //actionPane[k].setStyle("-fx-effect: dropshadow(three-pass-box, red, 5, 0, 0, 0);-fx-background-color:"+Main.config.get("bg_colour"));
            }

            rows[j].getChildren().addAll(actionPane);
        }

        for(String[] eachActionDetails : actions)
        {
            if(Integer.parseInt(eachActionDetails[7]) != layer)
                continue;

            //System.out.println("actions/icons/"+eachActionDetails[3]);
            ImageView icon = new ImageView(new File("actions/icons/"+eachActionDetails[4]).toURI().toString());
            icon.setFitHeight(90);
            icon.setPreserveRatio(false);
            icon.setFitWidth(90);

            Pane actionPane = new Pane(icon);
            actionPane.setPrefSize(90,90);
            actionPane.setPrefSize(90,90);
            //actionPane.getStyleClass().add("action_box");
            //actionPane.setStyle("-fx-effect: dropshadow(three-pass-box, "+eachActionDetails[4]+", 5, 0, 0, 0);-fx-background-color:"+Main.config.get("bg_colour"));
            actionPane.setId(eachActionDetails[2]+separator+eachActionDetails[3]+separator);
            actionPane.setOnTouchStationary(new EventHandler<TouchEvent>() {
                @Override
                public void handle(TouchEvent event) {
                    Node n = (Node) event.getSource();
                    sendAction(n.getId());
                }
            });
            actionPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Node n = (Node) event.getSource();
                    sendAction(n.getId());
                }
            });

            int rowNo = Integer.parseInt(eachActionDetails[5]);
            int colNo = Integer.parseInt(eachActionDetails[6]);
            rows[rowNo].getChildren().set(colNo, actionPane);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                if(currentLayer > layer)
                {
                    FadeOutRight gay = new FadeOutRight(actionsVBox);
                    gay.play();
                    gay.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            actionsVBox.getChildren().clear();
                            actionsVBox.getChildren().addAll(rows);
                            FadeInLeft fag = new FadeInLeft(actionsVBox);
                            fag.play();
                        }
                    });
                }
                else if(currentLayer < layer)
                {
                    FadeOutLeft gay = new FadeOutLeft(actionsVBox);
                    gay.play();
                    gay.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            actionsVBox.getChildren().clear();
                            actionsVBox.getChildren().addAll(rows);
                            FadeInRight fag = new FadeInRight(actionsVBox);
                            fag.play();
                        }
                    });
                }
                else
                {
                    actionsVBox.getChildren().clear();
                    actionsVBox.getChildren().addAll(rows);
                }


                if(layer != -1)
                    currentLayer = layer;
            }
        });
    }

    @FXML
    public Button returnToParentLayerButton;

    @FXML
    public void returnToParentLayerButtonClicked()
    {
        System.out.println(currentLayer);
        if(currentLayer > 0)
        {
            drawLayer(0);
        }
    }

    int maxLayers = 0;
    public void loadActions() throws Exception
    {
        maxLayers = 0;
        String[] allActionFiles = new File("actions/details").list();

        actionsVBox.setAlignment(Pos.TOP_LEFT);

        System.out.println("sx : "+allActionFiles.length);
        actions = new String[allActionFiles.length][8];

        int i = 0;
        int lowLayer = 0;
        for(String eachActionFile : allActionFiles)
        {
            String[] contentArray = io.readFileArranged("actions/details/"+eachActionFile,separator);
            System.out.println(io.readFileRaw("actions/details/"+eachActionFile));
            actions[i][0] = eachActionFile; //Action Unique ID
            actions[i][1] = contentArray[0]; //Casual Name
            actions[i][2] = contentArray[1]; //Action Type
            actions[i][3] = contentArray[2]; //Action Content
            actions[i][4] = contentArray[3]; //Icon
            //System.out.println("iconXX : "+actions[i][3]);
            //actions[i][4] = contentArray[3]; //Ambient Colour
            actions[i][5] = contentArray[4]; //Row No
            actions[i][6] = contentArray[5]; //Column No
            actions[i][7] = contentArray[6]; //Layer
            if(Integer.parseInt(contentArray[6])>lowLayer)
                lowLayer = Integer.parseInt(contentArray[6]);
            i++;
        }

        maxLayers = lowLayer;

        drawLayer(currentLayer);

        Thread.sleep(1500);
        //System.out.println("asdesaxxx");

        if(actions.length == 0)
        {
            closeLoadingPane();
        }

        if(isUpdateStuff)
        {
            isUpdateStuff = false;
            closeLoadingPane();
        }
    }

    public void sendAction(String rawActionContent)
    {
        //System.out.println("HOTKEY : "+hotkey);
        System.out.println("AXION" +rawActionContent);
        String[] splitz = rawActionContent.split("::");

        if(splitz[0].equals("folder"))
        {
            drawLayer(Integer.parseInt(splitz[1]));
        }
        else
        {
            try
            {
                writeToOS(rawActionContent);
            }
            catch (Exception e)
            {
                checkServerConnection();
                if(debugMode)
                    e.printStackTrace();
            }
        }
    }

    public void openSettings()
    {
        if(!isConnected)
        {
            closeSettingsButton.setVisible(false);
        }
        else
        {
            closeSettingsButton.setVisible(true);
        }
        //System.out.println("xcxc");
        if(!isSettingsOpen)
        {
            //System.out.println("dfdf");
            if(Main.config.get("animations_mode").equals("0"))
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        settingsPane.setOpacity(1);
                        settingsPane.toFront();
                    }
                });
            }
            else
            {
                new FadeInUp(settingsPane).play();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        settingsPane.toFront();
                    }
                });
            }
            isSettingsOpen = true;
        }
    }

    boolean isLoadingPaneOpen = false;
    public void openLoadingPane()
    {
        if(!isLoadingPaneOpen)
        {
            isLoadingPaneOpen = true;
            //System.out.println("Showing Loading Pane ...");
            if(Main.config.get("animations_mode").equals("0"))
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadingPane.setOpacity(1);
                        loadingPane.toFront();
                    }
                });
            }
            else
            {
                new FadeIn(loadingPane).play();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadingPane.toFront();
                    }
                });
            }
        }
    }

    public void closeLoadingPane()
    {
        if(isLoadingPaneOpen)
        {
            isLoadingPaneOpen = false;
            //System.out.println("Hiding Loading Pane ...");
            if(Main.config.get("animations_mode").equals("1"))
            {
                FadeOut lol = new FadeOut(loadingPane);
                lol.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                loadingPane.toBack();
                            }
                        });
                    }
                });
                lol.play();
            }
            else
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadingPane.setOpacity(0);
                        loadingPane.toBack();
                    }
                });
            }


        }
    }

    public void closeSettings()
    {
        if(!isConnected)
        {
            closeSettingsButton.setVisible(false);
        }
        else
        {
            closeSettingsButton.setVisible(true);
        }
        if(isSettingsOpen)
        {
            //System.out.println("closed!");
            isSettingsOpen = false;

            if(Main.config.get("animations_mode").equals("1"))
            {
                FadeOutDown s = new FadeOutDown(settingsPane);
                s.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        settingsPane.toBack();
                    }
                });
                s.play();
            }
            else
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        settingsPane.setOpacity(0);
                        settingsPane.toBack();
                    }
                });
            }
        }
    }

    public void closeSettingsDebug()
    {
        if(isConnected)
        {
            closeSettings();
        }
    }

    @FXML
    public void closeSettingsButtonClicked()
    {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if(isConnected)
                {
                    Thread.sleep(200);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            closeSettings();
                        }
                    });
                }
                return null;
            }
        }).start();

    }



    @FXML
    public void restartStreamPi()
    {
        try
        {
            isShutdown = true;
            if(isConnected)
            {
                //System.out.println("Closing connection to Server ...");
                s.close();
                //System.out.println("... Done!");
            }
            //System.out.println("Restarting ...");
            Runtime r = Runtime.getRuntime();
            r.exec("sudo reboot");
        }
        catch (Exception e)
        {
            if(debugMode)
                e.printStackTrace();
        }
    }

    boolean isShutdown = false;
    @FXML
    public void shutdownStreamPi()
    {
        try
        {
            isShutdown = true;
            goodbyePane.setVisible(true);
            goodbyePane.toFront();
            if(isConnected)
            {
                isConnected = false;
            }
            //System.out.println("Shutting Down ...");
            new Thread(new Task<Void>() {
                @Override
                protected Void call() {
                    try
                    {
                        Thread.sleep(3000);
                        Runtime r = Runtime.getRuntime();
                        r.exec("sudo halt");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            }).start();
        }
        catch (Exception e)
        {
            if(debugMode)
                e.printStackTrace();
        }
    }
}
