package com.boost.presignin.ui.mobileVerification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentMobileBinding
import com.framework.base.BaseFragment
import com.framework.extensions.onTextChanged
import com.framework.models.BaseViewModel
import com.framework.utils.hideKeyBoard

class MobileFragment : BaseFragment<FragmentMobileBinding, BaseViewModel>() {


    companion object {
        @JvmStatic
        fun newInstance() = MobileFragment().apply {}
    }

    override fun getLayout(): Int {
        return R.layout.fragment_mobile
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

        binding?.phoneEt?.onTextChanged {
            binding?.nextButton?.isEnabled = it.length == 10
        }


        binding?.nextButton?.setOnClickListener {

            activity?.hideKeyBoard()
            addFragmentReplace(com.framework.R.id.container, OtpVerificationFragment.newInstance(binding!!.phoneEt.text!!.toString()), true)
//            parentFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
//                    .add(com.framework.R.id.container,OtpVerificationFragment.newInstance(binding!!.phoneEt.text!!.toString()))
//                    .addToBackStack(null)
//                    .commit();
        }
    }


}