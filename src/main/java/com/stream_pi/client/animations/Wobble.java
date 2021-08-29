// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.animations;

import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.Node;

public class Wobble extends AnimationFX
{
    public Wobble(final Node node) {
        super(node);
    }
    
    public Wobble() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.getNode().setTranslateX(0.0);
        this.getNode().setRotate(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)0, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)0, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(150.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-0.25 * this.getNode().getBoundsInParent().getWidth()), AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-5), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(300.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(0.2 * this.getNode().getBoundsInParent().getWidth()), AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)3, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(450.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-0.15 * this.getNode().getBoundsInParent().getWidth()), AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-3), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(600.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(0.1 * this.getNode().getBoundsInParent().getWidth()), AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)2, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(750.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)(-0.05 * this.getNode().getBoundsInParent().getWidth()), AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-1), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().translateXProperty(), (Object)0, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)0, AnimateFXInterpolator.EASE) }) }));
    }
}
