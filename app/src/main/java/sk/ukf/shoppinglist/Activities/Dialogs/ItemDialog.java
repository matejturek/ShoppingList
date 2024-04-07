package sk.ukf.shoppinglist.Activities.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;

public class ItemDialog {

    public interface OnCreateClickListener {
        void onCreateClick(int quantity, String name, String shelf, String link, int categoryId);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick();
    }

    public static void showCreateDialog(Context context, final OnCreateClickListener createListener, final OnDeleteClickListener deleteListener, Item item, ArrayList<Category> categories) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.item_dialog, null);

        EditText quantityEt = dialogView.findViewById(R.id.quantity_et);
        EditText nameEt = dialogView.findViewById(R.id.name_et);
        EditText shelfEt = dialogView.findViewById(R.id.shelf_et);
        EditText linkEt = dialogView.findViewById(R.id.link_et);
        Spinner categorySpinner = dialogView.findViewById(R.id.category_sp);

        quantityEt.setText(String.valueOf(item.getQuantity()));
        nameEt.setText(String.valueOf(item.getName()));
        if (item.getShelf() != null && item.getShelf().length() > 0) {
            shelfEt.setText(String.valueOf(item.getShelf()));
        }
        if (item.getLink() != null && item.getLink().length() > 0) {
            linkEt.setText(String.valueOf(item.getLink()));
        }
        if (categories.size() > 0) {
            ArrayList<String> categoriesNames = new ArrayList<>();
            for (Category category: categories) {
                categoriesNames.add(category.getName());
            }
            categoriesNames.add("Uncategorized");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoriesNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();


            if (item.getCategoryId() > 0) {
                int categoryIndex = getCategoryIndexById(categories, item.getCategoryId());
                if (categoryIndex != -1) {
                    categorySpinner.setSelection(categoryIndex);
                }
            }

        } else {
            categorySpinner.setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Edit item")
                .setPositiveButton("Edit", (dialog, which) -> {
                    int quantity = Integer.parseInt(quantityEt.getText().toString());
                    String name = nameEt.getText().toString();
                    String shelf = shelfEt.getText().toString();
                    String link = linkEt.getText().toString();
                    int categoryIndex = categorySpinner.getSelectedItemPosition();
                    int categoryId = -1;
                    if (categoryIndex < categories.size()) {
                        categoryId = categories.get(categoryIndex).getId();
                    }
                    createListener.onCreateClick(quantity, name, shelf, link, categoryId);
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

    private static int getCategoryIndexById(ArrayList<Category> categories, int categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == categoryId) {
                return i;
            }
        }
        return -1; // Return -1 if category ID not found
    }
}
