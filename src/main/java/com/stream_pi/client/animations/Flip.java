// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.animations;

import javafx.scene.ParallelCamera;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.transform.Rotate;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Node;

public class Flip extends AnimationFX
{
    public Flip(final Node node) {
        super(node);
    }
    
    public Flip() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.getNode().setRotate(0.0);
        this.getNode().setScaleX(1.0);
        this.getNode().setScaleY(1.0);
        this.getNode().setScaleZ(1.0);
        this.getNode().setTranslateZ(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.getNode().getScene().setCamera((Camera)new PerspectiveCamera());
        this.getNode().setRotationAxis(Rotate.Y_AXIS);
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)360, Interpolator.EASE_OUT) }), new KeyFrame(Duration.millis(400.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)190, Interpolator.EASE_OUT), new KeyValue((WritableValue)this.getNode().translateZProperty(), (Object)(-150), Interpolator.EASE_OUT) }), new KeyFrame(Duration.millis(500.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)170, Interpolator.EASE_IN), new KeyValue((WritableValue)this.getNode().translateZProperty(), (Object)(-150), Interpolator.EASE_IN) }), new KeyFrame(Duration.millis(800.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)0.95, Interpolator.EASE_IN), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)0.95, Interpolator.EASE_IN), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)0.95, Interpolator.EASE_IN) }), new KeyFrame(Duration.millis(1000.0), new KeyValue[] { new KeyValue((WritableValue)this.getNode().rotateProperty(), (Object)0, Interpolator.EASE_IN), new KeyValue((WritableValue)this.getNode().scaleXProperty(), (Object)1, Interpolator.EASE_IN), new KeyValue((WritableValue)this.getNode().scaleYProperty(), (Object)1, Interpolator.EASE_IN), new KeyValue((WritableValue)this.getNode().scaleZProperty(), (Object)1, Interpolator.EASE_IN), new KeyValue((WritableValue)this.getNode().translateZProperty(), (Object)0, Interpolator.EASE_IN) }) }));
        this.getTimeline().setOnFinished(event -> this.getNode().getScene().setCamera((Camera)new ParallelCamera()));
    }
}
