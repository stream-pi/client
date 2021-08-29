// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.controller;

import javafx.scene.input.MouseEvent;
import java.util.TimerTask;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import com.stream_pi.client.window.Base;
import javafx.animation.Timeline;
import java.util.Timer;
import javafx.scene.layout.StackPane;

public class ScreenSaver extends StackPane
{
    private Timer timer;
    private Timeline showScreenSaverTimeline;
    private long timeout;
    
    public ScreenSaver(final Base base, final int timeout) {
        this.timeout = timeout * 1000L;
        this.setOpacity(0.0);
        this.getStyleClass().add((Object)"screensaver");
        (this.showScreenSaverTimeline = new Timeline()).setCycleCount(1);
        this.showScreenSaverTimeline.getKeyFrames().addAll((Object[])new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.opacityProperty(), (Object)0.0, Interpolator.EASE_IN) }), new KeyFrame(Duration.seconds(15.0), new KeyValue[] { new KeyValue((WritableValue)this.opacityProperty(), (Object)1.0, Interpolator.LINEAR) }) });
        this.startTimer();
        base.setOnMouseClicked(mouseEvent -> this.restart());
    }
    
    public void restart() {
        this.close();
        this.restartTimer();
    }
    
    public void stop() {
        this.stopTimer();
        this.setOpacity(0.0);
        this.toBack();
    }
    
    private void show() {
        Platform.runLater(() -> {
            this.setOpacity(0.0);
            this.toFront();
            this.showScreenSaverTimeline.play();
        });
    }
    
    private void close() {
        Platform.runLater(() -> {
            if (this.showScreenSaverTimeline.getStatus() == Animation.Status.RUNNING) {
                this.showScreenSaverTimeline.stop();
            }
            this.setOpacity(0.0);
            this.toBack();
            return;
        });
        this.restartTimer();
    }
    
    public void setTimeout(final int seconds) {
        this.timeout = seconds * 1000L;
    }
    
    public void restartTimer() {
        this.stopTimer();
        this.startTimer();
    }
    
    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }
    }
    
    private void startTimer() {
        (this.timer = new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                ScreenSaver.this.show();
            }
        }, this.timeout);
    }
}
