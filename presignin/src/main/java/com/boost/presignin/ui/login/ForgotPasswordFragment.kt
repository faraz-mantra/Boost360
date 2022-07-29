package com.boost.presignin.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentForgotPasswordBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.login.ForgotPassRequest
import com.boost.presignin.ui.LinkResetBottomSheet
import com.boost.presignin.ui.ResetLinkBottomSheet
import com.boost.presignin.viewmodel.ForgotPasswordViewModel
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.clientId
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*
import java.util.regex.Pattern

class ForgotPasswordFragment :
    AppBaseFragment<FragmentForgotPasswordBinding, ForgotPasswordViewModel>() {
    private var isValidText = false

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle?) = ForgotPasswordFragment().apply {
            arguments = bundle
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun getViewModelClass(): Class<ForgotPasswordViewModel> {
        return ForgotPasswordViewModel::class.java
    }

    override fun onCreateView() {
        WebEngageController.trackEvent(PS_FORGOT_PASSWORD_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        binding?.emailEt?.onTextChanged {
            binding?.getLinkBt?.isEnabled = it.isNotEmpty()
            validateText(it)
        }
        binding?.getLinkBt?.setOnClickListener {
            val sheet = LinkResetBottomSheet()
            sheet.onClick = { baseActivity.onNavPressed() }
            sheet.show(
                this@ForgotPasswordFragment.parentFragmentManager,
                ForgotPasswordFragment::class.java.name
            )


//            if (isValidText) {
//                binding?.emailEt?.background = ContextCompat.getDrawable(
//                    it.context,
//                    R.drawable.shape_border_and_background_login
//                )
//                binding?.invalidText?.visibility = View.GONE
//                showProgress()
//                viewModel?.forgotPassword(
//                    ForgotPassRequest(
//                        clientId,
//                        binding?.emailEt?.text?.toString()?.trim()
//                    )
//                )?.observeOnce(viewLifecycleOwner) {
//                    hideProgress()
//                    if (it.isSuccess()) {
//                        WebEngageController.trackEvent(
//                            PS_FORGOT_PASSWORD_SEND,
//                            LINK_SEND,
//                            NO_EVENT_VALUE
//                        )
//                        val sheet = ResetLinkBottomSheet()
//                        sheet.onClick = { baseActivity.onNavPressed() }
//                        sheet.show(
//                            this@ForgotPasswordFragment.parentFragmentManager,
//                            ForgotPasswordFragment::class.java.name
//                        )
//                    } else showShortToast(getString(R.string.please_enter_correct_user))
//                }
//            } else {
//                binding?.emailEt?.background = ContextCompat.getDrawable(
//                    it.context,
//                    R.drawable.shape_border_and_background_login1
//                )
//                binding?.invalidText?.visibility = View.VISIBLE
//                binding?.invalidText?.text = "Invalid Text"
//                showShortToast("Please enter a valid text")
//            }
        }
        binding?.arrowBack?.setOnClickListener { baseActivity.finish() }
    }

    private fun validateText(it: String) {
        isValidText = Pattern.compile("^(.+)@(\\S+) \$.")
            .matcher(it)
            .matches() || Pattern.compile("^\\d{10}$")
            .matcher(it)
            .matches() || Pattern.compile("^[a-zA-Z]*\$")
            .matcher(it)
            .matches()
    }

    override fun onResume() {
        super.onResume()
        binding?.emailEt?.post { baseActivity.showKeyBoard(binding?.emailEt) }
    }
}