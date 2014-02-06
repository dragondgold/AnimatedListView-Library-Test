package com.animatedlistview.tmax.library;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExpandableAnimatedArrayAdapter<T> extends ArrayAdapter<T> {

    private final int expandableResource;
    private final int layoutResource;
    private final SparseBooleanArray booleanArray = new SparseBooleanArray();

    private Map<Integer, Integer> heightList = new HashMap<Integer, Integer>();
    private Map<Integer, View> viewMap = new HashMap<Integer, View>();

    private Context context;

    public ExpandableAnimatedArrayAdapter(Context context, int layoutResource, int expandableResource, List<T> list) {
        super(context, layoutResource, list);

        this.expandableResource = expandableResource;
        this.layoutResource = layoutResource;
        this.context = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public void expand (final int position){
        booleanArray.put(position, true);

        View expandedView = viewMap.get(position).findViewById(expandableResource);

        ExpandCollapseAnimation a = new ExpandCollapseAnimation(
                expandedView,
                heightList.get(position),
                true);
        a.setDuration(300);
        expandedView.setAnimation(a);
        a.start();
        expandedView.requestLayout();
    }

    public void collapse (final int position){
        booleanArray.put(position, false);

        View expandedView = viewMap.get(position).findViewById(expandableResource);

        ExpandCollapseAnimation a = new ExpandCollapseAnimation(
                expandedView,
                heightList.get(position),
                false);
        a.setDuration(300);
        expandedView.setAnimation(a);
        a.start();
        expandedView.requestLayout();
    }

    public boolean isExpanded (int position){
        return booleanArray.get(position);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(!viewMap.containsKey(position)){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(layoutResource, null);
            View expandable = v.findViewById(expandableResource);

            expandable.measure(0, 0);

            viewMap.put(position, v);
            heightList.put(position, expandable.getMeasuredHeight());

            expandable.getLayoutParams().height = 0;
            expandable.setVisibility(View.VISIBLE);
        }

        return getItemView(position, viewMap.get(position), parent);
    }
}
