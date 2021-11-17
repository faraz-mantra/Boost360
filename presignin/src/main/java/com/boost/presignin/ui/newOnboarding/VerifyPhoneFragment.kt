package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.boost.presignin.databinding.FragmentVerifyPhoneBinding
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class VerifyPhoneFragment : AppBaseFragment<FragmentVerifyPhoneBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): VerifyPhoneFragment {
            val fragment = VerifyPhoneFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_verify_phone
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
       return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnListeners()
        initUI()
    }

    private fun initUI() {

    }

    private fun setOnListeners() {
        binding?.pinOtpVerify?.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                val otp = binding?.pinOtpVerify?.otp
                binding?.tvVerifyOtp?.isEnabled = otp != null && otp.length == 4
                binding?.linearWhatsApp?.apply {
                    if (otp != null && otp.length == 4) visible() else gone()
                }
            }

            override fun onOTPComplete(otp: String) {
                binding?.linearWhatsApp?.visible()
            }
        }

        binding?.tvVerifyOtp?.setOnClickListener {
            startFragmentFromNewOnBoardingActivity(
                activity = requireActivity(),
                type = com.boost.presignin.constant.FragmentType.WELCOME_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_help_on_boarding_new, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_help_onboard -> {
                showShortToast(getString(R.string.coming_soon))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}