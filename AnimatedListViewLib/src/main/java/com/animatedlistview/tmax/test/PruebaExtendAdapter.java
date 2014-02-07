package com.animatedlistview.tmax.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.animatedlistview.tmax.library.ExpandableAnimatedArrayAdapter;
import com.animatedlistview.tmax.library.R;

import java.util.List;

/**
 * Created by andres on 2/6/14.
 */
public class PruebaExtendAdapter extends ExpandableAnimatedArrayAdapter<String> {

    public PruebaExtendAdapter(Context context, int layoutResource, int expandableResource, List<String> mList) {
        super(context, layoutResource, expandableResource, mList);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {

        ((TextView)convertView.findViewById(R.id.title_view)).setText(getItem(position));
        ((TextView)convertView.findViewById(R.id.detail_view)).setText("Detalle " + getItem(position));

        return convertView;
    }

}
