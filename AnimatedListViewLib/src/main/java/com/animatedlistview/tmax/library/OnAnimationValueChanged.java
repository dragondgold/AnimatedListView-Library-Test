package com.animatedlistview.tmax.library;

public interface OnAnimationValueChanged {

    /**
     * Callback for every time applyTransformation() is called
     * @param value new animation value
     * @param change difference between previous value and the current value
     */
    public void onAnimationValueChanged (int value, int change);

}
