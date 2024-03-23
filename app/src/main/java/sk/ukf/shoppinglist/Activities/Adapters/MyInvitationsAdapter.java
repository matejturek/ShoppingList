package sk.ukf.shoppinglist.Activities.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import sk.ukf.shoppinglist.Models.Invitation;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;

public class MyInvitationsAdapter extends ArrayAdapter<Invitation> {

    private ArrayList<Invitation> invitations;
    private Context context;
    private CallbackListener listener;

    public MyInvitationsAdapter(Context context, ArrayList<Invitation> invitations, CallbackListener listener) {
        super(context, 0, invitations);
        this.context = context;
        this.invitations = invitations;
        this.listener = listener;
    }
    public interface CallbackListener {
        void onInvitationAction();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_invitations_layout, parent, false);
        }


        Invitation invitation = getItem(position);

        TextView emailTextView = convertView.findViewById(R.id.email_tv);
        ImageView acceptIcon = convertView.findViewById(R.id.accept_icon);
        ImageView removeIcon = convertView.findViewById(R.id.remove_icon);

        if (invitation != null) {
            emailTextView.setText(invitation.getListName());

            if (invitation.getStatus() != 0) {
                ViewGroup parentEl = (ViewGroup) acceptIcon.getParent();
                if (parentEl != null) {
                    parentEl.removeView(acceptIcon);
                }
            } else {
                acceptIcon.setOnClickListener(view -> acceptInvitation(invitation.getId()));
            }
            removeIcon.setOnClickListener(view -> deleteInvitation(invitation.getId()));
        }

        convertView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.invitation_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to remove this invitation?")
                            .setPositiveButton("Yes", (dialog, id) -> {
                                deleteInvitation(invitation.getId());
                            })
                            .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

                    // Create and show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            });

            popupMenu.show();
            return true;
        });

        return convertView;
    }

    private void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    private void deleteInvitation(int invitationId) {
        JSONObject jsonRequest = JsonUtils.deleteInvitation(invitationId);
        NetworkManager.performPostRequest(Endpoints.DELETE_INVITATION.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            listener.onInvitationAction();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Delete invitation error", Toast.LENGTH_LONG).show();
                        Log.e("DELETE INVITATION REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(context, "Delete invitation error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void acceptInvitation(int invitationId) {
        JSONObject jsonRequest = JsonUtils.deleteInvitation(invitationId);
        NetworkManager.performPostRequest(Endpoints.ACCEPT_INVITATION.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if ("success".equals(status)) {
                            listener.onInvitationAction();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Accept invitation error", Toast.LENGTH_LONG).show();
                        Log.e("ACCEPT INVITATION REQUEST", "Error parsing JSON", e);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(context, "Accept invitation error", Toast.LENGTH_LONG).show());
            }
        });
    }

}