package com.animatedlistview.tmax.library;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class ViewExpandCollapseHelper {

    private static boolean isExpandEnabled = true;

    private static ListView listView;
    private static ArrayAdapter mAdapter;
    private static int expandableResource;

    public static long expandAnimationDuration = 400;
    public static long collapseAnimationDuration = 400;

    private static ArrayList<Boolean> expandStateArray;

    /**
     * Init the helper class. This must be called before anything else.
     * @param list ListView
     * @param adapter Adapter
     * @param expandResource View that expands/collapse
     */
    public static void init (ListView list, ArrayAdapter adapter, int expandResource){
        listView = list;
        expandableResource = expandResource;
        mAdapter = adapter;

        // Create the ArrayList and fill it with false state (collapsed)
        expandStateArray = new ArrayList<Boolean>(adapter.getCount());
        expandStateArray.addAll(Collections.nCopies(adapter.getCount(), false));
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
     * Enable/Disable the Expand/Collapse ability
     * @param state true if enabled, false otherwise
     */
    public static void setEnabled (boolean state){
        isExpandEnabled = state;
    }

    public static boolean isEnabled(){
        return isExpandEnabled;
    }

    /**
     * Indicates if we should dispatch the touch event to the ListView
     * @return true if we should dispatch the touch event to the ListView
     */
    public static boolean dispatchEventToView(){
        return mTouchEventHandler.dispatchToView;
    }

    /**
     * Checks if the item is expanded or collapsed
     * @param position position to check
     */
    public static boolean isExpanded (int position){
        return expandStateArray.get(position);
    }

    /**
     * Gets the Child View of the ListView at the given position
     * @param position position of
     */
    private static View getViewAt (int position){
        final int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
        final int wantedChild = position - firstPosition;

        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            throw new IllegalArgumentException("Required position is not currently visible");
        }
        return listView.getChildAt(wantedChild);
    }

    /**
     * Expands the item at the given position
     * @param position position in the ListView to expand
     */
    public static void expand (final int position){
        final boolean viewOutOfBounds;
        final View view = getViewAt(position);
        final View expandedView = view.findViewById(expandableResource);
        ViewCompat.setHasTransientState(expandedView, true);
        expandedView.measure(0, 0);

        final ExpandCollapseAnimation expandAnimation = new ExpandCollapseAnimation(
                expandedView,
                expandedView.getMeasuredHeight(),
                true);
        expandAnimation.setDuration(expandAnimationDuration);

        // If I have to expand an item which doesn't fit in the ListView bounds when is expanded, scroll the
        //  ListView to fit the item and the expanded View
        if ((view.getBottom() + expandedView.getMeasuredHeight()) > listView.getBottom()) {
            // Item only visible partially at the bottom of the list
            if (view.getBottom() > listView.getBottom()) {
                int scrollDistance = view.getBottom() - listView.getBottom();
                scrollDistance += expandedView.getMeasuredHeight();

                listView.smoothScrollBy(scrollDistance, (int) expandAnimationDuration * (position == mAdapter.getCount() - 1 ? 4 : 2));
                viewOutOfBounds = true;
            } else viewOutOfBounds = false;
            // Scroll ListView while the animation expands the View
            expandAnimation.setAnimationTransformationListener(new OnAnimationValueChanged() {
                @Override
                public void onAnimationValueChanged(int value, int change) {
                    if (!viewOutOfBounds) listView.smoothScrollBy(change, 0);
                }
            });
            // Scroll the View if the item at the top of the list isn't displayed completely
        } else if (view.getTop() < listView.getTop()) {
            int scrollDistance = view.getTop() - listView.getTop();
            listView.smoothScrollBy(scrollDistance, (int) expandAnimationDuration * 2);
        }

        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                expandStateArray.set(position, true);
                ViewCompat.setHasTransientState(expandedView, false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        expandedView.startAnimation(expandAnimation);
    }

    /**
     * Collapse the item at the given position
     * @param position position in the ListView to expand
     */
    public static void collapse (final int position){

        final View view = getViewAt(position);
        final View expandedView = getViewAt(position).findViewById(expandableResource);
        ViewCompat.setHasTransientState(expandedView, true);
        expandedView.measure(0, 0);

        ExpandCollapseAnimation collapseAnimation = new ExpandCollapseAnimation(
                expandedView,
                expandedView.getMeasuredHeight(),
                false);
        collapseAnimation.setDuration(collapseAnimationDuration);

        // If view is partially displayed on the top side of the list
        if (view.getTop() < listView.getTop()) {
            int scrollDistance = view.getTop() - listView.getTop();
            listView.smoothScrollBy(scrollDistance, (int) collapseAnimationDuration * 2);
        }

        // If the title view is only partially displayed
        if ((view.getBottom() - expandedView.getMeasuredHeight()) > listView.getBottom()) {
            if (position == mAdapter.getCount() - 1) {
                int titleHeight = view.getHeight() - expandedView.getMeasuredHeight();
                int visibleSection = listView.getBottom() - view.getTop();
                int distance = titleHeight - visibleSection;

                listView.smoothScrollBy(distance, (int)collapseAnimationDuration*2);
            } else if(view.getBottom() > listView.getBottom()) {
                collapseAnimation.setAnimationTransformationListener(new OnAnimationValueChanged() {
                    @Override
                    public void onAnimationValueChanged(int value, int change) {
                        listView.smoothScrollBy(change, 0);
                    }
                });
            }
        }

        collapseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                expandStateArray.set(position, false);
                ViewCompat.setHasTransientState(expandedView, false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        expandedView.startAnimation(collapseAnimation);
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

    /**
     * Update the indexes of the state of each View after a deletion has ocurred
     * @param removedPosition View position that was removed
     */
    public static void updateExpandCollapseIndexes(int removedPosition){
        expandStateArray.remove(removedPosition);
    }

    private final static TouchEventHandler mTouchEventHandler = new TouchEventHandler(){
        @Override
        public boolean onDown(MotionEvent motionEvent, View view) {
            return true;
        }

        @Override
        public boolean onClick(MotionEvent motionEvent, View view) {
            if(!isExpandEnabled) return true;

            View currentClickedView = getClickedView(motionEvent);
            int position = listView.getPositionForView(currentClickedView);

            if(isExpanded(position)) collapse(position);
            else expand(position);

            return true;
        }

        @Override
        public boolean onOtherEvent(MotionEvent motionEvent, View view) {
            return true;
        }

        @Override
        public boolean onSwipeStart(MotionEvent motionEvent, View view) {
            return true;
        }

        @Override
        public boolean onSwipeFinish(MotionEvent motionEvent, View view, float endVelocity) {
            return true;
        }

        @Override
        public boolean onSwipeRight(MotionEvent motionEvent, View view, float distance) {
            return true;
        }

        @Override
        public boolean onSwipeLeft(MotionEvent motionEvent, View view, float distance) {
            return true;
        }
    };

}
