package com.boost.marketplace.ui.videos

import android.view.View
import android.widget.Toast
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetMyplanBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class VideosPopUpBottomSheet : BaseBottomSheetDialog<BottomSheetMyplanBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_videos
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    dialog.behavior.isDraggable = false
    setOnClickListener(
      binding?.btnFeatureDetails,
      binding?.btnUseFeature,
      binding?.rivCloseBottomSheet
    )
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnFeatureDetails -> {
        featuredetails()
      }
      binding?.btnUseFeature-> {
        Usefeature()
      }
      binding?.btnUseFeature, binding?.rivCloseBottomSheet -> {
        dismiss()
      }
    }
  }

  private fun Usefeature() {
    Toast.makeText(context, "Clicked on Usefeature", Toast.LENGTH_SHORT).show()
  }

  private fun featuredetails() {

    Toast.makeText(context, "Clicked on featuredetails", Toast.LENGTH_SHORT).show()



  }


}