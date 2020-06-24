package com.catlogservice.ui.widgets

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catlogservice.R
import com.catlogservice.databinding.BottomShettPaymentConfigurationBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel


class PaymentConfigBottomSheet() : BaseBottomSheetDialog<BottomShettPaymentConfigurationBinding, BaseViewModel>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.bottom_shett_payment_configuration
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        val content = SpannableString(getString(R.string.a_premium_service_by_boost_for_secure_payment_collection_learn_more_here))
        content.setSpan(UnderlineSpan(), content.indexOf("here"), content.length, 0)
        binding?.tvBoostPaymentGatewayDesc?.text = content


        setOnClickListener(binding?.vwBoostPaymentGateway, binding?.vwExternalUrl)//binding?.vwCustomPaymentGateway
        setOnClickListener(binding?.btnDone, binding?.btnCancel)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.vwBoostPaymentGateway -> {
                binding?.rbBoostPaymentGateway?.isChecked = true
//                binding?.rbCustomPaymentGateway?.isChecked = false
                binding?.rbExternalUrl?.isChecked = false
            }
            binding?.vwCustomPaymentGateway -> {
                binding?.rbBoostPaymentGateway?.isChecked = false
//                binding?.rbCustomPaymentGateway?.isChecked = true
                binding?.rbExternalUrl?.isChecked = false
            }
            binding?.vwExternalUrl -> {
                binding?.rbBoostPaymentGateway?.isChecked = false
//                binding?.rbCustomPaymentGateway?.isChecked = false
                binding?.rbExternalUrl?.isChecked = true
            }
            binding?.btnDone -> {
            }
            binding?.btnCancel -> {
                dismiss()
            }
        }
    }

}