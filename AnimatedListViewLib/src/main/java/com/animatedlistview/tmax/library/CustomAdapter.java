package com.animatedlistview.tmax.library;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andres on 2/5/14.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    private Context context;
    private SparseBooleanArray booleanArray = new SparseBooleanArray();

    private Map<Integer, Integer> heightList = new HashMap<Integer, Integer>();
    private Map<Integer, View> viewMap = new HashMap<Integer, View>();

    public CustomAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    public void expand (final int position){
        booleanArray.put(position, true);

        View expandedView = viewMap.get(position).findViewById(R.id.expandable);

        ExpandCollapseAnimation a = new ExpandCollapseAnimation(
                expandedView,
                heightList.get(position),
                true);
        a.setDuration(300);
        expandedView.setAnimation(a);
        expandedView.requestLayout();
        a.start();
    }

    public void collapse (final int position){
        booleanArray.put(position, false);

        View expandedView = viewMap.get(position).findViewById(R.id.expandable);

        ExpandCollapseAnimation a = new ExpandCollapseAnimation(
                expandedView,
                heightList.get(position),
                false);
        a.setDuration(300);
        expandedView.setAnimation(a);
        expandedView.requestLayout();
        a.start();
    }

    public boolean isExpanded (int position){
        return booleanArray.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(!viewMap.containsKey(position)){
            Log.i("Layout", "Inflating Layout for item " + position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_item, null);

            convertView.findViewById(R.id.expandable).measure(0, 0);

            heightList.put(position, convertView.findViewById(R.id.expandable).getMeasuredHeight());
            viewMap.put(position, convertView);

            convertView.findViewById(R.id.expandable).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.expandable).getLayoutParams().height = 0;
        }

        final View view = viewMap.get(position);

        ((TextView)view.findViewById(R.id.title_view)).setText(getItem(position));
        ((TextView)view.findViewById(R.id.detail_view)).setText(getItem(position));

        return view;
    }
}
