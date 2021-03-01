package com.appservice.appointment

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentPaymentCollectionSetupBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.framework.models.BaseViewModel

class FragmentPaymentCollectionsSettings : AppBaseFragment<FragmentPaymentCollectionSetupBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_payment_collection_setup
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentPaymentCollectionsSettings {
            return FragmentPaymentCollectionsSettings()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.boostPaymentGateway, binding?.btnAddAccount)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.boostPaymentGateway -> {
                showBootPaymentBottomSheet()
            }
            binding?.btnAddAccount -> {
                startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME)
            }
        }
    }

    private fun showBootPaymentBottomSheet() {
        val bottomSheetBoostPaymentConfig = BottomSheetBoostPaymentConfig()
        bottomSheetBoostPaymentConfig.show(parentFragmentManager, BottomSheetBoostPaymentConfig::class.java.name)

    }
}