// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.animations;

import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.Node;

public class RubberBand extends AnimationFX
{
    public RubberBand(final Node node) {
        super(node);
    }
    
    public RubberBand() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.getNode().setScaleX(1.0);
        this.getNode().setScaleY(1.0);
        this.getNode().setScaleZ(1.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(300.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.25, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)0.75, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(400.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)0.75, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.25, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(500.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.15, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)0.85, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(650.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)0.95, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.05, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(750.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.05, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)0.95, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE) }) }));
    }
}
