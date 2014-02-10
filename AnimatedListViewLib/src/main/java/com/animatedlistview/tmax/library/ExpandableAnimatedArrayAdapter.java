package com.animatedlistview.tmax.library;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ExpandableAnimatedArrayAdapter<T> extends ArrayAdapter<T> {

    private static final int DEFAULT_DELETE_DURATION = 400;
    private final int expandableResource;
    private final int layoutResource;
    private final ArrayList<Boolean> expandStateArray;

    private final Context context;
    private ListView listView;
    private boolean isSwipeToDelete = false;
    private long expandAnimationDuration = 400;
    private long collapseAnimationDuration = 400;

    private boolean enableExpansion = true;

    private OnItemDeleted onItemDeleted;

    /**
     * ExpandableAnimatedArrayAdapter constructor
     * @param context context from the ListView
     * @param layoutResource layout containing the child for each row
     * @param expandableResource layout id which will be expanded/collapsed
     * @param list List<T> containing the ArrayAdapter<T> data
     */
    public ExpandableAnimatedArrayAdapter(Context context, int layoutResource, int expandableResource, List<T> list) {
        super(context, layoutResource, list);

        this.expandableResource = expandableResource;
        this.layoutResource = layoutResource;
        this.context = context;

        // Create the ArrayList and fill it with false state (collapsed)
        expandStateArray = new ArrayList<Boolean>(list.size());
        expandStateArray.addAll(Collections.nCopies(list.size(), false));
    }

    public boolean isExpansionEnabled() {
        return enableExpansion;
    }

    /**
     * Enable or disable item expansion
     * @param enableExpansion true for enable, false otherwise
     */
    public void setEnableExpansion(boolean enableExpansion) {
        this.enableExpansion = enableExpansion;
    }

    /**
     * Set listener for item deletion
     * @param onItemDeleted callback
     */
    public void setOnItemDeleted(OnItemDeleted onItemDeleted) {
        this.onItemDeleted = onItemDeleted;
    }

    public void removeOnItemDeleted(OnItemDeleted onItemDeleted) {
        onItemDeleted = null;
    }

    /**
     * Sets the duration for the expanding animation
     * @param duration duration in milli-seconds
     */
    public void setExpandAnimationDuration (long duration){
        expandAnimationDuration = duration;
    }

    /**
     * Sets the duration for the collapsing animation
     * @param duration duration in milli-seconds
     */
    public void setCollapseAnimationDuration (long duration){
        collapseAnimationDuration = duration;
    }

    /**
     * Enable or disable Swipe to delete in the ListView
     * @param state true or false to enable or disable Swipe to delete
     */
    public void setSwipeToDelete (boolean state){
        isSwipeToDelete = state;
    }

    public boolean isSwipeToDeleteEnabled(){
        return isSwipeToDelete;
    }

    /**
     * Expands the item at the given position
     * @param position position in the ListView to expand
     */
    public void expand (final int position){

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

                listView.smoothScrollBy(scrollDistance, (int) expandAnimationDuration * (position == getCount() - 1 ? 4 : 2));
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
    public void collapse (final int position){

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
            if (position == getCount() - 1) {
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
     * Checks if the item is expanded or collapsed
     * @param position position to check
     */
    public boolean isExpanded (int position){
        return expandStateArray.get(position);
    }

    /**
     * You should Override this method in your ArrayAdapter instead of getView() method
     * @param position position in the ListView
     * @param convertView view that will be displayed in the ListView, it's already inflated so you
     *                    don't need to null check it. You must return it at the end of the method.
     * @param parent ViewGroup that holds all the views (ListView itself)
     * @return View to be displayed at the given position
     */
    public abstract View getItemView(int position, View convertView, ViewGroup parent);

    /**
     * Gets the Child View of the ListView at the given position
     * @param position position of
     */
    private View getViewAt (int position){
        final int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
        final int wantedChild = position - firstPosition;

        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            throw new IllegalArgumentException("Required position is not currently visible");
        }
        return listView.getChildAt(wantedChild);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The first time, get the ListView
        if(listView == null){
            listView = (ListView) parent;

            listView.setOnTouchListener(new TouchEventHandler(listView){
                @Override
                public void onSwipeRight(MotionEvent motionEvent, View view, float distance) {
                    if(!listView.isClickable()) return;
                    // If SwipeToDelete is enabled modify alpha and position of the View according to
                    //  the distance swiped
                    if(isSwipeToDeleteEnabled()){
                        view.setTranslationX(view.getTranslationX() + distance);

                        float alpha = view.getTranslationX() / (listView.getWidth());
                        view.setAlpha(1-alpha);
                    }
                    super.onSwipeRight(motionEvent, view, distance);
                }

                @Override
                public void onSwipeLeft(MotionEvent motionEvent, View view, float distance) {
                    if(!listView.isClickable()) return;
                    if(isSwipeToDeleteEnabled()){
                        view.setTranslationX(view.getTranslationX() + distance);

                        float alpha = -view.getTranslationX() / (listView.getWidth());
                        view.setAlpha(1-alpha);
                    }
                    super.onSwipeLeft(motionEvent, view, distance);
                }

                @Override
                public void onSwipeFinish(MotionEvent motionEvent, final View view, float velocity) {
                    if(!listView.isClickable()) return;
                    if(isSwipeToDeleteEnabled()){
                        // Delete to the right side
                        if(view.getTranslationX() > listView.getWidth()/2){
                            animateDeletion(view, listView.getWidth(), velocity);
                        }
                        // Delete to the left side
                        else if(view.getTranslationX() < -listView.getWidth()/2){
                            animateDeletion(view, -listView.getWidth(), velocity);
                        }
                        // Animate View to default positions
                        else{
                            ViewPropertyAnimator.animate(view).setDuration(DEFAULT_DELETE_DURATION).translationX(0).start();
                            ViewPropertyAnimator.animate(view).setDuration(DEFAULT_DELETE_DURATION).alpha(1).start();
                        }
                    }
                    super.onSwipeFinish(motionEvent, view, velocity);
                }

                @Override
                public void onClick(MotionEvent motionEvent, View view) {
                    if(!listView.isClickable()) return;
                    int position = listView.getPositionForView(view);

                    if(!isSwipeToDeleteEnabled() && enableExpansion){
                        if(isExpanded(position)) collapse(position);
                        else expand(position);
                        listView.invalidate();
                    }
                    super.onClick(motionEvent, view);
                }
            });
        }

        // Inflate the new Views, otherwise we reuse them
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResource, null);
        }

        // Expand/collapse the Views according to the saved state
        View expandable = convertView.findViewById(expandableResource);

        if(isExpanded(position)){
            expandable.measure(0,0);
            expandable.getLayoutParams().height = expandable.getMeasuredHeight();
        }else{
            expandable.getLayoutParams().height = 0;
        }

        // Let the user implement their own actions on the convertView and then return it
        return getItemView(position, convertView, parent);
    }

    /**
     * Animates View deletion to the right or to the left
     * @param view View to animate
     * @param target target translation (listView.getWidth() to the right and -listView.getWidth() to the left)
     * @param velocity velocity of the final Swipe gesture in pixels/ms
     */
    private void animateDeletion (final View view, final int target, final float velocity){
        final int defaultHeight = view.getHeight();
        // Disable ListView clicks while playing animation
        listView.setClickable(false);

        // v = d/t -> t = d/v
        long duration = (long) (Math.abs(target - view.getTranslationX()) / velocity);

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
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (onItemDeleted != null) {
                            // Delete item if returned value is true
                            if (onItemDeleted.onItemDeleted(position, view)) {
                                updateExpandCollapseIndexes(position);
                                remove(getItem(position));
                            }
                        }
                        // Default action, delete item
                        else {
                            updateExpandCollapseIndexes(position);
                            remove(getItem(position));
                        }

                        view.setTranslationX(0);
                        view.getLayoutParams().height = defaultHeight;
                        view.setAlpha(1);
                        ViewPropertyAnimator.animate(view).setListener(null);
                        listView.setClickable(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                view.startAnimation(animation);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        }).start();
    }

    /**
     * Update the indexes of the state of each View after a deletion has ocurred
     * @param removedPosition View position that was removed
     */
    private void updateExpandCollapseIndexes(int removedPosition){
        expandStateArray.remove(removedPosition);
    }
}