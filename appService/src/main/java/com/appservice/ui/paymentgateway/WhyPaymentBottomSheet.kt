package com.appservice.ui.paymentgateway

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetTermsPaymentGatewayBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel


class WhyPaymentBottomSheet : BaseBottomSheetDialog<BottomSheetTermsPaymentGatewayBinding, BaseViewModel>(){


    var onClicked: () -> Unit = { }
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_terms_payment_gateway
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.understoodBtnPaymentGateway)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.understoodBtnPaymentGateway -> dismiss()
        }
    }

//    override fun getMarginStart(): Int {
//        return resources.getDimensionPixelSize(R.dimen.size_20)
//    }
//
//    override fun getMarginEnd(): Int {
//        return resources.getDimensionPixelSize(R.dimen.size_20)
//    }
}