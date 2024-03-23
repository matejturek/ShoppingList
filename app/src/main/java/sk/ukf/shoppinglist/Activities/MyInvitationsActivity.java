package sk.ukf.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

public class MyInvitationsActivity extends AppCompatActivity implements MyInvitationsAdapter.CallbackListener {

    ListView invitationsLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);

        invitationsLv = findViewById(R.id.invitations_lv);

        getMyInvitations();
    }

    @Override
    public void onInvitationAction() {
        getMyInvitations();
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

                        MyInvitationsAdapter adapter = new MyInvitationsAdapter(MyInvitationsActivity.this, invitations, MyInvitationsActivity.this);
                        invitationsLv.setAdapter(adapter);

                    } catch (Exception e) {
                        Toast.makeText(MyInvitationsActivity.this, "Get invitations error", Toast.LENGTH_LONG).show();
                        Log.e("GET MY INVITATIONS REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MyInvitationsActivity.this, "Get invitations error", Toast.LENGTH_LONG).show());
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}