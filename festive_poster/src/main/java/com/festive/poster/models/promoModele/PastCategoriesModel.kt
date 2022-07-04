package com.festive.poster.models.promoModele

import android.content.Context
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
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