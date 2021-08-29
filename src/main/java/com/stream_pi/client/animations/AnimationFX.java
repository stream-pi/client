// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.animations;

import javafx.animation.Animation;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.animation.Timeline;

public abstract class AnimationFX
{
    public static final int INDEFINITE = -1;
    private Timeline timeline;
    private boolean reset;
    private Node node;
    private AnimationFX nextAnimation;
    private boolean hasNextAnimation;
    
    public AnimationFX(final Node node) {
        this.setNode(node);
    }
    
    public AnimationFX() {
        this.hasNextAnimation = false;
        this.reset = false;
    }
    
    private AnimationFX onFinished() {
        if (this.reset) {
            this.resetNode();
        }
        if (this.nextAnimation != null) {
            this.nextAnimation.play();
        }
        return this;
    }
    
    public AnimationFX playOnFinished(final AnimationFX animation) {
        this.setNextAnimation(animation);
        return this;
    }
    
    public AnimationFX setResetOnFinished(final boolean reset) {
        this.reset = reset;
        return this;
    }
    
    public void play() {
        this.timeline.play();
    }
    
    public AnimationFX stop() {
        this.timeline.stop();
        return this;
    }
    
    abstract AnimationFX resetNode();
    
    abstract void initTimeline();
    
    public Timeline getTimeline() {
        return this.timeline;
    }
    
    public void setTimeline(final Timeline timeline) {
        this.timeline = timeline;
    }
    
    public boolean isResetOnFinished() {
        return this.reset;
    }
    
    protected void setReset(final boolean reset) {
        this.reset = reset;
    }
    
    public Node getNode() {
        return this.node;
    }
    
    public void setNode(final Node node) {
        this.node = node;
        this.initTimeline();
        this.timeline.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals((Object)Animation.Status.STOPPED)) {
                this.onFinished();
            }
        });
    }
    
    public AnimationFX getNextAnimation() {
        return this.nextAnimation;
    }
    
    protected void setNextAnimation(final AnimationFX nextAnimation) {
        this.hasNextAnimation = true;
        this.nextAnimation = nextAnimation;
    }
    
    public boolean hasNextAnimation() {
        return this.hasNextAnimation;
    }
    
    protected void setHasNextAnimation(final boolean hasNextAnimation) {
        this.hasNextAnimation = hasNextAnimation;
    }
    
    public AnimationFX setCycleCount(final int value) {
        this.timeline.setCycleCount(value);
        return this;
    }
    
    public AnimationFX setSpeed(final double value) {
        this.timeline.setRate(value);
        return this;
    }
    
    public AnimationFX setDelay(final Duration value) {
        this.timeline.setDelay(value);
        return this;
    }
    
    public final void setOnFinished(final EventHandler<ActionEvent> value) {
        this.timeline.setOnFinished((EventHandler)value);
    }
}
