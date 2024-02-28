package sk.ukf.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.SharedPreferencesManager;
import sk.ukf.shoppinglist.Utils.JsonUtils;

public class ListManagementActivity extends AppCompatActivity {

    EditText nameEt, notesEt;
    Button submitBtn;

    private int mode;
    private String listId;

    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_management);

        nameEt = findViewById(R.id.name_et);
        notesEt = findViewById(R.id.notes_et);
        submitBtn = findViewById(R.id.submit_btn);

        mode = getIntent().getIntExtra("MODE", MODE_CREATE);
        if (mode == MODE_EDIT) {
            listId = getIntent().getStringExtra("listId");
            getList(listId);
        }

        submitBtn.setOnClickListener(view -> {
            String name = nameEt.getText().toString().trim();
            String notes = notesEt.getText().toString().trim();
            if (isValidInput(name, notes)) {
                if (mode == MODE_CREATE) {
                    createList(name, notes);
                } else {
                    editList(listId, name, notes);
                }
            }
        });
    }

    private boolean isValidInput(String name, String notes) {
        if (name.isEmpty()) {
            nameEt.setError("Enter list name");
            return false;
        }

        return true;
    }

    private void createList(String name, String notes) {

        JSONObject jsonRequest = JsonUtils.createListJson(SharedPreferencesManager.getUserId(ListManagementActivity.this), name, notes);
        NetworkManager.performPostRequest("createList.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ListManagementActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ListManagementActivity.this, "Create list error", Toast.LENGTH_LONG).show();
                        Log.e("CREATE LIST REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListManagementActivity.this, "Create list error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void getList(String listId) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("listId", listId);
        NetworkManager.performGetRequest(Endpoints.GET_LIST_DETAILS.getEndpoint(), queryParams, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String name = jsonResponse.getString("name");
                        String notes = jsonResponse.getString("notes");
                        fillValues(name, notes);
                    } catch (Exception e ) {
                        Toast.makeText(ListManagementActivity.this, "Get list error", Toast.LENGTH_LONG).show();
                        Log.e("GET LIST REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListManagementActivity.this, "Get list error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void editList(String listId, String name, String notes) {

        JSONObject jsonRequest = JsonUtils.editListJson(listId, name, notes);
        NetworkManager.performPostRequest(Endpoints.EDIT_LIST.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");
                    if ("success".equals(status)) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(ListManagementActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ListManagementActivity.this, "Edit list error", Toast.LENGTH_LONG).show();
                    Log.e("EDIT LIST REQUEST", "Error parsing JSON", e);
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ListManagementActivity.this, "Edit list error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void fillValues(String name, String notes) {
        nameEt.setText(name);
        notesEt.setText(notes);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}