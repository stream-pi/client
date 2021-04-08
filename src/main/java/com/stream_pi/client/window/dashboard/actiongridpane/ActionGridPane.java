package com.stream_pi.client.window.dashboard.actiongridpane;

import java.util.logging.Logger;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.client.connection.ClientListener;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class ActionGridPane extends GridPane implements ActionGridPaneListener
{

    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ClientListener clientListener;

    private ActionBox[][] actionBoxes;

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

        setCache(true);
        setCacheHint(CacheHint.SPEED);
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
        /*for(Node node : getChildren())
        {
            if(GridPane.getColumnIndex(node) == action.getLocation().getRow() &&
                    GridPane.getRowIndex(node) == action.getLocation().getCol())
            {

                ActionBox actionBox = (ActionBox) node;

                actionBox.animateStatus();

                break;
            }
        }*/

        actionBoxes[action.getLocation().getCol()][action.getLocation().getRow()].animateStatus();
    }


    public String getCurrentParent() {
        return currentParent;
    }

    public StackPane getFolderBackButton()
    {
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("action_box");
        stackPane.getStyleClass().add("action_box_valid");

        stackPane.setPrefSize(
                getClientProfile().getActionSize(),
                getClientProfile().getActionSize()
        );

        FontIcon fontIcon = new FontIcon("fas-chevron-left");
        fontIcon.getStyleClass().add("folder_action_back_button_icon");
        fontIcon.setIconSize(getClientProfile().getActionSize() - 30);

        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(fontIcon);

        stackPane.setOnMouseClicked(e->returnToPreviousParent());

        return stackPane;
    }

    private boolean isFreshRender = true;
    private Node folderBackButton = null;
    public void renderGrid()
    {
        setHgap(getClientProfile().getActionGap());
        setVgap(getClientProfile().getActionGap());

        if(isFreshRender)
        {
            clear();
            actionBoxes = new ActionBox[cols][rows];
        }

        boolean isFolder = false;

        if(getCurrentParent().equals("root"))
        {
            if(folderBackButton != null)
            {
                getChildren().remove(folderBackButton);
                folderBackButton = null;

                actionBoxes[0][0] = addBlankActionBox(0,0);
            }
        }
        else
        {
            isFolder = true;

            if(folderBackButton != null)
            {
                getChildren().remove(folderBackButton);
                folderBackButton = null;
            }
            else
            {
                getChildren().remove(actionBoxes[0][0]);
            }

            folderBackButton = getFolderBackButton();
            add(folderBackButton, 0,0);
        }

        for(int row = 0; row<rows; row++)
        {
            for(int col = 0; col<cols; col++)
            {
                if(row == 0 && col == 0 && isFolder)
                    continue;

                if(isFreshRender)
                {
                    actionBoxes[col][row] = addBlankActionBox(col, row);
                }
                else
                {
                    if(actionBoxes[col][row].getAction() != null)
                    {
                        logger.info("xc234213123123");
                        actionBoxes[col][row].clear();
                    }
                    else
                    {
                        logger.info("bbbbbb " +col+","+row);
                    }
                }
            }
        }

        isFreshRender = false;
    }

    public void setFreshRender(boolean isFreshRender) {
        this.isFreshRender = isFreshRender;
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
        actionBoxes[col][row].clear();
    }

    public ActionBox getActionBox(int col, int row)
    {
        return actionBoxes[col][row];
    }

    public ActionBox addBlankActionBox(int col, int row)
    {
        ActionBox actionBox = new ActionBox(getClientProfile().getActionSize(), exceptionAndAlertHandler, this, row, col);

        actionBox.setStreamPiParent(currentParent);

        add(actionBox, row, col);
        return actionBox;
    }

    public void toggleOffAllToggleActions()
    {
        for(Node each : getChildren())
        {
            if(each instanceof ActionBox)
            {
                ActionBox eachActionBox = (ActionBox) each;

                if(eachActionBox.getAction() != null)
                {
                    if(eachActionBox.getAction().getActionType() == ActionType.TOGGLE)
                    {
                        if(eachActionBox.getCurrentToggleStatus()) // ON
                        {
                            eachActionBox.toggle();
                        }
                    }
                }

            }
        }
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

        ActionBox actionBox = actionBoxes[location.getCol()][location.getRow()];

        boolean oldToggleStatus = actionBox.getCurrentToggleStatus();
        actionBox.clear();

        actionBox.setAction(action);

        actionBox.setStreamPiParent(currentParent);
        actionBox.setCurrentToggleStatus(oldToggleStatus);
        actionBox.init();

        /*ActionBox actionBox = new ActionBox(getClientProfile().getActionSize(), action, exceptionAndAlertHandler, this, location.getRow(), location.getCol());

        actionBox.setStreamPiParent(currentParent);

        clearActionBox(location.getCol(), location.getRow());

        System.out.println(location.getCol()+","+location.getRow());
        add(actionBox, location.getRow(), location.getCol());

        actionBoxes[location.getCol()][location.getRow()] = actionBox;*/
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
        renderGrid();
        renderActions();
    }

    @Override
    public void normalActionClicked(String ID)
    {
        clientListener.onActionClicked(getClientProfile().getID(), ID, false);
    }

    @Override
    public void toggleActionClicked(String ID, boolean toggleState)
    {
        clientListener.onActionClicked(getClientProfile().getID(), ID, toggleState);
    }

    @Override
    public boolean isConnected()
    {
        return clientListener.isConnected();
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

        renderGrid();
        renderActions();
    }
}
