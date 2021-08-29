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
import javafx.scene.transform.Rotate;

public class JackInTheBox extends AnimationFX
{
    private Rotate rotate;
    
    public JackInTheBox(final Node node) {
        super(node);
    }
    
    public JackInTheBox() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.getNode().setScaleX(1.0);
        this.getNode().setScaleZ(1.0);
        this.getNode().setScaleY(1.0);
        this.getNode().setOpacity(1.0);
        this.rotate.setAngle(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.rotate = new Rotate(30.0, this.getNode().getBoundsInParent().getWidth() / 2.0, this.getNode().getBoundsInParent().getHeight());
        this.getNode().getTransforms().add((Object)this.rotate);
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.rotate.angleProperty(), (Object)30, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)0.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)0.1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().opacityProperty(), (Object)0, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(500.0), new KeyValue[] { new KeyValue((WritableValue)this.rotate.angleProperty(), (Object)(-10), AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(700.0), new KeyValue[] { new KeyValue((WritableValue)this.rotate.angleProperty(), (Object)3, AnimateFXInterpolator.EASE) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.rotate.angleProperty(), (Object)0, AnimateFXInterpolator.EASE), new KeyValue((WritableValue)this.getNode().opacityProperty(), (Object)1, AnimateFXInterpolator.EASE) }) }));
    }
}
