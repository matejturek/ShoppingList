package sk.ukf.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import sk.ukf.shoppinglist.Requests.AccountManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.email_et);
        passwordEt = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();

            if (email.length() > 0 && password.length() > 0) {
                login(email, password);
            }
        });
    }

    void login(String email, String password) {

        if (email.length() > 0 && password.length() > 0) {
            String loginResponse = AccountManager.login(email, password);
            if (loginResponse.equals("SUCCESS")) {
                SharedPreferences preferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("email", email);
                editor.putString("password", password);
                finishActivity(1);
            } else {
                // TODO: error
            }
        }
    }
}