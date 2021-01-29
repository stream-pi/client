package com.StreamPi.Client.Window.Dashboard.ActionGridPane;

import java.util.logging.Logger;

import com.StreamPi.ActionAPI.Action.Action;
import com.StreamPi.ActionAPI.Action.Location;
import com.StreamPi.ActionAPI.OtherActions.FolderAction;
import com.StreamPi.Client.Connection.ClientListener;
import com.StreamPi.Client.IO.Config;
import com.StreamPi.Client.Profile.ClientProfile;
import com.StreamPi.Client.Window.ExceptionAndAlertHandler;
import com.StreamPi.Util.Alert.StreamPiAlertType;
import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Exception.SevereException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class ActionGridPane extends GridPane implements ActionGridPaneListener {

    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ClientListener clientListener;

    public ActionGridPane(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        this.clientListener = clientListener;

        logger = Logger.getLogger(ActionGridPane.class.getName());
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        getStyleClass().add("action_grid_pane");

        setPadding(new Insets(5.0));

        setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);


        setAlignment(Pos.CENTER);

        VBox.setVgrow(this, Priority.ALWAYS);
    }

    private String currentParent;

    public void setCurrentParent(String currentParent) {
        this.currentParent = currentParent;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    private int rows, cols;

    private ClientProfile clientProfile;

    public void setClientProfile(ClientProfile clientProfile)
    {
        this.clientProfile = clientProfile;

        System.out.println("CURRENT : "+clientProfile.getID());

        setCurrentParent("root");
        setRows(clientProfile.getRows());
        setCols(clientProfile.getCols());
    }

    public void actionFailed(String profileID, String actionID)
    {
        if(getClientProfile().getID().equals(profileID))
        {
            Action action = getClientProfile().getActionFromID(actionID);
            if(action != null)
            {
                if(currentParent.equals(action.getParent()))
                {
                    failShow(action);
                }
                else
                {
                    if(action.getLocation().getCol() == -1)
                    {
                        failShow(getClientProfile().getActionFromID(action.getParent()));
                    }
                }
            }
        }
    }

    public void failShow(Action action)
    {
        for(Node node : getChildren())
        {
            if(GridPane.getColumnIndex(node) == action.getLocation().getRow() &&
                    GridPane.getRowIndex(node) == action.getLocation().getCol())
            {

                ActionBox actionBox = (ActionBox) node;

                actionBox.getStatusIcon().setIconLiteral("fas-exclamation-triangle");
                actionBox.getStatusIcon().setIconColor(Color.RED);

                actionBox.animateStatus();

                break;
            }
        }
    }


    public String getCurrentParent() {
        return currentParent;
    }

    public StackPane getFolderBackButton() throws SevereException
    {
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("action_box");
        stackPane.getStyleClass().add("action_box_valid");

        stackPane.setPrefSize(
                getClientProfile().getActionSize(),
                getClientProfile().getActionSize()
        );

        FontIcon fontIcon = new FontIcon("fas-chevron-left");

        fontIcon.setIconSize(getClientProfile().getActionSize() - 30);

        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(fontIcon);

        stackPane.setOnMouseClicked(e->returnToPreviousParent());

        return stackPane;
    }

    public void renderGrid() throws SevereException {
        clear();

        setHgap(getClientProfile().getActionGap());
        setVgap(getClientProfile().getActionGap());

        boolean isFolder = false;

        if(!getCurrentParent().equals("root"))
        {
            isFolder = true;

            add(getFolderBackButton(), 0,0);
        }

        for(int row = 0; row<rows; row++)
        {
            for(int col = 0; col<cols; col++)
            {
                if(row == 0 && col == 0 && isFolder)
                    continue;

                addBlankActionBox(col, row);

            }
        }
    }

    public void renderActions()
    {
        StringBuilder errors = new StringBuilder();
        for(Action eachAction : getClientProfile().getActions())
        {
            logger.info("Action ID : "+eachAction.getID()+"\nInvalid : "+eachAction.isInvalid());

            try {
                renderAction(eachAction);
            }
            catch (SevereException e)
            {
                exceptionAndAlertHandler.handleSevereException(e);
            }
            catch (MinorException e)
            {
                errors.append("*").append(e.getShortMessage()).append("\n");
            }
        }

        if(!errors.toString().isEmpty())
        {
            exceptionAndAlertHandler.handleMinorException(new MinorException("Error while rendering following actions", errors.toString()));
        }
    }

    public void clear()
    {
        getChildren().clear();
    }

    private Logger logger;


    public void clearActionBox(int col, int row)
    {
        for(Node node : getChildren())
        {
            if(GridPane.getColumnIndex(node) == row &&
                    GridPane.getRowIndex(node) == col)
            {
                getChildren().remove(node);
                break;
            }
        }
    }

    public ActionBox getActionBox(int col, int row)
    {
        for(Node node : getChildren())
        {
            if(GridPane.getColumnIndex(node) == row &&
                    GridPane.getRowIndex(node) == col)
            {
                return (ActionBox) node;
            }
        }

        return null;
    }

    public void addBlankActionBox(int col, int row)
    {
        ActionBox actionBox = new ActionBox(getClientProfile().getActionSize(), this, row, col);

        actionBox.setStreamPiParent(currentParent);

        add(actionBox, row, col);
    }

    public void renderAction(Action action) throws SevereException, MinorException
    {
        if(!action.getParent().equals(currentParent))
        {
            logger.info("Skipping action "+action.getID()+", not current parent!");
            return;
        }

        if(action.getLocation().getRow()==-1)
        {
            logger.info("Action has -1 rowIndex. Probably Combine Action. Skipping ...");
            return;
        }

        if(action.getLocation().getRow() > rows || action.getLocation().getCol() > cols)
        {
            throw new MinorException("Action "+action.getDisplayText()+" ("+action.getID()+") falls outside bounds.\n" +
                    "   Consider increasing rows/cols from client settings and relocating/deleting it.");
        }


        Location location = action.getLocation();

        ActionBox actionBox = new ActionBox(getClientProfile().getActionSize(), action, exceptionAndAlertHandler, this, location.getRow(), location.getCol());

        actionBox.setStreamPiParent(currentParent);

        clearActionBox(location.getCol(), location.getRow());

        System.out.println(location.getCol()+","+location.getRow());
        add(actionBox, location.getRow(), location.getCol());

    }

    public void setRows(int rows)
    {
        this.rows = rows;
    }

    public void setCols(int cols)
    {
        this.cols = cols;
    }

    public int getRows()
    {
        return rows;
    }

    public int getCols()
    {
        return cols;
    }

    private String previousParent;

    public void setPreviousParent(String previousParent) {
        this.previousParent = previousParent;
    }

    public String getPreviousParent() {
        return previousParent;
    }

    @Override
    public void renderFolder(String actionID) {
        setCurrentParent(clientProfile.getActionFromID(actionID).getID());
        setPreviousParent(clientProfile.getActionFromID(actionID).getParent());
        try {
            renderGrid();
            renderActions();
        } catch (SevereException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void normalActionClicked(String ID) {
        if(clientListener.isConnected())
            clientListener.onNormalActionClicked(getClientProfile().getID(), ID);
        else
            exceptionAndAlertHandler.onAlert("Not Connected", "Not Connected to any Server", StreamPiAlertType.ERROR);
    }

    @Override
    public void combineActionClicked(String ID) {
        if(clientListener.isConnected())
        {
            new Thread(new Task<Void>() {
                @Override
                protected Void call()
                {
                    Action action = getClientProfile().getActionFromID(ID);

                    for(int i = 0;i<action.getClientProperties().get().size();i++)
                    {
                        try {
                            logger.info("Clicking "+i+", '"+action.getClientProperties().getSingleProperty(i+"").getRawValue()+"'");
                            normalActionClicked(action.getClientProperties().getSingleProperty(i+"").getRawValue());
                        } catch (MinorException e) {
                            e.printStackTrace();
                            exceptionAndAlertHandler.handleMinorException(e);
                        }
                    }

                    return null;
                }
            }).start();
        }
    }

    public void returnToPreviousParent()
    {
        setCurrentParent(getPreviousParent());

        if(!getPreviousParent().equals("root"))
        {
            System.out.println("parent : "+getPreviousParent());
            setPreviousParent(getClientProfile().getActionFromID(
                    getPreviousParent()
            ).getParent());
        }

        try {
            renderGrid();
            renderActions();
        } catch (SevereException e) {
            e.printStackTrace();
        }
    }
}
