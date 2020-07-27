package com.appservice.ui.paymentgateway

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentKycDetailsBinding
import com.framework.models.BaseViewModel

class KYCDetailsFragment : AppBaseFragment<FragmentKycDetailsBinding, BaseViewModel>(){

    private var useStoredBankAccount: Boolean = true

    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) : KYCDetailsFragment{
            val fragment = KYCDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_kyc_details
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(
                binding?.btnSubmitDetails,
                binding?.bankAccountToggleGroup
        )
        radioButtonToggle()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnSubmitDetails -> arguments?.let { startFragmentPaymentActivity(FragmentType.KYC_STATUS, it)}
        }
    }

    private fun radioButtonToggle(){
        binding?.rbStoredAccount?.isChecked = true
        binding?.bankAccountToggleGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{
            group, id ->
            val radio = binding?.bankAccountToggleGroup?.findViewById<RadioButton>(id)
            if(radio?.id == binding?.rbAddDifferentAccount?.id){
                binding?.llAddBankDetails?.visibility = View.VISIBLE
            }else if(radio?.id == binding?.rbStoredAccount?.id){
                binding?.llAddBankDetails?.visibility = View.GONE
            }

        })
    }

}