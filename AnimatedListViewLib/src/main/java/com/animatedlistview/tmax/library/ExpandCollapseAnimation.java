package com.animatedlistview.tmax.library;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by andres on 2/5/14.
 */
public class ExpandCollapseAnimation extends Animation {

    private final int targetHeight;
    private final View view;
    private final boolean expand;

    public ExpandCollapseAnimation(View view, int targetHeight, boolean expand) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.expand = expand;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        if (expand) {
            newHeight = (int) (targetHeight * interpolatedTime);
        } else {
            newHeight = (int) (targetHeight * (1 - interpolatedTime));
        }
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
