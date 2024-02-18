package sk.ukf.shoppinglist.Activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import sk.ukf.shoppinglist.R;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView listView = findViewById(R.id.listView);

        // Sample data
        String[] items = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

        CustomAdapter adapter = new CustomAdapter(items);
        listView.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        CustomAdapter(String[] data) {
            super(ListActivity.this, R.layout.list_item_layout, R.id.itemName, data);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_layout, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
                viewHolder.itemNameTextView = convertView.findViewById(R.id.itemName);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // Set data to views
            viewHolder.checkBox.setChecked(false); // Set your logic for checkbox here
            viewHolder.itemNameTextView.setText(getItem(position));

            return convertView;
        }

        private class ViewHolder {
            CheckBox checkBox;
            TextView itemNameTextView;
        }
    }
}
