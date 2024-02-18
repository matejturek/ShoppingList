package sk.ukf.shoppinglist.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;

public class ItemsAdapter extends BaseAdapter {

    private final Context context;
    private final List<Item> items;

    public ItemsAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate or reuse a layout for each item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        // Get the views within the item layout
        EditText itemNameEt = convertView.findViewById(R.id.itemName_et);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        // Get the current item
        Item currentItem = items.get(position);

        // Set data to the views
        itemNameEt.setText(currentItem.getName());
        checkBox.setChecked(currentItem.getStatus());

        return convertView;
    }
}