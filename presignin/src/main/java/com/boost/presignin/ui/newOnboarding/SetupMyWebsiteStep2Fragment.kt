package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.boost.presignin.databinding.FragmentWelcomeBinding
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep1Binding
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep2Binding
import com.framework.extensions.afterTextChanged
import com.framework.models.BaseViewModel

class SetupMyWebsiteStep2Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep2Binding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep2Fragment {
            val fragment = SetupMyWebsiteStep2Fragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }

    override fun getLayout(): Int {
      return R.layout.layout_set_up_my_website_step_2
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.tvNextStep2?.setOnClickListener {
            addFragment(R.id.inner_container,SetupMyWebsiteStep3Fragment.newInstance(Bundle()
                .apply
                {
                    putString(IntentConstant.EXTRA_PHONE_NUMBER.name,phoneNumber)
                    putString(IntentConstant.EXTRA_BUSINESS_NAME.name,binding?.edInputBusinessName?.text.toString())
                }),true)
        }

        binding?.edInputBusinessName?.afterTextChanged {
            binding?.tvNextStep2?.isEnabled = it.isEmpty().not()
            binding?.includeMobileView?.tvTitle?.text = it
        }
    }
}