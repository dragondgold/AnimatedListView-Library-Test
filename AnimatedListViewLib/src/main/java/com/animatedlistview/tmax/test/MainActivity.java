package com.animatedlistview.tmax.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.animatedlistview.tmax.library.R;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ListView mListView;
    private PruebaExtendAdapter pruebaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list);
        ArrayList<String> mList = new ArrayList<String>();

        for(int n = 0; n < 500; ++n){
            mList.add("Item " + n);
        }
        pruebaAdapter = new PruebaExtendAdapter(this, R.layout.expandable_list_item, R.id.expandable, mList);

        mListView.setAdapter(pruebaAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(pruebaAdapter.isExpanded(i)) pruebaAdapter.collapse(i);
                else pruebaAdapter.expand(i);
            }
        });
    }

}
