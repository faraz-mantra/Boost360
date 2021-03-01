package com.appservice.appointment

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetBoostPaymentConfigurationBinding
import com.appservice.databinding.BottomSheetCatalogDisplayBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetBoostPaymentConfig : BaseBottomSheetDialog<BottomSheetBoostPaymentConfigurationBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_boost_payment_configuration
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
       return BaseViewModel::class.java
    }

    override fun onCreateView() {
      setOnClickListener(binding?.btnDone,binding?.btnCancel)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        dismiss()
    }
}