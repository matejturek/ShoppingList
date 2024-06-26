package sk.ukf.shoppinglist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sk.ukf.shoppinglist.Activities.Adapters.InvitationAdapter;
import sk.ukf.shoppinglist.Activities.Adapters.ListAdapter;
import sk.ukf.shoppinglist.Activities.Dialogs.CategoryDialog;
import sk.ukf.shoppinglist.Activities.Dialogs.SearchDialog;
import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;

public class ListActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ListAdapter.CallbackListener, ListAdapter.ErrorActivityCallback {

    ListView listView;
    EditText newItemEt, searchEt;
    Button addItemBtn, filterBtn, cancelBtn, searchBtn;
    Spinner categorySpinner, sortSpinner;
    String listId;
    ArrayList<Category> allCategories = new ArrayList<>();
    ArrayList<Category> sortedCategories = new ArrayList<>();
    ArrayList<Item> items = new ArrayList<>();
    private boolean categoriesCompleted = false;
    private boolean itemsCompleted = false;
    ImageView profileIv, menuIv;
    ArrayList<String> categoriesNames;
    ListAdapter adapter;

    LinearLayout filterLl, addLl;


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
        filterBtn = findViewById(R.id.filter_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        filterLl = findViewById(R.id.filter_ll);
        addLl = findViewById(R.id.add_ll);
        searchEt = findViewById(R.id.search_et);
        searchBtn = findViewById(R.id.search_btn);
        sortSpinner = findViewById(R.id.sort_sp);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterLl.setVisibility(View.VISIBLE);
                addLl.setVisibility(View.GONE);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterLl.setVisibility(View.GONE);
                addLl.setVisibility(View.VISIBLE);
                adapter.clearSearch();
            }
        });


        String[] sortOptions = {"CLEAR", "ascending", "descending"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSortOption = sortOptions[position];
                if (selectedSortOption.equals("ascending")) {
                    adapter.sort(true);
                } else if (selectedSortOption.equals("descending")) {
                    adapter.sort(false);
                } else {
                    adapter.clearSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchQuery = searchEt.getText().toString();
                if (searchQuery.length() > 0) {
                    adapter.search(searchQuery);
                } else {
                    adapter.clearSearch();
                }
            }
        });


        addItemBtn.setOnClickListener(view -> {
            String item = newItemEt.getText().toString().trim();
            int categoryId = -1;
            if (allCategories.size() > 0) {
                int position = categorySpinner.getSelectedItemPosition();
                if (position < allCategories.size()) {
                    categoryId = allCategories.get(position).getId();
                }
            }
            if (isValidInput(item)) {
                createItem(item, categoryId);
                newItemEt.setText("");
            }
        });
        categoriesNames = new ArrayList<>();
        categorySpinner = findViewById(R.id.category_sp);

        menuIv.setOnClickListener(view -> showPopupMenu(view));
        menuIv.setVisibility(View.INVISIBLE);

        profileIv.setOnClickListener(view -> {
            Intent intent = new Intent(ListActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        init();
    }

    @Override
    public void onListAction() {
        adapter.clearData();
        init();
    }

    public void onErrorActivityStarted() {
        Intent errorIntent = new Intent(ListActivity.this, ErrorActivity.class);
        errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(errorIntent);
        finish();
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
                public void onCreateClick(String name) {
                    createCategory(name);
                }
            }, null);
        }
//        else if (item.getItemId() == R.id.search) {
//            SearchDialog.showCreateDialog(this, new SearchDialog.OnSearchClickListener() {
//                @Override
//                public void onSearchClick(String query) {
//                    adapter.search(query);
//                }
//            });
//        } else if (item.getItemId() == R.id.clear_search) {
//            adapter.clearData();
//            init();
//        }else if (item.getItemId() == R.id.sort_asc) {
//            adapter.sort(true);
//        } else if (item.getItemId() == R.id.sort_desc) {
//            adapter.sort(false);
//        }
        return false;
    }

    private void onBothAsyncMethodsCompleted() {
        if (!categoriesCompleted || !itemsCompleted) {
            return;
        }

        sortedCategories = getCategoryData();
        categoriesNames = new ArrayList<>();
        for (Category category: allCategories) {
            categoriesNames.add(category.getName());
        }

        if (categoriesNames.size() > 0) {
            categoriesNames.add("Uncategorized");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            ViewGroup parent = (ViewGroup) categorySpinner.getParent();
            if (parent != null) {
                parent.removeView(categorySpinner);
            }
        }

        adapter = new ListAdapter(ListActivity.this, sortedCategories, categoriesNames, items, ListActivity.this, ListActivity.this);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        menuIv.setVisibility(View.VISIBLE);
    }

    private ArrayList<Category> getCategoryData() {
        ArrayList<Category> rootCategories = new ArrayList<>();
        Map<Integer, Category> categoryMap = new HashMap<>();

        for (Category category : allCategories) {
            rootCategories.add(category);
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
                            adapter.clearData();
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
                runOnUiThread(() -> {
                    Toast.makeText(ListActivity.this, "Create item error", Toast.LENGTH_LONG).show();
                    Log.e("CREATE ITEM REQUEST", error);
                    Intent errorIntent = new Intent(ListActivity.this, ErrorActivity.class);
                    errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(errorIntent);
                    finish();
                });
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
                            categoriesCompleted = false;
                            itemsCompleted = false;
                            getCategories(listId);
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
                runOnUiThread(() -> {
                    Toast.makeText(ListActivity.this, "Create category error", Toast.LENGTH_LONG).show();
                    Log.e("CREATE CATEGORY REQUEST", error);
                    Intent errorIntent = new Intent(ListActivity.this, ErrorActivity.class);
                    errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(errorIntent);
                    finish();
                });
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
                            allCategories.add(new Category(id, name));
                        }

                    } catch (Exception e) {
                        Toast.makeText(ListActivity.this, "Get categories error", Toast.LENGTH_LONG).show();
                        Log.e("GET CATEGORIES REQUEST", "Error parsing JSON", e);
                    } finally {
                        categoriesCompleted = true;
                        onBothAsyncMethodsCompleted();
                    }

                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ListActivity.this, "Get categories error", Toast.LENGTH_LONG).show();
                    categoriesCompleted = true;
                    Log.e("GET CATEGORIES REQUEST", error);
                    onBothAsyncMethodsCompleted();
                    Intent errorIntent = new Intent(ListActivity.this, ErrorActivity.class);
                    errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(errorIntent);
                    finish();
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
                        Toast.makeText(ListActivity.this, "Get items error", Toast.LENGTH_LONG).show();
                        Log.e("GET ITEMS REQUEST", "Error parsing JSON", e);
                    } finally {
                        itemsCompleted = true;
                        onBothAsyncMethodsCompleted();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListActivity.this, "Get items error", Toast.LENGTH_LONG).show());
                itemsCompleted = true;
                Log.e("GET ITEMS REQUEST", error);
                onBothAsyncMethodsCompleted();
                Intent errorIntent = new Intent(ListActivity.this, ErrorActivity.class);
                errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(errorIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
