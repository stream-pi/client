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

public class Shake extends AnimationFX
{
    public Shake(final Node node) {
        super(node);
    }
    
    public Shake() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.getNode().setTranslateX(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)0, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(100.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-10), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(200.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)10, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(300.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-10), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(400.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)10, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(500.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-10), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(600.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)10, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(700.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-10), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(800.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)10, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(900.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-10), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)0, AnimateFXInterpolator.EASE) }) }));
    }
}
