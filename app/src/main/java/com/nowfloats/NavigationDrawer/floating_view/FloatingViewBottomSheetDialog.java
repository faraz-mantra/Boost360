package com.nowfloats.NavigationDrawer.floating_view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Utils;
import com.thinksity.R;
import com.thinksity.databinding.BottomSheetFloatingViewBinding;

import org.jetbrains.annotations.NotNull;

public class FloatingViewBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private Listener listener;
    private BottomSheetFloatingViewBinding binding;
    private BottomSheetDialog dialog;
    private UserSessionManager session;

    public FloatingViewBottomSheetDialog(UserSessionManager session, Listener listener) {
        this.listener = listener;
        this.session = session;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_floating_view, container, false);
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        dismissAllowingStateLoss();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addProductText.setText("Add a " + Utils.getSingleProductTaxonomyFromServiceCode(session.getFP_AppExperienceCode()).toLowerCase());
        binding.addProduct.setOnClickListener(this);
        binding.addImage.setOnClickListener(this);
        binding.addCustomPage.setOnClickListener(this);
        binding.addTestimonial.setOnClickListener(this);
        binding.addUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.addProduct:
                listener.onClickInventory(FLOATING_CLICK_TYPE.ADD_PRODUCT_SERVICE);
                break;
            case R.id.addImage:
                listener.onClickInventory(FLOATING_CLICK_TYPE.ADD_IMAGE);
                break;
            case R.id.addCustomPage:
                listener.onClickInventory(FLOATING_CLICK_TYPE.CREATE_CUSTOM_PAGE);
                break;
            case R.id.addTestimonial:
                listener.onClickInventory(FLOATING_CLICK_TYPE.ADD_TESTIMONIAL);
                break;
            case R.id.addUpdate:
                listener.onClickInventory(FLOATING_CLICK_TYPE.WRITE_UPDATE);
                break;
        }
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

    public enum FLOATING_CLICK_TYPE {
        ADD_PRODUCT_SERVICE, ADD_IMAGE, CREATE_CUSTOM_PAGE, ADD_TESTIMONIAL, WRITE_UPDATE
    }

    public interface Listener {
        void onClickInventory(FLOATING_CLICK_TYPE type);
    }
}
