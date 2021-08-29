// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.animations;

import javafx.animation.Interpolator;

public final class AnimateFXInterpolator
{
    public static final Interpolator EASE;
    
    private AnimateFXInterpolator() {
        throw new IllegalStateException("AnimateFX Interpolator");
    }
    
    static {
        EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0);
    }
}
