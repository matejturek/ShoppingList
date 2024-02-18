package sk.ukf.shoppinglist.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sk.ukf.shoppinglist.Activities.Adapters.CategoriesAdapter;
import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    EditText newItemEt;
    Button addItemBtn;
    String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listId = getIntent().getStringExtra("listId");

        listView = findViewById(R.id.listView);
        newItemEt = findViewById(R.id.newItem_et);
        addItemBtn = findViewById(R.id.addItem_btn);
        addItemBtn.setOnClickListener(view -> {
            String item = newItemEt.getText().toString().trim();
            if (isValidInput(item)) {
                createItem(item);
            }
        });

        getItems(listId);
    }

    private boolean isValidInput(String item) {
        if (item.isEmpty()) {
            newItemEt.setError("Enter item");
            return false;
        }
        return true;
    }

    private class ItemsAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_HEADER = 1;

        private final List<Object> data;

        public ItemsAdapter(List<Object> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return data.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return 2; // Number of view types (items and headers)
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = getItemViewType(position);

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                switch (viewType) {
                    case TYPE_ITEM:
                        convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
                        break;
                    case TYPE_HEADER:
                        convertView = inflater.inflate(R.layout.list_category_layout, parent, false);
                        break;
                }
            }

            switch (viewType) {
                case TYPE_ITEM:
                    // Populate item data
                    EditText itemNameEt = convertView.findViewById(R.id.itemName_et);
                    CheckBox checkBox = convertView.findViewById(R.id.checkBox);
                    Item item = (Item) data.get(position);
                    itemNameEt.setText(item.getName());
                    checkBox.setChecked(item.getStatus());
                    break;
                case TYPE_HEADER:
                    // Populate header data
                    TextView headerTextView = convertView.findViewById(R.id.category);
                    String headerText = (String) data.get(position);
                    headerTextView.setText(headerText);
                    break;
            }

            return convertView;
        }
    }


    private void createItem(String item) {

        JSONObject jsonRequest = JsonUtils.createItem(listId, item);
        NetworkManager.performPostRequest("createItem.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            getItems(listId);
                        } else {
                            Toast.makeText(ListActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ListActivity.this, "Create item error", Toast.LENGTH_LONG).show();
                        Log.e("CREATE ITEM REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListActivity.this, "Create item error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void getItems(String listId) {

        JSONObject jsonRequest = JsonUtils.getItemsJson(listId);
        NetworkManager.performPostRequest("getItems.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        // Create a Map to store categories and their items
                        Map<String, List<Item>> categoryMap = new HashMap<>();

                        // Iterate through each category in the JSON response
                        Iterator<String> keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            String categoryName = keys.next();

                            // Get the JSONArray for the current category
                            JSONArray itemsArray = jsonObject.getJSONArray(categoryName);

                            // Create a list to store items for the current category
                            List<Item> itemList = new ArrayList<>();

                            // Iterate through items array
                            for (int j = 0; j < itemsArray.length(); j++) {
                                JSONObject itemObject = itemsArray.getJSONObject(j);

                                // Extract values from the item JSON object
                                int itemId = itemObject.getInt("itemId");
//                                int listId = itemObject.getInt("listId");
//                                Integer categoryId = itemObject.isNull("categoryId") ? null : itemObject.getInt("categoryId");
                                String name = itemObject.getString("name");
//                                int quantity = itemObject.getInt("quantity");
//                                int status = itemObject.getInt("status");
//                                String link = itemObject.isNull("link") ? null : itemObject.getString("link");
//                                String shelf = itemObject.isNull("shelf") ? null : itemObject.getString("shelf");

                                // Create an Item object
                                Item item = new Item(itemId, name, false, 1);

                                // Add the item to the list
                                itemList.add(item);
                            }

                            // Add the category and its associated items to the map
                            categoryMap.put(categoryName, itemList);
                        }

                        // Now you have a Map containing categories and their associated items

                        // Example of using the Map to populate the adapters
                        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(ListActivity.this, categoryMap);
                        listView.setAdapter(categoriesAdapter);
                        // Now, categoryMap contains categories and their associated items
                        // You can use this map to populate your UI

                    } catch (Exception e) {
                        Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show();
                        Log.e("GET LIST REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
