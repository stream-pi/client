package com.stream_pi.client.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;


/**
 * @author Loïc Sculier aka typhon0
 */
public class ShakeUpDown extends AnimationFX {

    /**
     * Create new Shake
     *
     * @param node The node to affect
     */
    public ShakeUpDown(Node node) {
        super(node);
    }

    public ShakeUpDown() {
    }

    @Override
    AnimationFX resetNode() {

        getNode().setTranslateX(0);
        return this;
    }

    @Override
    void initTimeline() {
        setTimeline(new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(getNode().translateYProperty(), 0, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(getNode().translateYProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(getNode().translateYProperty(), 10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(getNode().translateYProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(getNode().translateYProperty(), 10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(getNode().translateYProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(getNode().translateYProperty(), 10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(700),
                        new KeyValue(getNode().translateYProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(800),
                        new KeyValue(getNode().translateYProperty(), 10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(900),
                        new KeyValue(getNode().translateYProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(getNode().translateYProperty(), 0, AnimateFXInterpolator.EASE)
                )
        ));
    }
}



