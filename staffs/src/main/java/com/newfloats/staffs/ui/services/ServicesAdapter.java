package com.newfloats.staffs.ui.services;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.framework.views.customViews.CustomCheckBox;
import com.newfloats.staffs.R;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_service, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.customCheckBox.setText(ServicesModel.getAllServices().get(position));
    }

    @Override
    public int getItemCount() {
        return ServicesModel.getAllServices().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomCheckBox customCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customCheckBox = itemView.findViewById(R.id.ccb_services);
        }
    }
}
