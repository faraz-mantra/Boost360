package com.boost.presignin.model.newOnboarding

import android.content.Context
import androidx.annotation.DrawableRes
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class IntroItemNew(
    val title: String? = null,
    @DrawableRes val imageResource: Int? = null
) : Serializable, AppBaseRecyclerViewItem {

    fun getData(c: Context): ArrayList<IntroItemNew> {
        return arrayListOf(
            IntroItemNew(
                c.getString(R.string.new_into_1_title),
                R.drawable.tutorial_1_new
            ),
            IntroItemNew(
                c.getString(R.string.new_into_2_title),
                R.drawable.tutorial_new_2
            ),
            IntroItemNew(
                c.getString(R.string.new_into_3_title),
                R.drawable.tutorial_1_new
            ),
            IntroItemNew(
                c.getString(R.string.new_into_4_title),
                R.drawable.tutorial_3_new
            ),
        )
    }

    override fun getViewType(): Int {
        return RecyclerViewItemType.INTRO_NEW_SLIDES.getLayout()
    }
}