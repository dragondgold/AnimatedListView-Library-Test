package com.animatedlistview.tmax.library;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public abstract class ExpandableAnimatedArrayAdapter<T> extends ArrayAdapter<T> {

    private final int expandableResource;
    private final int layoutResource;
    private final SparseBooleanArray booleanArray = new SparseBooleanArray();

    private Context context;
    private ListView listView;
    private long expandAnimationDuration = 400;

    public ExpandableAnimatedArrayAdapter(Context context, int layoutResource, int expandableResource, List<T> list) {
        super(context, layoutResource, list);

        this.expandableResource = expandableResource;
        this.layoutResource = layoutResource;
        this.context = context;
    }

    /**
     * Sets the duration for the expanding animation
     * @param duration duration in milli-seconds
     */
    public void setExpandAnimationDuration (long duration){
        expandAnimationDuration = duration;
    }

    /**
     * Expands the item at the given position
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
        if ((view.getBottom() + expandedView.getMeasuredHeight()) > listView.getHeight()) {
            // Item only visible partially at the bottom of the list
            if (view.getBottom() > listView.getHeight()) {
                int scrollDistance = view.getBottom() - listView.getHeight();
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
                booleanArray.put(position, true);
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
     */
    public void collapse (final int position){

        final View expandedView = getViewAt(position).findViewById(expandableResource);
        ViewCompat.setHasTransientState(expandedView, true);
        expandedView.measure(0, 0);

        ExpandCollapseAnimation collapseAnimation = new ExpandCollapseAnimation(
                expandedView,
                expandedView.getMeasuredHeight(),
                false);
        collapseAnimation.setDuration(expandAnimationDuration);

        final View view = getViewAt(position);
        if (view.getTop() < listView.getTop()) {
            int scrollDistance = view.getTop() - listView.getTop();
            listView.smoothScrollBy(scrollDistance, (int) expandAnimationDuration * 2);
        }

        if ((view.getBottom()-expandedView.getMeasuredHeight()) > listView.getBottom()) {
            if (position == getCount() - 1) {
                //#FIXME
            } else if(view.getBottom() > listView.getBottom()) {
                collapseAnimation.setAnimationTransformationListener(new OnAnimationValueChanged() {
                    @Override
                    public void onAnimationValueChanged(int value, int change) {
                        listView.smoothScrollBy(change, 0);
                    };
                });
            }
        }


        collapseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                booleanArray.put(position, false);
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
     */
    public boolean isExpanded (int position){
        return booleanArray.get(position);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent);

    /**
     * Gets the Child View of the ListView at the given position
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
        if(listView == null) listView = (ListView) parent;

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
}