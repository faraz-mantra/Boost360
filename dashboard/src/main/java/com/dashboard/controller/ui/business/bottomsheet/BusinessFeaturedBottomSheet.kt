package com.dashboard.controller.ui.business.bottomsheet

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.BottomSheetFeaturedImageBinding
import com.dashboard.utils.BulletTextUtils
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml

class BusinessFeaturedBottomSheet :
    BaseBottomSheetDialog<BottomSheetFeaturedImageBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_featured_image
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        dialog.behavior.isDraggable = false
        val makeBulletList = BulletTextUtils.makeBulletList(
            15,
            "Your office/storefront photo from outside.",
            "Your one key product or service that your business is famous for.",
            "For services business - a formal picture of your happy staff in full work attire.",
            " For services business - A happy customer being served at the premise.",
            " For an established business - the business owner&rsquo;s photo sitting confidently in his office, or while receiving any award."
        )
        setOnClickListener(binding?.btnUnderstood, binding?.rivCloseBottomSheet)
        val heading =
            "<p><strong>This image appears with your &lsquo;business description&rsquo; on your website</strong>. It works as the second most important key visual after your business logo which can improve your brand&rsquo;s recall among your customers.</p>\n" +
                    "<p><strong>Picture you can upload:</strong></p>"
        val footer =
            "<br /><br/>Supported formats: JPEG, PNG<br />Min. dimension: 600x600 px (square)</pre>"
        binding?.ctvWhatsThisHeading?.text = fromHtml(heading)
        binding?.ctvWhatsThisBullet?.text = makeBulletList
        binding?.ctvWhatsThisFooter?.text = fromHtml(footer)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnUnderstood, binding?.rivCloseBottomSheet -> {
                dismiss()
            }
        }
    }
}