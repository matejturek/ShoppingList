package sk.ukf.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.Utils.SharedPreferencesManager;

public class ProfileActivity extends AppCompatActivity {

    EditText emailEt, nameEt;
    TextView welcomeUserTv;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailEt = findViewById(R.id.email_et);
        nameEt = findViewById(R.id.name_et);
        welcomeUserTv = findViewById(R.id.welcomeUser_tv);

        saveBtn = findViewById(R.id.save_btn);

        saveBtn.setOnClickListener(view -> {
            String name = nameEt.getText().toString().trim();
            if (isValidInput(name)) {
                editProfile(name);
            }
        });
        getUserDetails();
    }
    private boolean isValidInput(String name) {
        if (name.isEmpty()) {
            nameEt.setError("Enter your name");
            return false;
        }
        return true;
    }


    private void editProfile(String name) {
        JSONObject jsonRequest = JsonUtils.editUserJson(SharedPreferencesManager.getUserId(ProfileActivity.this), name);
        NetworkManager.performPostRequest("editUser.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            fillValues(name);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Error editing profile", Toast.LENGTH_LONG).show();
                            Log.e("EDIT PROFILE REQUEST", "Error: " + message);
                        }

                    } catch (Exception e ) {
                        Toast.makeText(ProfileActivity.this, "Error editing profile", Toast.LENGTH_LONG).show();
                        Log.e("EDIT PROFILE REQUEST", "Error parsing JSON", e);
                    }

                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error editing profile", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void getUserDetails() {
        JSONObject jsonRequest = JsonUtils.getUserDetailsJson(SharedPreferencesManager.getUserId(ProfileActivity.this));
        NetworkManager.performPostRequest("getUserDetails.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String email = jsonResponse.getString("email");
                        String name = jsonResponse.getString("name");
                        fillValues(email, name);

                    } catch (Exception e ) {
                        Toast.makeText(ProfileActivity.this, "Error getting profile", Toast.LENGTH_LONG).show();
                        Log.e("GET USER DETAILS REQUEST", "Error parsing JSON", e);
                    }

                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error getting profile", Toast.LENGTH_LONG).show());
            }
        });
    }


    private void fillValues(String name) {
        welcomeUserTv.setText("Welcome " + name);
    }

    private void fillValues(String email, String name) {
        emailEt.setText(email);
        nameEt.setText(name);
        welcomeUserTv.setText("Welcome " + name);
    }

}