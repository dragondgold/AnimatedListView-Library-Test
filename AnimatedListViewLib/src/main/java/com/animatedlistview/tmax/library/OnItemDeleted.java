package com.animatedlistview.tmax.library;

import android.view.View;

public interface OnItemDeleted {

    /**
     * Interface called when a item is deleted from ListView
     * @param position position of the item going to be deleted
     * @param view View to be deleted
     * @return true if View should be deleted, false otherwise
     */
    public boolean onItemDeleted(int position, View view);

}
