package com.appservice.ui.domainbooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentAddingExistingDomainBinding
import com.appservice.databinding.FragmentBankAccountDetailsBinding
import com.appservice.databinding.FragmentBookADomainSslBinding
import com.appservice.ui.bankaccount.AccountFragmentContainerActivity
import com.appservice.ui.bankaccount.BankAccountFragment
import com.appservice.viewmodel.AccountViewModel
import com.framework.base.BaseFragment
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class AddingExistingDomainFragment :
    AppBaseFragment<FragmentAddingExistingDomainBinding, BaseViewModel>() {

    var addingExistingDomainStepStatus = 0

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
            ), resources.getDimensionPixelSize(R.dimen.size_44)
        )
        setUiStepStatus()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.btnNextStep?.setOnClickListener {
            addingExistingDomainStepStatus =
                if (addingExistingDomainStepStatus < 3) ++addingExistingDomainStepStatus else addingExistingDomainStepStatus
            setUiStepStatus()
        }
    }

    private fun setUiStepStatus() {
        when (addingExistingDomainStepStatus) {
            0 -> {
                binding?.tvStepIdentity?.text = getString(R.string.step_1_4)
                binding?.tvInfo?.text = getString(R.string.not_sure_provide_is)
                binding?.btnNextStep?.text = getString(R.string.i_logged_in)
                binding?.cardStep1?.visible()
                binding?.cardStep2?.gone()
                binding?.cardStep3?.gone()
                binding?.cardStepFinal?.gone()
                binding?.ivStep1?.setImageDrawable(
                    ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_domain_step_1
                ))
            }
            1 -> {
                binding?.tvStepIdentity?.text = getString(R.string.step_2_4)
                binding?.tvInfo?.text = getString(R.string.cant_find_domain_settings)
                binding?.btnNextStep?.text = getString(R.string.i_found_my_domain_settings)
                binding?.cardStep1?.gone()
                binding?.cardStep2?.visible()
                binding?.cardStep3?.gone()
                binding?.cardStepFinal?.gone()
                binding?.ivStep1?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_domain_step_2
                    ))
            }
            2 -> {
                binding?.tvStepIdentity?.text = getString(R.string.step_3_4)
                binding?.tvInfo?.text = getString(R.string.cant_find_nameservers)
                binding?.btnNextStep?.text = getString(R.string.i_found_the_nameservers)
                binding?.cardStep1?.gone()
                binding?.cardStep2?.gone()
                binding?.cardStep3?.visible()
                binding?.cardStepFinal?.gone()
                binding?.ivStep1?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_domain_step_3
                    ))
            }
            3 -> {
                binding?.tvStepIdentity?.text = getString(R.string.step_final)
                binding?.tvInfo?.text = getString(R.string.need_help)
                binding?.btnNextStep?.text = getString(R.string.i_replaced_the_nameservers)
                binding?.cardStep1?.gone()
                binding?.cardStep2?.gone()
                binding?.cardStep3?.gone()
                binding?.cardStepFinal?.visible()
                binding?.ivStep1?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_domain_final_step
                    ))
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_adding_existing_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}