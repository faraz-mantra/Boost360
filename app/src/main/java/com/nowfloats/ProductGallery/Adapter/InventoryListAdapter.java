package com.nowfloats.ProductGallery.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nowfloats.webactions.models.WebAction;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 13-04-2018.
 */

public class InventoryListAdapter extends ArrayAdapter<WebAction> {
    public InventoryListAdapter(@NonNull Context context, int resource, @NonNull List<WebAction> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WebAction webAction = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_list_row_layout, parent, false);
        }

        TextView tvDisplayName = convertView.findViewById(R.id.tv_inventory_display_name);
        tvDisplayName.setText(webAction.getDisplayName());
        return convertView;
    }
}
