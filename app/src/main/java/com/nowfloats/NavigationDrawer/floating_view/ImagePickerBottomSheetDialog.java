package com.nowfloats.NavigationDrawer.floating_view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.framework.utils.ScreenUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Utils;
import com.thinksity.R;
import com.thinksity.databinding.BottomSheetFloatingViewBinding;
import com.thinksity.databinding.BottomSheetImagePickerBinding;

import org.jetbrains.annotations.NotNull;

public class ImagePickerBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

  private Listener listener;
  private BottomSheetImagePickerBinding binding;
  private BottomSheetDialog dialog;
  private Boolean showDelete = false;

  public ImagePickerBottomSheetDialog(Listener listener) {
    this.listener = listener;
  }

  public ImagePickerBottomSheetDialog(Listener listener, Boolean showDelete) {
    this.listener = listener;
    this.showDelete = showDelete;
  }

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_image_picker, container, false);
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
    binding.camera.setOnClickListener(this);
    binding.gallery.setOnClickListener(this);
    binding.close.setOnClickListener(this);
    binding.delete.setOnClickListener(this);
    if (showDelete) {
      binding.delete.setVisibility(View.VISIBLE);
      binding.close.setVisibility(View.GONE);
    }
  }

  @Override
  public void onClick(View v) {
    dismiss();
    switch (v.getId()) {
      case R.id.camera:
        listener.onClickPicker(IMAGE_CLICK_TYPE.CAMERA);
        break;
      case R.id.gallery:
        listener.onClickPicker(IMAGE_CLICK_TYPE.GALLERY);
        break;
      case R.id.cancel:
        break;
      case R.id.delete:
        listener.onClickPicker(IMAGE_CLICK_TYPE.DELETE);
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
    new ScreenUtils().setWhiteNavigationBar(dialog);
    return dialog;
  }

  public enum IMAGE_CLICK_TYPE {
    CAMERA, GALLERY, DELETE
  }

  public interface Listener {
    void onClickPicker(IMAGE_CLICK_TYPE type);
  }
}
