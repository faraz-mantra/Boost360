package com.appservice.model.updateBusiness.pastupdates

import android.content.Context
import com.appservice.R
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class PastCategoriesModel(
    val postType: Int = 0,
    val categoryTitle: String? = null,
    var categoryCount: Int = 0
) : Serializable, AppBaseRecyclerViewItem {

    fun getData(c: Context): ArrayList<PastCategoriesModel> {
        return arrayListOf(
            PastCategoriesModel(
                postType = 0,
                categoryTitle = c.getString(R.string.cat_0_all_updates)
            ),
            PastCategoriesModel(
                postType = 1,
                categoryTitle = c.getString(R.string.cat_1_promotional_posts)
            ),
            PastCategoriesModel(
                postType = 2,
                categoryTitle = c.getString(R.string.cat_2_image_text)
            ),
            PastCategoriesModel(
                postType = 3,
                categoryTitle = c.getString(R.string.cat_3_text_only)
            )
        )
    }

    override fun getViewType(): Int {
        return RecyclerViewItemType.PAST_POST_CATEGORIES.getLayout()
    }
}