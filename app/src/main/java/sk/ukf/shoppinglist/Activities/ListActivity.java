package sk.ukf.shoppinglist.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.ukf.shoppinglist.Activities.Adapters.CategoriesAdapter;
import sk.ukf.shoppinglist.Activities.Dialogs.CategoryDialog;
import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;

public class ListActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ListView listView;
    EditText newItemEt;
    Button addItemBtn;
    Spinner typeSpinner;
    String listId;
    List<Category> categories = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    private boolean categoriesCompleted = false;
    private boolean itemsCompleted = false;
    ImageView profileIv, menuIv;
    ArrayList<String> categoriesNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listId = getIntent().getStringExtra("listId");

        listView = findViewById(R.id.listView);
        newItemEt = findViewById(R.id.newItem_et);
        addItemBtn = findViewById(R.id.addItem_btn);
        addItemBtn.setOnClickListener(view -> {
            if (typeSpinner.getSelectedItemPosition() == 0) {
                //Category
                String category = newItemEt.getText().toString().trim();
                if (isValidInput(category)) {
                    createCategory(category);
                }
            } else {
                //Item {
                String item = newItemEt.getText().toString().trim();
                if (isValidInput(item)) {
                    createItem(item);
                }
            }
        });
        categoriesNames = new ArrayList<>();

        typeSpinner = findViewById(R.id.type_sp);

        menuIv = findViewById(R.id.menu);

        menuIv.setOnClickListener(view -> showPopupMenu(view));
        menuIv.setVisibility(View.INVISIBLE);

        init();
    }


    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.list_items_menu);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    private void init() {
        getCategories(listId);
        getItems(listId);
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.create_category) {
            CategoryDialog.showCreateDialog(this, (name, category) -> Toast.makeText(ListActivity.this, name + " " + category, Toast.LENGTH_LONG).show(), categoriesNames);
        }
        return false;
    }

    private void onBothAsyncMethodsCompleted() {
        if (!categoriesCompleted || !itemsCompleted) {
            return;
        }
        Map<String, List<Item>> categoryMap = new HashMap<>();

        if (categories.size() > 0) {
            for (Category category : categories) {
                int categoryId = category.getId();
                List<Item> itemList = new ArrayList<>();

                for (Item item : items) {
                    if (item.getCategoryId() == categoryId) {
                        category.addItem(item);
                        itemList.add(item);
                    }
                }

                // Check if the category name is not null and the item list is not empty
                if (!category.getName().isEmpty() && !itemList.isEmpty()) {
                    categoryMap.put(category.getName(), itemList);
                }
                categoryMap.put(category.getName(), itemList);
                categoriesNames.add(category.getName());
            }
        }
        if (categoriesNames.size() > 0) {
            categoriesNames.add("");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapter);
        } else {
            ViewGroup parent = (ViewGroup) typeSpinner.getParent();
            if (parent != null) {
                parent.removeView(typeSpinner);
            }
        }


        if (items.size() > 0) {
            List<Item> uncategorizedItemList = new ArrayList<>();
            for (Item item : items) {
                if (item.getCategoryId() == -1) {
                    uncategorizedItemList.add(item);
                }
            }
            categoryMap.put("", uncategorizedItemList);
        }


        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(ListActivity.this, categoryMap);
        listView.setAdapter(categoriesAdapter);
        menuIv.setVisibility(View.VISIBLE);
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


    private void createCategory(String category) {

        JSONObject jsonRequest = JsonUtils.createCategory(listId, category);
        NetworkManager.performPostRequest(Endpoints.CREATE_CATEGORY.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
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
                        Toast.makeText(ListActivity.this, "Create category error", Toast.LENGTH_LONG).show();
                        Log.e("CREATE CATEGORY REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListActivity.this, "Create category error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void getCategories(String listId) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("listId", listId);

        NetworkManager.performGetRequest(Endpoints.GET_CATEGORIES.getEndpoint(), queryParams, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                       JSONArray jsonResponse = new JSONArray(result);
                       categories = new ArrayList<>();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            int id = jsonObject.getInt("categoryId");
                            String name = jsonObject.getString("name");
                            int parentId = jsonObject.optInt("parentCategoryId", -1);
                            categories.add(new Category(id, name, parentId));
                        }

                    } catch (Exception e) {
                        Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show();
                        Log.e("GET LIST REQUEST", "Error parsing JSON", e);
                    }
                    finally {
                        categoriesCompleted = true;
                        onBothAsyncMethodsCompleted();
                    }

                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show();

                    categoriesCompleted = true;
                    onBothAsyncMethodsCompleted();
                });
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
                        JSONArray jsonResponse = new JSONArray(result);
                        items = new ArrayList<>();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            int id = jsonObject.getInt("itemId");
                            int categoryId = jsonObject.optInt("categoryId", -1);
                            String name = jsonObject.getString("name");
                            int quantity = jsonObject.getInt("quantity");
                            boolean status = jsonObject.getInt("status") == 1;
                            String link = jsonObject.getString("link");
                            String shelf = jsonObject.getString("shelf");
                            items.add(new Item(id, categoryId, name, quantity, status, link, shelf));
                        }


                    } catch (Exception e) {
                        Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show();
                        Log.e("GET LIST REQUEST", "Error parsing JSON", e);
                    }
                    finally {
                        itemsCompleted = true;
                        onBothAsyncMethodsCompleted();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show());
                itemsCompleted = true;
                onBothAsyncMethodsCompleted();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



}
