package com.dashboard.controller.ui.business.bottomsheet

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.BottomSheetFeaturedImageBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml

class BusinessFeaturedBottomSheet:
    BaseBottomSheetDialog<BottomSheetFeaturedImageBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_featured_image
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        dialog.behavior.isDraggable = false
        setOnClickListener(binding?.btnUnderstood,binding?.rivCloseBottomSheet)
        val template = "<p><strong>This image appears with your &lsquo;business description&rsquo; on your website</strong>. It works as the second most important key visual after your business logo which can improve your brand&rsquo;s recall among your customers.</p>\n" +
                "<p><strong>Picture you can upload:</strong></p>" +
                "<pre>&bull; Your office/storefront photo from outside.</pre><br/>" +
                "<pre>&bull; Your one key product or service that your business is famous for.</pre><br/>" +
                "<pre>&bull; For services business - a formal picture of your happy staff in full work attire.</pre><br/>" +
                "<pre>&bull; For services business - A happy customer being served at the premise.</pre><br/>" +
                "<pre>&bull; For an established business - the business owner&rsquo;s photo sitting confidently in his office, or while receiving any award.<br /><br/>Supported formats: JPEG, PNG<br />Min. dimension: 600x600 px (square)</pre>"
        binding?.ctvWhatsThis?.text = fromHtml(template)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnUnderstood, binding?.rivCloseBottomSheet->{
                dismiss()
            }
        }
    }
}