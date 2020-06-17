package com.catlogservice.ui.service

import android.os.Bundle
import android.view.View
import com.catlogservice.R
import com.catlogservice.base.AppBaseFragment
import com.catlogservice.constant.FragmentType
import com.catlogservice.databinding.FragmentServiceDetailBinding
import com.catlogservice.ui.startFragmentActivity
import com.catlogservice.ui.widgets.PaymentConfigBottomSheet
import com.catlogservice.ui.widgets.ServiceDeliveryBottomSheet
import com.catlogservice.ui.widgets.ServiceDeliveryConfigBottomSheet
import com.catlogservice.viewmodel.ServiceViewModel

class ServiceDetailFragment : AppBaseFragment<FragmentServiceDetailBinding, ServiceViewModel>() {

    companion object {
        fun newInstance(): ServiceDetailFragment {
            return ServiceDetailFragment()
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_service_detail
    }

    override fun getViewModelClass(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.vwChangeDeliverConfig, binding?.vwChangeDeliverLocation, binding?.vwPaymentConfig, binding?.btnSavePublish)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.vwChangeDeliverConfig -> showServiceDeliveryConfigBottomSheet()
            binding?.vwChangeDeliverLocation -> showServiceDeliveryLocationBottomSheet()
            binding?.vwPaymentConfig -> showPaymentConfigBottomSheet()
            binding?.btnSavePublish -> startFragmentActivity(FragmentType.SERVICE_INFORMATION, Bundle())
        }
    }

    private fun showServiceDeliveryConfigBottomSheet() {
        val dialog = ServiceDeliveryConfigBottomSheet()
        dialog.show(parentFragmentManager, ServiceDeliveryConfigBottomSheet::class.java.name)
    }

    private fun showServiceDeliveryLocationBottomSheet() {
        val dialog = ServiceDeliveryBottomSheet()
        dialog.show(parentFragmentManager, ServiceDeliveryBottomSheet::class.java.name)
    }

    private fun showPaymentConfigBottomSheet() {
        val dialog = PaymentConfigBottomSheet()
        dialog.show(parentFragmentManager, PaymentConfigBottomSheet::class.java.name)
    }

}