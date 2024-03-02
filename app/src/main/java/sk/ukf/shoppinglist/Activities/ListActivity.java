package sk.ukf.shoppinglist.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import sk.ukf.shoppinglist.Utils.Endpoints;
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

    private void createItem(String item) {

        JSONObject jsonRequest = JsonUtils.createItem(listId, item);
        NetworkManager.performPostRequest(Endpoints.CREATE_ITEM.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
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

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("listId", listId);
        NetworkManager.performGetRequest(Endpoints.GET_ITEMS.getEndpoint(), queryParams, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONArray itemsArray = new JSONArray(result);

                        Map<String, List<Item>> categoryMap = new HashMap<>();

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject categoryObject = itemsArray.getJSONObject(i);

                            String categoryName = categoryObject.getString("categoryName");
                            JSONArray itemsArrayForCategory = categoryObject.getJSONArray("items");

                            List<Item> itemList = new ArrayList<>();

                            for (int j = 0; j < itemsArrayForCategory.length(); j++) {
                                JSONObject itemObject = itemsArrayForCategory.getJSONObject(j);

                                Item newItem = new Item(
                                        itemObject.getInt("itemId"),
                                        itemObject.getString("name"),
                                        (itemObject.getString("status").equals("1")),
                                        itemObject.getInt("quantity")
                                );

                                itemList.add(newItem);
                            }

                            categoryMap.put(categoryName, itemList);
                        }
                        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(ListActivity.this, categoryMap);
                        listView.setAdapter(categoriesAdapter);

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
