package com.appservice.ecommercesettings.ui

import android.view.View
import com.appservice.R
import com.appservice.appointment.model.DeliveryDetailsResponse
import com.appservice.appointment.model.DeliverySetup
import com.appservice.appointment.model.PaymentProfileResponse
import com.appservice.appointment.widgets.BottomSheetBoostPaymentConfig
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentEcommercePaymentCollectionSetupBinding
import com.appservice.rest.TaskCode
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.staffs.UserSession
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible

class FragmentEcommercePaymentCollectionSetup : AppBaseFragment<FragmentEcommercePaymentCollectionSetupBinding, AppointmentSettingsViewModel>() {
    var isEdit: Boolean = false

    override fun getLayout(): Int {
        return R.layout.fragment_ecommerce_payment_collection_setup
    }

    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentEcommercePaymentCollectionSetup {
            return FragmentEcommercePaymentCollectionSetup()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.boostPaymentGateway, binding?.btnAddAccount)
        getDeliveryStatus()
        getAccountDetails()
        grayScaleImage()
        binding?.toggleCod?.setOnToggledListener { toggleableView, isOn ->
            updateDeliveryStatus(isOn)
        }
    }

    private fun grayScaleImage() {
        binding?.upi?.makeGreyscale()
        binding?.visa?.makeGreyscale()
        binding?.ncpi?.makeGreyscale()
        binding?.mastercard?.makeGreyscale()
    }

    private fun getAccountDetails() {
        hitApi(viewModel?.getPaymentProfileDetails(UserSession.fpId, UserSession.clientId), (R.string.error_getting_bank_details))
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
                if (!isEdit)
                    startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME)

            }
        }
    }

    private fun showBootPaymentBottomSheet() {
        val bottomSheetBoostPaymentConfig = BottomSheetBoostPaymentConfig()
        bottomSheetBoostPaymentConfig.show(parentFragmentManager, BottomSheetBoostPaymentConfig::class.java.name)

    }

    private fun onReceivedBankDetails(it: BaseResponse) {
        val paymentProfileResponse = it as PaymentProfileResponse
        isEdit = paymentProfileResponse.result?.bankAccountDetails != null
        if (isEdit) {
            binding?.btnAddAccount?.gone()
            binding?.llBankStatus?.visible()
            binding?.ctvAccountText?.gone()
            binding?.arrowRight?.visible()
            binding?.edtBankAccount?.setOnClickListener {
                startFragmentActivity(FragmentType.EDIT_ACCOUNT_DETAILS)
            }
            binding?.llDisclaimer?.visible()
            binding?.bankAddedStatus?.text = "Bank Account Added (${(paymentProfileResponse.result?.bankAccountDetails?.getVerifyText())})"
            binding?.bankNameAccountNumber?.text = "${paymentProfileResponse.result?.bankAccountDetails?.bankName} - ${paymentProfileResponse.result?.bankAccountDetails?.accountNumber}"
        } else {
            setUpBankDetails()

        }

    }

    override fun onSuccess(it: BaseResponse) {
        super.onSuccess(it)
        when (it.taskcode) {
            TaskCode.GET_DELIVERY_DETAILS.ordinal -> onDeliveryDetailsReceived(it)
            TaskCode.GET_PAYMENT_PROFILE_DETAILS.ordinal -> onReceivedBankDetails(it)

        }
    }

    override fun onFailure(it: BaseResponse) {
        super.onFailure(it)
        when (it.taskcode) {
            TaskCode.GET_PAYMENT_PROFILE_DETAILS.ordinal -> setUpBankDetails()
        }
    }

    private fun setUpBankDetails() {
        binding?.btnAddAccount?.visible()
        binding?.llDisclaimer?.gone()
        binding?.llBankStatus?.gone()
        binding?.ctvAccountText?.visible()
        binding?.arrowRight?.gone()
    }

    private fun onDeliveryDetailsReceived(it: BaseResponse) {
        val data = it as? DeliveryDetailsResponse
        binding?.toggleCod?.isOn = data?.result?.isBusinessLocationPickupAllowed ?: false


    }
}