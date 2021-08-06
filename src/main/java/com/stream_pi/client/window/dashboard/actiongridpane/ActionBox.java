package com.stream_pi.client.window.dashboard.actiongridpane;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.animations.*;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

public class ActionBox extends StackPane
{

    private Label displayTextLabel;

    private int row;
    private int col;

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
    
    private Logger logger;

    

    public void clear()
    {
        setStyle(null);
        setAction(null);
        setCurrentToggleStatus(false);
        getStyleClass().clear();
        setBackground(Background.EMPTY);
        removeFontIcon();
        getChildren().clear();
        baseInit();
    }

    private FontIcon statusIcon;
    
    public void baseInit()
    {
        displayTextLabel = new Label();
        displayTextLabel.setWrapText(true);
        displayTextLabel.setTextAlignment(TextAlignment.CENTER);
        displayTextLabel.getStyleClass().add("action_box_display_text_label");

        displayTextLabel.prefHeightProperty().bind(heightProperty());
        displayTextLabel.prefWidthProperty().bind(widthProperty());


        statusIcon = new FontIcon("fas-exclamation-triangle");
        statusIcon.getStyleClass().add("action_box_error_icon");
        statusIcon.setOpacity(0);
        statusIcon.setCache(true);
        statusIcon.setCacheHint(CacheHint.SPEED);
        statusIcon.setIconSize(size - 30);

    
        getChildren().addAll(statusIcon, displayTextLabel);

        setMinSize(size, size);
        setMaxSize(size, size);

        getStyleClass().clear();
        getStyleClass().add("action_box");
        getStyleClass().add("action_box_"+row+"_"+col);

        setIcon(null);

        setOnMouseClicked(touchEvent -> actionClicked());

        setOnMousePressed(TouchEvent -> {
            if(action != null)
            {
                getStyleClass().add("action_box_onclick");
            }
        });
        setOnMouseReleased(TouchEvent ->{
            if(action != null)
            {
                getStyleClass().remove("action_box_onclick");
            }
        });

        statusIconAnimation = new Timeline(
                new KeyFrame(
                        Duration.millis(0.0D),
                        new KeyValue(statusIcon.opacityProperty(), 0.0D, Interpolator.EASE_IN)),
                new KeyFrame(
                        Duration.millis(100.0D),
                        new KeyValue(statusIcon.opacityProperty(), 1.0D, Interpolator.EASE_IN)),
                new KeyFrame(
                        Duration.millis(600.0D),
                        new KeyValue(statusIcon.opacityProperty(), 1.0D, Interpolator.EASE_OUT)),
                new KeyFrame(
                        Duration.millis(700.0D),
                        new KeyValue(statusIcon.opacityProperty(), 0.0D, Interpolator.EASE_OUT))
        );

        statusIconAnimation.setOnFinished(event -> {
            statusIcon.toBack();
        });

        setCache(true);
        setCacheHint(CacheHint.QUALITY);
    }

    public void actionClicked()
    {
        if(action!=null)
        {
            if(action.getActionType() == ActionType.FOLDER)
            {
                getActionGridPaneListener().renderFolder(action.getID());
            }
            else
            {
                if(!getActionGridPaneListener().isConnected())
                {
                    try
                    {
                        if(Config.getInstance().isTryConnectingWhenActionClicked())
                        {
                            clientListener.setupClientConnection(this::actionClicked);
                        }
                        else
                        {
                            exceptionAndAlertHandler.handleMinorException(new MinorException("Not Connected", "Not Connected to any Server"));
                        }
                        return;
                    }
                    catch (SevereException e)
                    {
                        exceptionAndAlertHandler.handleSevereException(e);
                    }
                }

                if(action.getActionType() == ActionType.COMBINE || action.getActionType() == ActionType.NORMAL)
                {
                    getActionGridPaneListener().normalOrCombineActionClicked(action.getID());
                }
                else if(action.getActionType() == ActionType.TOGGLE)
                {
                    toggle();
                    getActionGridPaneListener().toggleActionClicked(action.getID(), getCurrentToggleStatus());
                }
            }
        }
        try
        {
            playActionAnimation();
        } catch (SevereException e){
            Logger.getLogger("").warning(e.getMessage());
        }
    }

