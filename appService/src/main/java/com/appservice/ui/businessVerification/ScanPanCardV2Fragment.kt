package com.appservice.ui.businessVerification

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentScanPanCardV2Binding
import com.framework.models.BaseViewModel

class ScanPanCardV2Fragment : AppBaseFragment<FragmentScanPanCardV2Binding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.fragment_scan_pan_card_v2
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }
}