package sk.ukf.shoppinglist.Activities.Dialogs;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import sk.ukf.shoppinglist.R;

public class InvitationDialog {

    public interface OnCreateClickListener {
        void onCreateClick(String email);
    }

    public static void showCreateDialog(Context context, final OnCreateClickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.invite_dialog, null);

        EditText emailEt = dialogView.findViewById(R.id.email_et);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Invite person")
                .setPositiveButton("Invite", (dialog, which) -> {
                    String email = emailEt.getText().toString();
                    listener.onCreateClick(email);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
