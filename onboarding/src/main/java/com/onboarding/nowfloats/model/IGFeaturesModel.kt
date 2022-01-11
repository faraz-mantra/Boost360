package com.onboarding.nowfloats.model

import androidx.annotation.DrawableRes
import com.framework.models.BaseRecyclerViewItem
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

class IGFeaturesModel(
    val title:String,
    @DrawableRes
    val img:Int
): AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.IG_FEATURES.getLayout()
    }
}