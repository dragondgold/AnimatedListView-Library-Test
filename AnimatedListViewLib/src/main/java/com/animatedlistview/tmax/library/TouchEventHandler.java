package com.animatedlistview.tmax.library;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;

public abstract class TouchEventHandler implements OnTouchListener {

    private boolean swiping = false;
    private boolean isClick = false;
    public boolean dispatchToView = false;

    private final VelocityTracker velocityTracker;
    private float prevX = 0, prevY = 0;

    private int lastAction;

    public TouchEventHandler(){
        velocityTracker = VelocityTracker.obtain();
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        lastAction = action;

        if(action == MotionEvent.ACTION_DOWN){
            velocityTracker.addMovement(motionEvent);

            prevX = motionEvent.getRawX();
            prevY = motionEvent.getRawY();
            isClick = true;

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(lastAction == MotionEvent.ACTION_DOWN) dispatchToView = onLongClick(motionEvent, view);
                }
            }, ViewConfiguration.getLongPressTimeout());

            dispatchToView = onDown(motionEvent, view);

        }else if(action == MotionEvent.ACTION_MOVE){
            isClick = false;
            velocityTracker.addMovement(motionEvent);

            if(!swiping) dispatchToView = onSwipeStart(motionEvent, view);
            swiping = true;

            // Swipe left/right
            if(Math.abs(motionEvent.getRawX() - prevX) > Math.abs(motionEvent.getRawY() - prevY)){
                if(motionEvent.getRawX() - prevX > 0){
                    dispatchToView = onSwipeRight(motionEvent, view, motionEvent.getRawX() - prevX);
                }else{
                    dispatchToView = onSwipeLeft(motionEvent, view, motionEvent.getRawX() - prevX);
                }
            // Swipe up/down
            }else{
                if(motionEvent.getRawY() - prevY > 0){
                    dispatchToView = onSwipeDown(motionEvent, view, motionEvent.getRawY() - prevY);
                }else {
                    dispatchToView = onSwipeUp(motionEvent, view, motionEvent.getRawY() - prevY);
                }
            }

            prevY = motionEvent.getRawY();
            prevX = motionEvent.getRawX();

        }else if(action == MotionEvent.ACTION_UP || (swiping && action == MotionEvent.ACTION_CANCEL)){
            velocityTracker.addMovement(motionEvent);
            if(swiping){
                // Compute velocity in pixels per milliseconds
                velocityTracker.computeCurrentVelocity(1);
                dispatchToView = onSwipeFinish(motionEvent, view, velocityTracker.getXVelocity());
                swiping = false;
            }
            else if(isClick){
                // Dispatch click event only if it wasn't a long click
                long time = motionEvent.getEventTime() - motionEvent.getDownTime();
                if(time < ViewConfiguration.getLongPressTimeout())
                    dispatchToView = onClick(motionEvent, view);
            }else{
                dispatchToView = onOtherEvent(motionEvent, view);
            }
        }else{
            dispatchToView = onOtherEvent(motionEvent, view);
        }

        return true;
    }

    /**
     * onClick() callback
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @return true if the event should be dispatched to parent View
     */
    public abstract boolean onClick(MotionEvent motionEvent, final View view);

    /**
     * onLongClick() callback
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @return true if the event should be dispatched to parent View
     */
    public abstract boolean onLongClick(MotionEvent motionEvent, final View view);

    /**
     * onDown() callback
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @return true if the event should be dispatched to the ListView
     */
    public abstract boolean onDown(MotionEvent motionEvent, final View view);

    /**
     * Callback called when none of the others callback are called
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @return true if the event should be dispatched to the ListView
     */
    public abstract boolean onOtherEvent(MotionEvent motionEvent, final View view);

    /**
     * Called when a swipe gesture starts
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @return true if the event should be dispatched to the ListView
     */
    public abstract boolean onSwipeStart(MotionEvent motionEvent, final View view);

    /**
     * Callback when Swipe gesture has finished
     * @param motionEvent MotionEvent that finished the Swipe gesture
     * @param view View where whe MotionEvent occurred
     * @param endVelocity velocity in pixels/ms when the Swipe gesture ended
     */
    public abstract boolean onSwipeFinish(MotionEvent motionEvent, final View view, float endVelocity);

    /**
     * onSwipeRight() callback
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @param distance distance swiped since the last event
     * @return true if the event should be dispatched to the ListView
     */
    public abstract boolean onSwipeRight(MotionEvent motionEvent, final View view, float distance);

    /**
     * onSwipeLeft() callback
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @param distance distance swiped since the last event. It's negative because of swiping left
     * @return true if the event should be dispatched to the ListView
     */
    public abstract boolean onSwipeLeft(MotionEvent motionEvent, final View view, float distance);

    /**
     * onSwipeUp() callback
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @param distance distance swiped since the last event
     * @return true if the event should be dispatched to the ListView. It's negative because of swiping up
     */
    public abstract boolean onSwipeUp(MotionEvent motionEvent, final View view, float distance);

    /**
     * onSwipeDown() callback
     * @param motionEvent MotionEvent
     * @param view Clicked View
     * @param distance distance swiped since the last event
     * @return true if the event should be dispatched to the ListView
     */
    public abstract boolean onSwipeDown(MotionEvent motionEvent, final View view, float distance);
}