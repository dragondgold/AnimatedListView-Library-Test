package com.animatedlistview.tmax.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andres on 2/6/14.
 */
public class PruebaExtendAdapter extends ExpandableAnimatedArrayAdapter<String> {

    private final Context context;
    private final List<String> mList;

    public PruebaExtendAdapter(Context context, int layoutResource, int expandableResource, List<String> mList) {
        super(context, layoutResource, expandableResource, mList);
        this.context = context;
        this.mList = mList;
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {

        ((TextView)convertView.findViewById(R.id.title_view)).setText(getItem(position));
        ((TextView)convertView.findViewById(R.id.detail_view)).setText("Detalle " + getItem(position));

        return convertView;
    }

}
