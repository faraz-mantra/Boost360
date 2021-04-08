package com.boost.presignin.ui.registration

import android.os.Bundle
import android.view.WindowManager
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentBusinessDetailsBinding
import com.boost.presignin.extensions.isBusinessNameValid
import com.boost.presignin.extensions.isEmailValid
import com.boost.presignin.extensions.isNameValid
import com.boost.presignin.extensions.isPhoneValid
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.RequestFloatsModel
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel


class BusinessDetailsFragment : BaseFragment<FragmentBusinessDetailsBinding, BaseViewModel>() {

    private lateinit var registerRequest: RequestFloatsModel;


    companion object {
        @JvmStatic
        fun newInstance(registerRequest: RequestFloatsModel) =
                BusinessDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("request", registerRequest)
                    }
                }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_business_details
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        registerRequest = requireArguments().getParcelable("request")!!
        binding?.confirmButton?.setOnClickListener {

            val name = binding?.nametEt?.text?.toString()
            val businessName = binding?.businessNameEt?.text?.toString();
            val email = binding?.emailEt?.text?.toString()
            val phone = binding?.phoneEt?.text?.toString()


            if (!name.isNameValid()) {
                showShortToast("Enter valid name")
                return@setOnClickListener
            }

            if (!businessName.isBusinessNameValid()) {
                showShortToast("Enter valid email")
                return@setOnClickListener
            }

            if (!email.isEmailValid()) {
                showShortToast("Enter valid email")
                return@setOnClickListener
            }
            if (!phone.isPhoneValid()) {
                showShortToast("Enter valid phone number")
                return@setOnClickListener
            }

            val whatsappNoFlag = binding!!.checkbox.isChecked


            registerRequest.contactInfo = BusinessInfoModel(name!!, businessName!!, email!!, phone!!)
            addFragmentReplace(com.framework.R.id.container, BusinessWebsiteFragment.newInstance(registerRequest), true);
        }

    }
}