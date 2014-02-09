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
    private OnAnimationValueChanged animationValueChanged;

    public ExpandCollapseAnimation(View view, int targetHeight, boolean expand) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.expand = expand;
    }

    public void setAnimationTransformationListener(OnAnimationValueChanged animationValueChanged){
        this.animationValueChanged = animationValueChanged;
    }

    public void removeAnimationTransformationListener(){
        animationValueChanged = null;
    }

    public int getTargetHeight(){
        return targetHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        if (expand) {
            newHeight = (int) (targetHeight * interpolatedTime);
        } else {
            newHeight = (int) (targetHeight * (1 - interpolatedTime));
        }

        int change = Math.abs(view.getLayoutParams().height - newHeight);

        view.getLayoutParams().height = newHeight;
        view.requestLayout();

        if(animationValueChanged != null)
            animationValueChanged.onAnimationValueChanged(newHeight, change);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
