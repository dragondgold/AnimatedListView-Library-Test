package com.animatedlistview.tmax.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.animatedlistview.tmax.library.OnItemDeleted;
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

        pruebaAdapter.setOnItemDeleted(new OnItemDeleted() {
            @Override
            public boolean onItemDeleted(int position, View view) {
                Toast.makeText(MainActivity.this, "Item " + position + " deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // In onItemClick() the View hast not yet updated the expand/collapse state
                //  so we invert the state which will be the future state
                if(pruebaAdapter.isExpanded(i)){
                    Toast.makeText(MainActivity.this, "Item " + i + " collapsed", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Item " + i + " expanded", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "Item " + i + " long clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.setSubtitle(mListView.getCheckedItemCount() + " item(s) selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                pruebaAdapter.multiChoiceModeEnabled();
                actionMode.setTitle("Select Items");
                actionMode.setSubtitle("1 item selected");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                Toast.makeText(MainActivity.this, mListView.getCheckedItemCount() + " items were selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                pruebaAdapter.multiChoiceModeDisabled();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
            case R.id.swipe_to_delete:
                if(pruebaAdapter.isSwipeToDeleteEnabled()){
                    pruebaAdapter.setSwipeToDelete(false);
                    item.setTitle("Enable Swipe to delete");
                    Toast.makeText(this, "Swipe to delete disable", Toast.LENGTH_SHORT).show();
                }else{
                    pruebaAdapter.setSwipeToDelete(true);
                    item.setTitle("Disable Swipe to delete");
                    Toast.makeText(this, "Swipe to delete enabled", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.enable_expansion:
                if(pruebaAdapter.isExpansionEnabled()){
                    pruebaAdapter.setEnableExpansion(false);
                    item.setTitle("Enable item expansion");
                    Toast.makeText(this, "Item expansion disabled", Toast.LENGTH_SHORT).show();
                }else{
                    pruebaAdapter.setEnableExpansion(true);
                    item.setTitle("Disable item expansion");
                    Toast.makeText(this, "Item expansion enabled", Toast.LENGTH_SHORT).show();
                }
        }
        return true;
    }
}
