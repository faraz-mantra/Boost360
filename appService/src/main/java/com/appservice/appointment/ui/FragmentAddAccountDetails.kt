package com.appservice.appointment.ui

import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.appointment.model.AddBankAccountRequest
import com.appservice.appointment.model.PaymentProfileResponse
import com.appservice.appointment.widgets.BottomSheetVerificationUnderProcess
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentAddBankDetailsBinding
import com.appservice.rest.TaskCode
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.invisible

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
        getBundle()
    }

    private fun getAccountDetails() {
        hitApi(viewModel?.getPaymentProfileDetails(UserSession.fpId, UserSession.clientId), (R.string.error_getting_bank_details))
    }

    private fun getBundle() {
        when (arguments?.getBoolean(IntentConstant.IS_EDIT.name)) {
            false -> {

                binding?.edtBankName?.background = resources.getDrawable(R.drawable.rounded_stroke_grey_4_solid_gray)
                binding?.edtAccountName?.background = resources.getDrawable(R.drawable.rounded_stroke_grey_4_solid_gray)
                binding?.edtAlias?.background = resources.getDrawable(R.drawable.rounded_stroke_grey_4_solid_gray)
                binding?.edtAccountNumber?.background = resources.getDrawable(R.drawable.rounded_stroke_grey_4_solid_gray)
                binding?.edtIfsc?.background = resources.getDrawable(R.drawable.rounded_stroke_grey_4_solid_gray)
                binding?.edtConfirmNumber?.invisible()
                binding?.edtAccountName?.isEnabled = false
                binding?.edtAlias?.isEnabled = false
                binding?.edtAccountNumber?.isEnabled = false
                binding?.edtIfsc?.isEnabled = false
                binding?.edtBankName?.isEnabled = false
                showAccountVerificationStatus()
            }
            true -> {
                getAccountDetails()
            }
        }
    }

    private fun showAccountVerificationStatus() {
        val bottomSheetVerificationUnderProcess = BottomSheetVerificationUnderProcess()
        bottomSheetVerificationUnderProcess.show(parentFragmentManager, BottomSheetVerificationUnderProcess::javaClass.name)
    }
    private fun showSuccessVerificationStatus() {
        val bottomSheetVerificationUnderProcess = BottomSheetVerificationUnderProcess()
        bottomSheetVerificationUnderProcess.show(parentFragmentManager, BottomSheetVerificationUnderProcess::javaClass.name)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.submitBtn -> {
                if (isValid()) addBankAccount()
            }
        }
    }

    private fun addBankAccount() {
        hitApi(viewModel?.addBankAccount(addBankAccountRequest!!), R.string.error_adding_bank_account)
    }

    override fun onSuccess(it: BaseResponse) {
        super.onSuccess(it)
        when (it.taskcode) {
            TaskCode.ADD_BANK_ACCOUNT.ordinal -> onAddingBankAccount(it)
            TaskCode.GET_PAYMENT_PROFILE_DETAILS.ordinal -> onReceivedBankDetails(it)
        }
    }

    private fun onReceivedBankDetails(it: BaseResponse) {
        val paymentProfileResponse = it as PaymentProfileResponse
        binding?.edtBankName?.setText(paymentProfileResponse.result?.bankAccountDetails?.bankName)
        binding?.edtAccountName?.setText(paymentProfileResponse.result?.bankAccountDetails?.accountNumber)
        binding?.edtAlias?.setText(paymentProfileResponse.result?.bankAccountDetails?.accountAlias)
        binding?.edtAccountNumber?.setText(paymentProfileResponse.result?.bankAccountDetails?.bankName)
        binding?.edtIfsc?.setText(paymentProfileResponse.result?.bankAccountDetails?.iFSC)
        showAccountVerificationStatus()

    }

    private fun onAddingBankAccount(it: BaseResponse) {
        if (it.isSuccess()) {
            val bundle = Bundle()
            bundle.putBoolean(IntentConstant.IS_EDIT.name, false)
            startFragmentActivity(FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS, bundle, false, isResult = false)
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
        if (accountNumberConfirm.isEmpty()) {
            showLongToast(getString(R.string.please_confirm_account_number))
            return false
        }
        if (accountNumberConfirm != accountNumber) {
            showLongToast(getString(R.string.account_number_is_not_same))
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