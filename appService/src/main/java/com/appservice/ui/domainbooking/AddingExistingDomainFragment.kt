package com.appservice.ui.domainbooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBankAccountDetailsBinding
import com.appservice.databinding.FragmentBookADomainSslBinding
import com.appservice.ui.bankaccount.AccountFragmentContainerActivity
import com.appservice.ui.bankaccount.BankAccountFragment
import com.appservice.viewmodel.AccountViewModel
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

class AddingExistingDomainFragment : AppBaseFragment<FragmentBookADomainSslBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): AddingExistingDomainFragment {
            val fragment = AddingExistingDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
            resources.getString(
                R.string.adding_existing_domain
            ), resources.getDimensionPixelSize(R.dimen.size_44))
    }

    override fun getLayout(): Int {
        return R.layout.fragment_adding_existing_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}