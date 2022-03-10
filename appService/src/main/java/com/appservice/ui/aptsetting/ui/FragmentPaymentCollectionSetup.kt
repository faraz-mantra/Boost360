package com.appservice.ui.aptsetting.ui

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
import com.appservice.databinding.FragmentPaymentCollectionSetupBinding
import com.appservice.rest.TaskCode
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.*
import com.framework.webengageconstant.PAYMENT_COLLECTION_SETUP_PAGE_LOAD

class FragmentPaymentCollectionSetup : AppBaseFragment<FragmentPaymentCollectionSetupBinding, AppointmentSettingsViewModel>() {

  var isEdit: Boolean = false

  override fun getLayout(): Int {
//    return R.layout.fragment_payment_collection_setup_v2
    return R.layout.fragment_payment_collection_setup
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  companion object {
    fun newInstance(): FragmentPaymentCollectionSetup {
      return FragmentPaymentCollectionSetup()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PAYMENT_COLLECTION_SETUP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.boostPaymentGateway, binding?.btnAddAccount, binding?.btnConfirm)
    getDeliveryStatus()
    grayScaleImage()
//        binding?.toggleCod?.setOnToggledListener { _, isOn ->
//            updateDeliveryStatus(isOn)
//        }
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

  private fun updateDeliveryStatus() {
    showProgress()
    hitApi(liveData = viewModel?.setupDelivery(
        DeliverySetup(
          isPickupAllowed = false, isBusinessLocationPickupAllowed = binding?.toggleCod?.isOn,
          isWarehousePickupAllowed = false, isHomeDeliveryAllowed = false, flatDeliveryCharge = "0",
          clientId = clientId, sessionLocal.fPID
        )
      ), errorStringId = R.string.error_getting_delivery_details
    )
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnAddAccount -> {
        WebEngageController.trackEvent(PAYMENT_COLLECTION_BANK_ACCOUNT_CLICK, CLICK, NO_EVENT_VALUE)
        if (!isEdit) startFragmentActivity(FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME)
      }
      binding?.btnConfirm -> {
        updateDeliveryStatus()
      }
      binding?.boostPaymentGateway -> {
        showBootPaymentBottomSheet()
      }
    }
  }

  private fun showBootPaymentBottomSheet() {
    val bottomSheetBoostPaymentConfig = BottomSheetBoostPaymentConfig()
    bottomSheetBoostPaymentConfig.show(parentFragmentManager, BottomSheetBoostPaymentConfig::class.java.name)
  }

  private fun onReceivedBankDetails(it: BaseResponse) {
    hideProgress()
    val paymentProfileResponse = it as? PaymentProfileResponse
    if (paymentProfileResponse != null) {
      isEdit = paymentProfileResponse.result?.bankAccountDetails != null && (paymentProfileResponse.result?.bankAccountDetails?.isValidAccount() == true)
      onBankAccountAddedOrUpdated(isEdit)
      if (isEdit) {
        binding?.btnAddAccount?.gone()
        binding?.llBankStatus?.visible()
        binding?.ctvAccountText?.gone()
        binding?.arrowRight?.visible()
        binding?.edtBankAccount?.setOnClickListener { startFragmentActivity(FragmentType.EDIT_ACCOUNT_DETAILS,isResult = true) }
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
    when (it.taskcode) {
      TaskCode.SETUP_DELIVERY.ordinal -> setupDeliveryResponse(it)
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

  private fun setupDeliveryResponse(it: BaseResponse) {
    hideProgress()
  }

  private fun onDeliveryDetailsReceived(it: BaseResponse) {
    hideProgress()
    val data = it as? DeliveryDetailsResponse
    binding?.toggleCod?.isOn = data?.result?.isBusinessLocationPickupAllowed ?: false
    getAccountDetails()
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