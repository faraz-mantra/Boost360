package com.appservice.ui.domainbooking.model

import android.text.SpannableString
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.recyclerView.BaseRecyclerViewItem

class DomainStepsModel(
    var desc:SpannableString
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.DOMAIN_STEPS.getLayout()
    }
}