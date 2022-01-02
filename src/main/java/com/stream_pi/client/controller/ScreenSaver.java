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

package com.stream_pi.client.controller;

import com.stream_pi.client.window.Base;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.exception.SevereException;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenSaver extends StackPane
{
    private Timer timer;

    private Timeline showScreenSaverTimeline;
    private long timeout;
    private Logger logger;
    private final String rpiOfficialScreenBacklightPowerLocation = "/sys/class/backlight/rpi_backlight/bl_power";
    private boolean isChangeRpiScreenBacklightPower = false;

    public ScreenSaver(Base base, int timeout, Logger logger)
    {
        this.timeout = timeout* 1000L;
        this.logger = logger;

        setOpacity(0);
        getStyleClass().add("screensaver");


        showScreenSaverTimeline = new Timeline();
        showScreenSaverTimeline.setCycleCount(1);
        showScreenSaverTimeline.setOnFinished(actionEvent -> setRaspberryPiBacklightState(false));


        showScreenSaverTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(0.0D),
                        new KeyValue(opacityProperty(),
                                0.0D, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(15D),
                        new KeyValue(opacityProperty(),
                                1.0D, Interpolator.LINEAR))
        );

        isChangeRpiScreenBacklightPower = new File(rpiOfficialScreenBacklightPowerLocation).exists();

        logger.info("Is Rpi Screen detected ? "+isChangeRpiScreenBacklightPower);

        startTimer();

        base.setOnMouseClicked(mouseEvent -> {
            restart();
        });

    }

    public void restart()
    {
        close();
        restartTimer();
    }



    public void stop()
    {
        stopTimer();
        setOpacity(0);
        toBack();
    }


    private void show()
    {
        Platform.runLater(()->{
            setOpacity(0);
            toFront();
            showScreenSaverTimeline.play();
        });
    }

    private void close()
    {
        Platform.runLater(()->{
            if(showScreenSaverTimeline.getStatus() == Animation.Status.RUNNING)
            {
                showScreenSaverTimeline.stop();
            }

            setRaspberryPiBacklightState(true);
            setOpacity(0.0);
            toBack();
        });

        restartTimer();
    }

    public void setTimeout(int seconds)
    {
        this.timeout = seconds* 1000L;
    }

    public void restartTimer()
    {
        stopTimer();
        startTimer();
    }

    private void stopTimer()
    {
        if(timer != null)
        {
            timer.cancel();
            timer.purge();
        }
    }

    private void startTimer()
    {
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                show();
            }
        },timeout);
    }

    private void setRaspberryPiBacklightState(boolean state)
    {
        if (!isChangeRpiScreenBacklightPower)
            return;

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rpiOfficialScreenBacklightPowerLocation));

            if(state) // ON
            {
                writer.write("0");
            }
            else // OFF
            {
                writer.write("1");
            }

            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();

            logger.warning("Unable to change backlight power ...");
            logger.log(Level.WARNING, e.getMessage(), e);

            logger.warning("Disable backlight changer ...");
            isChangeRpiScreenBacklightPower = false;
        }
    }
}
