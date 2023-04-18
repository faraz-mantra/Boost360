package com.appservice.ui.aptsetting.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import com.appservice.utils.WebEngageController
import com.appservice.utils.changeColorOfSubstring
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.ADD_UPDATE_BANK_ACCOUNT
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE

class FragmentEditBankDetails : AppBaseFragment<FragmentEditBankDetailsBinding, AppointmentSettingsViewModel>() {

  var addBankAccountRequest: AddBankAccountRequest? = null

  val clickableSpan: ClickableSpan = object : ClickableSpan() {
    override fun onClick(textView: View) {
      val intent = Intent(Intent.ACTION_SENDTO)
      intent.data = Uri.parse("mailto:") // only email apps should handle this
      intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ria@nowfloats.com"))
      intent.putExtra(Intent.EXTRA_SUBJECT, "Need help with Boost360")
      if (intent.resolveActivity(context!!.packageManager) != null) {
        context!!.startActivity(intent)
      } else {
        Toast.makeText(context, "Unable to send email", Toast.LENGTH_SHORT).show()
      }
    }
  }

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
    setupUIColor()
    setUpSpannableText()
    getAccountDetails()
  }

  private fun setUpSpannableText() {
    val spannableString = SpannableString(getString(R.string.verification_underway_pending_message))
    val underlineSpan = UnderlineSpan()
    val textPaint = TextPaint()
    underlineSpan.updateDrawState(textPaint)
    spannableString.setSpan(underlineSpan, 194,211, 0)
    spannableString.setSpan(clickableSpan, 194,211, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    binding.layoutAccountUnderProcess.textDesc.text = spannableString
    binding.layoutAccountUnderProcess.textDesc.isClickable = true
    binding.layoutAccountUnderProcess.textDesc.movementMethod = LinkMovementMethod.getInstance()
  }

  private fun setupUIColor() {
    changeColorOfSubstring(R.string.name_of_the_account_holder_astrick, R.color.black_4a4a4a, "*", binding?.tvName!!)
    changeColorOfSubstring(R.string.account_number_aestrick, R.color.black_4a4a4a, "*", binding?.tvNumber!!)
    changeColorOfSubstring(R.string.confirm_account_number_astrick, R.color.black_4a4a4a, "*", binding?.titleConfirmAccount!!)
    changeColorOfSubstring(R.string.bank_name_astrick, R.color.black_4a4a4a, "*", binding?.tvBankName!!)
    changeColorOfSubstring(R.string.bank_ifsc_code_astrick, R.color.black_4a4a4a, "*", binding?.tvBankIfsc!!)
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
    WebEngageController.trackEvent(ADD_UPDATE_BANK_ACCOUNT, CLICK, NO_EVENT_VALUE)
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