package sk.ukf.shoppinglist.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sk.ukf.shoppinglist.Activities.ListActivity;
import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;

public class CategoriesAdapter extends BaseAdapter {

    private final Context context;
    private final List<Category> categoryList;

    public CategoriesAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_category_layout, parent, false);
        }

        Category category = categoryList.get(position);
        String categoryName = (category == null) ? "" : category.getName();

        TextView categoryNameTextView = convertView.findViewById(R.id.category);
        categoryNameTextView.setText(categoryName);

//        List<Item> items = new ArrayList<>();
//        if (category != null) {
//            items = category.getItems();
//            if (items == null) {
//                items = new ArrayList<>();
//            }
//
//            List<Category> subcategories = category.getSubcategories(categoryList);
//            if (subcategories != null && !subcategories.isEmpty()) {
//                // If there are subcategories, inflate a nested ListView
//                CategoriesAdapter subcategoriesAdapter = new CategoriesAdapter(context, subcategories);
//                ListView subcategoriesListView = convertView.findViewById(R.id.categoryListView);
//                subcategoriesListView.setAdapter(subcategoriesAdapter);
//            }
//        }
//
//        ItemsAdapter itemsAdapter = new ItemsAdapter(context, items);
//        ListView itemsListView = convertView.findViewById(R.id.categoryListView);
//        itemsListView.setAdapter(itemsAdapter);

        return convertView;
    }
}