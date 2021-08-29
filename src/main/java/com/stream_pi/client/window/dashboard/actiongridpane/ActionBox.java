// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.dashboard.actiongridpane;

import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import com.stream_pi.client.animations.Wobble;
import com.stream_pi.client.animations.Tada;
import com.stream_pi.client.animations.Shake;
import com.stream_pi.client.animations.RubberBand;
import com.stream_pi.client.animations.Pulse;
import com.stream_pi.client.animations.Jello;
import com.stream_pi.client.animations.Swing;
import com.stream_pi.client.animations.JackInTheBox;
import com.stream_pi.client.animations.Bounce;
import com.stream_pi.client.animations.Flip;
import javafx.geometry.Pos;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import java.io.InputStream;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import javafx.scene.layout.BackgroundImage;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.client.io.Config;
import com.stream_pi.action_api.action.ActionType;
import javafx.beans.value.WritableValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.Node;
import javafx.scene.CacheHint;
import javafx.beans.value.ObservableValue;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.Background;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.client.controller.ClientListener;
import javafx.animation.Timeline;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ActionBox extends StackPane
{
    private Label displayTextLabel;
    private int row;
    private int col;
    private Logger logger;
    private FontIcon statusIcon;
    private Timeline statusIconAnimation;
    private int size;
    private ActionGridPaneListener actionGridPaneListener;
    private ClientListener clientListener;
    private Action action;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private String parent;
    FontIcon fontIcon;
    
    public int getRow() {
        return this.row;
    }
    
    public int getCol() {
        return this.col;
    }
    
    public void clear() {
        this.setStyle((String)null);
        this.setAction(null);
        this.setCurrentToggleStatus(false);
        this.getStyleClass().clear();
        this.setBackground(Background.EMPTY);
        this.removeFontIcon();
        this.getChildren().clear();
        this.baseInit();
    }
    
    public void baseInit() {
        (this.displayTextLabel = new Label()).setWrapText(true);
        this.displayTextLabel.setTextAlignment(TextAlignment.CENTER);
        this.displayTextLabel.getStyleClass().add((Object)"action_box_display_text_label");
        this.displayTextLabel.prefHeightProperty().bind((ObservableValue)this.heightProperty());
        this.displayTextLabel.prefWidthProperty().bind((ObservableValue)this.widthProperty());
        this.statusIcon = new FontIcon("fas-exclamation-triangle");
        this.statusIcon.getStyleClass().add((Object)"action_box_error_icon");
        this.statusIcon.setOpacity(0.0);
        this.statusIcon.setCache(true);
        this.statusIcon.setCacheHint(CacheHint.SPEED);
        this.statusIcon.setIconSize(this.size - 30);
        this.getChildren().addAll((Object[])new Node[] { (Node)this.statusIcon, (Node)this.displayTextLabel });
        this.setMinSize((double)this.size, (double)this.size);
        this.setMaxSize((double)this.size, (double)this.size);
        this.getStyleClass().clear();
        this.getStyleClass().add((Object)"action_box");
        this.getStyleClass().add("action_box_" + this.row + "_" + this.col);
        this.setIcon(null);
        this.setOnMouseClicked(touchEvent -> this.actionClicked());
        this.setOnMousePressed(TouchEvent -> {
            if (this.action != null) {
                this.getStyleClass().add((Object)"action_box_onclick");
            }
        });
        this.setOnMouseReleased(TouchEvent -> {
            if (this.action != null) {
                this.getStyleClass().remove((Object)"action_box_onclick");
            }
        });
        (this.statusIconAnimation = new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.statusIcon.opacityProperty(), (Object)0.0, Interpolator.EASE_IN) }), new KeyFrame(Duration.millis(100.0), new KeyValue[] { new KeyValue((WritableValue)this.statusIcon.opacityProperty(), (Object)1.0, Interpolator.EASE_IN) }), new KeyFrame(Duration.millis(600.0), new KeyValue[] { new KeyValue((WritableValue)this.statusIcon.opacityProperty(), (Object)1.0, Interpolator.EASE_OUT) }), new KeyFrame(Duration.millis(700.0), new KeyValue[] { new KeyValue((WritableValue)this.statusIcon.opacityProperty(), (Object)0.0, Interpolator.EASE_OUT) }) })).setOnFinished(event -> this.statusIcon.toBack());
        this.setCache(true);
        this.setCacheHint(CacheHint.QUALITY);
    }
    
    public void actionClicked() {
        if (this.action != null) {
            if (this.action.getActionType() == ActionType.FOLDER) {
                this.getActionGridPaneListener().renderFolder(this.action.getID());
            }
            else {
                if (!this.getActionGridPaneListener().isConnected()) {
                    try {
                        if (Config.getInstance().isTryConnectingWhenActionClicked()) {
                            this.clientListener.setupClientConnection(this::actionClicked);
                        }
                        else {
                            this.exceptionAndAlertHandler.handleMinorException(new MinorException("Not Connected", "Not Connected to any Server"));
                        }
                        return;
                    }
                    catch (SevereException e) {
                        this.exceptionAndAlertHandler.handleSevereException(e);
                    }
                }
                if (this.action.getActionType() == ActionType.COMBINE || this.action.getActionType() == ActionType.NORMAL) {
                    this.getActionGridPaneListener().normalOrCombineActionClicked(this.action.getID());
                }
                else if (this.action.getActionType() == ActionType.TOGGLE) {
                    this.toggle();
                    this.getActionGridPaneListener().toggleActionClicked(this.action.getID(), this.getCurrentToggleStatus());
                }
            }
        }
        try {
            this.playActionAnimation();
        }
        catch (SevereException e) {
            Logger.getLogger("").warning(e.getMessage());
        }
    }
    
    public Timeline getStatusIconAnimation() {
        return this.statusIconAnimation;
    }
    
    public ActionGridPaneListener getActionGridPaneListener() {
        return this.actionGridPaneListener;
    }
    
    public ActionBox(final int size, final ExceptionAndAlertHandler exceptionAndAlertHandler, final ClientListener clientListener, final ActionGridPaneListener actionGridPaneListener, final int row, final int col) {
        this.action = null;
        this.fontIcon = null;
        this.actionGridPaneListener = actionGridPaneListener;
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.size = size;
        this.row = row;
        this.col = col;
        this.clientListener = clientListener;
        this.logger = Logger.getLogger("");
        this.baseInit();
    }
    
    public Logger getLogger() {
        return this.logger;
    }
    
    public void setIcon(final byte[] iconByteArray) {
        this.removeFontIcon();
        if (iconByteArray == null) {
            this.getStyleClass().remove((Object)"action_box_icon_present");
            this.getStyleClass().add((Object)"action_box_icon_not_present");
            this.setBackground((Background)null);
        }
        else {
            this.getStyleClass().add((Object)"action_box_icon_present");
            this.getStyleClass().remove((Object)"action_box_icon_not_present");
            this.setBackground(new Background(new BackgroundImage[] { new BackgroundImage(new Image((InputStream)new ByteArrayInputStream(iconByteArray), (double)this.size, (double)this.size, false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100.0, 100.0, true, true, true, false)) }));
        }
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public String getStreamPiParent() {
        return this.parent;
    }
    
    public void setStreamPiParent(final String parent) {
        this.parent = parent;
    }
    
    public void setAction(final Action action) {
        this.action = action;
    }
    
    public void init() {
        this.setBackground((Background)null);
        this.setStyle((String)null);
        this.displayTextLabel.setStyle((String)null);
        if (this.getAction().isShowDisplayText()) {
            this.setDisplayTextAlignment(this.action.getDisplayTextAlignment());
            this.setDisplayTextFontColourAndSize(this.action.getDisplayTextFontColourHex());
            this.setDisplayTextLabel(this.getAction().getDisplayText());
        }
        else {
            this.setDisplayTextLabel("");
        }
        this.setBackgroundColour(this.action.getBgColourHex());
        try {
            if (this.action.getActionType() == ActionType.TOGGLE) {
                this.toggle(this.getCurrentToggleStatus());
            }
            else if (this.action.isHasIcon()) {
                if (!this.action.getCurrentIconState().isBlank()) {
                    this.setIcon(this.action.getCurrentIcon());
                }
            }
            else {
                this.setIcon(null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setCurrentToggleStatus(final boolean currentToggleStatus) {
        if (this.getAction() != null) {
            this.getAction().setCurrentToggleStatus(currentToggleStatus);
        }
    }
    
    public boolean getCurrentToggleStatus() {
        return this.getAction() != null && this.getAction().getCurrentToggleStatus();
    }
    
    public void toggle() {
        this.setCurrentToggleStatus(!this.getCurrentToggleStatus());
        this.toggle(this.getCurrentToggleStatus());
    }
    
    public void toggle(final boolean isON) {
        final String[] toggleStatesHiddenStatus = this.action.getCurrentIconState().split("__");
        final boolean isToggleOffHidden = toggleStatesHiddenStatus[0].equals("true");
        final boolean isToggleOnHidden = toggleStatesHiddenStatus[1].equals("true");
        if (isON) {
            if (this.action.isHasIcon()) {
                final boolean isToggleOnPresent = this.action.getIcons().containsKey("toggle_on");
                if (isToggleOnPresent) {
                    if (isToggleOnHidden) {
                        this.setDefaultToggleIcon(true);
                    }
                    else {
                        this.setIcon(this.action.getIcons().get("toggle_on"));
                    }
                }
                else {
                    this.setDefaultToggleIcon(true);
                }
            }
            else {
                this.setDefaultToggleIcon(true);
            }
        }
        else if (this.action.isHasIcon()) {
            final boolean isToggleOffPresent = this.action.getIcons().containsKey("toggle_off");
            if (isToggleOffPresent) {
                if (isToggleOffHidden) {
                    this.setDefaultToggleIcon(false);
                }
                else {
                    this.setIcon(this.action.getIcons().get("toggle_off"));
                }
            }
            else {
                this.setDefaultToggleIcon(false);
            }
        }
        else {
            this.setDefaultToggleIcon(false);
        }
    }
    
    public void setDefaultToggleIcon(final boolean isToggleOn) {
        String styleClass;
        if (isToggleOn) {
            styleClass = "action_box_toggle_on";
        }
        else {
            styleClass = "action_box_toggle_off";
        }
        this.setBackground((Background)null);
        if (this.fontIcon != null) {
            this.fontIcon.getStyleClass().removeIf(s -> s.equals("action_box_toggle_off") || s.equals("action_box_toggle_on"));
        }
        else {
            (this.fontIcon = new FontIcon()).setIconSize((int)(this.size * 0.8));
            this.getChildren().add((Object)this.fontIcon);
        }
        this.fontIcon.getStyleClass().add((Object)styleClass);
        this.fontIcon.toBack();
    }
    
    public void removeFontIcon() {
        if (this.fontIcon != null) {
            this.getChildren().remove((Object)this.fontIcon);
            this.fontIcon = null;
        }
    }
    
    public void animateStatus() {
        this.statusIcon.toFront();
        this.statusIconAnimation.play();
    }
    
    public void setDisplayTextLabel(final String text) {
        this.displayTextLabel.setText(text);
    }
    
    public void setDisplayTextAlignment(final DisplayTextAlignment displayTextAlignment) {
        if (displayTextAlignment == DisplayTextAlignment.CENTER) {
            this.displayTextLabel.setAlignment(Pos.CENTER);
        }
        else if (displayTextAlignment == DisplayTextAlignment.BOTTOM) {
            this.displayTextLabel.setAlignment(Pos.BOTTOM_CENTER);
        }
        else if (displayTextAlignment == DisplayTextAlignment.TOP) {
            this.displayTextLabel.setAlignment(Pos.TOP_CENTER);
        }
    }
    
    public void setDisplayTextFontColourAndSize(final String colour) {
        String totalStyle = "";
        if (!colour.isEmpty()) {
            totalStyle = totalStyle + "-fx-text-fill : " + colour;
        }
        if (this.getAction().getNameFontSize() > -1.0) {
            totalStyle = totalStyle + "-fx-font-size: " + this.getAction().getNameFontSize();
        }
        if (!totalStyle.isBlank()) {
            this.displayTextLabel.setStyle(totalStyle);
        }
    }
    
    public void playActionAnimation() throws SevereException {
        final Config config = Config.getInstance();
        final String currentAnimationName = config.getCurrentAnimationName();
        switch (currentAnimationName) {
            case "None": {
                break;
            }
            case "Flip": {
                new Flip((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Bounce": {
                new Bounce((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Jack In The Box": {
                new JackInTheBox((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Swing": {
                new Swing((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Jello": {
                new Jello((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Pulse": {
                new Pulse((Node)this.getChildren().get(1)).play();
                break;
            }
            case "RubberBand": {
                new RubberBand((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Shake": {
                new Shake((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Tada": {
                new Tada((Node)this.getChildren().get(1)).play();
                break;
            }
            case "Wobble": {
                new Wobble((Node)this.getChildren().get(1)).play();
                break;
            }
            default: {
                Logger.getLogger("").warning("Invalid Option/n Please contact quimodotcom to solve this error!");
                break;
            }
        }
    }
    
    public void setBackgroundColour(final String colour) {
        if (!colour.isEmpty()) {
            this.setStyle("-fx-background-color : " + colour);
        }
    }
}
