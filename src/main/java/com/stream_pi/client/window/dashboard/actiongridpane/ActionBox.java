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

package com.stream_pi.client.window.dashboard.actiongridpane;

import com.gluonhq.attach.vibration.VibrationService;
import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.action.ActionType;
import com.stream_pi.action_api.action.DisplayTextAlignment;
import com.stream_pi.action_api.actionproperty.gaugeproperties.GaugeProperties;
import com.stream_pi.action_api.actionproperty.gaugeproperties.SerializableColor;
import com.stream_pi.action_api.externalplugin.inputevent.*;
import com.stream_pi.client.controller.ClientExecutorService;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.profile.ClientAction;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionGridPaneListener;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Gauge;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.controlsfx.control.spreadsheet.Grid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class ActionBox extends StackPane
{

    private Label displayTextLabel;

    private Gauge gauge;

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
        setIcon(null);
        setCurrentToggleStatus(false);
        setBackground(Background.EMPTY);
        removeFontIcon();
        getChildren().clear();
        getStyleClass().clear();
        gauge = null;
        baseInit();
    }

    private FontIcon statusIcon;

    private void initMouseAndTouchListeners()
    {
        addEventFilter(MouseEvent.ANY, this::handleInputEvent);
        addEventFilter(SwipeEvent.ANY, this::handleInputEvent);
        addEventFilter(RotateEvent.ANY, this::handleInputEvent);
        addEventFilter(TouchEvent.ANY, this::handleInputEvent);
        addEventFilter(ZoomEvent.ANY, this::handleInputEvent);
    }

    private boolean isClick(StreamPiInputEvent inputEvent)
    {
        return inputEvent instanceof StreamPiMouseEvent && inputEvent.getEventType() == MouseEvent.MOUSE_CLICKED;
    }

    private final AtomicBoolean isNotConnectedPromptShowing = new AtomicBoolean(false);
    private void handleInputEvent(InputEvent rawInputEvent)
    {
        if (getAction() == null)
        {
            return;
        }

        // ignore following input events for NOW:

        if(List.of(MouseEvent.MOUSE_DRAGGED,
                MouseEvent.MOUSE_MOVED,
                MouseEvent.MOUSE_ENTERED,
                MouseEvent.MOUSE_ENTERED_TARGET,
                MouseEvent.MOUSE_EXITED_TARGET,
                MouseEvent.MOUSE_EXITED).contains(rawInputEvent.getEventType()))
        {
            return;
        }

        StreamPiInputEvent inputEvent;

        if(rawInputEvent instanceof MouseEvent mouseEvent)
        {
            inputEvent = new StreamPiMouseEvent(mouseEvent.getEventType(), mouseEvent.getClickCount(), mouseEvent.getButton());
        }
        else if(rawInputEvent instanceof SwipeEvent swipeEvent)
        {
            inputEvent = new StreamPiSwipeEvent(swipeEvent.getEventType(), swipeEvent.getTouchCount());
        }
        else if(rawInputEvent instanceof RotateEvent rotateEvent)
        {
            inputEvent = new StreamPiRotateEvent(rotateEvent.getEventType(), rotateEvent.getAngle(), rotateEvent.getTotalAngle());
        }
        else if(rawInputEvent instanceof TouchEvent touchEvent)
        {
            inputEvent = new StreamPiTouchEvent(touchEvent.getEventType(), touchEvent.getTouchCount(), touchEvent.getEventSetId());
        }
        else if(rawInputEvent instanceof ZoomEvent zoomEvent)
        {
            inputEvent = new StreamPiZoomEvent(zoomEvent.getEventType(), zoomEvent.getZoomFactor(), zoomEvent.getTotalZoomFactor());
        }
        else
        {
            getLogger().severe("No handler for "+rawInputEvent.getEventType()+"! Ignoring ...");
            return;
        }


        try
        {
            if(isClick(inputEvent))
            {
                if(Config.getInstance().isVibrateOnActionClicked())
                {
                    VibrationService.create().ifPresent(VibrationService::vibrate);
                }
            }

            if(action.getActionType() == ActionType.FOLDER && isClick(inputEvent))
            {
                getActionGridPaneListener().renderFolder(action.getID());
            }
            else
            {
                if(!getActionGridPaneListener().isConnected())
                {
                    if(Config.getInstance().isTryConnectingWhenActionClicked())
                    {
                        clientListener.setupClientConnection(()->handleInputEvent(rawInputEvent));
                    }
                    else
                    {
                        if (!isNotConnectedPromptShowing.get())
                        {
                            exceptionAndAlertHandler.handleMinorException(new MinorException("Not Connected", "Not Connected to any Server"));
                            isNotConnectedPromptShowing.set(true);
                        }
                    }
                    return;
                }

                isNotConnectedPromptShowing.set(false);

                if(action.getActionType() == ActionType.NORMAL || action.getActionType() == ActionType.TOGGLE)
                {
                    clientListener.getClient().sendInputEvent(clientListener.getCurrentProfile().getID(), getAction().getID(), inputEvent);
                }


                if(isClick(inputEvent))
                {
                    if(action.getActionType() == ActionType.COMBINE)
                    {
                        clientListener.getClient().sendInputEvent(clientListener.getCurrentProfile().getID(), getAction().getID(), inputEvent);
                    }
                    else if(action.getActionType() == ActionType.TOGGLE)
                    {
                        toggle();
                        clientListener.getClient().setToggleStatus(clientListener.getCurrentProfile().getID(), getAction().getID(), getCurrentToggleStatus());
                    }
                }
            }
        }
        catch (SevereException e)
        {
            exceptionAndAlertHandler.handleSevereException(e);
        }
    }

    public void baseInit()
    {
        configureSize(profileDefaultSize, profileDefaultSize);

        GridPane.setRowSpan(this, 1);
        GridPane.setColumnSpan(this, 1);

        getStyleClass().add("action_box");
        getStyleClass().add("action_box_"+row+"_"+col);
        getStyleClass().add("action_box_unoccupied");

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
        statusIcon.setCacheHint(CacheHint.QUALITY);

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



        getChildren().addAll(statusIcon, displayTextLabel);

        statusIconAnimation.setOnFinished(event -> statusIcon.toBack());
    }

    private Timeline statusIconAnimation;

    public ActionGridPaneListener getActionGridPaneListener() {
        return actionGridPaneListener;
    }

    private double height, width, profileDefaultSize;
    private ActionGridPaneListener actionGridPaneListener;
    private ClientListener clientListener;

    private double profileDisplayTextFontSize;

    public ActionBox(double size, ExceptionAndAlertHandler exceptionAndAlertHandler,
                     ClientListener clientListener, ActionGridPaneListener actionGridPaneListener, int row, int col, double profileDisplayTextFontSize)
    {
        this.actionGridPaneListener = actionGridPaneListener;
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.height = size;
        this.width = size;
        this.profileDefaultSize = size;
        this.row = row;
        this.col = col;
        this.clientListener = clientListener;
        this.logger = Logger.getLogger("");
        this.profileDisplayTextFontSize = profileDisplayTextFontSize;

        this.managedProperty().bind(visibleProperty());

        setCache(true);
        setCacheHint(CacheHint.QUALITY);

        baseInit();
        initMouseAndTouchListeners();
    }

    private int iconSize;
    public void configureSize(double width, double height)
    {
        this.height = height;
        this.width = width;

        setMinSize(width, height);
        setMaxSize(width, height);
        setPrefSize(width, height);


        iconSize = (int) Math.min(height, width);
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
            setBackground(null);
        }
        else
        {
            getStyleClass().add("action_box_icon_present");


            setBackground(
                    new Background(
                            new BackgroundImage(new Image(
                                    new ByteArrayInputStream(iconByteArray), width, height, true, true
                            ), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                    new BackgroundSize(100, 100, true, true, true, false))
                    )
            );
        }
    }

    private ClientAction action = null;
    
    public ClientAction getAction()
    {
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

    public void setAction(ClientAction action)
    {
        this.action = action;
    }

    public void init()
    {
        statusIcon.setIconSize((int) (iconSize * 0.8));
        displayTextLabel.setStyle(null);

        getStyleClass().add("action_box_"+getAction().getID());
        getStyleClass().add("action_box_type_"+getAction().getActionType());

        getStyleClass().remove("action_box_unoccupied");
        getStyleClass().add("action_box_occupied");

        if (getAction().getUniqueID()!=null) // NORMAL, TOGGLE, GAUGE
        {
            getStyleClass().add("action_box_"+getAction().getUniqueID().replace(".","-"));
        }

        try
        {
            updateDisplayTextLabel();

            if(getAction().isShowDisplayText())
            {
                setDisplayTextAlignment(getAction().getDisplayTextAlignment());
                setDisplayTextFontColourAndSize(getAction().getDisplayTextFontColourHex());
            }
            else
            {
                clearDisplayTextLabel();
            }

            if (getAction().getActionType() == ActionType.GAUGE)
            {
                if (gauge == null)
                {
                    gauge = new Gauge();
                    gauge.addEventFilter(MouseEvent.ANY, this::handleInputEvent);
                    gauge.addEventFilter(SwipeEvent.ANY, this::handleInputEvent);
                    gauge.addEventFilter(RotateEvent.ANY, this::handleInputEvent);
                    gauge.addEventFilter(TouchEvent.ANY, this::handleInputEvent);
                    gauge.addEventFilter(ZoomEvent.ANY, this::handleInputEvent);
                    gauge.setAnimated(getAction().isGaugeAnimated());

                    getChildren().add(gauge);
                }


                setDisplayTextAlignment(getAction().getDisplayTextAlignment());
                setDisplayTextFontColourAndSize(getAction().getDisplayTextFontColourHex());


                setGaugeTitle(getAction().getDisplayText());

                updateGauge();

                setGaugeVisible(clientListener.isConnected());

            }

            if(getAction().getActionType() == ActionType.TOGGLE)
            {
                toggle(getCurrentToggleStatus());
            }
            else
            {
                if(getAction().isHasIcon())
                {
                    if(!getAction().getCurrentIconState().isBlank())
                    {
                        setIcon(getAction().getCurrentIcon());
                    }
                }
                else
                {
                    setIcon(null);
                }
            }

            setBackgroundColour(getAction().getBgColourHex());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setGaugeTextColour(String colorStr)
    {
        Color color = Color.valueOf("#242424");
        if (!colorStr.isEmpty())
        {
            color = Color.valueOf(colorStr);
        }

        gauge.setTitleColor(color);
        gauge.setSubTitleColor(color);
        gauge.setUnitColor(color);
        gauge.setValueColor(color);
    }


    public void setGaugeBarColor(SerializableColor newCol)
    {
        if (newCol != null)
        {
            gauge.setBarColor(newCol.getColor());
        }
    }

    public void setGaugeForegroundBaseColor(SerializableColor newCol)
    {
        if (newCol != null)
        {
            gauge.setForegroundBaseColor(newCol.getColor());
        }
    }


    public void updateGauge(GaugeProperties gaugeProperties)
    {
        getAction().setGaugeProperties(gaugeProperties);
        updateGauge();
    }

    public void updateGauge()
    {
        GaugeProperties gaugeProperties = getAction().getGaugeProperties();
        gauge.setSkinType(gaugeProperties.getSkinType());
        gauge.setMinValue(gaugeProperties.getMinValue());
        gauge.setMaxValue(gaugeProperties.getMaxValue());
        gauge.setSections(gaugeProperties.getSections());
        gauge.setUnit(gaugeProperties.getUnit());
        gauge.setSubTitle(gaugeProperties.getSubTitle());
        gauge.setDecimals(gaugeProperties.getDecimals());

        gauge.setSectionsVisible(gaugeProperties.isSectionsVisible());

        setGaugeForegroundBaseColor(gaugeProperties.getForegroundBaseColor());

        setGaugeBarColor(gaugeProperties.getBarColor());

        setGaugeTextColour(getAction().getDisplayTextFontColourHex());

        updateGaugeValue(gaugeProperties.getValue());

        setGaugeVisible(true);
    }

    public void updateGaugeValue(double value)
    {
        gauge.setValue(value);
    }

    public void setGaugeVisible(boolean visible)
    {
        gauge.setVisible(visible);
        displayTextLabel.setVisible(!visible);
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
            fontIcon.setIconSize((int) (iconSize * 0.8));
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

    public void updateDisplayTextLabel()
    {
        if (getAction().getTemporaryDisplayText() == null)
        {
            if(getAction().isShowDisplayText())
            {
                displayTextLabel.setText(getAction().getDisplayText());
            }
        }
        else
        {
            displayTextLabel.setText(getAction().getTemporaryDisplayText());
        }
     }

    public void clearDisplayTextLabel()
    {
        displayTextLabel.setText("");
    }

    public void setGaugeTitle(String text)
    {
        gauge.setTitle(text);
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

        if(getAction().getDisplayTextFontSize() > -1)
        {
            totalStyle+="-fx-font-size: "+getAction().getDisplayTextFontSize()+";";
        }
        else
        {
            totalStyle+="-fx-font-size: "+profileDisplayTextFontSize+";";
        }

        if(!totalStyle.isBlank())
        {
            displayTextLabel.setStyle(totalStyle);
        }
    }

    public void setBackgroundColour(String colour)
    {
        if(!colour.isEmpty())
        {
            setStyle("-fx-background-color : "+colour);
        }
    }

    public void updateTemporaryDisplayText(String displayText)
    {
        getAction().setTemporaryDisplayText(displayText);
        updateDisplayTextLabel();
    }
}


