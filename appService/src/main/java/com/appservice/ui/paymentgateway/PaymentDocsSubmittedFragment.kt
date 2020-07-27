package com.appservice.ui.paymentgateway

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentPaymentDocsSubmittedBinding
import com.framework.models.BaseViewModel

class PaymentDocsSubmittedFragment : AppBaseFragment<FragmentPaymentDocsSubmittedBinding, BaseViewModel>(){

    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) : PaymentDocsSubmittedFragment{
            val fragment = PaymentDocsSubmittedFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_payment_docs_submitted
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(
                binding?.btnCheckDocsStatus
        )
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnCheckDocsStatus -> arguments?.let { startFragmentPaymentActivity(FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED, it) }
        }
    }
}