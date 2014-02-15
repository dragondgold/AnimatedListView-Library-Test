## AnimatedListView
----
 * AnimatedListView is a library which is a custom ArrayAdapter to create fully animated ListView or ExpandableListView with Swipe to delete option and more features to come!

## Features
----
* Fully Animated ExpandableListView
* Fully Animated ListView
* Swipe to delete
* Supports Android 1.6+
* If you would like an specific feature [create a new issue](https://github.com/dragondgold/AnimatedListView-Library-Test/issues/new) explaining the feature you would like me to add

## Usage
----
* Download source
* Import into Android Studio

## Example
----
* Create your custom adapter as you always do but extend `AnimatedArrayAdapter` instead of `ArrayAdapter`

* Override `public View getItemView(int position, View convertView, ViewGroup parent)` (**DO NOT OVERRIDE `public View getItemView(int position, View convertView, ViewGroup parent)` IT IS USED INTERNALLY**)

* On `getItemView()` do whatever you want to the `convertView` which is the View that will be displayed and then return it.

``` java
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
```

* You can see the sample app for more details [here](https://github.com/dragondgold/AnimatedListView-Library-Test/tree/master/AnimatedListViewLib/src/main/java/com/animatedlistview/tmax/test)

## Bugs
----
* Feel free to contact me for any bugs you find.

## Thanks
----
* [Ferran Negre](https://github.com/DIFORT) for the motivation to create this project due to issues/lack of features we had with others libraries and the great help to find new bugs.
* [Jake Wharton](https://github.com/JakeWharton) for his awesome [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids) library
