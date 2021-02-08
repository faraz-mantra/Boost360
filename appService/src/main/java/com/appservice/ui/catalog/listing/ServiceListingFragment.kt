package com.appservice.ui.catalog.listing

import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentServiceListingBinding
import com.appservice.viewmodel.ServiceViewModel

class ServiceListingFragment : AppBaseFragment<FragmentServiceListingBinding, ServiceViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_service_listing
    }

    companion object {
        fun newInstance(): ServiceListingFragment {
            return ServiceListingFragment()
        }
    }

    override fun getViewModelClass(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setEmptyView()

    }
    private fun setEmptyView() {
        val spannableString = SpannableString(resources.getString(R.string.you_don_t_have_any_service_added_to_your_digital_catalog_as_of_yet_watch_video))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showShortToast("video link")
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, spannableString.length.minus(11), spannableString.length, 0)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.black_4a4a4a)), spannableString.length.minus(11), spannableString.length, 0)
        spannableString.setSpan(UnderlineSpan(), spannableString.length.minus(11), spannableString.length, 0)
        binding?.serviceListingEmpty?.ctvAddServiceSubheading?.text = spannableString
        binding?.serviceListingEmpty?.ctvAddServiceSubheading?.movementMethod = LinkMovementMethod.getInstance()
        binding?.serviceListingEmpty?.ctvAddServiceSubheading?.highlightColor = resources.getColor(android.R.color.transparent)

    }

}