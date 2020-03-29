package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.framework.extensions.isVisible
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessContactInfoBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.registration.RegistrationViewModel

class RegistrationBusinessContactInfoFragment : BaseRegistrationFragment<FragmentRegistrationBusinessContactInfoBinding, RegistrationViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationBusinessContactInfoFragment {
      val fragment = RegistrationBusinessContactInfoFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    binding?.viewImage?.post {
      (binding?.viewImage?.fadeIn(600L)?.mergeWith(binding?.viewBusiness?.fadeIn()))
              ?.andThen(binding?.title?.fadeIn(150L)?.mergeWith(binding?.subTitle?.fadeIn(150L)))
              ?.andThen(binding?.viewForm?.fadeIn())?.andThen(binding?.next?.fadeIn(150L))
              ?.subscribe()
    }
    binding?.contactInfo = viewModel
    setOnClickListener(binding?.next)
    binding?.number?.setOnFocusChangeListener { v, hasFocus ->
      if (hasFocus) {
        if (binding?.number?.text?.startsWith("+91") != true) {
          binding?.number?.setText("+91${binding?.number?.text ?: ""}")
          binding?.number?.setSelection(3)
        }
      } else {
        if (binding?.number?.text?.toString() == "+91") {
          binding?.number?.setText("")
        }
      }
    }
  }

  override fun onClick(v: View) {
    when (v) {
      binding?.next -> if (binding?.textBtn?.isVisible() == true && isValid()) {
        requestFloatsModel?.contactInfo = viewModel
        getDotProgress()?.let {
          binding?.textBtn?.visibility = GONE
          binding?.next?.addView(it)
          it.startAnimation()
          Handler().postDelayed({
            it.stopAnimation()
            it.removeAllViews()
            binding?.textBtn?.visibility = VISIBLE
            gotoBusinessWebsite()
          }, 1000)
        }
      }
    }
  }

  private fun isValid(): Boolean {
    viewModel?.businessName = binding?.storeName?.text?.toString()
    viewModel?.address = binding?.address?.text?.toString()
    viewModel?.email = binding?.email?.text?.toString()

    try {
      viewModel?.number = binding?.number?.text?.substring(3)
    } catch (e: StringIndexOutOfBoundsException) {
      viewModel?.number = null
    }
    return viewModel?.let {
      return if (it.businessName.isNullOrBlank()) {
        showShortToast(resources.getString(R.string.business_cant_empty))
        false
      } else if (it.address.isNullOrBlank()) {
        showShortToast(resources.getString(R.string.business_address_cant_empty))
        false
      } else if (!it.isEmailValid()) {
        showShortToast(resources.getString(R.string.email_invalid))
        false
      } else if (!it.isNumberValid()) {
        showShortToast(resources.getString(R.string.phone_number_invalid))
        false
      } else true
    } ?: false
  }

  override fun getViewModelClass(): Class<RegistrationViewModel> {
    return RegistrationViewModel::class.java
  }
}