// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.animations;

import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.Node;

public class Bounce extends AnimationFX
{
    public Bounce(final Node node) {
        super(node);
    }
    
    public Bounce() {
    }
    
    public AnimationFX resetNode() {
        this.getNode().setTranslateY(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateYProperty(), (Object)0, Interpolator.SPLINE(0.215, 0.61, 0.355, 1.0)) }), new KeyFrame(Duration.millis(400.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateYProperty(), (Object)(-30), Interpolator.SPLINE(0.755, 0.05, 0.855, 0.06)) }), new KeyFrame(Duration.millis(550.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateYProperty(), (Object)0, Interpolator.SPLINE(0.215, 0.61, 0.355, 1.0)) }), new KeyFrame(Duration.millis(700.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateYProperty(), (Object)(-15), Interpolator.SPLINE(0.755, 0.05, 0.855, 0.06)) }), new KeyFrame(Duration.millis(800.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateYProperty(), (Object)0, Interpolator.SPLINE(0.215, 0.61, 0.355, 1.0)) }), new KeyFrame(Duration.millis(900.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateYProperty(), (Object)(-5), Interpolator.SPLINE(0.755, 0.05, 0.855, 0.06)) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateYProperty(), (Object)0, Interpolator.SPLINE(0.215, 0.61, 0.355, 1.0)) }) }));
    }
}
