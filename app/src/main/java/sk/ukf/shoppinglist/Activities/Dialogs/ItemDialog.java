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

import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;

public class ItemDialog {

    public interface OnCreateClickListener {

        void onCreateClick(int quantity, String name, String shelf, String link);
    }

    public static void showCreateDialog(Context context, final OnCreateClickListener listener, Item item) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.item_dialog, null);

        EditText quantityEt = dialogView.findViewById(R.id.quantity_et);
        EditText nameEt = dialogView.findViewById(R.id.name_et);
        EditText shelfEt = dialogView.findViewById(R.id.shelf_et);
        EditText linkEt = dialogView.findViewById(R.id.link_et);

        quantityEt.setText(String.valueOf(item.getQuantity()));
        nameEt.setText(String.valueOf(item.getName()));
        if (item.getShelf() != null && item.getShelf().length() > 0) {
            shelfEt.setText(String.valueOf(item.getShelf()));
        }
        if (item.getLink() != null && item.getLink().length() > 0) {
            linkEt.setText(String.valueOf(item.getLink()));
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Create category")
                .setPositiveButton("Edit", (dialog, which) -> {
                    int quantity = Integer.parseInt(quantityEt.getText().toString());
                    String name = nameEt.getText().toString();
                    String shelf = shelfEt.getText().toString();
                    String link = linkEt.getText().toString();
                    listener.onCreateClick(quantity, name, shelf, link);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}