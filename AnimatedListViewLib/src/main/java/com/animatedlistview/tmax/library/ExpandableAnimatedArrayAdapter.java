package com.animatedlistview.tmax.library;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public abstract class ExpandableAnimatedArrayAdapter<T> extends ArrayAdapter<T> {

    private final int expandableResource;
    private final int layoutResource;
    private final SparseBooleanArray booleanArray = new SparseBooleanArray();

    private Context context;
    private ListView listView;
    private long expandAnimationDuration = 200;

    public ExpandableAnimatedArrayAdapter(Context context, int layoutResource, int expandableResource, List<T> list) {
        super(context, layoutResource, list);

        this.expandableResource = expandableResource;
        this.layoutResource = layoutResource;
        this.context = context;
    }

    public void setExpandAnimationDuration(long duration) {
        expandAnimationDuration = duration;
    }

    /**
     * Expande la item en la posición dada
     *
     * @param position
     */
    public void expand(final int position) {

        final boolean viewOutOfBounds;
        final View view = getViewAt(position);
        final View expandedView = view.findViewById(expandableResource);
        ViewCompat.setHasTransientState(expandedView, true);
        expandedView.measure(0, 0);

        final ExpandCollapseAnimation expandAnimation = new ExpandCollapseAnimation(
                expandedView,
                expandedView.getMeasuredHeight(),
                true);
        expandAnimation.setDuration(expandAnimationDuration);

        // Si expando el último item de la lista o si el item que expando no entra en los márgenes del ListView
        //  voy deslizando la lista a medida que la animación expande el View
        if ((view.getBottom() + expandedView.getMeasuredHeight()) > listView.getHeight()) {

            // Si el View esta pasándose de los limites pero se ve (por ejemplo un item al final que se ve
            //  sólo la mitad) muevo la lista hasta que se vea el View por completo
            if (view.getBottom() > listView.getHeight()) {
                int scrollDistance = view.getBottom() - listView.getHeight();
                scrollDistance += expandedView.getMeasuredHeight();

                listView.smoothScrollBy(scrollDistance, (int) expandAnimationDuration * (position == getCount() - 1 ? 4 : 2));
                viewOutOfBounds = true;
            } else viewOutOfBounds = false;

            // Scrolleo la lista a medida que expando. No hago nada si expando un View que se ve incompleto
            //  en el final de la lista porque ya me encargué del scroll antes
            expandAnimation.setAnimationTransformationListener(new OnAnimationValueChanged() {
                @Override
                public void onAnimationValueChanged(int value, int change) {
                    if (!viewOutOfBounds) listView.smoothScrollBy(change, 0);
                }

                ;
            });
        } else if (view.getTop() < listView.getTop()) {
            int scrollDistance = view.getTop() - listView.getTop();
            listView.smoothScrollBy(scrollDistance, (int) expandAnimationDuration * 2);
        }

        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                booleanArray.put(position, true);
                ViewCompat.setHasTransientState(expandedView, false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        expandedView.startAnimation(expandAnimation);
    }

    /**
     * Cierra el item en la posición dada
     *
     * @param position
     */
    public void collapse(final int position) {

        final View expandedView = getViewAt(position).findViewById(expandableResource);
        ViewCompat.setHasTransientState(expandedView, true);
        expandedView.measure(0, 0);

        ExpandCollapseAnimation collapseAnimation = new ExpandCollapseAnimation(
                expandedView,
                expandedView.getMeasuredHeight(),
                false);
        collapseAnimation.setDuration(expandAnimationDuration);

        final View view = getViewAt(position);
        if (view.getTop() < listView.getTop()) {
            int scrollDistance = view.getTop() - listView.getTop();
            listView.smoothScrollBy(scrollDistance, (int) expandAnimationDuration * 2);
        }

        if ((view.getBottom()-expandedView.getMeasuredHeight()) > listView.getBottom()) {
            if (position == getCount() - 1) {
                //#FIXME
            } else if(view.getBottom() > listView.getBottom()) {
                collapseAnimation.setAnimationTransformationListener(new OnAnimationValueChanged() {
                    @Override
                    public void onAnimationValueChanged(int value, int change) {
                        listView.smoothScrollBy(change, 0);
                    };
                });
            }
        }


        collapseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                booleanArray.put(position, false);
                ViewCompat.setHasTransientState(expandedView, false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        expandedView.startAnimation(collapseAnimation);
    }

    /**
     * Determina si el item en la posición dada esta expandido o no
     *
     * @param position
     * @return
     */
    public boolean isExpanded(int position) {
        return booleanArray.get(position);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent);

    /**
     * Obtiene el Child del ListView en la posición dada basando en la posición guardada
     * en el ViewHolder de cada View
     *
     * @param position posición del View que se quiere obtener
     * @return View en la posición indicada
     */
    private View getViewAt(int position) {
        final int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
        final int wantedChild = position - firstPosition;

        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            throw new IllegalArgumentException("La posicion requerida no se encuentra visible");
        }
        return listView.getChildAt(wantedChild);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Defino el ListView la primera vez
        if (listView == null) listView = (ListView) parent;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResource, null);
        }

        View expandable = convertView.findViewById(expandableResource);

        if (isExpanded(position)) {
            expandable.measure(0, 0);
            expandable.getLayoutParams().height = expandable.getMeasuredHeight();
        } else {
            expandable.getLayoutParams().height = 0;
        }

        return getItemView(position, convertView, parent);
    }
}