// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window.dashboard.actiongridpane;

import javafx.scene.input.MouseEvent;
import com.stream_pi.action_api.action.Location;
import com.stream_pi.action_api.action.ActionType;
import javafx.geometry.Orientation;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.io.Config;
import java.util.Iterator;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.layout.StackPane;
import com.stream_pi.action_api.action.Action;
import javafx.scene.CacheHint;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import java.util.logging.Logger;
import javafx.scene.Node;
import com.stream_pi.client.profile.ClientProfile;
import javafx.scene.layout.GridPane;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import javafx.scene.control.ScrollPane;

public class ActionGridPane extends ScrollPane implements ActionGridPaneListener
{
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ClientListener clientListener;
    private ActionBox[][] actionBoxes;
    private GridPane actionsGridPane;
    private String currentParent;
    private int rows;
    private int cols;
    private ClientProfile clientProfile;
    private boolean isFreshRender;
    private Node folderBackButton;
    private Logger logger;
    private String previousParent;
    
    public ActionGridPane(final ExceptionAndAlertHandler exceptionAndAlertHandler, final ClientListener clientListener) {
        this.isFreshRender = true;
        this.folderBackButton = null;
        this.clientListener = clientListener;
        this.logger = Logger.getLogger(ActionGridPane.class.getName());
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.getStyleClass().add((Object)"action_grid_pane_parent");
        (this.actionsGridPane = new GridPane()).setPadding(new Insets(5.0));
        this.actionsGridPane.getStyleClass().add((Object)"action_grid_pane");
        this.actionsGridPane.prefWidthProperty().bind((ObservableValue)this.widthProperty().subtract(20));
        this.actionsGridPane.prefHeightProperty().bind((ObservableValue)this.heightProperty().subtract(20));
        this.setContent((Node)this.actionsGridPane);
        this.actionsGridPane.setAlignment(Pos.CENTER);
        VBox.setVgrow((Node)this, Priority.ALWAYS);
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
    }
    
    public void setCurrentParent(final String currentParent) {
        this.currentParent = currentParent;
    }
    
    public ClientProfile getClientProfile() {
        return this.clientProfile;
    }
    
    public void setClientProfile(final ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
        this.setCurrentParent("root");
        this.setRows(clientProfile.getRows());
        this.setCols(clientProfile.getCols());
    }
    
    public void actionFailed(final String profileID, final String actionID) {
        if (this.getClientProfile().getID().equals(profileID)) {
            final Action action = this.getClientProfile().getActionFromID(actionID);
            if (action != null) {
                if (this.currentParent.equals(action.getParent())) {
                    this.failShow(action);
                }
                else if (action.getLocation().getCol() == -1) {
                    this.failShow(this.getClientProfile().getActionFromID(action.getParent()));
                }
            }
        }
    }
    
    public void failShow(final Action action) {
        this.actionBoxes[action.getLocation().getCol()][action.getLocation().getRow()].animateStatus();
    }
    
    public String getCurrentParent() {
        return this.currentParent;
    }
    
    public StackPane getFolderBackButton() {
        final StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add((Object)"action_box");
        stackPane.getStyleClass().add((Object)"action_box_valid");
        stackPane.setPrefSize((double)this.getClientProfile().getActionSize(), (double)this.getClientProfile().getActionSize());
        final FontIcon fontIcon = new FontIcon("fas-chevron-left");
        fontIcon.getStyleClass().add((Object)"folder_action_back_button_icon");
        fontIcon.setIconSize(this.getClientProfile().getActionSize() - 30);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().add((Object)fontIcon);
        stackPane.setOnMouseClicked(e -> this.returnToPreviousParent());
        return stackPane;
    }
    
    public void renderGrid() {
        this.actionsGridPane.setHgap((double)this.getClientProfile().getActionGap());
        this.actionsGridPane.setVgap((double)this.getClientProfile().getActionGap());
        if (this.isFreshRender) {
            this.clear();
            this.actionBoxes = new ActionBox[this.cols][this.rows];
        }
        boolean isFolder = false;
        if (this.getCurrentParent().equals("root")) {
            if (this.folderBackButton != null) {
                this.actionsGridPane.getChildren().remove((Object)this.folderBackButton);
                this.folderBackButton = null;
                this.actionBoxes[0][0] = this.addBlankActionBox(0, 0);
            }
        }
        else {
            isFolder = true;
            if (this.folderBackButton != null) {
                this.actionsGridPane.getChildren().remove((Object)this.folderBackButton);
                this.folderBackButton = null;
            }
            else {
                this.actionsGridPane.getChildren().remove((Object)this.actionBoxes[0][0]);
            }
            this.folderBackButton = (Node)this.getFolderBackButton();
            this.actionsGridPane.add(this.folderBackButton, 0, 0);
        }
        for (int row = 0; row < this.rows; ++row) {
            for (int col = 0; col < this.cols; ++col) {
                if (row != 0 || col != 0 || !isFolder) {
                    if (this.isFreshRender) {
                        this.actionBoxes[col][row] = this.addBlankActionBox(col, row);
                    }
                    else if (this.actionBoxes[col][row].getAction() != null) {
                        this.actionBoxes[col][row].clear();
                    }
                }
            }
        }
        this.isFreshRender = false;
    }
    
