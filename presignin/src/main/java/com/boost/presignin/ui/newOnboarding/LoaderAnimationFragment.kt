package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentLoaderAnimationBinding
import com.boost.presignin.databinding.FragmentWelcomeBinding
import com.framework.models.BaseViewModel

class LoaderAnimationFragment : AppBaseFragment<FragmentLoaderAnimationBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): LoaderAnimationFragment {
            val fragment = LoaderAnimationFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_loader_animation
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
       return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        initUI()
    }

    private fun initUI() {
    }
}