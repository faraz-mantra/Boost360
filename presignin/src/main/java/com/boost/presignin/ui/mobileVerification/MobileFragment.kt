package com.boost.presignin.ui.mobileVerification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentMobileBinding
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

class MobileFragment : BaseFragment<FragmentMobileBinding, BaseViewModel>() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mobile, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MobileFragment().apply {}
    }

    override fun getLayout(): Int {
        return  R.layout.fragment_mobile
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}