    public void setFreshRender(final boolean isFreshRender) {
        this.isFreshRender = isFreshRender;
    }
    
    public void renderActions() {
        final StringBuilder errors = new StringBuilder();
        for (final Action eachAction : this.getClientProfile().getActions()) {
            this.logger.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, eachAction.getID(), eachAction.isInvalid()));
            try {
                this.renderAction(eachAction);
            }
            catch (SevereException e) {
                this.exceptionAndAlertHandler.handleSevereException(e);
            }
            catch (MinorException e2) {
                errors.append("*").append(e2.getMessage()).append("\n");
            }
        }
        if (!errors.toString().isEmpty()) {
            this.exceptionAndAlertHandler.handleMinorException(new MinorException("Error while rendering following actions", errors.toString()));
        }
    }
    
    public void clear() {
        this.actionsGridPane.getChildren().clear();
    }
    
    public void clearActionBox(final int col, final int row) {
        this.actionBoxes[col][row].clear();
    }
    
    public ActionBox getActionBox(final int col, final int row) {
        return this.actionBoxes[col][row];
    }
    
    public ActionBox addBlankActionBox(final int col, final int row) {
        final ActionBox actionBox = new ActionBox(this.getClientProfile().getActionSize(), this.exceptionAndAlertHandler, this.clientListener, this, row, col);
        actionBox.setStreamPiParent(this.currentParent);
        try {
            if (Config.getInstance().isInvertRowsColsOnDeviceRotate() && ClientInfo.getInstance().isPhone()) {
                if (this.clientListener.getCurrentOrientation() == Orientation.HORIZONTAL) {
                    this.actionsGridPane.add((Node)actionBox, col, row);
                }
                else {
                    this.actionsGridPane.add((Node)actionBox, row, col);
                }
            }
            else {
                this.actionsGridPane.add((Node)actionBox, col, row);
            }
        }
        catch (SevereException e) {
            this.exceptionAndAlertHandler.handleSevereException(e);
        }
        return actionBox;
    }
    
    public void toggleOffAllToggleActions() {
        for (final Node each : this.actionsGridPane.getChildren()) {
            if (each instanceof ActionBox) {
                final ActionBox eachActionBox = (ActionBox)each;
                if (eachActionBox.getAction() == null || eachActionBox.getAction().getActionType() != ActionType.TOGGLE || !eachActionBox.getCurrentToggleStatus()) {
                    continue;
                }
                eachActionBox.toggle();
            }
        }
    }
    
    public void renderAction(final Action action) throws SevereException, MinorException {
        if (!action.getParent().equals(this.currentParent)) {
            this.logger.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, action.getID()));
            return;
        }
        if (action.getLocation().getRow() == -1) {
            this.logger.info("Action has -1 rowIndex. Probably Combine Action. Skipping ...");
            return;
        }
        if (action.getLocation().getRow() >= this.rows || action.getLocation().getCol() >= this.cols) {
            throw new MinorException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, action.getDisplayText(), action.getID()));
        }
        final Location location = action.getLocation();
        if (this.getClientProfile().getCols() < location.getCol() || this.getClientProfile().getRows() < location.getRow()) {
            return;
        }
        final ActionBox actionBox = this.actionBoxes[location.getCol()][location.getRow()];
        if (actionBox.getAction() != null) {
            if (!actionBox.getAction().getID().equals(action.getID())) {
                actionBox.clear();
            }
        }
        else {
            actionBox.clear();
        }
        final boolean oldToggleStatus = action.getCurrentToggleStatus();
        actionBox.setAction(action);
        actionBox.setCurrentToggleStatus(oldToggleStatus);
        actionBox.setStreamPiParent(this.currentParent);
        actionBox.init();
    }
    
    public void setRows(final int rows) {
        this.rows = rows;
    }
    
    public void setCols(final int cols) {
        this.cols = cols;
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public int getCols() {
        return this.cols;
    }
    
    public void setPreviousParent(final String previousParent) {
        this.previousParent = previousParent;
    }
    
    public String getPreviousParent() {
        return this.previousParent;
    }
    
    public void renderFolder(final String actionID) {
        this.setCurrentParent(this.clientProfile.getActionFromID(actionID).getID());
        this.setPreviousParent(this.clientProfile.getActionFromID(actionID).getParent());
        this.renderGrid();
        this.renderActions();
    }
    
    public void normalOrCombineActionClicked(final String ID) {
        this.clientListener.onActionClicked(this.getClientProfile().getID(), ID, false);
    }
    
    public void toggleActionClicked(final String ID, final boolean toggleState) {
        this.clientListener.onActionClicked(this.getClientProfile().getID(), ID, toggleState);
    }
    
    public ActionBox getActionBoxByLocation(final Location location) {
        return this.getActionBox(location.getCol(), location.getRow());
    }
    
    public boolean isConnected() {
        return this.clientListener.isConnected();
    }
    
    public void returnToPreviousParent() {
        this.setCurrentParent(this.getPreviousParent());
        if (!this.getPreviousParent().equals("root")) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getPreviousParent()));
            this.setPreviousParent(this.getClientProfile().getActionFromID(this.getPreviousParent()).getParent());
        }
        this.renderGrid();
        this.renderActions();
    }
}
