package com.appservice.ui.aptsetting.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.appservice.R
import com.appservice.model.aptsetting.AddBankAccountRequest
import com.appservice.model.aptsetting.PaymentProfileResponse
import com.appservice.ui.aptsetting.widgets.BottomSheetAccountVerified
import com.appservice.ui.aptsetting.widgets.BottomSheetVerificationUnderProcess
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentAddBankDetailsBinding
import com.appservice.rest.TaskCode
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId

class FragmentAddAccountDetails : AppBaseFragment<FragmentAddBankDetailsBinding, AppointmentSettingsViewModel>() {

  var addBankAccountRequest: AddBankAccountRequest? = null

  override fun getLayout(): Int {
    return R.layout.fragment_add_bank_details
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  companion object {
    fun newInstance(): FragmentAddAccountDetails {

      return FragmentAddAccountDetails()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.submitBtn)
    getAccountDetails()
    sessionLocal = UserSessionManager(requireActivity())
  }

  private fun getAccountDetails() {
    showProgress()
    hitApi(viewModel?.getPaymentProfileDetails(sessionLocal.fPID, clientId = clientId), (R.string.error_getting_bank_details))
  }

  private fun showAccountVerificationStatus() {
    val bottomSheetVerificationUnderProcess = BottomSheetVerificationUnderProcess()
    bottomSheetVerificationUnderProcess.show(parentFragmentManager, BottomSheetVerificationUnderProcess::javaClass.name)
  }

  private fun showSuccessVerificationStatus() {
    val bottomSheetVerificationUnderProcess = BottomSheetAccountVerified()
    bottomSheetVerificationUnderProcess.show(parentFragmentManager, BottomSheetAccountVerified::javaClass.name)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.submitBtn -> {
        if (isValid()) addBankAccount()
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_edit, menu)
    super.onCreateOptionsMenu(menu, inflater)

  }


  private fun addBankAccount() {
    showProgress()
    hitApi(viewModel?.addBankAccount(sessionLocal.fPID, clientId = clientId, addBankAccountRequest!!), R.string.error_adding_bank_account)
  }

  override fun onSuccess(it: BaseResponse) {
    super.onSuccess(it)
    when (it.taskcode) {
      TaskCode.ADD_BANK_ACCOUNT.ordinal -> onAddingBankAccount(it)
      TaskCode.GET_PAYMENT_PROFILE_DETAILS.ordinal -> onReceivedBankDetails(it)
    }
  }

  private fun onReceivedBankDetails(it: BaseResponse) {
    hideProgress()
    val paymentProfileResponse = it as PaymentProfileResponse
    binding?.edtBankName?.setText(paymentProfileResponse.result?.bankAccountDetails?.bankName)
    binding?.edtAccountName?.setText(paymentProfileResponse.result?.bankAccountDetails?.accountName)
    binding?.edtAlias?.setText(paymentProfileResponse.result?.bankAccountDetails?.accountAlias)
    binding?.edtAccountNumber?.setText(paymentProfileResponse.result?.bankAccountDetails?.accountNumber)
    binding?.edtConfirmNumber?.setText("")
    binding?.edtIfsc?.setText(paymentProfileResponse.result?.bankAccountDetails?.iFSC)

  }

  private fun onAddingBankAccount(it: BaseResponse) {
    hideProgress()
    if (it.isSuccess()) {
      val bundle = Bundle()
      bundle.putBoolean(IntentConstant.IS_EDIT.name, false)
      startFragmentActivity(FragmentType.APPOINTMENT_PAYMENT_SETTINGS, bundle, true, isResult = false)
    }
  }

  private fun isValid(): Boolean {
    val bankName = binding?.edtBankName?.text.toString()
    val accountName = binding?.edtAccountName?.text.toString()
    val alias = binding?.edtAlias?.text.toString()
    val accountNumberConfirm = binding?.edtConfirmNumber?.text.toString()
    val accountNumber = binding?.edtAccountNumber?.text.toString()
    val ifcs = binding?.edtIfsc?.text.toString()
    if (accountName.isEmpty()) {
      showLongToast(getString(R.string.enter_account_holder_name))
      return false
    }
    if (accountNumber.isEmpty()) {
      showLongToast(getString(R.string.enter_account_number))
      return false
    }
    if (accountNumber.length < 9) {
      showShortToast(getString(R.string.account_less_than_nine))
      return false
    }
    if (accountNumber.length > 18) {
      showShortToast(getString(R.string.account_greater_than_nine))
      return false
    }
    if (accountNumberConfirm.isEmpty()) {
      showLongToast(getString(R.string.please_confirm_account_number))
      return false
    }
    if (accountNumberConfirm != accountNumber) {
      showLongToast(getString(R.string.account_number_is_not_same))
      return false
    }
    if (accountNumberConfirm.contains("*")) {
      showLongToast(getString(R.string.please_enter_valid_account_number))
      return false
    }
    if (bankName.isEmpty()) {
      showLongToast(getString(R.string.enter_bank_name))
      return false
    }
    if (ifcs.isEmpty()) {
      showLongToast(getString(R.string.enter_bank_ifcs))
      return false
    }
    if (addBankAccountRequest == null) addBankAccountRequest = AddBankAccountRequest()
    addBankAccountRequest?.accountName = accountName
    addBankAccountRequest?.bankName = bankName
    addBankAccountRequest?.accountAlias = alias
    addBankAccountRequest?.iFSC = ifcs
    addBankAccountRequest?.accountNumber = accountNumber
//        addBankAccountRequest?.kYCDetails = accountName
    return true
  }
}