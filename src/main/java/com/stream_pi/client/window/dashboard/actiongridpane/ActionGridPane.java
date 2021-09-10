package com.stream_pi.client.window.dashboard.actiongridpane;

import java.util.logging.Logger;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class ActionGridPane extends ScrollPane implements ActionGridPaneListener
{

    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ClientListener clientListener;

    private ActionBox[][] actionBoxes;

    private GridPane actionsGridPane;

    public ActionGridPane(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        this.clientListener = clientListener;

        logger = Logger.getLogger(ActionGridPane.class.getName());
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;

        getStyleClass().add("action_grid_pane_parent");

        actionsGridPane = new GridPane();
        actionsGridPane.setPadding(new Insets(5.0));
        actionsGridPane.getStyleClass().add("action_grid_pane");
        actionsGridPane.prefWidthProperty().bind(widthProperty().subtract(20));
        actionsGridPane.prefHeightProperty().bind(heightProperty().subtract(20));

        setContent(actionsGridPane);

        actionsGridPane.setAlignment(Pos.CENTER);


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
        actionsGridPane.setHgap(getClientProfile().getActionGap());
        actionsGridPane.setVgap(getClientProfile().getActionGap());

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
                actionsGridPane.getChildren().remove(folderBackButton);
                folderBackButton = null;

                actionBoxes[0][0] = addBlankActionBox(0,0);
            }
        }
        else
        {
            isFolder = true;

            if(folderBackButton != null)
            {
                actionsGridPane.getChildren().remove(folderBackButton);
                folderBackButton = null;
            }
            else
            {
                actionsGridPane.getChildren().remove(actionBoxes[0][0]);
            }

            folderBackButton = getFolderBackButton();
            actionsGridPane.add(folderBackButton, 0,0);
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
                        actionBoxes[col][row].clear();
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
                errors.append("*").append(e.getMessage()).append("\n");
            }
        }

        if(!errors.toString().isEmpty())
        {
            exceptionAndAlertHandler.handleMinorException(new MinorException("Error while rendering following actions", errors.toString()));
        }
    }

    public void clear()
    {
        actionsGridPane.getChildren().clear();
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
        ActionBox actionBox = new ActionBox(getClientProfile().getActionSize(), exceptionAndAlertHandler, clientListener, this, row, col);

        actionBox.setStreamPiParent(currentParent);

        try
        {
            if(Config.getInstance().isInvertRowsColsOnDeviceRotate() && ClientInfo.getInstance().isPhone())
            {
                if(clientListener.getCurrentOrientation() == Orientation.HORIZONTAL)
                {
                    actionsGridPane.add(actionBox, col, row);
                }
                else
                {
                    actionsGridPane.add(actionBox, row, col);
                }
            }
            else
            {
                actionsGridPane.add(actionBox, col, row);
            }
        }
        catch (SevereException e)
        {
            exceptionAndAlertHandler.handleSevereException(e);
        }


        return actionBox;
    }

    public void toggleOffAllToggleActions()
    {
        for(Node each : actionsGridPane.getChildren())
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

        if(action.getLocation().getRow() >= rows || action.getLocation().getCol() >= cols)
        {
            throw new MinorException("Action "+action.getDisplayText()+" ("+action.getID()+") falls outside bounds.\n" +
                    "   Consider increasing rows/cols from client settings and relocating/deleting it.");
        }


        Location location = action.getLocation();

        if( getClientProfile().getCols() < location.getCol() || getClientProfile().getRows() < location.getRow())
            return;

        ActionBox actionBox = actionBoxes[location.getCol()][location.getRow()];

        if(actionBox.getAction()!=null)
        {
            if(!actionBox.getAction().getID().equals(action.getID()))
            {
                actionBox.clear();
            }
        }
        else
        {
            actionBox.clear();
        }


        boolean oldToggleStatus = action.getCurrentToggleStatus();


        actionBox.setAction(action);


        actionBox.setCurrentToggleStatus(oldToggleStatus);


        actionBox.setStreamPiParent(currentParent);
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
    public ActionBox getActionBoxByLocation(Location location)
    {
        return getActionBox(location.getCol(), location.getRow());
    }


    @Override
    public boolean isConnected()
    {
        return clientListener.isConnected();
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
