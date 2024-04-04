package sk.ukf.shoppinglist.Activities.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import sk.ukf.shoppinglist.R;

public class CategoryDialog {

    public interface OnCreateClickListener {
        void onCreateClick(String name);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick();
    }

    public static void showCreateDialog(Context context, final OnCreateClickListener listener, final OnDeleteClickListener deleteListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.category_dialog, null);

        EditText nameEditText = dialogView.findViewById(R.id.name_et);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle(deleteListener == null ? "Create category" : "Edit category")
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = nameEditText.getText().toString();
                    listener.onCreateClick(name);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());


        if (deleteListener != null) {
            builder.setNeutralButton("Delete", (dialog, which) -> {
                deleteListener.onDeleteClick();
                dialog.dismiss();
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
