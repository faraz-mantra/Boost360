package com.boost.presignin.ui.login

import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentForgetPassBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.login.ForgotPassRequest
import com.boost.presignin.ui.ResetLinkBottomSheet
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.clientId
import com.framework.webengageconstant.*

class ForgetPassFragment : AppBaseFragment<FragmentForgetPassBinding, LoginSignUpViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance() = ForgetPassFragment()
  }

  override fun getLayout(): Int {
    return R.layout.fragment_forget_pass
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(FORGOT_PASSWORD, PAGE_VIEW,NO_EVENT_VALUE)
    binding?.emailEt?.onTextChanged { binding?.getLinkBt?.isEnabled = it.isNotEmpty() }
    binding?.getLinkBt?.setOnClickListener {
      showProgress()
      viewModel?.forgotPassword(ForgotPassRequest(clientId, binding?.emailEt?.text?.toString()?.trim()))?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess()) {
          WebEngageController.trackEvent(FORGOT_PASSWORD_SEND, RESTE_PASSWORD_LINK_SEND,NO_EVENT_VALUE)
          val sheet = ResetLinkBottomSheet()
          sheet.onClick = { baseActivity.onNavPressed() }
          sheet.show(this@ForgetPassFragment.parentFragmentManager, ForgetPassFragment::class.java.name)
        } else showShortToast(getString(R.string.please_enter_correct_user))
      })
    }
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener {
      parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
  }

}