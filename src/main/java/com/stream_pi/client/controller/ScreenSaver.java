package com.stream_pi.client.controller;

import com.stream_pi.client.window.Base;
import com.stream_pi.util.exception.SevereException;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenSaver extends StackPane
{
    private boolean isEnabled = false;
    private Timer timer;

    private Timeline showScreenSaverTimeline;
    private long timeout;

    public ScreenSaver(boolean isEnabled, Base base, int timeout)
    {
        this.isEnabled = isEnabled;
        this.timeout = timeout* 1000L;


        setOpacity(0);
        getStyleClass().add("screensaver");

        if(isEnabled)
        {

            showScreenSaverTimeline = new Timeline();
            showScreenSaverTimeline.setCycleCount(1);


            showScreenSaverTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(0.0D),
                            new KeyValue(opacityProperty(),
                                    0.0D, Interpolator.EASE_IN)),
                    new KeyFrame(Duration.seconds(15D),
                            new KeyValue(opacityProperty(),
                                    1.0D, Interpolator.LINEAR))
            );

            startTimer();

            base.setOnMouseClicked(mouseEvent -> {
                close();
                restartTimer();
            });


        }
    }



    public void stop()
    {
        stopTimer();
        setOpacity(0);
        toBack();
    }

    public boolean isEnabled() {
        return isEnabled;
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


            setOpacity(0.0);
            toBack();
        });

        restartTimer();
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
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
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                show();
            }
        },timeout);
    }
}
