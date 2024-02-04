package sk.ukf.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

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
                    if (result.equals("ERROR_EMAIL_EXISTS")) {
                        Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_LONG).show();
                    } else if (result.equals("SUCCESS")) {
                        finish();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration error", Toast.LENGTH_LONG).show());
            }
        });
    }
}