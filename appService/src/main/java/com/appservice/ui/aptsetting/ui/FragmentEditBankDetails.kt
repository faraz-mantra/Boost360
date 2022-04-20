package com.appservice.ui.aptsetting.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.model.aptsetting.AddBankAccountRequest
import com.appservice.model.aptsetting.PaymentProfileResponse
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentEditBankDetailsBinding
import com.appservice.rest.TaskCode
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId

class FragmentEditBankDetails : AppBaseFragment<FragmentEditBankDetailsBinding, AppointmentSettingsViewModel>() {

  var addBankAccountRequest: AddBankAccountRequest? = null

  override fun getLayout(): Int {
    return R.layout.fragment_edit_bank_details
  }

  override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
    return AppointmentSettingsViewModel::class.java
  }

  companion object {
    fun newInstance(): FragmentEditBankDetails {
      return FragmentEditBankDetails()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.submitBtn, binding?.layoutAccountUnderProcess?.verificationBtn)
    getAccountDetails()
  }

  private fun getAccountDetails() {
    showProgress()
    hitApi(viewModel?.getPaymentProfileDetails(sessionLocal.fPID, clientId = clientId), (R.string.error_getting_bank_details))
  }


  private fun showAccountVerificationStatus() {
    binding?.layoutAccountUnderProcess?.root?.visible()
    binding?.layoutAccountVerified?.root?.gone()

  }

  private fun showSuccessVerificationStatus() {
    binding?.layoutAccountUnderProcess?.root?.gone()
    binding?.layoutAccountVerified?.root?.visible()
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.ic_menu_appointment_edit, menu)
    super.onCreateOptionsMenu(menu, inflater)

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.layoutAccountUnderProcess?.verificationBtn -> {
        baseActivity.onNavPressed()
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_edit -> {
        startFragmentActivity(FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS, isResult = true)
      }
      else -> {
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun addBankAccount() {
    showProgress()
    hitApi(viewModel?.addBankAccount(sessionLocal.fPID, clientId = clientId, addBankAccountRequest!!), R.string.error_adding_bank_account)

  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      if (data?.extras?.getBoolean(IntentConstant.IS_BACK_PRESS.name) == true) {
        val intent = Intent()
        intent.putExtra(IntentConstant.IS_UPDATED.name, true)
        baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
        baseActivity.onBackPressed()
      }
    }
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
    binding?.edtBankName?.text = paymentProfileResponse.result?.bankAccountDetails?.bankName
    binding?.edtAccountName?.text = paymentProfileResponse.result?.bankAccountDetails?.accountName
    binding?.edtAlias?.text = paymentProfileResponse.result?.bankAccountDetails?.accountAlias
    binding?.edtAccountNumber?.text = paymentProfileResponse.result?.bankAccountDetails?.accountNumber
    binding?.edtConfirmNumber?.text = paymentProfileResponse.result?.bankAccountDetails?.accountNumber
    binding?.edtIfsc?.text = paymentProfileResponse.result?.bankAccountDetails?.iFSC
    val verifyText = it.result?.bankAccountDetails?.getVerifyText()
    if (verifyText == "unverified") {
      showAccountVerificationStatus()
    } else {
      showSuccessVerificationStatus()
    }
    onBankAccountAddedOrUpdated(paymentProfileResponse.result?.bankAccountDetails != null)
  }


  private fun onAddingBankAccount(it: BaseResponse) {
    hideProgress()
    if (it.isSuccess()) {
      startFragmentActivity(FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS, isResult = true)
    }
  }
}