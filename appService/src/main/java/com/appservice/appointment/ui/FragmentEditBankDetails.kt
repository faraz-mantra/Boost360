package com.appservice.appointment.ui
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.appservice.R
import com.appservice.appointment.model.AddBankAccountRequest
import com.appservice.appointment.model.PaymentProfileResponse
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
        setOnClickListener(binding?.submitBtn)
        getAccountDetails()
        sessionLocal = UserSessionManager(requireActivity())
    }

    private fun getAccountDetails() {
        showProgress()
        hitApi(viewModel?.getPaymentProfileDetails(sessionLocal.fPID,clientId = clientId), (R.string.error_getting_bank_details))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                val bundle = Bundle()
                bundle.putBoolean(IntentConstant.EDIT_ACCOUNT.name, true)
                startFragmentActivity(FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS, bundle, false, isResult = false)
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addBankAccount() {
        showProgress()
        hitApi(viewModel?.addBankAccount(sessionLocal.fPID,clientId = clientId,addBankAccountRequest!!), R.string.error_adding_bank_account)

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
        binding?.edtConfirmNumber?.setText(paymentProfileResponse.result?.bankAccountDetails?.accountNumber)
        binding?.edtIfsc?.setText(paymentProfileResponse.result?.bankAccountDetails?.iFSC)
        val verifyText = it.result?.bankAccountDetails?.getVerifyText()
        if (verifyText == "unverified") {
            showAccountVerificationStatus()
        } else {
            showSuccessVerificationStatus()
        }

    }

    private fun onAddingBankAccount(it: BaseResponse) {
        hideProgress()
        if (it.isSuccess()) {
            val bundle = Bundle()
            bundle.putBoolean(IntentConstant.IS_EDIT.name, false)
            startFragmentActivity(FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS, bundle, false, isResult = false)
        }
    }

}