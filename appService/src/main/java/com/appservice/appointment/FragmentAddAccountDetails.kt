package com.appservice.appointment

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentAddBankDetailsBinding
import com.framework.models.BaseViewModel

class FragmentAddAccountDetails : AppBaseFragment<FragmentAddBankDetailsBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_add_bank_details
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentAddAccountDetails {
            return FragmentAddAccountDetails()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.submitBtn)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.submitBtn -> {
                isValid()
            }
        }
    }

    private fun isValid(): Boolean {
        val bankName = binding?.edtBankName?.text
        val accountName = binding?.edtAccountName?.text
        val alias = binding?.edtAlias?.text ?: ""
        val accountNumberConfirm = binding?.edtConfirmNumber?.text
        val accountNumber = binding?.edtAccountNumber?.text
        val ifcs = binding?.edtIfsc?.text
        if (accountName.isNullOrEmpty()) {
            showLongToast(getString(R.string.enter_account_holder_name))
            return false
        }
        if (accountNumber.isNullOrEmpty()) {
            showLongToast(getString(R.string.enter_account_number))
            return false
        }
        if (accountNumberConfirm.isNullOrEmpty()) {
            showLongToast(getString(R.string.please_confirm_account_number))
            return false
        }
        if (accountNumberConfirm != accountNumber) {
            showLongToast(getString(R.string.account_number_is_not_same))
            return false
        }
        if (bankName.isNullOrEmpty()) {
            showLongToast(getString(R.string.enter_bank_name))
            return false
        }
        if (ifcs.isNullOrEmpty()) {
            showLongToast(getString(R.string.enter_bank_ifcs))
            return false
        }

        return true
    }
}