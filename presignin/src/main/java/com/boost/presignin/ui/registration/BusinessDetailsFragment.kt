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
import com.boost.presignin.rest.userprofile.BusinessProfileResponse
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.BaseFragment
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.clientId2
import com.framework.utils.getNumberFormat


class BusinessDetailsFragment : BaseFragment<FragmentBusinessDetailsBinding, LoginSignUpViewModel>() {

    private  var registerRequest: RequestFloatsModel? = null


    companion object {
        @JvmStatic
        fun newInstance(registerRequest: RequestFloatsModel?) =
                BusinessDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("request", registerRequest)
                    }
                }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_business_details
    }

    override fun getViewModelClass(): Class<LoginSignUpViewModel> {
        return LoginSignUpViewModel::class.java
    }

    override fun onCreateView() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        registerRequest = requireArguments().getSerializable("request") as? RequestFloatsModel
        binding?.phoneEt?.setText(registerRequest?.ProfileProperties?.userMobile)
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

              if (registerRequest?.ProfileProperties==null)registerRequest?.ProfileProperties = BusinessInfoModel()
            registerRequest?.ProfileProperties?.userName = name!!
            registerRequest?.ProfileProperties?.userEmail = email!!
            registerRequest?.ProfileProperties?.userMobile = phone!!
            registerRequest?.ProfileProperties?.businessName = businessName!!
            registerRequest?.AuthToken = phone
            registerRequest?.ClientId = clientId2
            registerRequest?.LoginKey = email
            registerRequest?.LoginSecret = ""
            registerRequest?.Provider = "EMAIL"
            registerRequest?.whatsAppFlag = whatsappNoFlag

            viewModel?.createMerchantProfile(request = registerRequest)?.observeOnce(viewLifecycleOwner,{
                val businessProfileResponse = it as? BusinessProfileResponse
                if (it.isSuccess()&&businessProfileResponse!=null){
                    showLongToast(getString(R.string.profile_created))
                    addFragmentReplace(com.framework.R.id.container, BusinessWebsiteFragment.newInstance(registerRequest!!), true);

                }else{
                    showShortToast(getString(R.string.unable_to_create_profile))
                }
            })
        }

    }
}