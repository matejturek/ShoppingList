package sk.ukf.shoppinglist.Activities.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class InvitationAdapter extends ArrayAdapter<Invitation> {

    private final ArrayList<Invitation> invitations;
    private final Context context;
    private final CallbackListener listener;
    private final ErrorActivityCallback errorListener;

    public InvitationAdapter(Context context, ArrayList<Invitation> invitations, CallbackListener listener, ErrorActivityCallback errorListener) {
        super(context, 0, invitations);
        this.context = context;
        this.invitations = invitations;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    public interface ErrorActivityCallback {
        void onErrorActivityStarted();
    }

    public interface CallbackListener {
        void onInvitationAction();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.invite_layout, parent, false);
        }


        Invitation invitation = getItem(position);

        TextView emailTextView = convertView.findViewById(R.id.email_tv);
        ImageView statusIcon = convertView.findViewById(R.id.status_icon);

        if (invitation != null) {
            emailTextView.setText(invitation.getUserEmail());

            // Set icon based on invitation status
            if (invitation.getStatus() == 0) {
                statusIcon.setImageResource(R.drawable.ic_pending_black);
            } else {
                statusIcon.setImageResource(R.drawable.ic_accepted_black);
            }
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
                runOnUiThread(() -> {
                    Toast.makeText(context, "Delete invitation error", Toast.LENGTH_LONG).show();
                    Log.e("DELETE INVITATION REQUEST", error);
                    errorListener.onErrorActivityStarted();
                });
            }
        });
    }
}