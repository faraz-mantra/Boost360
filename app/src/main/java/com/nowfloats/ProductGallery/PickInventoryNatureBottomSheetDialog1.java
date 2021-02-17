package com.nowfloats.ProductGallery;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.inventoryorder.databinding.BottomSheetPickInventoryNatureBinding;
import com.inventoryorder.model.bottomsheet.PickInventoryNatureModel;
import com.nowfloats.ProductGallery.Adapter.InventoryPickAdapter;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class PickInventoryNatureBottomSheetDialog1 extends BottomSheetDialogFragment {

    private ArrayList<PickInventoryNatureModel> list;
    private Listener listener;
    private BottomSheetPickInventoryNatureBinding binding;
    private BottomSheetDialog dialog;
    private PickInventoryNatureModel item = null;
    private InventoryPickAdapter adapter;

    PickInventoryNatureBottomSheetDialog1(ArrayList<PickInventoryNatureModel> list, Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_pick_inventory_nature, container, false);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialog1 = (BottomSheetDialog) dialog;
            if (dialog1 != null) {
                View bottomSheet = dialog1.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                    behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        dismissAllowingStateLoss();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRecyclerViewPickInventoryNature();
        binding.buttonDone.setOnClickListener(v -> {
            if (item != null) listener.onClickInventory(item);
            dismiss();
        });
        binding.tvCancel.setOnClickListener(v -> dismiss());
    }

    private void setRecyclerViewPickInventoryNature() {
        adapter = new InventoryPickAdapter(requireActivity()(), list, this::onClickItemGet);
        binding.recyclerViewPickInventoryNature.setLayoutManager(new LinearLayoutManager(requireActivity()(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewPickInventoryNature.setAdapter(adapter);
    }

    private void onClickItemGet(int position, PickInventoryNatureModel item, int actionType) {
        this.item = item;
        for (PickInventoryNatureModel it : list) {
            it.setSelected(Objects.requireNonNull(it.getInventoryName()).equalsIgnoreCase(item.getInventoryName()));
        }
        adapter.notifyDataSetChanged();
    }


    public interface Listener {
        void onClickInventory(PickInventoryNatureModel item);
    }
}
