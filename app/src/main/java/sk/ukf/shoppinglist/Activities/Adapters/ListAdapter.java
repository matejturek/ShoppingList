package sk.ukf.shoppinglist.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sk.ukf.shoppinglist.Activities.Dialogs.CategoryDialog;
import sk.ukf.shoppinglist.Activities.Dialogs.ItemDialog;
import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;

public class ListAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    final private Context context;
    final private ArrayList<Category> categories;
    final private ArrayList<String> categoriesNames;
    final private ArrayList<Item> items;
    final private ArrayList<Object> mergedData;
    final private ArrayList<Object> unfilteredData;
    private final CallbackListener listener;
    private final ErrorActivityCallback errorListener;

    public ListAdapter(Context context, ArrayList<Category> categories, ArrayList<String> categoriesNames, ArrayList<Item> items, CallbackListener listener, ErrorActivityCallback errorListener) {
        this.context = context;
        this.categories = sortCategories(categories);
        this.categoriesNames = categoriesNames;
        this.items = sortItems(items);
        this.mergedData = generateMergedData();
        this.unfilteredData = new ArrayList<>();
        this.unfilteredData.addAll(this.mergedData);
        this.listener = listener;
        this.errorListener = errorListener;
    }

    public interface ErrorActivityCallback {
        void onErrorActivityStarted();
    }

    public interface CallbackListener {
        void onListAction();
    }

    public void clearData() {
        categories.clear();
        categoriesNames.clear();
        items.clear();
        notifyDataSetChanged();
    }

    public void clearSearch() {
        mergedData.clear();
        mergedData.addAll(unfilteredData);
        notifyDataSetChanged();
    }
    public void search(String query) {
        ArrayList<Object> filteredList = new ArrayList<>();

        for (Object data : unfilteredData) {
            if (data instanceof Category) {
                for (Item item: ((Category) data).getItems()) {
                    if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
            }
            if (data instanceof Item) {
                Item item = (Item) data;
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(data);
                }
            }
        }

        mergedData.clear();
        mergedData.addAll(filteredList);
        notifyDataSetChanged();
    }

    public void sort(boolean ascending) {
        ArrayList<Object> filteredList = new ArrayList<>();

        for (Object data : unfilteredData) {
            if (data instanceof Category) {
                filteredList.addAll(((Category) data).getItems());
            }
            if (data instanceof Item) {
                filteredList.add(data);
            }
        }


        filteredList.sort((o1, o2) -> {
            String name1 = ((Item) o1).getName().toLowerCase();
            String name2 = ((Item) o2).getName().toLowerCase();
            return ascending ? name1.compareTo(name2) : name2.compareTo(name1);
        });

        mergedData.clear();
        mergedData.addAll(filteredList);
        notifyDataSetChanged();
    }

    private ArrayList<Category> sortCategories(ArrayList<Category> unsortedCategories) {
        unsortedCategories.sort(Comparator.comparing(Category::getName));
        return unsortedCategories;
    }

    private ArrayList<Item> sortItems(ArrayList<Item> unsortedItems) {
        unsortedItems.sort(Comparator.comparing(Item::getName));
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
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof Category) {
            return VIEW_TYPE_CATEGORY;
        } else if (item instanceof Item) {
            return VIEW_TYPE_ITEM;
        }
        return -1;
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
                    convertView.setOnLongClickListener(view -> {
                        CategoryDialog.showCreateDialog(context, name -> {
                            Category category = (Category) getItem(position);
                            editCategory(category.getId(), name);
                        }, () -> deleteCategory(categories.get(position).getId()));
                        return false;
                    });

                    if (position % 2 == 0) {
                        convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    } else {
                        convertView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    break;

                case VIEW_TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
                    holder.checkBox = convertView.findViewById(R.id.checkBox);
                    holder.quantityTv = convertView.findViewById(R.id.quantity_tv);
                    holder.nameTv = convertView.findViewById(R.id.name_tv);
                    holder.shelfTv = convertView.findViewById(R.id.shelf_tv);
                    holder.linkIv = convertView.findViewById(R.id.link_iv);
                    convertView.setOnLongClickListener(view -> {

                        int adjustedPosition = getAdjustedPosition(position);
                        ItemDialog.showCreateDialog(context, (quantity, name, shelf, link, categoryId) -> setItem(items.get(adjustedPosition).getId(), quantity, name, shelf, link, categoryId), () -> deleteItem(items.get(position).getId()), items.get(position), categories);
                        return false;
                    });
                    if (position % 2 == 0) {
                        convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                    } else {
                        convertView.setBackgroundColor(Color.TRANSPARENT);
                    }

                    break;
                default:
                    break;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (viewType) {
            case VIEW_TYPE_CATEGORY:
                Category category = (Category) getItem(position);
                String categoryName = (category == null) ? "" : category.getName();

                holder.categoryNameTextView.setText(categoryName);
                holder.containerLayout.removeAllViews();

                List<Item> items = category.getItems();
                if (items != null && !items.isEmpty()) {
                    for (Item categoryItem : items) {
                        View itemView = inflater.inflate(R.layout.list_item_layout, parent, false);
                        CheckBox checkBox = itemView.findViewById(R.id.checkBox);
                        TextView quantityTv = itemView.findViewById(R.id.quantity_tv);
                        TextView nameTv = itemView.findViewById(R.id.name_tv);
                        TextView shelfTv = itemView.findViewById(R.id.shelf_tv);
                        ImageView linkIv = itemView.findViewById(R.id.link_iv);

                        checkBox.setOnCheckedChangeListener(null);
                        checkBox.setChecked(categoryItem.getStatus());
                        quantityTv.setText(String.valueOf(categoryItem.getQuantity()));
                        nameTv.setText(categoryItem.getName());
                        if (categoryItem.getShelf() != null && categoryItem.getShelf().length() > 0) {
                            shelfTv.setText(categoryItem.getShelf());
                        }

                        checkBox.setChecked(categoryItem.getStatus());
                        quantityTv.setText(String.valueOf(categoryItem.getQuantity()));
                        nameTv.setText(categoryItem.getName());
                        if (categoryItem.getShelf() != null && categoryItem.getShelf().length() > 0) {
                            shelfTv.setText(categoryItem.getShelf());
                        }

                        if (categoryItem.getLink() != null && categoryItem.getLink().length() > 0) {
                            linkIv.setOnClickListener(view -> {
                                Uri uri = Uri.parse(categoryItem.getLink());

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);

                                if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
                                    context.startActivity(browserIntent);
                                } else {
                                }
                            });
                        } else {
                            if (linkIv != null) {
                                ViewGroup parentEl = (ViewGroup) linkIv.getParent();
                                if (parentEl != null) {
                                    parentEl.removeView(linkIv);
                                }
                            }
                        }
                        itemView.setOnLongClickListener(view -> {
                            ItemDialog.showCreateDialog(context, (quantity, name, shelf, link, categoryId) -> setItem(categoryItem.getId(), quantity, name, shelf, link, categoryId), () -> deleteItem(categoryItem.getId()), categoryItem, categories);
                            return false;
                        });

                        checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                            setItemChecked(String.valueOf(categoryItem.getId()), checked);
                        });

                        holder.containerLayout.addView(itemView);
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

                holder.checkBox.setChecked(item.getStatus());
                holder.quantityTv.setText(String.valueOf(item.getQuantity()));
                holder.nameTv.setText(item.getName());
                if (item.getShelf() != null && item.getShelf().length() > 0) {
                    holder.shelfTv.setText(item.getShelf());
                }

                if (item.getLink() != null && item.getLink().length() > 0) {
                    holder.linkIv.setOnClickListener(view -> {
                        Uri uri = Uri.parse(item.getLink());

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);

                        if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(browserIntent);
                        } else {
                        }
                    });
                } else {
                    if (holder.linkIv != null) {
                        ViewGroup parentEl = (ViewGroup) holder.linkIv.getParent();
                        if (parentEl != null) {
                            parentEl.removeView(holder.linkIv);
                        }
                    }
                }
                holder.checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                    setItemChecked(String.valueOf(item.getId()), checked);
                });

                break;

            default:
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
        ImageView linkIv;
    }

    private int getAdjustedPosition(int position) {
        int categoryCount = 0;
        for (int i = 0; i < mergedData.size(); i++) {
            if (mergedData.get(i) instanceof Category) {
                categoryCount++;
            }
            if (categoryCount == position) {
                return i;
            }
        }
        return -1; // Return -1 if no adjustment is possible
    }


    private void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public void setItemChecked(String itemId, Boolean checked) {
        JSONObject jsonRequest = JsonUtils.setItemStatusJson(itemId, checked);
        NetworkManager.performPostRequest(Endpoints.SET_ITEM_STATUS.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {

                runOnUiThread(() -> {
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
                        Toast.makeText(context, "Item set error", Toast.LENGTH_LONG).show();
                        Log.e("ITEM SET ERROR", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Item set error", Toast.LENGTH_LONG).show();
                    Log.e("ITEM SET ERROR", error);
                    errorListener.onErrorActivityStarted();
                });
            }
        });
    }

    public void setItem(int itemId, int quantity, String name, String shelf, String link, int categoryId) {
        JSONObject jsonRequest = JsonUtils.setItem(itemId, quantity, name, shelf, link, categoryId);
        NetworkManager.performPostRequest(Endpoints.SET_ITEM.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            listener.onListAction();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Item set error", Toast.LENGTH_LONG).show();
                        Log.e("ITEM SET ERROR", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Item set error", Toast.LENGTH_LONG).show();
                    Log.e("ITEM SET ERROR", error);
                    errorListener.onErrorActivityStarted();
                });
            }
        });
    }

    private void deleteItem(int itemId) {
        JSONObject jsonRequest = JsonUtils.deleteItemJson(itemId);
        NetworkManager.performPostRequest(Endpoints.DELETE_ITEM.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            listener.onListAction();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Item delete error", Toast.LENGTH_LONG).show();
                        Log.e("ITEM DELETE ERROR", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Item delete error", Toast.LENGTH_LONG).show();
                    Log.e("ITEM DELETE ERROR", error);
                    errorListener.onErrorActivityStarted();
                });
            }
        });
    }

    private void deleteCategory(int categoryId) {
        JSONObject jsonRequest = JsonUtils.deleteCategoryJson(categoryId);
        NetworkManager.performPostRequest(Endpoints.DELETE_CATEGORY.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            listener.onListAction();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Delete category error", Toast.LENGTH_LONG).show();
                        Log.e("DELETE CATEGORY REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Delete category error", Toast.LENGTH_LONG).show();
                    Log.e("DELETE CATEGORY REQUEST", error);
                    errorListener.onErrorActivityStarted();
                });
            }
        });
    }

    private void editCategory(int categoryId, String category) {

        JSONObject jsonRequest = JsonUtils.editCategoryJson(categoryId, category);
        NetworkManager.performPostRequest(Endpoints.SET_CATEGORY.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            listener.onListAction();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Edit category error", Toast.LENGTH_LONG).show();
                        Log.e("EDIT CATEGORY REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Edit category error", Toast.LENGTH_LONG).show();
                    Log.e("EDIT CATEGORY REQUEST", error);
                    errorListener.onErrorActivityStarted();
                });
            }
        });
    }
}
