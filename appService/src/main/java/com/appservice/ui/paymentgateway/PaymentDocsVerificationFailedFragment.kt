package com.appservice.ui.paymentgateway

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPaymentDocsVerificationFailedBinding
import com.framework.models.BaseViewModel

class PaymentDocsVerificationFailedFragment : AppBaseFragment<FragmentPaymentDocsVerificationFailedBinding, BaseViewModel>(){

    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) : PaymentDocsVerificationFailedFragment{
            val fragment = PaymentDocsVerificationFailedFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_payment_docs_verification_failed
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }


}