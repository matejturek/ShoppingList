package sk.ukf.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import sk.ukf.shoppinglist.Requests.AccountManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    void init() {
        SharedPreferences preferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE);

        String email = preferences.getString("email", "");
        String password = preferences.getString("username", "");

        if (email.length() > 0 && password.length() > 0) {
            String loginResponse = AccountManager.login(email, password);
            if (loginResponse.equals("SUCCESS")) {
                // TODO: continue

            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }


}