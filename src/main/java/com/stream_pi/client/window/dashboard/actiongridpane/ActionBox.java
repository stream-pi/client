package com.stream_pi.client.window.dashboard.actiongridpane;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
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
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

public class ActionBox extends StackPane{

    private Label displayTextLabel;

    private int row;
    private int col;

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    

    public void clear()
    {   
        setAction(null);
        setBackground(Background.EMPTY);
        setStyle(null);
        getChildren().clear();
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
        getStyleClass().add("action_box_icon_not_present");
        getStyleClass().add("action_box_"+row+"_"+col);

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
        setCacheHint(CacheHint.SPEED);
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
                if(action.getActionType() == ActionType.COMBINE)
                {
                    getActionGridPaneListener().combineActionClicked(action.getID());
                }
                else if(action.getActionType() == ActionType.NORMAL)
                {
                    getActionGridPaneListener().normalActionClicked(action.getID());
                }
            }
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

    public ActionBox(int size, ActionGridPaneListener actionGridPaneListener, int row, int col)
    {
        this.actionGridPaneListener = actionGridPaneListener;
        this.size = size;
        this.row = row;
        this.col = col;

        baseInit();
    }

    public static Action deserialize(ByteBuffer buffer) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(buffer.array());
            ObjectInputStream ois = new ObjectInputStream(is);
            return (Action) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setIcon(byte[] iconByteArray)
    {
        if(iconByteArray == null)
        { 
            getStyleClass().remove("action_box_icon_present");
            getStyleClass().add("action_box_icon_not_present");
            setBackground(Background.EMPTY);
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

    private Action action;
    
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

    public ActionBox(int size, Action action, ExceptionAndAlertHandler exceptionAndAlertHandler,
     ActionGridPaneListener actionGridPaneListener, int row, int col)
    {
        this.actionGridPaneListener = actionGridPaneListener;
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.action = action;
        this.size = size;

        this.row = row;
        this.col = col;

        baseInit();

        init();

    }

    public void setAction(Action action)
    {
        this.action = action;
    }

    public void init()
    {

        setDisplayTextFontColour(action.getDisplayTextFontColourHex());
        
        if(action.isShowDisplayText())
            setDisplayTextLabel(action.getDisplayText());
        else
            setDisplayTextLabel("");

        setDisplayTextAlignment(action.getDisplayTextAlignment());
        setBackgroundColour(action.getBgColourHex());

        if(action.isHasIcon() && action.isShowIcon())
        {
            setIcon(action.getIconAsByteArray());
        }
        else
        {
            setIcon(null);
        }


    }

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
    public void setDisplayTextFontColour(String colour)
    {   
        System.out.println("'"+colour+"'COLOR");
        if(!colour.isEmpty())
        {
            System.out.println(
                "putting ..." + Thread.currentThread().getName()
            );
            
            
            displayTextLabel.setStyle("-fx-text-fill : "+colour+";");
        }

    }


    public void setBackgroundColour(String colour)
    {
        System.out.println("COLOr : "+colour);
        if(!colour.isEmpty() && action.getIconAsByteArray() == null)
            setStyle("-fx-background-color : "+colour);
    }
}
