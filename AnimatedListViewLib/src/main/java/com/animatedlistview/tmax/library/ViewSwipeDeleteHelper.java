package com.animatedlistview.tmax.library;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class ViewSwipeDeleteHelper {

    private static boolean isSwipeToDeleteEnabled = false;
    private static final int DEFAULT_DELETE_DURATION = 400;
    private static boolean isSwiping = false;

    private static ListView listView;
    private static View currentClickedView;
    private static OnItemDeleted mOnItemDeleted;

    /**
     * Init the helper class. This must be called before anything else.
     * @param list ListView
     * @param onItemDeleted callback called when an item is deleted
     */
    public static void init (ListView list, OnItemDeleted onItemDeleted){
        listView = list;
        mOnItemDeleted = onItemDeleted;
    }

    /**
     * View and the respective TouchEvent. This should be called in the ListView onTouch()
     * @param view ListView
     * @param motionEvent MotionEvent
     */
    public static void onTouchEvent (View view, MotionEvent motionEvent){
        // Dispatch the touch event to the event handler
        mTouchEventHandler.onTouch(view, motionEvent);
    }

    /**
     * Enable/Disable the Swipe to delete ability
     * @param state true if enabled, false otherwise
     */
    public static void setEnabled (boolean state){
        isSwipeToDeleteEnabled = state;
    }

    public static boolean isEnabled(){
        return isSwipeToDeleteEnabled;
    }

    /**
     * Indicates if we should dispatch the touch event to the ListView
     * @return true if we should dispatch the touch event to the ListView
     */
    public static boolean dispatchEventToView(){
        return mTouchEventHandler.dispatchToView;
    }

    /**
     * Find the Child View touched in the ListView from the coordinates in the MotionEvent
     * @param motionEvent onTouch() MotionEvent
     * @return clicked Child View
     */
    private static View getClickedView(MotionEvent motionEvent){
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

    private final static TouchEventHandler mTouchEventHandler = new TouchEventHandler(){
        @Override
        public boolean onDown(MotionEvent motionEvent, View view) {
            currentClickedView = getClickedView(motionEvent);
            return true;
        }

        @Override
        public boolean onClick(MotionEvent motionEvent, View view) {
            return true;
        }

        @Override
        public boolean onOtherEvent(MotionEvent motionEvent, View view) {
            return !isSwiping;
        }

        @Override
        public boolean onSwipeStart(MotionEvent motionEvent, View view) {
            isSwiping = true;
            return false;
        }

        @Override
        public boolean onSwipeFinish(MotionEvent motionEvent, View view, float endVelocity) {
            isSwiping = false;
            if(!listView.isClickable()) return false;
            if(isSwipeToDeleteEnabled){
                // Delete to the right side
                if(currentClickedView.getTranslationX() > listView.getWidth()/2){
                    animateDeletion(currentClickedView, listView.getWidth(), endVelocity);
                }
                // Delete to the left side
                else if(currentClickedView.getTranslationX() < -listView.getWidth()/2){
                    animateDeletion(currentClickedView, -listView.getWidth(), endVelocity);
                }
                // Animate View to default positions element is not removed
                else{
                    ViewPropertyAnimator.animate(currentClickedView).setDuration(DEFAULT_DELETE_DURATION).translationX(0).start();
                    ViewPropertyAnimator.animate(currentClickedView).setDuration(DEFAULT_DELETE_DURATION).alpha(1).start();
                }
            }
            return false;
        }

        @Override
        public boolean onSwipeRight(MotionEvent motionEvent, View view, float distance) {
            if(!listView.isClickable()) return false;
            // If SwipeToDelete is enabled modify alpha and position of the View according to
            //  the distance swiped
            if(isSwipeToDeleteEnabled){
                currentClickedView.setTranslationX(currentClickedView.getTranslationX() + distance);

                float alpha = currentClickedView.getTranslationX() / (listView.getWidth());
                currentClickedView.setAlpha(1-alpha);
            }
            return false;
        }

        @Override
        public boolean onSwipeLeft(MotionEvent motionEvent, View view, float distance) {
            if(!listView.isClickable()) return false;
            if(isSwipeToDeleteEnabled){
                currentClickedView.setTranslationX(currentClickedView.getTranslationX() + distance);

                float alpha = -currentClickedView.getTranslationX() / (listView.getWidth());
                currentClickedView.setAlpha(1-alpha);
            }
            return false;
        }
    };

    /**
     * Animates View deletion to the right or to the left
     * @param view View to animate
     * @param target target translation (listView.getWidth() to the right and -listView.getWidth() to the left)
     * @param velocity velocity of the final Swipe gesture in pixels/ms
     */
    private static void animateDeletion (final View view, final int target, final float velocity){
        final int defaultHeight = view.getHeight();
        // Disable ListView clicks while playing animation
        listView.setClickable(false);

        // v = d/t -> t = d/v
        long duration = (long) Math.abs((target - view.getTranslationX()) / velocity);
        if(duration > DEFAULT_DELETE_DURATION) duration = DEFAULT_DELETE_DURATION;

        ViewPropertyAnimator.animate(view).translationX(target).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                final int position = listView.getPositionForView(view);

                ExpandCollapseAnimation animation = new ExpandCollapseAnimation(view, defaultHeight, false);
                animation.setDuration(DEFAULT_DELETE_DURATION);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mOnItemDeleted.onItemDeleted(position, view);

                        view.setTranslationX(0);
                        view.getLayoutParams().height = defaultHeight;
                        view.setAlpha(1);
                        ViewPropertyAnimator.animate(view).setListener(null);
                        listView.setClickable(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                view.startAnimation(animation);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        }).start();
    }

}
