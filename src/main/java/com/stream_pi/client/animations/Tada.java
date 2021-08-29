// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.animations;

import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.transform.Rotate;
import javafx.scene.Node;

public class Tada extends AnimationFX
{
    public Tada(final Node node) {
        super(node);
    }
    
    public Tada() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.getNode().setScaleX(1.0);
        this.getNode().setScaleY(1.0);
        this.getNode().setScaleZ(1.0);
        this.getNode().setRotate(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.getNode().setRotationAxis(Rotate.Z_AXIS);
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)0, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(100.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)0.9, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)0.9, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)0.9, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-3), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(200.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)0.9, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)0.9, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)0.9, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-3), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(300.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)3, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(400.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-3), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(500.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)3, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(600.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-3), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(700.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)3, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(800.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)(-3), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(900.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)3, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)0, AnimateFXInterpolator.EASE) }) }));
    }
}
