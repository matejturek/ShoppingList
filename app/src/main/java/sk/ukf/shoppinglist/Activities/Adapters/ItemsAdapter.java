package sk.ukf.shoppinglist.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import sk.ukf.shoppinglist.Activities.Dialogs.CategoryDialog;
import sk.ukf.shoppinglist.Activities.Dialogs.ItemDialog;
import sk.ukf.shoppinglist.Activities.ListActivity;
import sk.ukf.shoppinglist.Models.Category;
import sk.ukf.shoppinglist.Models.Item;
import sk.ukf.shoppinglist.R;
import sk.ukf.shoppinglist.Utils.Endpoints;
import sk.ukf.shoppinglist.Utils.JsonUtils;
import sk.ukf.shoppinglist.Utils.NetworkManager;

public class ItemsAdapter extends BaseAdapter {

    private final Context context;
    private final List<Item> items;

    public ItemsAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate or reuse a layout for each item
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        TextView quantityTv = convertView.findViewById(R.id.quantity_tv);
        TextView nameTv = convertView.findViewById(R.id.name_tv);
        TextView shelfTv = convertView.findViewById(R.id.shelf_tv);
        ImageView editIv = convertView.findViewById(R.id.edit_iv);
        ImageView linkIv = convertView.findViewById(R.id.link_iv);

        Item currentItem = items.get(position);

        checkBox.setChecked(currentItem.getStatus());
        quantityTv.setText(String.valueOf(currentItem.getQuantity()));
        nameTv.setText(currentItem.getName());
        if (currentItem.getShelf() != null && currentItem.getShelf().length() > 0 ){
            shelfTv.setText(currentItem.getShelf());
        }

        if (currentItem.getLink() != null && currentItem.getLink().length() > 0 ){
            linkIv.setOnClickListener(view -> {
                try {
                    Uri uri = Uri.parse(currentItem.getLink());
                    if (uri != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalArgumentException e) {
                    // Handle the case where the URL is not valid (e.g., malformed URL)
                    Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
//            ViewGroup parentEl = (ViewGroup) linkIv.getParent();
//            if (parentEl != null) {
//                parentEl.removeView(linkIv);
//            }
        }
        editIv.setOnClickListener(view -> {
            ItemDialog.showCreateDialog(context, new ItemDialog.OnCreateClickListener() {
                @Override
                public void onCreateClick(int quantity, String name, String shelf, String link) {
                    setItem(currentItem.getId(), quantity, name, shelf, link);
                }
            }, currentItem);

        });

        checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            setItemChecked(String.valueOf(currentItem.getId()), checked);
        });

        return convertView;
    }

    public void setItemChecked(String itemId, Boolean checked) {
        JSONObject jsonRequest = JsonUtils.setItemStatusJson(itemId, checked);
        NetworkManager.performPostRequest(Endpoints.SET_ITEM_STATUS.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");
                    if ("success".equals(status)) {
                        //DO nothing
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Item save error", Toast.LENGTH_LONG).show();
                    Log.e("ITEM SET ERROR", "Error parsing JSON", e);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ITEM SET ERROR", "Error parsing JSON");
            }
        });
    }
    public void setItem(int itemId, int quantity, String name, String shelf, String link) {
        JSONObject jsonRequest = JsonUtils.setItem(itemId, quantity, name, shelf, link);
        NetworkManager.performPostRequest(Endpoints.SET_ITEM.getEndpoint(), jsonRequest, new NetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");
                    if ("success".equals(status)) {
                        //TODO refresh
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Item save error", Toast.LENGTH_LONG).show();
                    Log.e("ITEM SET ERROR", "Error parsing JSON", e);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ITEM SET ERROR", "Error parsing JSON");
            }
        });
    }
}