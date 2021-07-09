package com.onboarding.nowfloats.bottomsheet

import SectionsFeature
import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.views.customViews.CustomImageView
import com.framework.views.customViews.CustomTextView
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.builder.BottomDialogBuilder
import com.onboarding.nowfloats.bottomsheet.builder.StatusCallback
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.listenToUpdate
import com.onboarding.nowfloats.extensions.fadeIn
import kotlinx.android.synthetic.main.header_bottom_sheet.view.*


fun BottomDialogBuilder.contentHeader(data: Any, round: Boolean = false) {
    header(BottomSheetHeader()) {
        this.data = data
        isRound = round
    }
}

class BottomSheetHeader : ContentBuilder(), StatusCallback {
    override val layoutRes: Int
        get() = R.layout.header_bottom_sheet

    var data: Any? by listenToUpdate(null, this)
    var isRound: Boolean = false

    lateinit var title: CustomTextView
    lateinit var subTitle: CustomTextView
    lateinit var iconShare: CustomImageView
    lateinit var filllay: View
    lateinit var maimView: View

    override fun init(view: View) {
        filllay = view.fill_layout
        maimView = view.maim_view
        iconShare = view.icon_share
        title = view.title
        subTitle = view.sub_title
        setData()
    }

    private fun setData() {
        dialog.headerView.setBackgroundColor(0x0)
        if (isRound) maimView.setBackgroundResource(R.drawable.toolbar_round_bg)
        if (data is String) {
            title.text = data as? String
            subTitle.visible()
            iconShare.fadeIn().andThen(title.fadeIn(100L)).andThen(subTitle.fadeIn(100L)).subscribe()
        } else if (data is SectionsFeature) {
            val feature = data as? SectionsFeature
            title.text = feature?.title
            subTitle.gone()
            val drawable = feature?.getDrawable(dialog.context)
            drawable?.let { img -> iconShare.setImageDrawable(img) }
            iconShare.fadeIn().andThen(title.fadeIn(200L)).subscribe()
        }
    }

    override fun updateContent(type: Int, data: Any?) {
    }

}
