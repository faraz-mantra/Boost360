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
import com.framework.models.BaseViewModel

class SetupMyWebsiteStep1Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep1Binding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep1Fragment {
            val fragment = SetupMyWebsiteStep1Fragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }


    override fun getLayout(): Int {
      return R.layout.layout_set_up_my_website_step_1
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.tvNextStep1?.setOnClickListener {
            addFragment(R.id.inner_container,SetupMyWebsiteStep2Fragment.newInstance(Bundle()
                .apply
                {
                    putString(IntentConstant.EXTRA_PHONE_NUMBER.name,phoneNumber)
                }),true)
        }
    }
}