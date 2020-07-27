package com.appservice.ui.paymentgateway

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentKycStatusBinding
import com.framework.models.BaseViewModel

class KYCStatusFragment : AppBaseFragment<FragmentKycStatusBinding, BaseViewModel>(){

    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) : KYCStatusFragment{
            val fragment = KYCStatusFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_kyc_status
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

}