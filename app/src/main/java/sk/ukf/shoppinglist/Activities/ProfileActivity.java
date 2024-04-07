package sk.ukf.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.Utils.SharedPreferencesManager;

public class ProfileActivity extends AppCompatActivity {

    EditText emailEt, nameEt;
    TextView welcomeUserTv, dynamicLetterTv;
    Button saveBtn, logoutBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailEt = findViewById(R.id.email_et);
        nameEt = findViewById(R.id.name_et);
        welcomeUserTv = findViewById(R.id.welcomeUser_tv);
        dynamicLetterTv = findViewById(R.id.dynamicLetter_tv);

        saveBtn = findViewById(R.id.save_btn);
        logoutBtn = findViewById(R.id.logout_btn);

        saveBtn.setOnClickListener(view -> {
            String name = nameEt.getText().toString().trim();
            if (isValidInput(name)) {
                editProfile(name);
            }
        });

        logoutBtn.setOnClickListener(view -> logout());
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
        NetworkManager.performPostRequest(Endpoints.EDIT_ACCOUNT.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
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
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Error editing profile", Toast.LENGTH_LONG).show();
                    Log.e("EDIT PROFILE REQUEST", error);
                    Intent errorIntent = new Intent(ProfileActivity.this, ErrorActivity.class);
                    errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(errorIntent);
                    finish();
                });
            }
        });
    }

    private void getUserDetails() {
        JSONObject jsonRequest = JsonUtils.getUserDetailsJson(SharedPreferencesManager.getUserId(ProfileActivity.this));
        NetworkManager.performPostRequest(Endpoints.GET_ACCOUNT_DETAILS.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
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
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Error getting profile", Toast.LENGTH_LONG).show();
                    Log.e("GET USER DETAILS REQUEST", error);
                    Intent errorIntent = new Intent(ProfileActivity.this, ErrorActivity.class);
                    errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(errorIntent);
                    finish();
                });
            }
        });
    }

    private void logout() {
        SharedPreferencesManager.clearData(ProfileActivity.this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void fillValues(String name) {
        welcomeUserTv.setText("Welcome " + name);
        dynamicLetterTv.setText(String.valueOf(name.charAt(0)).toUpperCase());
    }

    @SuppressLint("SetTextI18n")
    private void fillValues(String email, String name) {
        emailEt.setText(email);
        nameEt.setText(name);
        welcomeUserTv.setText("Welcome " + name);
        dynamicLetterTv.setText(String.valueOf(name.charAt(0)).toUpperCase());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}