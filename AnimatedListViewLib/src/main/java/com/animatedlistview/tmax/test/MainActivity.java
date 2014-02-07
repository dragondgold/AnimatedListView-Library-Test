package com.animatedlistview.tmax.test;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.animatedlistview.tmax.library.CustomAdapter;
import com.animatedlistview.tmax.library.PruebaExtendAdapter;
import com.animatedlistview.tmax.library.R;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    private Random crazy = new Random();
    private CustomAdapter customAdapter;
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
        //customAdapter = new CustomAdapter(this, R.layout.expandable_list_item, mList);
        pruebaAdapter = new PruebaExtendAdapter(this, R.layout.expandable_list_item, R.id.expandable, mList);

        mListView.setAdapter(pruebaAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, "Item " + i + " clickeado", Toast.LENGTH_SHORT).show();
                if(pruebaAdapter.isExpanded(i)) pruebaAdapter.collapse(i);
                else pruebaAdapter.expand(i);
            }
        });
    }

}
