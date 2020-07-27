package com.appservice.ui.paymentgateway

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPaymentDocsUnderVerificationBinding
import com.framework.models.BaseViewModel

class PaymentDocsUnderVerificationFragment : AppBaseFragment<FragmentPaymentDocsUnderVerificationBinding, BaseViewModel>(){

    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) : PaymentDocsUnderVerificationFragment{
            val fragment = PaymentDocsUnderVerificationFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_payment_docs_under_verification
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }


}