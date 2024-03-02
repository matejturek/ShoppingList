package sk.ukf.shoppinglist.Activities.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import sk.ukf.shoppinglist.Activities.ListActivity;
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

        // Get the views within the item layout
        EditText itemNameEt = convertView.findViewById(R.id.itemName_et);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        // Get the current item
        Item currentItem = items.get(position);

        // Set data to the views
        itemNameEt.setText(currentItem.getName());
        checkBox.setChecked(currentItem.getStatus());

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
}