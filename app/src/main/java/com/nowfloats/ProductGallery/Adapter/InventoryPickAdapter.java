package com.nowfloats.ProductGallery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.inventoryorder.databinding.ItemBottomSheetPickInventoryNatureBinding;
import com.inventoryorder.model.bottomsheet.PickInventoryNatureModel;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InventoryPickAdapter extends RecyclerView.Adapter<InventoryPickAdapter.MyViewHolder> {

    private ArrayList<PickInventoryNatureModel> list;
    private Context mContext;
    private ItemClickListener mClickListener;
    private LayoutInflater layoutInflater;

    public InventoryPickAdapter(Context mContext, ArrayList<PickInventoryNatureModel> list, ItemClickListener mClickListener) {
        this.mContext = mContext;
        this.list = list;
        this.mClickListener = mClickListener;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(this.mContext);
        ItemBottomSheetPickInventoryNatureBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_bottom_sheet_pick_inventory_nature, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
        PickInventoryNatureModel data = list.get(position);
        Integer icon = data.getSelectIcon();
        holder.binding.tvInventoryName.setTextColor(ContextCompat.getColor(mContext, data.getColorTitle()));
        holder.binding.tvInventoryDescription.setTextColor(ContextCompat.getColor(mContext, data.getColorDesc()));
        holder.binding.inventorySelectedOrUnselectedIcon.setImageResource(icon);
        holder.binding.tvInventoryName.setText(data.getInventoryName());
        holder.binding.tvInventoryDescription.setText(data.getInventoryDescription());
        Integer inventoryIcon = data.getIconType();
        if (inventoryIcon != null) holder.binding.ivInventoryType.setImageResource(inventoryIcon);
        holder.binding.mainView.setOnClickListener(v -> mClickListener.onItemClick(position, data, 1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position, PickInventoryNatureModel item, int actionType);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemBottomSheetPickInventoryNatureBinding binding;

        public MyViewHolder(final ItemBottomSheetPickInventoryNatureBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }
}
