package sk.ukf.shoppinglist.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.ukf.shoppinglist.Activities.Dialogs.InvitationDialog;
import sk.ukf.shoppinglist.Models.ListItem;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.SharedPreferencesManager;
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        createListFab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListManagementActivity.class);
            intent.putExtra("MODE", MODE_CREATE);
            createListLauncher.launch(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onBackPressed() {
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        ConfirmationDialogFragment dialogFragment = new ConfirmationDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "ConfirmationDialog");
    }

    public static class ConfirmationDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Exit App?");
            builder.setMessage("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> getActivity().finish());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            return builder.create();
        }
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

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private class ListAdapter extends BaseAdapter {

        private final ListItem[] data;

        public ListAdapter(ListItem[] data) {
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
                intent.putExtra("listId", String.valueOf(data[position].getId()));
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
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);

        popupMenu.getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                ListItem listItem = ((ListItem) listview.getItemAtPosition(position));
                String listId = String.valueOf(listItem.getId());

                Intent editIntent = new Intent(MainActivity.this, ListManagementActivity.class);
                editIntent.putExtra("MODE", MODE_EDIT);
                editIntent.putExtra("listId", listId);
                createListLauncher.launch(editIntent);

                return true;
            } else if (item.getItemId() == R.id.menu_invite) {
                ListItem listItem = ((ListItem) listview.getItemAtPosition(position));
                String listId = String.valueOf(listItem.getId());
                InvitationDialog.showCreateDialog(MainActivity.this, new InvitationDialog.OnCreateClickListener() {
                    @Override
                    public void onCreateClick(String email) {
                        invitePerson(listId, email);
                    }
                });
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

        popupMenu.show();
    }

    private void showDeleteConfirmationDialog(String listId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this list?");

        builder.setPositiveButton("Delete", (dialog, which) -> deleteList(listId));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void login(String email, String password) {

        JSONObject jsonRequest = JsonUtils.createLoginJson(email, password);
        NetworkManager.performPostRequest(Endpoints.LOGIN.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            String userId = jsonResponse.getString("userId");

                            SharedPreferencesManager.saveEmail(MainActivity.this, email);
                            SharedPreferencesManager.savePassword(MainActivity.this, password);
                            SharedPreferencesManager.saveUserId(MainActivity.this, userId);

                            getLists(userId);
                        } else {
                            Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_LONG).show();
                            Log.e("LOGIN REQUEST", "Error: " + message);
                            SharedPreferencesManager.clearData(MainActivity.this);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_LONG).show();
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
        NetworkManager.performPostRequest(Endpoints.DELETE_LIST.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
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
                        Toast.makeText(MainActivity.this, "Delete list error", Toast.LENGTH_LONG).show();
                        Log.e("DELETE LIST REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Delete list error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void getLists(String userId) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        NetworkManager.performGetRequest(Endpoints.GET_LISTS.getEndpoint(), queryParams, new NetworkManager.ResultCallback() {
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

                        ListItem[] data = listItems.toArray(new ListItem[0]);

                        ListAdapter adapter = new ListAdapter(data);

                        ListView listView = findViewById(R.id.listView);
                        listView.setAdapter(adapter);

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Get list error", Toast.LENGTH_LONG).show();
                        Log.e("GET LIST REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Get list error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void invitePerson(String listId, String email) {
        JSONObject jsonRequest = JsonUtils.createInviteJson(listId, email);
        NetworkManager.performPostRequest(Endpoints.CREATE_INVITATION.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            Toast.makeText(MainActivity.this, "Person invited", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Invitation error", Toast.LENGTH_LONG).show();
                            Log.e("INVITE REQUEST", "Error: " + message);
                            SharedPreferencesManager.clearData(MainActivity.this);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Invitation error", Toast.LENGTH_LONG).show();
                        Log.e("INVITE REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Invitation error", Toast.LENGTH_LONG).show());
            }
        });
    }

}