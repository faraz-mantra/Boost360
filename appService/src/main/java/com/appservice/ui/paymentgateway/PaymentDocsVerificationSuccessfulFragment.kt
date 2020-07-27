package com.appservice.ui.paymentgateway

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPaymentDocsVerificationSuccessfulBinding
import com.framework.models.BaseViewModel

class PaymentDocsVerificationSuccessfulFragment : AppBaseFragment<FragmentPaymentDocsVerificationSuccessfulBinding, BaseViewModel>(){

    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) : PaymentDocsVerificationSuccessfulFragment{
            val fragment = PaymentDocsVerificationSuccessfulFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_payment_docs_verification_successful
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

}