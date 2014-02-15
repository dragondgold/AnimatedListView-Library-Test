package com.animatedlistview.tmax.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public abstract class AnimatedArrayAdapter<T> extends ArrayAdapter<T> implements OnItemDeleted{

    private final int expandableResource;
    private final int layoutResource;

    private final Context context;
    private ListView listView;

    private boolean prevExpansionEnabled;
    private boolean prevSwipeDismissEnabled;

    private OnItemDeleted onItemDeleted;

    /**
     * AnimatedArrayAdapter constructor
     * @param context context from the ListView
     * @param layoutResource layout containing the child for each row
     * @param expandableResource layout id which will be expanded/collapsed
     * @param list List<T> containing the ArrayAdapter<T> data
     */
    public AnimatedArrayAdapter(Context context, int layoutResource, int expandableResource, List<T> list) {
        super(context, layoutResource, list);

        this.expandableResource = expandableResource;
        this.layoutResource = layoutResource;
        this.context = context;
    }

    /**
     * This should be called when MultiChoiceMode is created so when the user click to select
     * items the views doesn't get expanded
     */
    public void multiChoiceModeEnabled(){
        prevExpansionEnabled = isExpansionEnabled();
        prevSwipeDismissEnabled = isSwipeToDeleteEnabled();

        setEnableExpansion(false);
        setSwipeToDelete(false);
    }

    /**
     * This should be called when MultiChoiceMode is destroyed so it restore the state saved in
     * multiChoiceModeEnabled()
     */
    public void multiChoiceModeDisabled(){
        setEnableExpansion(prevExpansionEnabled);
        setSwipeToDelete(prevSwipeDismissEnabled);
    }

    public boolean isExpansionEnabled() {
        return ViewExpandCollapseHelper.isEnabled();
    }

    public boolean isExpanded(int position){
        return ViewExpandCollapseHelper.isExpanded(position);
    }

    /**
     * Enable or disable item expansion
     * @param enableExpansion true for enable, false otherwise
     */
    public void setEnableExpansion(boolean enableExpansion) {
        ViewExpandCollapseHelper.setEnabled(enableExpansion);
    }

    /**
     * Set listener for item deletion. Set to null for deletion.
     * @param onItemDeleted callback
     */
    public void setOnItemDeleted(OnItemDeleted onItemDeleted) {
        this.onItemDeleted = onItemDeleted;
    }

    /**
     * Sets the duration for the expanding animation
     * @param duration duration in milli-seconds
     */
    public void setExpandAnimationDuration (long duration){
        ViewExpandCollapseHelper.expandAnimationDuration = duration;
    }

    /**
     * Sets the duration for the collapsing animation
     * @param duration duration in milli-seconds
     */
    public void setCollapseAnimationDuration (long duration){
        ViewExpandCollapseHelper.collapseAnimationDuration = duration;
    }

    /**
     * Enable or disable Swipe to delete in the ListView
     * @param state true or false to enable or disable Swipe to delete
     */
    public void setSwipeToDelete (boolean state){
        ViewSwipeDeleteHelper.setEnabled(state);
    }

    public boolean isSwipeToDeleteEnabled(){
        return ViewSwipeDeleteHelper.isEnabled();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The first time, get the ListView
        if(listView == null){
            listView = (ListView) parent;

            ViewSwipeDeleteHelper.init(listView, AnimatedArrayAdapter.this);
            ViewExpandCollapseHelper.init(listView, this, expandableResource);

            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ViewSwipeDeleteHelper.onTouchEvent(view, motionEvent);
                    ViewExpandCollapseHelper.onTouchEvent(view, motionEvent);

                    // Dispatch touch events to ListView
                    if(ViewSwipeDeleteHelper.dispatchEventToView() && ViewExpandCollapseHelper.dispatchEventToView())
                        listView.onTouchEvent(motionEvent);
                    return true;
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

        if(ViewExpandCollapseHelper.isExpanded(position)){
            expandable.measure(0,0);
            expandable.getLayoutParams().height = expandable.getMeasuredHeight();
        }else{
            expandable.getLayoutParams().height = 0;
        }

        // Let the user implement their own actions on the convertView and then return it
        return getItemView(position, convertView, parent);
    }

    @Override
    public boolean onItemDeleted(int position, View view) {
        if(onItemDeleted.onItemDeleted(position, view)){
            remove(getItem(position));
            ViewExpandCollapseHelper.updateExpandCollapseIndexes(position);
            return true;
        }
        return false;
    }

}