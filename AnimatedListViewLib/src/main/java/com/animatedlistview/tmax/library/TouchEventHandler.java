package com.animatedlistview.tmax.library;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchEventHandler implements OnTouchListener {

    private boolean swiping = false;
    private float prevX = 0;

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        if(action == MotionEvent.ACTION_DOWN){
            prevX = motionEvent.getRawX();

        }else if(action == MotionEvent.ACTION_MOVE){

            if(!swiping) onSwipeStart(motionEvent, view);

            if(motionEvent.getRawX() - prevX > 0){
                onSwipeRight(motionEvent, view, motionEvent.getRawX() - prevX);
            }else{
                onSwipeLeft(motionEvent, view, motionEvent.getRawX() - prevX);
            }

            prevX = motionEvent.getRawX();
            swiping = true;

        }else if(action == MotionEvent.ACTION_UP || ( swiping && action == MotionEvent.ACTION_CANCEL)){
            if(swiping){
                onSwipeFinish(motionEvent, view);
                swiping = false;
            }
            else onClick(motionEvent, view);
        }

        return true;
    }

    public void onClick(MotionEvent motionEvent, final View view) {}

    public void onSwipeStart(MotionEvent motionEvent, final View view) {}

    public void onSwipeFinish(MotionEvent motionEvent, final View view) {}

    public void onSwipeRight(MotionEvent motionEvent, final View view, float distance) {}

    public void onSwipeLeft(MotionEvent motionEvent, final View view, float distance) {}

}
