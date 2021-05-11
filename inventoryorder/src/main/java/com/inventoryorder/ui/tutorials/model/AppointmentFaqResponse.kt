package com.inventoryorder.ui.tutorials.model

import com.google.gson.annotations.SerializedName
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import com.inventoryorder.ui.tutorials.model.product.Faq
import com.inventoryorder.ui.tutorials.model.product.TutorialsDataResponse
import java.io.Serializable

data class AppointmentFaqResponse(

        @field:SerializedName("Contents")
        var contents: ArrayList<Faq>? = null,

        @field:SerializedName("Fragment Data")
        var fragmentData: FragmentData? = null,
) : Serializable


data class ContentsItem(

        @field:SerializedName("question")
        var question: String? = null,

        @field:SerializedName("answer")
        var answer: String? = null,

        ) : Serializable, AppBaseRecyclerViewItem {
    var recyclerViewType = RecyclerViewItemType.ITEM_FAQ.getLayout()
    override fun getViewType(): Int {
        return recyclerViewType
    }
}
