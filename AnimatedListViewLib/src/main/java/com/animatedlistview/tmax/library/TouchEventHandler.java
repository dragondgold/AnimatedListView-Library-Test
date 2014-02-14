package com.animatedlistview.tmax.library;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class TouchEventHandler implements OnTouchListener {

    private final ListView listView;
    private View currentClickedView;
    private boolean swiping = false;

    private final VelocityTracker velocityTracker;
    private float prevX = 0;

    public TouchEventHandler(ListView listView){
        this.listView = listView;
        velocityTracker = VelocityTracker.obtain();

    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        if(action == MotionEvent.ACTION_DOWN){
            velocityTracker.addMovement(motionEvent);
            currentClickedView = getClickedView(motionEvent);
            prevX = motionEvent.getRawX();

        }else if(action == MotionEvent.ACTION_MOVE){
            velocityTracker.addMovement(motionEvent);
            if(!swiping) onSwipeStart(motionEvent, view);

            if(motionEvent.getRawX() - prevX > 0){
                onSwipeRight(motionEvent, currentClickedView, motionEvent.getRawX() - prevX);
            }else{
                onSwipeLeft(motionEvent, currentClickedView, motionEvent.getRawX() - prevX);
            }

            prevX = motionEvent.getRawX();
            swiping = true;

        }else if(action == MotionEvent.ACTION_UP || (swiping && action == MotionEvent.ACTION_CANCEL)){
            velocityTracker.addMovement(motionEvent);
            if(swiping){
                // Compute velocity in pixels per milliseconds
                velocityTracker.computeCurrentVelocity(1);
                onSwipeFinish(motionEvent, currentClickedView, velocityTracker.getXVelocity());
                swiping = false;
                return true;
            }
            else{
                onClick(motionEvent, currentClickedView);
            }
        }
        // Dispatch touch event to the ListView if we are not swiping away a child View so we don't scroll the list
        if(!swiping) listView.onTouchEvent(motionEvent);

        return true;
    }

    /**
     * Find the Child View touched in the ListView from the coordinates in the MotionEvent
     * @param motionEvent onTouch() MotionEvent
     * @return clicked Child View
     */
    private View getClickedView(MotionEvent motionEvent){
        // Find the child view that was touched (perform a hit test)
        Rect rect = new Rect();
        int childCount = listView.getChildCount();
        int[] listViewCoords = new int[2];

        listView.getLocationOnScreen(listViewCoords);
        int x = (int) motionEvent.getRawX() - listViewCoords[0];
        int y = (int) motionEvent.getRawY() - listViewCoords[1];

        View child;
        View clickedView = null;
        for (int i = 0; i < childCount; i++) {
            child = listView.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(x,y)) {
                clickedView = child;  // Found View
                break;
            }
        }
        return clickedView;
    }

    public void onClick(MotionEvent motionEvent, final View view) {}

    public void onSwipeStart(MotionEvent motionEvent, final View view) {}

    /**
     * Callback when Swipe gesture has finished
     * @param motionEvent MotionEvent that finished the Swipe gesture
     * @param view View where whe MotionEvent occurred
     * @param endVelocity velocity in pixels/ms when the Swipe gesture ended
     */
    public void onSwipeFinish(MotionEvent motionEvent, final View view, float endVelocity) {}

    public void onSwipeRight(MotionEvent motionEvent, final View view, float distance) {}

    public void onSwipeLeft(MotionEvent motionEvent, final View view, float distance) {}

}
