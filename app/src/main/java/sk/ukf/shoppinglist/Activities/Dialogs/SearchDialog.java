package sk.ukf.shoppinglist.Activities.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import sk.ukf.shoppinglist.R;

public class SearchDialog {

    public interface OnSearchClickListener {
        void onSearchClick(String name);
    }

    public static void showCreateDialog(Context context, final OnSearchClickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.search_dialog, null);

        EditText searchEditText = dialogView.findViewById(R.id.search_et);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Search")
                .setPositiveButton("Search", (dialog, which) -> {
                    String name = searchEditText.getText().toString();
                    listener.onSearchClick(name);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
