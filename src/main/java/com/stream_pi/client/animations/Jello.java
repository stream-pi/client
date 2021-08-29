// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.animations;

import javafx.geometry.Bounds;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.Node;
import javafx.scene.transform.Shear;

public class Jello extends AnimationFX
{
    private Shear shear;
    
    public Jello(final Node node) {
        super(node);
    }
    
    public Jello() {
    }
    
    @Override
    AnimationFX resetNode() {
        this.shear.setX(0.0);
        this.shear.setY(0.0);
        return this;
    }
    
    @Override
    void initTimeline() {
        this.shear = new Shear();
        final Bounds bounds = this.getNode().getLayoutBounds();
        this.shear.setPivotX(bounds.getWidth() / 2.0);
        this.shear.setPivotY(bounds.getHeight() / 2.0);
        this.getNode().getTransforms().add((Object)this.shear);
        this.setTimeline(new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(0.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)0), new KeyValue((WritableValue)this.shear.yProperty(), (Object)0) }), new KeyFrame(Duration.millis(111.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)0.125), new KeyValue((WritableValue)this.shear.yProperty(), (Object)0.125) }), new KeyFrame(Duration.millis(222.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)(-0.625)), new KeyValue((WritableValue)this.shear.yProperty(), (Object)(-0.625)) }), new KeyFrame(Duration.millis(333.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)0.3125), new KeyValue((WritableValue)this.shear.yProperty(), (Object)0.3125) }), new KeyFrame(Duration.millis(444.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)(-0.15625)), new KeyValue((WritableValue)this.shear.yProperty(), (Object)(-0.15625)) }), new KeyFrame(Duration.millis(555.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)0.078125), new KeyValue((WritableValue)this.shear.yProperty(), (Object)0.078125) }), new KeyFrame(Duration.millis(666.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)(-0.0390625)), new KeyValue((WritableValue)this.shear.yProperty(), (Object)(-0.0390625)) }), new KeyFrame(Duration.millis(777.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)0.01953125), new KeyValue((WritableValue)this.shear.yProperty(), (Object)0.01953125) }), new KeyFrame(Duration.millis(888.0), new KeyValue[] { new KeyValue((WritableValue)this.shear.xProperty(), (Object)0), new KeyValue((WritableValue)this.shear.yProperty(), (Object)0) }) }));
    }
}
