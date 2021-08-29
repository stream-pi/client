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
import javafx.scene.transform.Rotate;

public class Swing extends AnimationFX
{
    private Rotate rotation;
    
    public Swing(final Node node) {
        super(node);
    }
    
    public Swing() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.rotation.setAngle(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        (this.rotation = new Rotate()).setPivotX(this.getNode().getLayoutBounds().getWidth() / 2.0);
        this.rotation.setPivotY(-this.getNode().getLayoutBounds().getHeight());
        this.getNode().getTransforms().add((Object)this.rotation);
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.rotation.angleProperty(), (Object)0, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(200.0), new KeyValue[] { new KeyValue((WritableValue)this.rotation.angleProperty(), (Object)15, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(400.0), new KeyValue[] { new KeyValue((WritableValue)this.rotation.angleProperty(), (Object)(-10), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(600.0), new KeyValue[] { new KeyValue((WritableValue)this.rotation.angleProperty(), (Object)5, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(800.0), new KeyValue[] { new KeyValue((WritableValue)this.rotation.angleProperty(), (Object)(-5), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.rotation.angleProperty(), (Object)0, AnimateFXInterpolator.EASE) }) }));
    }
}
