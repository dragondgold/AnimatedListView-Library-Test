package com.animatedlistview.tmax.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.animatedlistview.tmax.library.AnimatedArrayAdapter;
import com.animatedlistview.tmax.library.R;

import java.util.List;

/**
 * Simple ArrayAdapter<>. Just extends AnimatedArrayAdapter<> and animations are done automatically
 */
public class PruebaExtendAdapter extends AnimatedArrayAdapter<String> {

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
