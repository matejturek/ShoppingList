package sk.ukf.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.JsonUtils;

public class RegisterActivity extends AppCompatActivity {


    private EditText emailEt, nameEt, passwordEt, passwordRepeatEt;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt = findViewById(R.id.email_et);
        nameEt = findViewById(R.id.name_et);
        passwordEt = findViewById(R.id.password_et);
        passwordRepeatEt = findViewById(R.id.password_repeat_et);

        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(view -> {
            String email = emailEt.getText().toString();
            String name = nameEt.getText().toString();
            String password = passwordEt.getText().toString();
            String passwordRepeat = passwordRepeatEt.getText().toString();

            if (isValidInput(email, name, password, passwordRepeat)) {
                register(email, name, password);
            }
        });
    }

    private boolean isValidInput(String email, String name, String password, String passwordRepeat) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Enter a valid email address");
            return false;
        }

        if (name.isEmpty()) {
            nameEt.setError("Enter your name");
            return false;
        }

        if (password.isEmpty() || password.length() < 5) {
            passwordEt.setError("Password must be at least 5 characters long");
            return false;
        }

        if (!password.equals(passwordRepeat)) {
            passwordRepeatEt.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    void register(String email, String name, String password) {
        JSONObject jsonRequest = JsonUtils.createRegistrationJson(email, name, password);
        NetworkManager.performPostRequest("register.php", jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration error", Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}