    private Timeline statusIconAnimation;

    public Timeline getStatusIconAnimation() {
        return statusIconAnimation;
    }

    public ActionGridPaneListener getActionGridPaneListener() {
        return actionGridPaneListener;
    }

    private int size;
    private ActionGridPaneListener actionGridPaneListener;
    private ClientListener clientListener;

    public ActionBox(int size, ExceptionAndAlertHandler exceptionAndAlertHandler,
                     ClientListener clientListener, ActionGridPaneListener actionGridPaneListener, int row, int col)
    {
        this.actionGridPaneListener = actionGridPaneListener;
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.size = size;
        this.row = row;
        this.col = col;
        this.clientListener = clientListener;
        this.logger = Logger.getLogger("");

        baseInit();
    }

    public Logger getLogger() 
    {
        return logger;
    }

    public void setIcon(byte[] iconByteArray)
    {
        removeFontIcon();

        if(iconByteArray == null)
        { 
            getStyleClass().remove("action_box_icon_present");
            getStyleClass().add("action_box_icon_not_present");
            setBackground(null);
        }
        else
        {
            getStyleClass().add("action_box_icon_present");
            getStyleClass().remove("action_box_icon_not_present");


            setBackground(
                    new Background(
                            new BackgroundImage(new Image(
                                    new ByteArrayInputStream(iconByteArray), size, size, false, true
                            ), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,

                                    new BackgroundSize(100, 100, true, true, true, false))
                    )
            );
        }
    }

    private Action action = null;
    
    public Action getAction() {
        return action;
    }

    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private String parent;

    public String getStreamPiParent() {
        return parent;
    }

    public void setStreamPiParent(String parent) {
        this.parent = parent;
    }

    public void setAction(Action action)
    {
        this.action = action;
    }

