package com.appservice.ui.paymentgateway

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentPaymentActiveBinding
import com.framework.models.BaseViewModel

class PaymentGatewayFragment : AppBaseFragment<FragmentPaymentActiveBinding, BaseViewModel>(){

    // flag to check if user has an instamojo account
    private var haveInstamojoAccount: Boolean = false;

    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): PaymentGatewayFragment {
            val fragment = PaymentGatewayFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(
                binding?.paymentGatewayTermsToggle,
                binding?.activePaymentBottomButton
        )
        radioButtonToggle()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_payment_active
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v){
            binding?.paymentGatewayTermsToggle ->  bottomSheetWhy()
        }
        if(haveInstamojoAccount){
            when(v){
                // Move to whatever fragment is required here
            }
        }else{
            when(v){
                binding?.activePaymentBottomButton -> arguments?.let { startFragmentPaymentActivity(FragmentType.SCAN_PAN_CARD, it)}
            }
        }
    }

    private fun bottomSheetWhy(){
        WhyPaymentBottomSheet().show(this@PaymentGatewayFragment.parentFragmentManager, com.appservice.ui.bankaccount.WhyBottomSheet::class.java.name)
    }

    private fun radioButtonToggle(){
        binding?.rbNoInstamojoAccount?.isChecked = true
        binding?.paymentGatewayToggleGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{
            group, id ->
            val radio = binding?.paymentGatewayToggleGroup?.findViewById<RadioButton>(id)
            if(radio?.id == binding?.rbHaveInstaMojoAccount?.id){
                binding?.activePaymentBottomButton?.text = getString(R.string.get_started)
                binding?.tvKeepDocsReady?.visibility = View.INVISIBLE
                haveInstamojoAccount = true

            }else if(radio?.id == binding?.rbNoInstamojoAccount?.id){
                binding?.activePaymentBottomButton?.text = getString(R.string.start_with_pan_card)
                binding?.tvKeepDocsReady?.visibility = View.VISIBLE
                haveInstamojoAccount = false
            }

        });
    }
}