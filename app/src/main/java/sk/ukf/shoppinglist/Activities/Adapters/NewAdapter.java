package sk.ukf.shoppinglist.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sk.ukf.shoppinglist.Activities.Dialogs.ItemDialog;
import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;

public class NewAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private ArrayList<Category> categories;
    private ArrayList<Item> items;
    private ArrayList<Object> mergedData; // Combined list of categories and items

    public NewAdapter(Context context, ArrayList<Category> categories, ArrayList<Item> items) {
        this.context = context;
        this.categories = sortCategories(categories);
        this.items = sortItems(items);

        this.mergedData = generateMergedData();
    }

    private ArrayList<Category> sortCategories(ArrayList<Category> unsortedCategories) {
        // Sort categories based on some criteria (e.g., category ID or name)
        Collections.sort(unsortedCategories, new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {
                // Modify this based on your sorting criteria
                return category1.getName().compareTo(category2.getName());
            }
        });
        return unsortedCategories;
    }

    private ArrayList<Item> sortItems(ArrayList<Item> unsortedItems) {
        // Sort items based on some criteria (e.g., item ID or name)
        Collections.sort(unsortedItems, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                // Modify this based on your sorting criteria
                return item1.getName().compareTo(item2.getName());
            }
        });
        return unsortedItems;
    }

    private ArrayList<Object> generateMergedData() {
        ArrayList<Object> mergedList = new ArrayList<>();

        for (Category category : categories) {
            mergedList.add(category);
        }
        for (Item item : items) {
            if (item.getCategoryId() == -1) {
                mergedList.add(item);
            }
        }

        return mergedList;
    }

    @Override
    public int getCount() {
        return mergedData.size();
    }

    @Override
    public Object getItem(int position) {
        return mergedData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // Use position as the ID
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Two types: Category and Item
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof Category) {
            return VIEW_TYPE_CATEGORY;
        } else if (item instanceof Item) {
            return VIEW_TYPE_ITEM;
        }
        return -1; // Unknown type
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        int viewType = getItemViewType(position);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            switch (viewType) {
                case VIEW_TYPE_CATEGORY:
                    convertView = inflater.inflate(R.layout.list_category_layout, parent, false);
                    holder.categoryNameTextView = convertView.findViewById(R.id.category);
                    holder.containerLayout = convertView.findViewById(R.id.containerLayout);
                    break;

                case VIEW_TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
                    holder.checkBox = convertView.findViewById(R.id.checkBox);
                    holder.quantityTv = convertView.findViewById(R.id.quantity_tv);
                    holder.nameTv = convertView.findViewById(R.id.name_tv);
                    holder.shelfTv = convertView.findViewById(R.id.shelf_tv);
                    holder.editIv = convertView.findViewById(R.id.edit_iv);
                    holder.linkIv = convertView.findViewById(R.id.link_iv);
                    break;

                // Other cases...

                default:
                    // Handle default case or throw an exception
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind data to views using holder

        switch (viewType) {
            case VIEW_TYPE_CATEGORY:
                Category category = (Category) getItem(position);
                String categoryName = (category == null) ? "" : category.getName();

                holder.categoryNameTextView.setText(categoryName);
                holder.containerLayout.removeAllViews(); // Clear previous views

                // Handle subcategories and items here
                List<Category> subcategories = category.getSubcategories();
                // Handle items if no subcategories
                List<Item> items = category.getItems();
                if (items != null && !items.isEmpty()) {
                    for (Item categoryItem : items) {
                        View itemView = inflater.inflate(R.layout.list_item_layout, parent, false);
                        // Bind data for Item
                        CheckBox checkBox = itemView.findViewById(R.id.checkBox);
                        TextView quantityTv = itemView.findViewById(R.id.quantity_tv);
                        TextView nameTv = itemView.findViewById(R.id.name_tv);
                        TextView shelfTv = itemView.findViewById(R.id.shelf_tv);
                        ImageView editIv = itemView.findViewById(R.id.edit_iv);
                        ImageView linkIv = itemView.findViewById(R.id.link_iv);

                        checkBox.setOnCheckedChangeListener(null);
                        checkBox.setChecked(categoryItem.getStatus());
                        quantityTv.setText(String.valueOf(categoryItem.getQuantity()));
                        nameTv.setText(categoryItem.getName());
                        if (categoryItem.getShelf() != null && categoryItem.getShelf().length() > 0) {
                            shelfTv.setText(categoryItem.getShelf());
                        }

                        // Add other item bindings here

                        holder.containerLayout.addView(itemView);
                    }
                }
                if (subcategories != null && !subcategories.isEmpty()) {
                    for (Category subcategory : subcategories) {
                        View subcategoryView = inflater.inflate(R.layout.list_category_layout, parent, false);
                        TextView subcategoryNameTextView = subcategoryView.findViewById(R.id.category);
                        subcategoryNameTextView.setText(subcategory.getName());
                        holder.containerLayout.addView(subcategoryView);

                        // Handle items if subcategory exists
                        List<Item> subcategoryItems = subcategory.getItems();
                        if (subcategoryItems != null && !subcategoryItems.isEmpty()) {
                            for (Item subcategoryItem : subcategoryItems) {
                                View itemView = inflater.inflate(R.layout.list_item_layout, parent, false);
                                // Bind data for Item
                                CheckBox checkBox = itemView.findViewById(R.id.checkBox);
                                TextView quantityTv = itemView.findViewById(R.id.quantity_tv);
                                TextView nameTv = itemView.findViewById(R.id.name_tv);
                                TextView shelfTv = itemView.findViewById(R.id.shelf_tv);
                                ImageView editIv = itemView.findViewById(R.id.edit_iv);
                                ImageView linkIv = itemView.findViewById(R.id.link_iv);

                                checkBox.setOnCheckedChangeListener(null);
                                checkBox.setChecked(subcategoryItem.getStatus());
                                quantityTv.setText(String.valueOf(subcategoryItem.getQuantity()));
                                nameTv.setText(subcategoryItem.getName());
                                if (subcategoryItem.getShelf() != null && subcategoryItem.getShelf().length() > 0) {
                                    shelfTv.setText(subcategoryItem.getShelf());
                                }

                                // Add other item bindings here

                                holder.containerLayout.addView(itemView);
                            }
                        }
                    }
                }

                break;

            case VIEW_TYPE_ITEM:
                Item item = (Item) getItem(position);

                holder.checkBox.setOnCheckedChangeListener(null);
                holder.checkBox.setChecked(item.getStatus());
                holder.quantityTv.setText(String.valueOf(item.getQuantity()));
                holder.nameTv.setText(item.getName());
                if (item.getShelf() != null && item.getShelf().length() > 0) {
                    holder.shelfTv.setText(item.getShelf());
                }

                // Add other item bindings here

                // Handle item click listeners here

                break;

            // Other cases...

            default:
                // Handle default case or throw an exception
        }

        return convertView;
    }

    static class ViewHolder {
        TextView categoryNameTextView;
        LinearLayout containerLayout;
        CheckBox checkBox;
        TextView quantityTv;
        TextView nameTv;
        TextView shelfTv;
        ImageView editIv;
        ImageView linkIv;
        // Add other views here
    }


    public void setItemChecked(String itemId, Boolean checked) {
        JSONObject jsonRequest = JsonUtils.setItemStatusJson(itemId, checked);
        NetworkManager.performPostRequest(Endpoints.SET_ITEM_STATUS.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");
                    if ("success".equals(status)) {
                        //DO nothing
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Item save error", Toast.LENGTH_LONG).show();
                    Log.e("ITEM SET ERROR", "Error parsing JSON", e);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ITEM SET ERROR", "Error parsing JSON");
            }
        });
    }

    public void setItem(int itemId, int quantity, String name, String shelf, String link) {
        JSONObject jsonRequest = JsonUtils.setItem(itemId, quantity, name, shelf, link);
        NetworkManager.performPostRequest(Endpoints.SET_ITEM.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");
                    if ("success".equals(status)) {
                        //TODO refresh
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Item save error", Toast.LENGTH_LONG).show();
                    Log.e("ITEM SET ERROR", "Error parsing JSON", e);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ITEM SET ERROR", "Error parsing JSON");
            }
        });
    }
}
