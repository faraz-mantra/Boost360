package com.boost.presignin.ui.registration

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentBusinessWebsiteBinding
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

class BusinessWebsiteFragment : BaseFragment<FragmentBusinessWebsiteBinding, BaseViewModel>() {


    companion object {

        @JvmStatic
        fun newInstance() =
                BusinessWebsiteFragment().apply {
                    arguments = Bundle().apply {}
                }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_business_website
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

        val amountSpannableString = SpannableString("'downtownspa'").apply {
           // setSpan(ForegroundColorSpan(Color.rgb(0,0,0)), 0, length, 0)
            setSpan(StyleSpan(Typeface.BOLD), 0,length,0)
        }

        binding?.subHeadingTv?.text = SpannableStringBuilder().apply {
            append(getString(R.string.website_available_text))
            append(amountSpannableString)
        }
        binding?.confirmButton?.setOnClickListener {
            addFragmentReplace(com.framework.R.id.container, RegistrationSuccessFragment.newInstance(),true);
        }
    }



}