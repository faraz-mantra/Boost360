package com.festive.poster.ui.googleAds

import com.festive.poster.R
import com.festive.poster.databinding.BottomSheetEditGoogleAdsBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BusinessDescriptionBottomSheet : BaseBottomSheetDialog<BottomSheetEditGoogleAdsBinding, BaseViewModel>() {



  override fun getLayout(): Int {
    return R.layout.bottom_sheet_edit_google_ads
  }


  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    dialog.behavior.isDraggable = false

  }

}