package com.appservice.ui.ecommerce

import android.app.Activity
import android.content.Intent
import android.view.View
import com.appservice.R
import com.appservice.model.aptsetting.DeliveryDetailsResponse
import com.appservice.model.aptsetting.DeliverySetup
import com.appservice.model.aptsetting.PaymentProfileResponse
import com.appservice.ui.aptsetting.widgets.BottomSheetBoostPaymentConfig
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentEcommercePaymentCollectionSetupBinding
import com.appservice.rest.TaskCode
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.*

class FragmentEcommercePaymentCollectionSetup : AppBaseFragment<FragmentEcommercePaymentCollectionSetupBinding, AppointmentSettingsViewModel>() {

  var isEdit: Boolean = false

  companion object {
    fun newInstance(): FragmentEcommercePaymentCollectionSetup {
      return FragmentEcommercePaymentCollectionSetup()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_ecommerce_payment_collection_setup
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    sessionLocal = UserSessionManager(requireActivity())
    WebEngageController.trackEvent(PAYMENT_COLLECTION_SETUP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.boostPaymentGateway, binding?.btnAddAccount)
    getAccountDetails()
    grayScaleImage()
    binding?.toggleCod?.setOnToggledListener { toggleableView, isOn ->
      updateHomeStoreStatus(isStore = isOn)
    }
    binding?.toggleHome?.setOnToggledListener { toggleableView, isOn ->
      updateHomeStoreStatus(isHome = isOn)
    }
  }

  private fun grayScaleImage() {
    binding?.upi?.makeGreyscale()
    binding?.visa?.makeGreyscale()
    binding?.ncpi?.makeGreyscale()
    binding?.mastercard?.makeGreyscale()
  }

  private fun getAccountDetails() {
    showProgress()
    hitApi(viewModel?.getPaymentProfileDetails(sessionLocal.fPID, clientId), (R.string.error_getting_bank_details))
  }

  private fun getDeliveryStatus() {
    showProgress()
    hitApi(viewModel?.getDeliveryDetails(sessionLocal.fPID, clientId), R.string.error_getting_delivery_details)
  }

  private fun updateHomeStoreStatus(isHome: Boolean? = binding?.toggleHome?.isOn, isStore: Boolean? = binding?.toggleCod?.isOn) {
    showProgress()
    hitApi(
      liveData = viewModel?.setupDelivery(DeliverySetup(isPickupAllowed = isStore, isBusinessLocationPickupAllowed = false, isWarehousePickupAllowed = false, isHomeDeliveryAllowed = isHome, flatDeliveryCharge = "0", clientId = clientId, floatingPointId = sessionLocal.fPID)),
      errorStringId = R.string.error_getting_delivery_details
    )
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.boostPaymentGateway -> {
        showBootPaymentBottomSheet()
      }
      binding?.btnAddAccount -> {
        WebEngageController.trackEvent(PAYMENT_COLLECTION_BANK_ACCOUNT_CLICK, CLICK, NO_EVENT_VALUE)
        if (!isEdit) startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME)
      }
    }
  }

  private fun showBootPaymentBottomSheet() {
    val bottomSheetBoostPaymentConfig = BottomSheetBoostPaymentConfig()
    bottomSheetBoostPaymentConfig.show(parentFragmentManager, BottomSheetBoostPaymentConfig::class.java.name)

  }

  private fun onReceivedBankDetails(it: BaseResponse) {
    getDeliveryStatus()
    val paymentProfileResponse = it as? PaymentProfileResponse
    if (paymentProfileResponse != null) {
      isEdit = paymentProfileResponse.result?.bankAccountDetails != null && (paymentProfileResponse.result?.bankAccountDetails?.isValidAccount() == true)
      if (isEdit) {
        binding?.btnAddAccount?.gone()
        binding?.llBankStatus?.visible()
        binding?.ctvAccountText?.gone()
        binding?.arrowRight?.visible()
        binding?.edtBankAccount?.setOnClickListener {
          startFragmentActivity(FragmentType.EDIT_ACCOUNT_DETAILS, isResult = true)
        }
        binding?.llDisclaimer?.visible()
        binding?.bankAddedStatus?.text = "Bank Account Added (${(paymentProfileResponse.result?.bankAccountDetails?.getVerifyText())})"
        binding?.bankNameAccountNumber?.text = "${paymentProfileResponse.result?.bankAccountDetails?.bankName} - ${paymentProfileResponse.result?.bankAccountDetails?.accountNumber}"
      } else {
        setUpBankDetails()

      }
    }

  }

  override fun onSuccess(it: BaseResponse) {
    super.onSuccess(it)
    hideProgress()
    when (it.taskcode) {
      TaskCode.GET_DELIVERY_DETAILS.ordinal -> onDeliveryDetailsReceived(it)
      TaskCode.GET_PAYMENT_PROFILE_DETAILS.ordinal -> onReceivedBankDetails(it)

    }
  }

  override fun onFailure(it: BaseResponse) {
    hideProgress()
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
    if (data != null) {
      binding?.toggleCod?.isOn = data.result?.isPickupAllowed ?: false
      binding?.toggleHome?.isOn = data.result?.isHomeDeliveryAllowed ?: false
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      if (data?.extras?.getBoolean(IntentConstant.IS_UPDATED.name) == true) {
        getAccountDetails()
      }
    }
  }
}