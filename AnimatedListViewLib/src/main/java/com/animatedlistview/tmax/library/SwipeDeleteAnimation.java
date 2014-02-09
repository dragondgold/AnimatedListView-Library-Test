package com.animatedlistview.tmax.library;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by andres on 2/5/14.
 */
public class SwipeDeleteAnimation extends Animation {

    private final float target;
    private final View view;
    private final boolean directionRight;

    public SwipeDeleteAnimation(View view, float to) {
        this.view = view;

        if(to - view.getTranslationX() > 0)
            this.directionRight = true;
        else directionRight = false;

        this.target = Math.abs(to - view.getTranslationX());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float newTranslation;
        if (directionRight) {
            newTranslation = view.getTranslationX() + (target * interpolatedTime);
        } else {
            newTranslation = view.getTranslationX() - (target * interpolatedTime);
        }

        view.setTranslationX(newTranslation);
        view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}