    public void init()
    {
        setBackground(null);
        setStyle(null);
        displayTextLabel.setStyle(null);


        if(getAction().isShowDisplayText())
        {
            setDisplayTextAlignment(action.getDisplayTextAlignment());
            setDisplayTextFontColourAndSize(action.getDisplayTextFontColourHex());
            setDisplayTextLabel(getAction().getDisplayText());
        }
        else
            setDisplayTextLabel("");

        setBackgroundColour(action.getBgColourHex());

        try
        {
            if(action.getActionType() == ActionType.TOGGLE)
            {
                toggle(getCurrentToggleStatus());
            }
            else
            {
                if(action.isHasIcon())
                {
                    if(!action.getCurrentIconState().isBlank())
                    {
                        setIcon(action.getCurrentIcon());
                    }
                }
                else
                {
                    setIcon(null);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void setCurrentToggleStatus(boolean currentToggleStatus)
    {
        if(getAction() != null)
            getAction().setCurrentToggleStatus(currentToggleStatus);
    }

    public boolean getCurrentToggleStatus()
    {
        if(getAction() == null)
            return false;

        return getAction().getCurrentToggleStatus();
    }

    public void toggle()
    {
        setCurrentToggleStatus(!getCurrentToggleStatus());

        toggle(getCurrentToggleStatus());
    }

    public void toggle(boolean isON)
    {
        String[] toggleStatesHiddenStatus = action.getCurrentIconState().split("__");

        boolean isToggleOffHidden = toggleStatesHiddenStatus[0].equals("true");
        boolean isToggleOnHidden = toggleStatesHiddenStatus[1].equals("true");

        if(isON) // ON
        {
            if(action.isHasIcon())
            {
                boolean isToggleOnPresent = action.getIcons().containsKey("toggle_on");

                if(isToggleOnPresent)
                {
                    if(isToggleOnHidden)
                    {
                        setDefaultToggleIcon(true);
                    }
                    else
                    {
                        setIcon(action.getIcons().get("toggle_on"));
                    }
                }
                else
                {
                    setDefaultToggleIcon(true);
                }
            }
            else
            {
                setDefaultToggleIcon(true);
            }
        }
        else // OFF
        {
            if(action.isHasIcon())
            {
                boolean isToggleOffPresent = action.getIcons().containsKey("toggle_off");

                if(isToggleOffPresent)
                {
                    if(isToggleOffHidden)
                    {
                        setDefaultToggleIcon(false);
                    }
                    else
                    {
                        setIcon(action.getIcons().get("toggle_off"));
                    }
                }
                else
                {
                    setDefaultToggleIcon(false);
                }
            }
            else
            {
                setDefaultToggleIcon(false);
            }
        }
    }


    public void setDefaultToggleIcon(boolean isToggleOn)
    {
        String styleClass;

        if(isToggleOn)
        {
            styleClass = "action_box_toggle_on";
        }
        else
        {
            styleClass = "action_box_toggle_off";
        }


        setBackground(null);


        if(fontIcon!=null)
        {
            fontIcon.getStyleClass().removeIf(s -> s.equals("action_box_toggle_off") || s.equals("action_box_toggle_on"));
        }
        else
        {
            fontIcon = new FontIcon();
            fontIcon.setIconSize((int) (size * 0.8));
            getChildren().add(fontIcon);
        }

        fontIcon.getStyleClass().add(styleClass);

        fontIcon.toBack();
    }

    public void removeFontIcon()
    {
        if(fontIcon!=null)
        {
            getChildren().remove(fontIcon);
            fontIcon = null;
        }
    }

    FontIcon fontIcon = null;

    public void animateStatus()
    {
        statusIcon.toFront();
        statusIconAnimation.play();
    }

    public void setDisplayTextLabel(String text)
    {
        displayTextLabel.setText(text);
    }

    public void setDisplayTextAlignment(DisplayTextAlignment displayTextAlignment)
    {
        if(displayTextAlignment == DisplayTextAlignment.CENTER)
            displayTextLabel.setAlignment(Pos.CENTER);
        else if (displayTextAlignment == DisplayTextAlignment.BOTTOM)
            displayTextLabel.setAlignment(Pos.BOTTOM_CENTER);
        else if (displayTextAlignment == DisplayTextAlignment.TOP)
            displayTextLabel.setAlignment(Pos.TOP_CENTER);
    }

    public void setDisplayTextFontColourAndSize(String colour)
    {
        String totalStyle = "";
        if(!colour.isEmpty())
        {
            totalStyle+="-fx-text-fill : "+colour+";";
        }

        if(getAction().getNameFontSize() > -1)
        {
            totalStyle+="-fx-font-size: "+getAction().getNameFontSize()+";";
        }

        if(!totalStyle.isBlank())
        {
            displayTextLabel.setStyle(totalStyle);
        }
    }
    
    public void playActionAnimation() throws SevereException
    {
        Config config = Config.getInstance();
        switch(config.getCurrentAnimationName())
        {
            case "None":
                break;
            case "Flip":
                new Flip(getChildren().get(1)).play();
                break;
            case "Bounce":
                new Bounce(getChildren().get(1)).play();
                break;
            case "Jack In The Box":
                new JackInTheBox(getChildren().get(1)).play();
                break;
            case "Swing":
                new Swing(getChildren().get(1)).play();
                break;
            case "Jello":
                new Jello(getChildren().get(1)).play();
                break;
            case "Pulse":
                new Pulse(getChildren().get(1)).play();
                break;
            case "RubberBand":
                new RubberBand(getChildren().get(1)).play();
                break;
            case "Shake":
                new Shake(getChildren().get(1)).play();
                break;
            case "Tada":
                new Tada(getChildren().get(1)).play();
                break;
            case "Wobble":
                new Wobble(getChildren().get(1)).play();
                break;
            default:
                Logger.getLogger("").warning("Invalid Option/n Please contact quimodotcom to solve this error!");
        }
    }

    public void setBackgroundColour(String colour)
    {
        if(!colour.isEmpty())
            setStyle("-fx-background-color : "+colour);
    }
}
