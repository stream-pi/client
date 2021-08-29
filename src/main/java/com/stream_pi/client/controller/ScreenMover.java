// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.controller;

import java.util.TimerTask;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.Timer;

public class ScreenMover
{
    private Timer timer;
    private long interval;
    private boolean isOldLocation;
    private double originalX;
    private double originalY;
    private double changeX;
    private double changeY;
    private Stage stage;
    
    public ScreenMover(final Stage stage, final long interval, final int changeX, final int changeY) {
        this.stage = stage;
        this.changeX = changeX;
        this.changeY = changeY;
        this.originalX = stage.getX();
        this.originalY = stage.getY();
        this.isOldLocation = true;
        this.interval = interval;
        this.startTimer();
    }
    
    public void stop() {
        this.isOldLocation = true;
        this.shiftScreen();
        this.stopTimer();
    }
    
    public void restart() {
        this.stop();
        this.startTimer();
    }
    
    private void shiftScreen() {
        Platform.runLater(() -> {
            if (this.isOldLocation) {
                this.isOldLocation = false;
                this.stage.setX(this.originalX + this.changeX);
                this.stage.setY(this.originalY + this.changeY);
            }
            else {
                this.isOldLocation = true;
                this.stage.setX(this.originalX);
                this.stage.setY(this.originalY);
            }
        });
    }
    
    public void setInterval(final int seconds) {
        this.interval = seconds;
    }
    
    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }
    }
    
    private void startTimer() {
        (this.timer = new Timer()).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ScreenMover.this.shiftScreen();
            }
        }, this.interval, this.interval);
    }
}
