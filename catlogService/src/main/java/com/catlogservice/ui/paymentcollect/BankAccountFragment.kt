package com.catlogservice.ui.paymentcollect

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import com.catlogservice.R
import com.catlogservice.base.AppBaseFragment
import com.catlogservice.constant.IntentConstant
import com.catlogservice.databinding.FragmentBankAccountDetailsBinding
import com.catlogservice.model.account.AccountCreateRequest
import com.catlogservice.model.account.BankAccountDetailsN
import com.catlogservice.model.account.response.AccountCreateResponse
import com.catlogservice.model.accountDetails.AccountDetailsResponse
import com.catlogservice.model.accountDetails.BankAccountDetails
import com.catlogservice.model.accountDetails.KYCDetails
import com.catlogservice.model.accountDetails.Result
import com.catlogservice.model.razor.RazorDataResponse
import com.catlogservice.viewmodel.AccountViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible


class BankAccountFragment : AppBaseFragment<FragmentBankAccountDetailsBinding, AccountViewModel>() {
  private var isUpdated = true
  private var fpId: String = ""
  private var clientId: String = ""
  private var menuClose: MenuItem? = null
  private var request: AccountCreateRequest? = null
  private var requestAccount: BankAccountDetailsN? = null
  private var isValidIfsc: Boolean = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BankAccountFragment {
      val fragment = BankAccountFragment()
      fragment.arguments = bundle
      return fragment
    }
  }


  override fun getLayout(): Int {
    return R.layout.fragment_bank_account_details
  }

  override fun getViewModelClass(): Class<AccountViewModel> {
    return AccountViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.submitBtn, binding?.whyBtn, binding?.verificationBtn)
    fpId = arguments?.getString(IntentConstant.FP_ID.name) ?: ""
    clientId = arguments?.getString(IntentConstant.CLIENT_ID.name) ?: ""
    binding?.edtIfsc?.afterTextChanged { isIfscValid(binding?.edtIfsc?.text.toString().trim()) }
    getUserDetails()
  }

  private fun isIfscValid(ifsc: String) {
    if (ifsc.length == 11) {
      viewModel?.ifscDetail(ifsc)?.observeOnce(viewLifecycleOwner, Observer {
        val data = it as? RazorDataResponse
        if ((it.status == 200 || it.status == 201 || it.status == 202) && data != null) {
          isValidIfsc = true
          binding?.edtBankName?.setText(data.bANK ?: "")
          if (data.bRANCH.isNullOrEmpty().not()) {
            binding?.edtBankBranch?.setText(data.bRANCH)
            binding?.txtBranch?.visible()
            binding?.edtBankBranch?.visible()
          }
        } else ifscUiUpdate()
      })
    } else ifscUiUpdate()
  }

  private fun ifscUiUpdate() {
    isValidIfsc = false
    binding?.edtBankName?.setText("")
    binding?.txtBranch?.gone()
    binding?.edtBankBranch?.gone()
  }

  private fun getUserDetails() {
    showProgress()
    viewModel?.userAccountDetails(fpId, clientId)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it.error is NoNetworkException) {
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        val response = it as? AccountDetailsResponse
        if (response != null) {
          checkBankAccountDetail(response.result)
        } else {
          isUpdated = false
          showLongToast("User detail getting empty")
        }
      } else {
        isUpdated = false
        showLongToast(it.message())
      }
    })
  }

  private fun checkBankAccountDetail(result: Result?) {
    if (result?.bankAccountDetails != null) {
      isUpdated = true
      uiUpdate(false)
      menuClose?.isVisible = true
      setEditTextAll(result.bankAccountDetails)
      if ((result.bankAccountDetails?.kYCDetails?.verificationStatus == KYCDetails.Status.PENDING.name).not()) {
        setToolbarTitle(resources.getString(R.string.link_bank_account))
        binding?.verificationBtn?.text = resources.getString(R.string.change_bank_detail)
        var buttonDrawable: Drawable = (binding?.verificationBtn?.background ?: ContextCompat.getDrawable(baseActivity, R.drawable.bg_button_rounded_orange)) as Drawable
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        DrawableCompat.setTint(buttonDrawable, ContextCompat.getColor(baseActivity, R.color.pinkish_grey))
        binding?.verificationBtn?.background = buttonDrawable
        binding?.textVerification?.text = resources.getString(R.string.how_it_works)
        binding?.textDesc?.text = resources.getString(R.string.verify_desc_account)
        (baseActivity as? AccountFragmentContainerActivity)?.setToolbarTitle(resources.getString(R.string.link_your_account))
        (baseActivity as? AccountFragmentContainerActivity)?.changeTheme(R.color.colorAccent)
      } else setToolbarTitle(resources.getString(R.string.my_bank_account))
    } else {
      uiUpdate(true)
      isUpdated = false
    }
  }

  private fun setEditTextAll(bankAccountDetails: BankAccountDetails?) {
    binding?.edtAccountName?.setText(bankAccountDetails?.accountName ?: "")
    binding?.edtAccountNumber?.setText(bankAccountDetails?.accountNumber ?: "")
    binding?.edtBankName?.setText(bankAccountDetails?.bankName ?: "")
    binding?.edtAlias?.setText(bankAccountDetails?.accountAlias ?: "")
    binding?.edtIfsc?.setText(bankAccountDetails?.iFSC ?: "")
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.submitBtn -> {
        if (isValid()) {
          if (isUpdated.not()) createApiAccount() else updateApiAccount()
        }
      }
      binding?.whyBtn -> bottomSheetWhy()
      binding?.verificationBtn -> {
        if (binding?.verificationBtn?.text?.toString()?.trim() != resources.getString(R.string.understood).trim()) {
          uiUpdate(true)
          menuClose?.isVisible = false
        }
      }
    }
  }


  private fun uiUpdate(isEditable: Boolean) {
    if (isEditable) setToolbarTitle(resources.getString(R.string.adding_bank_account))
    val views = arrayListOf(binding?.edtAccountName, binding?.edtAccountNumber, binding?.edtBankName, binding?.edtAlias, binding?.edtIfsc)
    binding?.verificationUi?.visibility = if (isEditable) View.GONE else View.VISIBLE
    binding?.createUi?.visibility = if (isEditable) View.VISIBLE else View.GONE
    binding?.edtConfirmNumber?.visibility = if (isEditable) View.VISIBLE else View.GONE
    binding?.titleConfirmAccount?.visibility = if (isEditable) View.VISIBLE else View.GONE
    views.forEach {
      it?.background = ContextCompat.getDrawable(baseActivity, if (isEditable) R.drawable.rounded_edit_stroke else R.drawable.rounded_edit_fill)
      it?.isFocusable = isEditable
      it?.isFocusableInTouchMode = isEditable
    }
    binding?.edtBankBranch?.background = ContextCompat.getDrawable(baseActivity, if (isEditable) R.drawable.rounded_edit_stroke else R.drawable.rounded_edit_fill)

    binding?.edtAccountName?.hint = if (isEditable) resources.getString(R.string.write_the_name_as_mentioned_in_bank_account) else ""
    binding?.edtAccountNumber?.hint = if (isEditable) resources.getString(R.string.xxxxxxxxxxxxxxxxxx) else ""
    binding?.edtConfirmNumber?.hint = if (isEditable) resources.getString(R.string.xxxxxxxxxxxxxxxxxx) else ""
    binding?.edtBankName?.hint = if (isEditable) resources.getString(R.string.write_the_name_of_your_bank) else ""
    binding?.edtAlias?.hint = if (isEditable) resources.getString(R.string.write_account_alias) else ""
    binding?.edtIfsc?.hint = if (isEditable) resources.getString(R.string.type_ifsc_code) else ""
  }

  private fun createApiAccount() {
    showProgress()
    request = AccountCreateRequest(clientId = clientId, floatingPointId = fpId, bankAccountDetails = requestAccount,
        additionalKYCDocuments = AccountCreateRequest().setKYCBlankValue(), registeredBusinessAddress = AccountCreateRequest().setAddressBlankValue(),
        registeredBusinessContactDetails = AccountCreateRequest().setContactDetailBlankValue(), taxDetails = AccountCreateRequest().setTaxBlankValue()
    )
    viewModel?.createAccount(request)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        hideProgress()
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      val response = it as? AccountCreateResponse
      if (response?.status == 200 || response?.status == 201 || response?.status == 202) {
        getUserDetails()
      } else {
        hideProgress()
        showLongToast(response?.errorN?.getMessage())
      }
    })
  }

  private fun updateApiAccount() {
    showProgress()
    viewModel?.updateAccount(fpId, clientId, requestAccount)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        hideProgress()
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      val response = it as? AccountCreateResponse
      if (response?.status == 200 || response?.status == 201 || response?.status == 202) {
        getUserDetails()
      } else {
        hideProgress()
        showLongToast(response?.errorN?.getMessage())
      }
    })
  }

  private fun bottomSheetWhy() {
    WhyBottomSheet().show(this@BankAccountFragment.parentFragmentManager, WhyBottomSheet::class.java.name)
  }

  private fun isValid(): Boolean {
    val nameAccount = binding?.edtAccountName?.text?.toString()
    val accountNumber = binding?.edtAccountNumber?.text?.toString()
    val confirmNumber = binding?.edtConfirmNumber?.text?.toString()
    val bankName = binding?.edtBankName?.text?.toString()
    val alias = binding?.edtAlias?.text?.toString()
    val ifsc = binding?.edtIfsc?.text?.toString()
    if (nameAccount.isNullOrEmpty()) {
      showShortToast("Bank account name can't empty.")
      return false
    } else if (accountNumber.isNullOrEmpty()) {
      showShortToast("Bank account number can't empty.")
      return false
    } else if (confirmNumber.isNullOrEmpty()) {
      showShortToast("Confirm bank account number can't empty.")
      return false
    } else if ((confirmNumber == accountNumber).not()) {
      showShortToast("Enter valid confirm account number.")
      return false
    } else if (ifsc.isNullOrEmpty()) {
      showShortToast("Bank IFSC can't empty.")
      return false
    } else if (ifsc.length < 11 || !isValidIfsc) {
      showLongToast("Please enter valid IFSC code")
      return false
    } else if (bankName.isNullOrEmpty()) {
      showShortToast("Bank name can't empty.")
      return false
    }
    requestAccount = BankAccountDetailsN(accountName = nameAccount, accountNumber = accountNumber, iFSC = ifsc, bankName = bankName, accountAlias = alias, kYCDetails = BankAccountDetailsN().kycObj())
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_close_icon, menu)
    menuClose = menu.findItem(R.id.menu_edit)
    menuClose?.isVisible = false
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_edit -> {
        uiUpdate(true)
        menuClose?.isVisible = false
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}