package sk.ukf.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sk.ukf.shoppinglist.Activities.Adapters.MyInvitationsAdapter;
import sk.ukf.shoppinglist.Models.Invitation;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.NetworkManager;
import sk.ukf.shoppinglist.Utils.SharedPreferencesManager;

public class MyInvitationsActivity extends AppCompatActivity implements MyInvitationsAdapter.CallbackListener, MyInvitationsAdapter.ErrorActivityCallback {

    ListView invitationsLv;

    ImageView profileIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);

        invitationsLv = findViewById(R.id.invitations_lv);
        invitationsLv.setDivider(null);
        profileIv = findViewById(R.id.profileIcon);
        profileIv.setOnClickListener(view -> {
            Intent intent = new Intent(MyInvitationsActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        getMyInvitations();
    }

    @Override
    public void onInvitationAction() {
        getMyInvitations();
    }

    public void onErrorActivityStarted() {
        Intent errorIntent = new Intent(MyInvitationsActivity.this, ErrorActivity.class);
        errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(errorIntent);
        finish();
    }

    private void getMyInvitations() {
        String userId =  SharedPreferencesManager.getUserId(MyInvitationsActivity.this);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        NetworkManager.performGetRequest(Endpoints.GET_MY_INVITATIONS.getEndpoint(), queryParams, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonResponse = new JSONArray(result);
                        ArrayList<Invitation> invitations = new ArrayList<>();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            int id = jsonObject.getInt("invitationId");
                            int listId = jsonObject.getInt("listId");
                            String listName = jsonObject.getString("name");
                            String email = jsonObject.getString("email");
                            int status = jsonObject.getInt("status");
                            invitations.add(new Invitation(id, listId, listName, Integer.parseInt(userId), email, status));
                        }

                        MyInvitationsAdapter adapter = new MyInvitationsAdapter(MyInvitationsActivity.this, invitations, MyInvitationsActivity.this, MyInvitationsActivity.this);
                        invitationsLv.setAdapter(adapter);

                    } catch (Exception e) {
                        Toast.makeText(MyInvitationsActivity.this, "Get invitations error", Toast.LENGTH_LONG).show();
                        Log.e("GET MY INVITATIONS REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MyInvitationsActivity.this, "Get invitations error", Toast.LENGTH_LONG).show();
                    Log.e("GET MY INVITATIONS REQUEST", error);
                    Intent errorIntent = new Intent(MyInvitationsActivity.this, ErrorActivity.class);
                    errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(errorIntent);
                    finish();
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}