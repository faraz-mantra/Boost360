package com.appservice.ui.domainbooking.model

import android.text.SpannableString
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem

class DomainStepsModel(
    var desc: SpannableString,
    var isBulletIndicated: Boolean,
    var isTextDark: Boolean
) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.DOMAIN_STEPS.getLayout()
    }
}