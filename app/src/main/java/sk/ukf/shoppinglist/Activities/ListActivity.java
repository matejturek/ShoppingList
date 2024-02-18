package sk.ukf.shoppinglist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.Models.ListItem;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.Utils.SharedPreferencesManager;

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

        private final Item[] data;

        public ItemsAdapter(Item[] data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflate or reuse a layout for each item
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_layout, parent, false);
            }

            EditText itemNameEt = convertView.findViewById(R.id.itemName_et);
            CheckBox checkBox = convertView.findViewById(R.id.checkBox);
            itemNameEt.setText(data[position].getName());
            checkBox.setChecked(data[position].getStatus());

            return convertView;
        }
    }

    private void createItem(String item) {

        JSONObject jsonRequest = JsonUtils.createItem(listId, item);
        NetworkManager.performPostRequest("createItem.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {JSONObject jsonResponse = new JSONObject(result);
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
                        JSONArray jsonResponse = new JSONArray(result);
                        List<Item> listItems = new ArrayList<>();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            int id = jsonObject.getInt("itemId");
                            String name = jsonObject.getString("name");
                            Boolean status = jsonObject.getString("status").equals("1");
                            listItems.add(new Item(id, name, status));
                        }

                        // Step 3: Convert to array
                        Item[] data = listItems.toArray(new Item[0]);

                        ItemsAdapter adapter = new ItemsAdapter(data);

                        // Set the adapter for the ListView
                        ListView listView = findViewById(R.id.listView);
                        listView.setAdapter(adapter);

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
}
