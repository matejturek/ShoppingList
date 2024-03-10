package sk.ukf.shoppinglist.Activities;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.ukf.shoppinglist.Activities.Adapters.CategoriesAdapter;
import sk.ukf.shoppinglist.Activities.Adapters.NewAdapter;
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
    Spinner categorySpinner;
    String listId;
    ArrayList<Category> allCategories = new ArrayList<>();
    ArrayList<Category> sortedCategories = new ArrayList<>();
    ArrayList<Item> items = new ArrayList<>();
    private boolean categoriesCompleted = false;
    private boolean itemsCompleted = false;
    ImageView profileIv, menuIv;
    ArrayList<String> categoriesNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listId = getIntent().getStringExtra("listId");

        listView  = findViewById(R.id.listView);
        newItemEt = findViewById(R.id.newItem_et);
        addItemBtn = findViewById(R.id.addItem_btn);
        menuIv = findViewById(R.id.menu);
        profileIv = findViewById(R.id.profileIcon);
        addItemBtn.setOnClickListener(view -> {
            String item = newItemEt.getText().toString().trim();
            int categoryId = -1;
            if (allCategories.size() > 0) {
                categoryId = allCategories.get(categorySpinner.getSelectedItemPosition()).getId();
            }
            if (isValidInput(item)) {
                createItem(item, categoryId);
            }
        });
        categoriesNames = new ArrayList<>();
        categorySpinner = findViewById(R.id.type_sp);

        menuIv.setOnClickListener(view -> showPopupMenu(view));
        menuIv.setVisibility(View.INVISIBLE);

        profileIv.setOnClickListener(view -> {
            Intent intent = new Intent(ListActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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
            CategoryDialog.showCreateDialog(this, new CategoryDialog.OnCreateClickListener() {
                @Override
                public void onCreateClick(String name, String categoryName) {
                    Category foundCategory = allCategories.stream()
                            .filter(category -> categoryName.equals(category.getName()))
                            .findFirst()
                            .orElse(null);
                    createCategory(name, (foundCategory != null ? foundCategory.getId() : -1));
                }
            }, categoriesNames);
        }
        return false;
    }

    private void onBothAsyncMethodsCompleted() {
        if (!categoriesCompleted || !itemsCompleted) {
            return;
        }

        sortedCategories = getCategoryData();

        for (Category category: allCategories) {
            categoriesNames.add(category.getName());
        }

        if (categoriesNames.size() > 0) {
            categoriesNames.add("");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        } else {
            ViewGroup parent = (ViewGroup) categorySpinner.getParent();
            if (parent != null) {
                parent.removeView(categorySpinner);
            }
        }

        NewAdapter adapter = new NewAdapter(ListActivity.this, sortedCategories, items);
        listView.setAdapter(adapter);
        menuIv.setVisibility(View.VISIBLE);
    }

    private ArrayList<Category> getCategoryData() {
        ArrayList<Category> rootCategories = new ArrayList<>();
        Map<Integer, Category> categoryMap = new HashMap<>();

        for (Category category : allCategories) {
            int parentId = category.getParentId();
            if (parentId == -1) {
                rootCategories.add(category);
            } else {
                Category parentCategory = categoryMap.get(parentId);
                if (parentCategory != null) {
                    if (parentCategory.getSubcategories() == null) {
                        parentCategory.setSubcategories(new ArrayList<>());
                    }
                    parentCategory.getSubcategories().add(category);
                }
            }
            for (Item item : items) {
                if (item.getCategoryId() == category.getId()) {
                    category.addItem(item);
                }
            }
            categoryMap.put(category.getId(), category);
        }

        return rootCategories;
    }

    private boolean isValidInput(String item) {
        if (item.isEmpty()) {
            newItemEt.setError("Enter item");
            return false;
        }
        return true;
    }

    private void createItem(String item, int categoryId) {

        JSONObject jsonRequest = JsonUtils.createItem(listId, item, categoryId);
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
                            init();
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


    private void createCategory(String category, int parentCategory) {

        JSONObject jsonRequest = JsonUtils.createCategory(listId, category, parentCategory);
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
                        allCategories = new ArrayList<>();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            int id = jsonObject.getInt("categoryId");
                            String name = jsonObject.getString("name");
                            int parentId = jsonObject.optInt("parentCategoryId", -1);
                            allCategories.add(new Category(id, name, parentId));
                        }

                    } catch (Exception e) {
                        Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show();
                        Log.e("GET LIST REQUEST", "Error parsing JSON", e);
                    } finally {
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
                            if (link.equals("null")) {
                                link = null;
                            }
                            String shelf = jsonObject.getString("shelf");
                            if (shelf.equals("null")) {
                                shelf = null;
                            }
                            items.add(new Item(id, categoryId, name, quantity, status, link, shelf));
                        }


                    } catch (Exception e) {
                        Toast.makeText(ListActivity.this, "Get list error", Toast.LENGTH_LONG).show();
                        Log.e("GET LIST REQUEST", "Error parsing JSON", e);
                    } finally {
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
