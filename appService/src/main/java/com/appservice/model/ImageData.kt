package com.appservice.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem

data class ImageData(var url:String) : AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return RecyclerViewItemType.BACKGROUND_IMAGE_RV.getLayout()
    }
}
