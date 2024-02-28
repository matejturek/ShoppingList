package sk.ukf.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.SharedPreferencesManager;
import sk.ukf.shoppinglist.Utils.JsonUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.email_et);
        passwordEt = findViewById(R.id.password_et);
        Button loginBtn = findViewById(R.id.login_btn);
        Button registerBtn = findViewById(R.id.register_btn);

        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();

            if (email.length() > 0 && password.length() > 0) {
                login(email, password);
            }
        });

        registerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    void login(String email, String password) {

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
                            // Successful response, handle accordingly
                            String userId = jsonResponse.getString("userId");

                            SharedPreferencesManager.saveEmail(LoginActivity.this, email);
                            SharedPreferencesManager.savePassword(LoginActivity.this, password);
                            SharedPreferencesManager.saveUserId(LoginActivity.this, userId);
                            finishActivityWithResult(RESULT_OK);
                        } else {
                            // Handle other scenarios
                            Log.e("LOGIN REQUEST", "Error: " + message);
                            SharedPreferencesManager.clearData(LoginActivity.this);
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_LONG).show();
                        Log.e("LOGIN REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void finishActivityWithResult(int result) {
        Intent resultIntent = new Intent();
        setResult(result, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}