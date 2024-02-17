package sk.ukf.shoppinglist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sk.ukf.shoppinglist.Models.ListItem;
import sk.ukf.shoppinglist.Utils.JsonUtils;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    ImageView profileIv;

    FloatingActionButton createListFab;

    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listView);
        profileIv = findViewById(R.id.profileIcon);
        createListFab = findViewById(R.id.fab);

        profileIv.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        createListFab.setOnClickListener(view -> {
            Intent createIntent = new Intent(MainActivity.this, ListManagementActivity.class);
            createIntent.putExtra("MODE", MODE_CREATE);
            createListLauncher.launch(createIntent);

        });
        init();

    }

    private final ActivityResultLauncher<Intent> createListLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            getLists(SharedPreferencesManager.getUserId(MainActivity.this));
        }
    });

    private void init() {
        String userId = SharedPreferencesManager.getUserId(MainActivity.this);
        String email = SharedPreferencesManager.getEmail(MainActivity.this);
        String password = SharedPreferencesManager.getPassword(MainActivity.this);


        if (email.length() > 0 && password.length() > 0 && userId.length() > 0) {
            login(email, password);
        } else {
            navigateToLogin();
        }
    }

    void login(String email, String password) {

        JSONObject jsonRequest = JsonUtils.createLoginJson(email, password);
        NetworkManager.performPostRequest("login.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            // Successful response, handle accordingly
                            String userId = jsonResponse.getString("userId");

                            SharedPreferencesManager.saveEmail(MainActivity.this, email);
                            SharedPreferencesManager.savePassword(MainActivity.this, password);
                            SharedPreferencesManager.saveUserId(MainActivity.this, userId);

                            getLists(userId);
                        } else {
                            // Handle other scenarios
                            Log.e("LOGIN REQUEST", "Error: " + message);
                            SharedPreferencesManager.clearData(MainActivity.this);
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void getLists(String userId) {

        JSONObject jsonRequest = JsonUtils.getListsJson(userId);
        NetworkManager.performPostRequest("getLists.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonResponse = new JSONArray(result);
                        List<ListItem> listItems = new ArrayList<>();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            int id = jsonObject.getInt("listId");
                            String name = jsonObject.getString("name");
                            listItems.add(new ListItem(id, name));
                        }

                        // Step 3: Convert to array
                        ListItem[] data = listItems.toArray(new ListItem[0]);

                        CustomAdapter adapter = new CustomAdapter(data);

                        // Set the adapter for the ListView
                        ListView listView = findViewById(R.id.listView);
                        listView.setAdapter(adapter);

                    } catch (Exception e) {
                        Log.e("LOGIN REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void deleteList(String listId) {

        JSONObject jsonRequest = JsonUtils.deleteListJson(listId);
        NetworkManager.performPostRequest("deleteList.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            getLists(SharedPreferencesManager.getUserId(MainActivity.this));
                        } else {
                            Log.e("DELETE REQUEST", "Error: " + message);
                        }

                    } catch (Exception e) {
                        Log.e("LOGIN REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {

        private final ListItem[] data;

        public CustomAdapter(ListItem[] data) {
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
                convertView = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
            }

            convertView.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            });
            ImageView menuBtn = convertView.findViewById(R.id.listview_menu);
            menuBtn.setOnClickListener(view -> showRecordMenu(view, position));

            TextView textView = convertView.findViewById(R.id.listview_name);
            textView.setText(data[position].getName());

            return convertView;
        }
    }

    private void showRecordMenu(View view, int position) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);

        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());

        // Set an item click listener for the menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            // Handle menu item clicks here
            if (item.getItemId() == R.id.menu_edit) {
                // Handle Edit click
                ListItem listItem = ((ListItem) listview.getItemAtPosition(position));
                String listId = String.valueOf(listItem.getId());

                Intent editIntent = new Intent(MainActivity.this, ListManagementActivity.class);
                editIntent.putExtra("MODE", MODE_EDIT);
                editIntent.putExtra("listId", listId);
                createListLauncher.launch(editIntent);

                return true;
            } else if (item.getItemId() == R.id.menu_invite) {
                // Handle Invite click
                Toast.makeText(MainActivity.this, "Invite clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                ListItem listItem = ((ListItem) listview.getItemAtPosition(position));
                String listId = String.valueOf(listItem.getId());
                showDeleteConfirmationDialog(listId);
                return true;
            } else {
                return false;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    private void showDeleteConfirmationDialog(String listId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this list?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Handle the deletion here
            deleteList(listId);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing, simply dismiss the dialog
            dialog.dismiss();
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}