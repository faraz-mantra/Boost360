package com.boost.presignin.ui.registration

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentBusinessWebsiteBinding
import com.boost.presignin.extensions.isWebsiteValid
import com.boost.presignin.model.RequestFloatsModel
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

class BusinessWebsiteFragment : BaseFragment<FragmentBusinessWebsiteBinding, BaseViewModel>() {


    private lateinit var registerRequest: RequestFloatsModel;

    companion object {

        @JvmStatic
        fun newInstance( registerRequest: RequestFloatsModel) =
                BusinessWebsiteFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("request", registerRequest)
                    }
                }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_business_website
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        registerRequest = requireArguments().getParcelable("request")!!

        val websiteHint = registerRequest.contactInfo!!.businessName.trim().replace(" ","")
        val amountSpannableString = SpannableString("'$websiteHint' ").apply {
           // setSpan(ForegroundColorSpan(Color.rgb(0,0,0)), 0, length, 0)
            setSpan(StyleSpan(Typeface.BOLD), 0,length,0)
        }

        binding?.websiteEt?.setText(websiteHint)

        binding?.websiteStatusTv?.text = SpannableStringBuilder().apply {
            append(amountSpannableString)
            append(getString(R.string.website_available_text))
        }
        binding?.confirmButton?.setOnClickListener {
            val website = binding?.websiteEt?.text?.toString()

            if(!website.isWebsiteValid()){
                showShortToast("Enter a valid website name")
                return@setOnClickListener
            }

            registerRequest.websiteUrl = "$website.nowfloats.com"

            addFragmentReplace(com.framework.R.id.container, RegistrationSuccessFragment.newInstance(registerRequest),true);
        }
    }



}