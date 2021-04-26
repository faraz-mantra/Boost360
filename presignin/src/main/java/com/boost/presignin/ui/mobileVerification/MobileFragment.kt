package com.boost.presignin.ui.mobileVerification

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentMobileBinding
import com.boost.presignin.model.userprofile.ResponseMobileIsRegistered
import com.boost.presignin.ui.AccountNotFoundActivity
import com.boost.presignin.ui.intro.IntroActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.clientId2
import com.framework.utils.hideKeyBoard

class MobileFragment : AppBaseFragment<FragmentMobileBinding, LoginSignUpViewModel>() {


    companion object {
        private val PHONE_NUMBER = "phone_number"
        @JvmStatic
        fun newInstance() = MobileFragment().apply {}
        fun newInstance(phoneNumber: String) =
                MobileFragment().apply {
                    arguments = Bundle().apply {
                        putString(PHONE_NUMBER, phoneNumber)
                    }
                }
    }
    private val phoneNumber by lazy { requireArguments().getString(PHONE_NUMBER) }

    override fun getLayout(): Int {
        return R.layout.fragment_mobile
    }

    override fun getViewModelClass(): Class<LoginSignUpViewModel> {
        return LoginSignUpViewModel::class.java
    }

    override fun onCreateView() {

        binding?.phoneEt?.onTextChanged {
            binding?.nextButton?.isEnabled = it.length == 10
        }
//        binding?.phoneEt?.setText(phoneNumber)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBack()
            }
        })
        val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
        backButton?.setOnClickListener {
            goBack()
        }
        binding?.nextButton?.setOnClickListener {

            activity?.hideKeyBoard()
            checkIfUserIsRegistered()
//            parentFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
//                    .add(com.framework.R.id.container,OtpVerificationFragment.newInstance(binding!!.phoneEt.text!!.toString()))
//                    .addToBackStack(null)
//                    .commit();
        }
    }

    private fun goBack() {
        startActivity(Intent(requireContext(), IntroActivity::class.java))
        requireActivity().finish()
    }

    private fun checkIfUserIsRegistered() {
        showProgress(getString(R.string.loading), false)
        viewModel?.checkMobileIsRegistered(binding?.phoneEt?.text.toString().toLong(),clientId2)?.observeOnce(viewLifecycleOwner, Observer {
            hideProgress()
            if (it.isSuccess()) {
                val data = it as? ResponseMobileIsRegistered
                if (data?.result == true) {
                    //user is registered generate otp and verify it
                    addFragmentReplace(com.framework.R.id.container, OtpVerificationFragment.newInstance(binding!!.phoneEt.text!!.toString()), addToBackStack = true)

                } else {
                    //user is not registered open signup flow
                    navigator?.startActivity(AccountNotFoundActivity::class.java, args = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, binding?.phoneEt?.text.toString()) })

                }

            } else {
                showShortToast(getString(R.string.something_doesnt_seem_right))
            }

        })
    }


}