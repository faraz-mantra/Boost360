package com.nowfloats.instagram.models

import androidx.annotation.DrawableRes
import com.framework.models.BaseRecyclerViewItem
import com.nowfloats.instagram.constants.RecyclerViewItemType
import com.nowfloats.instagram.recyclerView.AppBaseRecyclerViewItem

class IGFeaturesModel(
    val title:String,
    @DrawableRes
    val img:Int
):AppBaseRecyclerViewItem{

    override fun getViewType(): Int {
        return RecyclerViewItemType.IG_FEATURES.getLayout()
    }
}