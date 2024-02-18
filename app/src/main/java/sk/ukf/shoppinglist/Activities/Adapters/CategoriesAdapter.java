package sk.ukf.shoppinglist.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;

public class CategoriesAdapter extends BaseAdapter {

    private final Context context;
    private final Map<String, List<Item>> categories;

    public CategoriesAdapter(Context context, Map<String, List<Item>> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        // Convert the map values to a list and get the item at the specified position
        List<List<Item>> categoryLists = (List<List<Item>>) categories.values();
        return categoryLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate or reuse a layout for each item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_category_layout, parent, false);
        }

        // Get the category name and associated items
        String categoryName = (String) categories.keySet().toArray()[position];
        List<Item> items = categories.get(categoryName);

        // Set the category name to a TextView
        TextView categoryNameTextView = convertView.findViewById(R.id.category);
        categoryNameTextView.setText(categoryName);

        // Use another adapter (e.g., ItemsAdapter) to display the items within the category
        ItemsAdapter itemsAdapter = new ItemsAdapter(context, items);
        ListView itemsListView = convertView.findViewById(R.id.categoryListView);
        itemsListView.setAdapter(itemsAdapter);

        return convertView;
    }
}