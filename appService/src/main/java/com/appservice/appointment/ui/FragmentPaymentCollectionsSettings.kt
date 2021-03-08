package com.appservice.appointment.ui

import android.view.View
import com.appservice.R
import com.appservice.appointment.model.DeliverySetup
import com.appservice.appointment.widgets.BottomSheetBoostPaymentConfig
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentPaymentCollectionSetupBinding
import com.appservice.rest.TaskCode
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse

class FragmentPaymentCollectionsSettings : AppBaseFragment<FragmentPaymentCollectionSetupBinding, AppointmentSettingsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_payment_collection_setup
    }

    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentPaymentCollectionsSettings {
            return FragmentPaymentCollectionsSettings()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.boostPaymentGateway, binding?.btnAddAccount)
        getDeliveryStatus()
        binding?.toggleCod?.setOnToggledListener { toggleableView, isOn ->
            updateDeliveryStatus(isOn)
        }
    }

    private fun getDeliveryStatus() {
        hitApi(viewModel?.getDeliveryDetails(UserSession.fpId, UserSession.clientId), R.string.error_getting_delivery_details)
    }

    private fun updateDeliveryStatus(isOn: Boolean) {
        hitApi(liveData = viewModel?.setupDelivery(DeliverySetup(isPickupAllowed = false, isBusinessLocationPickupAllowed = isOn, isWarehousePickupAllowed = false, isHomeDeliveryAllowed = false, flatDeliveryCharge = "0", clientId = UserSession.clientId, floatingPointId = UserSession.fpId)), errorStringId = R.string.error_getting_delivery_details)
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

    override fun onSuccess(it: BaseResponse) {
        super.onSuccess(it)
        when (it.taskcode) {
            TaskCode.GET_SERVICE_LISTING.ordinal -> onDeliveryDetailsReceived(it)


        }
    }

    private fun onDeliveryDetailsReceived(it: BaseResponse) {
        val data = it as DeliverySetup
        binding?.toggleCod?.isOn = data.isBusinessLocationPickupAllowed ?: false
    }
}