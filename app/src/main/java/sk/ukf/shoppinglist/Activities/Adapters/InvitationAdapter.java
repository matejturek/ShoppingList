package sk.ukf.shoppinglist.Activities.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import sk.ukf.shoppinglist.Models.Invitation;
import sk.ukf.shoppinglist.R;

public class InvitationAdapter extends ArrayAdapter<Invitation> {

    private ArrayList<Invitation> invitations;
    private Context context;

    public InvitationAdapter(Context context, ArrayList<Invitation> invitations) {
        super(context, 0, invitations);
        this.context = context;
        this.invitations = invitations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.invite_layout, parent, false);
        }

        Invitation invitation = getItem(position);

        TextView emailTextView = convertView.findViewById(R.id.email_tv);

        if (invitation != null) {
            emailTextView.setText(invitation.getUserEmail());
        }

        convertView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.invitation_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to remove this invitation?")
                            .setPositiveButton("Yes", (dialog, id) -> {
                                deleteInvitation();
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

    private void deleteInvitation() {
        //TODO
    }
}