package sk.ukf.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.ukf.shoppinglist.Utils.JsonUtils;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    ImageView profileIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = findViewById(R.id.listView);
        profileIv = findViewById(R.id.profileIcon);

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        init();

    }

    private void init() {
        SharedPreferences preferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE);

        String email = preferences.getString("email", "");
        String password = preferences.getString("username", "");


        getLists();
//        if (email.length() > 0 && password.length() > 0) {
//            login(email, password);
//        } else {
//            navigateToLogin();
//        }
    }

    void login(String email, String password) {

        JSONObject jsonRequest = JsonUtils.createLoginJson(email, password);
        NetworkManager.performPostRequest("login.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                    getLists();
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

    private void getLists(){
//        JSONArray jsonArray = new JSONArray();
//
//        // Create first JSON object
//        JSONObject object1 = new JSONObject();
//        object1.put("name", "John Doe");
//
//        // Create second JSON object
//        JSONObject object2 = new JSONObject();
//        object2.put("name", "Jane Smith");
//
//        // Add objects to the array
//        jsonArray.put(object1);
//        jsonArray.put(object2);
//
//        for (int i = 0; i < jsonArray.length(); i++) {
//            // Get the JSON object at index i
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//            // Access and print values
//            String name = jsonObject.getString("name");
//        }
        String[] data = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

        CustomAdapter adapter = new CustomAdapter(data);

        // Set the adapter for the ListView
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

//        NetworkManager.performPostRequest("`getLists`.php", null, new NetworkManager.ResultCallback() {
//            @Override
//            public void onSuccess(String result) {
//            }
//
//            @Override
//            public void onError(String error) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_LONG).show());
//            }
//        });
    }

    private class CustomAdapter extends BaseAdapter {

        private final String[] data;

        public CustomAdapter(String[] data) {
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

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    startActivity(intent);
                }
            });
            ImageView menuBtn = convertView.findViewById(R.id.listview_menu);
            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the button click action here
                    // For example, show a Toast message
                    showRecordMenu(v);
                }
            });

            // Get the TextView and set the text for the current item
            TextView textView = convertView.findViewById(R.id.listview_name);
            textView.setText(data[position]);

            return convertView;
        }
    }

    private void showRecordMenu(View view) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);

        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());

        // Set an item click listener for the menu items
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks here
                if (item.getItemId() == R.id.menu_edit) {
                    // Handle Edit click
                    Toast.makeText(MainActivity.this, "Edit clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.menu_invite) {
                    // Handle Invite click
                    Toast.makeText(MainActivity.this, "Invite clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    // Handle Delete click
                    Toast.makeText(MainActivity.this, "Delete clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }
}