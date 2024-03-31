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
        void onCreateClick(String name, String category);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick();
    }

    public static void showCreateDialog(Context context, final OnCreateClickListener listener, final OnDeleteClickListener deleteListener, ArrayList<String> categories) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.category_dialog, null);

        EditText nameEditText = dialogView.findViewById(R.id.name_et);
        Spinner categorySpinner = dialogView.findViewById(R.id.category_et);

        if (categories.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        } else {
            ViewGroup parent = (ViewGroup) categorySpinner.getParent();
            if (parent != null) {
                parent.removeView(categorySpinner);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Create category")
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = nameEditText.getText().toString();
                    String category = null;
                    if (categories.size() > 0) {
                        category = categories.get(categorySpinner.getSelectedItemPosition());
                    }
                    listener.onCreateClick(name, category);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());


        if (deleteListener != null) {
            builder.setNeutralButton("Delete", (dialog, which) -> {
                deleteListener.onDeleteClick();
                // Dismiss the dialog after performing the delete action
                dialog.dismiss();
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
